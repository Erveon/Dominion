package net.ultradev.dominion.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	private List<OnlineGame> onlineGames;
	private Map<Session, OnlineGame> inGames;
	
	public GameManager(GameServer gs) {
		this.localGames = new HashMap<>();
		this.onlineGames = new ArrayList<>();
		this.inGames = new HashMap<>();
		this.gs = gs;
	}
	
	public GameServer getGameServer() {
		return gs;
	}
	
	public List<OnlineGame> getOnlineGames() {
		return onlineGames;
	}
	
	public OnlineGame getOnlineGame(Session session) {
		if(inGames.containsKey(session)) {
			return inGames.get(session);
		}
		return null;
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
	
	public JSONObject handleOnlineRequest(Map<String, String> map, Session session) {
		return new JSONObject().accumulate("response", "OK");
	}
	
	public JSONObject getInvalid(String reason) {
		return new JSONObject()
				.accumulate("response", "invalid")
				.accumulate("reason", reason);
	}
	
}
