package cz.muni.fi;

import org.json.JSONObject;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.PGNotification;
import java.sql.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;

public class Listener extends Thread {
    private final PgConnection pgconn;
    public static BlockingQueue<JSONObject> queue = new LinkedBlockingQueue();
     private static final String DB_CREATION = "db_creation";

    Listener(Connection conn) throws SQLException {
        this.pgconn = (PgConnection) conn;
        Statement st = conn.createStatement();
        st.execute("LISTEN \"SIMPLE_NOTIFY_CHANNEL\"");
        st.close();
    }
    @Override
    public void run() {
        while (true) {
            try {
                PGNotification notifications[] = pgconn.getNotifications();
                if (notifications != null) {
                    for (PGNotification notification : notifications) {
                        JSONObject jsonObj = new JSONObject(notification.getParameter());
                        // fix for db creation timezone
                        DateTime dbCreation = DateTime.parse(String.valueOf(jsonObj.get(DB_CREATION)));
                        jsonObj.put("db_creation", dbCreation);
                        jsonObj.put("backend_receiving", new DateTime());
                        queue.put(jsonObj);
                    }
                }
            } catch (SQLException sqle) {
                Logger.getLogger(WebSocketServer.class.getName()).log(Level.SEVERE, null, sqle);
            } catch (InterruptedException ie) {
                 Logger.getLogger(WebSocketServer.class.getName()).log(Level.SEVERE, null, ie);
            }
        }
    }
   
}