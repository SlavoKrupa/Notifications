import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class Init implements ServletContextListener {

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("Initialization of Java Notification app");
        Application.main(null);
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
