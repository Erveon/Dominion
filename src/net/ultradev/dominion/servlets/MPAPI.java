package net.ultradev.dominion.servlets;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/mpapi")
public class MPAPI {
	
	@OnOpen
    public void onOpen(Session session) throws IOException {
        session.getBasicRemote().sendText("connected");
    	System.out.println(session.getId() + " disconnected");
    }

	@OnMessage
    public void onMessage(String message, Session session){
        System.out.println("Message from " + session.getId() + ": " + message);
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {
    	System.out.println(session.getId() + " disconnected");
    }
    
}