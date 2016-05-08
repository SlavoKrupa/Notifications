package cz.muni.fi;


import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ServerEndpoint("/java")
public class WebSocketServer {
     @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("Open new websocket connection " + userSession.getId());
        Thread messageSender = new WebSocketSender(userSession);
        messageSender.start();
        

    }

    @OnClose
    public void onClose(Session userSession) {
        System.out.println("Close websocket connection " + userSession.getId() );
         try {
             userSession.close();
         } catch (IOException ex) {
             Logger.getLogger(WebSocketServer.class.getName()).log(Level.SEVERE, null, ex);
         }
       
    }

    @OnError
    public void onError(Throwable t) {
         System.out.println("Error on websocket connection.");
    }

    @OnMessage
    public void onMessage(String message) {
      // we don't need this
    }
}
