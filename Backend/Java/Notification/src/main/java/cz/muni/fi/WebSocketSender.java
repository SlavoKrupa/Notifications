package cz.muni.fi;


import java.io.IOException;
import javax.websocket.Session;
import org.json.JSONObject;

public class WebSocketSender extends Thread {

    private final Session userSession;

    public WebSocketSender(Session userSession) {
        this.userSession = userSession;
    }

    @Override
    public void run() {
        JSONObject notification = null;
        try {
            
            while (this.userSession.isOpen()) {

                notification = Listener.queue.take();
                if (notification != null) {
                    // System.out.println("Popping from queue: " + notification);
                    this.userSession.getBasicRemote().sendText(notification.toString());
                }

            }
        } catch (IOException | InterruptedException |IllegalStateException e) {
            if(notification != null) {
                Listener.queue.add(notification);
            }            
        } finally {
            System.out.println("Ending sending of files for: " + userSession.getId());
        }

    }

}
