package cz.muni.fi;

import org.glassfish.tyrus.server.Server;

import javax.websocket.DeploymentException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Application {

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "password";

    public static void main(String[] args) {
        try {
            //create connection with DB
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            if (conn != null) {
                System.out.println("Successfully connected to DB");
            }

            //listen to db channel and add incoming notifications to q
            Listener listener = new Listener(conn);
            listener.start();

            //start websocket server endpoint
            Server server = new Server("127.0.0.1", 8082, "", WebSocketServer.class);
            server.start();

        } catch (SQLException e) {
            Logger.getLogger(WebSocketServer.class.getName()).log(Level.SEVERE, null, e);
        } catch (DeploymentException e) {
            Logger.getLogger(WebSocketServer.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}