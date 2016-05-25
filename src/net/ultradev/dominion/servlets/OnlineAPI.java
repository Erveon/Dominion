package net.ultradev.dominion.servlets;

import java.io.IOException;
import java.util.UUID;

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
    	getGameServer().getUtils().debug(session.getId() + " connected");
    	getGameServer().getGameManager().addConnection(session);
    }

	@OnMessage
    public void onMessage(String message, Session session){
		GameManager gm = getGameServer().getGameManager();
		if(!isJSONValid(message)) {
			send(session, gm.getInvalid("Object sent is not valid JSON"));
			return;
		}
		
		JSONObject json = JSONObject.fromObject(message);
		OnlineGame game = gm.getGameFor(session);
		
		
		if(json.containsKey("type")) {
			switch(json.getString("type").toLowerCase()) {
				case "lobbies":
					sendLobbies(session);
					break;
				case "chat":
					if(isGame(game, session)) {
						String chatmessage = json.getString("message");
						game.sendChatMessage(session, chatmessage);
					}
					break;
				case "createlobby":
					String lobbyname = json.getString("name");
					String displayname = json.getString("displayname");
					OnlineGame newgame = gm.createOnlineGame(lobbyname);
					Player creator = new Player(newgame, displayname, session);
					newgame.setCreator(creator);
					newgame.getConfig().setCardset("FIRSTGAME");
					newgame.updateGameInfo();
					break;
				case "changelobbyname":
					if(isGame(game, session)) {
						String name = json.getString("name");
						game.setName(name, true);
					}
					break;
				case "joinlobby":
					UUID uuid = UUID.fromString(json.getString("id"));
					String joinname = json.getString("name");
					OnlineGame tojoin = getGameServer().getGameManager().getOnlineGame(uuid);
					if(!tojoin.hasStarted()) {
						tojoin.addPlayer(joinname, session);
					}
					break;
				case "leavelobby":
					if(isGame(game, session)) {
						game.leave(session);
					}
					break;
				case "startgame":
					if(isGame(game, session)) {
						if(game.getCreator().equals(session)) {
							if(game.getPlayers().size() >= 2) {
								game.start();
							}
						}
					}
					break;
				case "setcardset":
					if(isGame(game, session)) {
						if(game.getCreator().equals(session) && !game.hasStarted()) {
							game.getConfig().setCardset(json.getString("cardset"));
							game.updateGameInfo();
						}
					}
					break;
				case "info":
					if(isGame(game, session)) {
						send(session, game.getAsJson());
					}
					break;
				case "play":
					if(isGame(game, session)) {
						play(session, game, json);
					}
					break;
				case "destroy":
					if(isGame(game, session)) {
						if(game.getCreator().equals(session)) {
							game.end();
						}
					}
					break;
				default:
					send(session, gm.getInvalid("Type not found: " + json.getString("type")));
			}
		} else {
			send(session, gm.getInvalid("Requests require a type"));
		}
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {
    	getGameServer().getUtils().debug(session.getId() + " disconnected");
    	getGameServer().getGameManager().removeConnection(session);
    }
    
    public boolean isGame(OnlineGame game, Session session) {
    	if(game == null) {
			getGameServer().getUtils().debug("Request without a game for session " + session.getId());
			return false;
    	}
    	return true;
    }
    
    public void play(Session session, OnlineGame game, JSONObject json) {
    	JSONObject response = null;
    	switch(json.getJSONObject("request").getString("action").toLowerCase()) {
	    	case "endphase":
	    		response = game.endPhase();
	    		game.broadcast(game.getAsJson());
	    		break;
	    	case "playcard":
	    		if(json.containsKey("card")) {
	    			response = game.getTurn().playCard(json.getString("card"));
	    		}
	    		break;
	    	case "buycard":
	    		if(json.containsKey("card")) {
	    			response = game.getTurn().buyCard(json.getString("card"));
	    		}
	    		break;
	    	case "selectcard":
	    		if(json.containsKey("card")) {
	    			response = game.getTurn().selectCard(json.getString("card"));
	    		}
	    		break;
			case "stopaction":
				response = game.getTurn().stopAction();
				break;
	    	default:
	    		getGameServer().getUtils().debug("Unrecognized action in online game: " + json.getString("action"));
	    		break;
    	}
		if(response != null) {
			response.accumulate("type", "game");
			if(isWorthSending(response)) {
				game.broadcast(response);
			} else {
				send(session, response);
			}
		}
    }
    
    public boolean isWorthSending(JSONObject json) {
    	if(json.containsKey("result")) {
    		if(json.getString("result").equalsIgnoreCase("DONE")) {
    			return false;
    		}
    	}
    	return true;
    }
    
    public void sendLobbies(Session session) {
		send(session, new JSONObject()
				.accumulate("type", "lobbies")
				.accumulate("lobbies", getGameServer().getGameManager().getLobbies()));
    }
	
	public void send(Session session, JSONObject message) {
		try {
			if(session.isOpen()) {
				session.getBasicRemote().sendText(message.toString());
			}
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