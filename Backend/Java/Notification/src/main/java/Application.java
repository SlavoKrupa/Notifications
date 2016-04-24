import org.glassfish.tyrus.server.Server;

import javax.websocket.DeploymentException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;

public class Application {

    private static String url = "jdbc:postgresql://localhost:5432/test";
    private static String username = "postgres";
    private static String password = "admin123";

    public static void main(String[] args) {
        try {
            //create connection with DB
            Connection conn = DriverManager.getConnection(url, username, password);
            if (conn != null) {
                System.out.println("Successfully connected to DB");
            }

            //listen to db channel and add incoming notifications to q
            Listener listener = new Listener(conn);
            listener.start();

            //start websocket server endpoint
            Server server = new Server("127.0.0.1", 8080, "", WebSocketServer.class);
            server.start();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DeploymentException e) {
            e.printStackTrace();
        }
    }
}