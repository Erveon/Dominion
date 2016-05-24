package net.ultradev.dominion.servlets;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.GameManager;

@ServerEndpoint("/socket")
public class OnlineAPI {
	
	// Examples of expected incoming message (in JSON format)
	// -----------------------------------------------------
	// GAME DATA
	// { 
	//   "type": "game",
	//   "game": {
	//              // The usual key value parameters but in JSON
	//   		 }
	// }
	// ASKING FOR LOBBIES
	// { 
	//   "type": "lobbies"
	// }
	
	private GameServer gs;
	
	public OnlineAPI() {
		this.gs = GameServer.get();
	}
	
	public GameServer getGameServer() {
		return gs;
	}
	
	@OnOpen
    public void onOpen(Session session) throws IOException {
        session.getBasicRemote().sendText("connected");
    	System.out.println(session.getId() + " connected");
    }

	@OnMessage
    public void onMessage(String message, Session session){
		GameManager gm = getGameServer().getGameManager();
		if(!isJSONValid(message)) {
			send(session, gm.getInvalid("Object sent is not valid JSON").toString());
			return;
		}
		
		JSONObject json = JSONObject.fromObject(message);
		
		if(json.containsKey("type")) {
			switch(json.getString("type").toLowerCase()) {
				case "lobbies":
					getGameServer().getUtils().debug("Session " + session.getId() + " has asked for the lobbies");
					break;
				default:
					send(session, gm.getInvalid("Type not found: " + json.getString("type")).toString());
			}
		} else {
			send(session, gm.getInvalid("Requests require a type").toString());
		}
    }
	
	public void send(Session session, String message) {
		try {
			session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			e.printStackTrace();
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
    
    public boolean isJSONValid(String message) {
        try {
            JSONObject.fromObject(message);
        } catch (JSONException ex) {
            try {
                JSONArray.fromObject(message);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
    
}