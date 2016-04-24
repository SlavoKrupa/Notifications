import org.joda.time.LocalDateTime;
import org.json.JSONObject;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.PGNotification;
import java.sql.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Listener extends Thread {
    private PgConnection pgconn;
    public static BlockingQueue<JSONObject> queue;

    Listener(Connection conn) throws SQLException {
        this.pgconn = (PgConnection) conn;
        queue = new LinkedBlockingQueue();
        Statement st = conn.createStatement();
        st.execute("LISTEN \"SIMPLE_CHANNEL\"");
        st.close();
    }

    public void run() {
        while (true) {
            try {
                PGNotification notifications[] = pgconn.getNotifications();
                if (notifications != null) {
                    for (int i = 0; i < notifications.length; i++) {
                        JSONObject jsonObj = new JSONObject(notifications[i].getParameter());
                        jsonObj.put("backend_receiving", new LocalDateTime());
                        System.out.println("Got notification: "
                                + notifications[i].getName() + " | "
                                + notifications[i].getParameter() + " | "
                                + notifications[i].getPID());
                        //adding to notification queue
                        System.out.println("Pushing to queue: " + jsonObj.toString());
                        queue.put(jsonObj);
                    }
                }
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
}