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
import net.ultradev.dominion.game.online.OnlineGame;
import net.ultradev.dominion.game.player.Player;

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
    	System.out.println(session.getId() + " connected");
    	getGameServer().getGameManager().addConnection(session);
    }

	@OnMessage
    public void onMessage(String message, Session session){
		GameManager gm = getGameServer().getGameManager();
		if(!isJSONValid(message)) {
			send(session, gm.getInvalid("Object sent is not valid JSON").toString());
			return;
		}
		
		JSONObject json = JSONObject.fromObject(message);
		OnlineGame game = gm.getGameFor(session);
		
		if(json.containsKey("type")) {
			switch(json.getString("type").toLowerCase()) {
				case "lobbies":
					sendLobbies(session);
					break;
				case "createlobby":
					String lobbyname = json.getString("name");
					String displayname = json.getString("displayname");
					OnlineGame newgame = gm.createOnlineGame(lobbyname);
					Player creator = new Player(newgame, displayname, session);
					newgame.setCreator(creator);
					break;
				case "changelobbyname":
					if(game != null) {
						String name = json.getString("name");
						game.setName(name);
					}
					break;
				case "startgame":
					if(game != null) {
						if(game.getCreator().equals(session)) {
							game.start();
						}
					}
					break;
				case "setcardset":
					if(game != null) {
						if(game.getCreator().equals(session)) {
							game.getConfig().setCardset(json.getString("cardset"));
						}
					}
					break;
				default:
					send(session, gm.getInvalid("Type not found: " + json.getString("type")).toString());
			}
		} else {
			send(session, gm.getInvalid("Requests require a type").toString());
		}
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {
    	System.out.println(session.getId() + " disconnected");
    	getGameServer().getGameManager().removeConnection(session);
    }
    
    public void sendLobbies(Session session) {
		send(session, new JSONObject()
				.accumulate("type", "lobbies")
				.accumulate("lobbies", getGameServer().getGameManager().getLobbies()).toString());
    }
	
	public void send(Session session, String message) {
		try {
			session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
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