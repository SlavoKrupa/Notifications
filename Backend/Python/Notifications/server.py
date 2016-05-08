import psycopg2
from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT

import eventlet
from eventlet.hubs import trampoline

import iso8601
import json
from datetime import datetime

import tornado.ioloop
import tornado.web
import tornado.websocket

from tornado.options import define

dbname = 'postgres'
host = '127.0.0.1'
user = 'postgres'
password = 'password'

dsn = 'dbname=%s host=%s user=%s password=%s' % (dbname, host, user, password)

define("port", default=5432, help="run on the given port", type=int)
communication_queue = eventlet.Queue()
eventlet.monkey_patch()


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

            # Hack with time zone
            time_with_timezone = iso8601.parse_date(json_notification["db_creation"])
            json_notification["db_creation"] = time_with_timezone.strftime("%Y.%m.%d %H:%M:%S.%f")

            q.put(json_notification)


def send_data_to_ws(ws):
    while 1:
        notification = communication_queue.get()
        ws.write_message(json.dumps(notification))

    print 'Closing connection'


class WSHandler(tornado.websocket.WebSocketHandler):
    def __init__(self, app, request, **kwargs):
        super(WSHandler, self).__init__(app, request, **kwargs)
        self.thread = None

    def data_received(self, chunk):
        pass

    def open(self):
        print 'new connection'
        self.thread = eventlet.spawn(send_data_to_ws, self)

    def on_message(self, message):
        print 'message received:  %s' % message
        # Reverse Message and send it back
        print 'sending back message: %s' % message[::-1]
        self.write_message(message[::-1])

    def on_close(self):
        print 'connection closed'
        self.thread.kill()

    def check_origin(self, origin):
        return True


application = tornado.web.Application([
    (r'/Python', WSHandler),
])

if __name__ == "__main__":
    eventlet.spawn(db_listen, communication_queue)
    application.listen(8081)
    tornado.ioloop.IOLoop.instance().start()
