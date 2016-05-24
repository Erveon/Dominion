package net.ultradev.dominion.game;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import net.sf.json.JSONObject;
import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.local.LocalGame;
import net.ultradev.dominion.game.online.OnlineGame;

public class GameManager {
	
	GameServer gs;
	private Map<HttpSession, LocalGame> localGames;
	private Map<UUID, OnlineGame> onlineGames;
	private Map<Session, UUID> connected;
	
	public GameManager(GameServer gs) {
		this.localGames = new HashMap<>();
		this.onlineGames = new HashMap<>();
		this.connected = new HashMap<>();
		this.gs = gs;
		createOnlineGame("This is a test game from the server", null);
	}
	
	public GameServer getGameServer() {
		return gs;
	}
	
	public void addConnection(Session session) {
		connected.put(session, null);
	}
	
	public void removeConnection(Session session) {
		if(connected.containsKey(session)) {
			if(isinGame(session)) {
				//TODO broadcast logout to others
			}
			connected.remove(session);
		}
	}
	
	public void setOnlineGameFor(Session session, OnlineGame game) {
		connected.put(session, game.getUniqueId());
	}
	
	public Map<UUID, OnlineGame> getOnlineGames() {
		return onlineGames;
	}
	
	public void createOnlineGame(String name, Session owner) {
		UUID uuid = UUID.randomUUID();
		while(getOnlineGame(uuid) != null) {
			uuid = UUID.randomUUID();
		}
		OnlineGame game = new OnlineGame(getGameServer(), uuid);
		game.setName(name);
		onlineGames.put(uuid, game);
		String message = new JSONObject()
				.accumulate("type", "addlobby")
				.accumulate("lobby", game.getLobbyInfo()).toString();
		broadcast(message);
	}
	
	public void removeOnlineGame(UUID uuid) {
		if(onlineGames.containsKey(uuid)) {
			String message = new JSONObject()
					.accumulate("type", "dellobby")
					.accumulate("id", uuid.toString()).toString();
			broadcast(message);
			onlineGames.remove(uuid);
		}
	}
	
	public OnlineGame getOnlineGame(UUID uuid) {
		if(onlineGames.containsKey(uuid)) {
			return onlineGames.get(uuid);
		}
		return null;
	}
	
	public boolean isinGame(Session session) {
		return connected.containsKey(session) && connected.get(session) != null;
	}
	
	public OnlineGame getGameFor(Session session) {
		if(connected.containsKey(session)) {
			UUID gameId = connected.get(session);
			if(gameId != null) {
				return onlineGames.get(gameId);
			}
		}
		return null;
	}
	
	public void broadcast(String message) {
		for(Session sess : connected.keySet()) {
			try {
				sess.getBasicRemote().sendText(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public LocalGame getLocalGame(HttpSession session) {
		if(localGames.containsKey(session)) {
			return localGames.get(session);
		}
		return null;
	}
	
	// If null, it's a java GUI game
	public LocalGame createLocalGame(HttpSession session) {
		LocalGame game = new LocalGame(getGameServer());
		localGames.put(session, game);
		return game;
	}
	
	public void destroyFor(HttpSession session) {
		if(localGames.containsKey(session)) {
			localGames.remove(session);
			System.gc(); // Free the memory!!
			getGameServer().getUtils().debug("A local game has been destroyed");
		}
	}
	
	public JSONObject handleLocalRequest(Map<String, String> map) {
		return handleLocalRequest(map, null, null);
	}
	
	/**
	 * Java Front-End support, it simulates a request to the server
	 * @param map Parameters
	 * @param g Game, may be null
	 * @return Response
	 */
	public JSONObject handleLocalRequest(Map<String, String> map, LocalGame g) {
		return handleLocalRequest(map, g, null);
	}
	
	public JSONObject handleLocalRequest(Map<String, String> map, LocalGame g, HttpSession session) {
		JSONObject response = new JSONObject();
		String action = map.get("action").toLowerCase();
		
		// Actions that need a game to be running
		if(action.equals("info") || action.equals("setconfig") || action.equals("addplayer") || action.equals("removeplayer") 
				|| action.equals("start") || action.equals("endturn") || action.equals("buycard") || action.equals("playcard")
				|| action.equals("selectcard")|| action.equals("stopaction")) {
			if(g == null) {
				return getInvalid("No game running");
			}
		}
		
		switch(action) {
			case "setup":
				if(!map.containsKey("cardset") || !map.containsKey("players")) {
					return getInvalid("Requires players & carset");
				}
				String[] players = map.get("players").split("¤");
				if(players.length < 2) {
					return getInvalid("Requires at least 2 players");
				}
				Game game = createLocalGame(session);
				Stream.of(players).forEach(player -> game.addPlayer(player, null));
				String cardset = map.get("cardset");
				game.getConfig().setCardset(cardset);
				game.start();
				response.accumulate("who", game.getTurn().getPlayer().getDisplayname());
				return response.accumulate("response", "OK");
			case "destroy":
				destroyFor(session);
				return response.accumulate("response", "OK");case "info":
				return response
						.accumulate("response", "OK")
						.accumulate("game", g.getAsJson());
			case "endphase":
				return g.endPhase();
			case "playcard":
				if(!map.containsKey("card")) {
					return getInvalid("Card parameter doesn't exist");
				}
				return g.getTurn().playCard(map.get("card"));
			case "buycard":
				if(!map.containsKey("card")) {
					return getInvalid("Card parameter doesn't exist");
				}
				return g.getTurn().buyCard(map.get("card"));
			case "selectcard":
				if(!map.containsKey("card")) {
					return getInvalid("Card parameter doesn't exist");
				}
				return g.getTurn().selectCard(map.get("card"));
			case "stopaction":
				return g.getTurn().stopAction();
			default:
				return getInvalid("Action not recognized: " + action);
		}
	}
	
	public List<JSONObject> getLobbies() {
		return onlineGames.values().stream().map(OnlineGame::getLobbyInfo).collect(Collectors.toList());
	}
	
	public JSONObject handleOnlineRequest(Map<String, String> map, Session session) {
		return new JSONObject().accumulate("response", "OK");
	}
	
	public JSONObject getInvalid(String reason) {
		return new JSONObject()
				.accumulate("response", "invalid")
				.accumulate("reason", reason);
	}
	
}
