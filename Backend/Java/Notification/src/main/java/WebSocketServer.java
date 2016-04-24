import org.json.JSONObject;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/java")
public class WebSocketServer {
    Session userSession = null;

    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("Open new websocket connection " + userSession.getId());
        this.userSession = userSession;

        while(true) {
            try {
                JSONObject notification = Listener.queue.take();
                if(notification != null) {
                    System.out.println("Popping from queue: " + notification);
                    this.userSession.getBasicRemote().sendText(notification.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClose
    public void onClose() {
        System.out.println("Close websocket connection " + this.userSession.getId());
        this.userSession = null;
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Message received from " + userSession.getId() + ": " + message);
        try {
            System.out.println("Sending back to " + userSession.getId() + ": " + message);
            userSession.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
