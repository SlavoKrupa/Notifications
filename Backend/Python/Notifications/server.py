import psycopg2
from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT

import eventlet
from eventlet.hubs import trampoline

import json
from datetime import datetime

import tornado.ioloop
import tornado.web
import tornado.websocket

from tornado.options import define, options, parse_command_line

dbname = 'notifications'
host = '127.0.0.1'
user = 'postgres'
password = 'password'

dsn = 'dbname=%s host=%s user=%s password=%s' % (dbname, host, user, password)

define("port", default=8080, help="run on the given port", type=int)


def db_listen(q):
    """
    Open a db connection and add notifications to *q*.
    """
    cnn = psycopg2.connect(dsn)
    cnn.set_isolation_level(ISOLATION_LEVEL_AUTOCOMMIT)
    cur = cnn.cursor()
    cur.execute("LISTEN \"SIMPLE_NOTIFY_CHANNEL\";")
    while 1:
        trampoline(cnn, read=True)
        cnn.poll()
        while cnn.notifies:
            notify = cnn.notifies.pop()
            json_notification = json.loads(notify.payload)
            json_notification["backend_receiving"] = str(datetime.now())
            q.put(json_notification)


class WSHandler(tornado.websocket.WebSocketHandler):
    def data_received(self, chunk):
        pass

    def open(self):
        print 'new connection'
        q = eventlet.Queue()
        eventlet.spawn(db_listen, q)
        while 1:
            notification = q.get()
            self.write_message(json.dumps(notification))

    def on_message(self, message):
        print 'message received:  %s' % message
        # Reverse Message and send it back
        print 'sending back message: %s' % message[::-1]
        self.write_message(message[::-1])

    def on_close(self):
        print 'connection closed'

    def check_origin(self, origin):
        return True


application = tornado.web.Application([
    (r'/data', WSHandler),
])

if __name__ == "__main__":
    application.listen(8080)
    tornado.ioloop.IOLoop.instance().start()
