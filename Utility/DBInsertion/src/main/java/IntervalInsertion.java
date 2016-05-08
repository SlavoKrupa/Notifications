import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class IntervalInsertion {

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "password";

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Argument Error: Insert delay in seconds");
            System.exit(0);
        }
        int seconds = Integer.parseInt(args[0]);

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            if (conn != null) {
                System.out.println("Successfully connected to DB");
            }

            Random rand = new Random();

            while (true) {
                char c = (char) (rand.nextInt(26) + 'a');

                Statement st = conn.createStatement();
                st.execute("INSERT INTO simple_notifications(message) VALUES ('" + c + "');");
                System.out.println("Record added to DB: " + c);
                st.close();
                try {
                    Thread.sleep(seconds * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
