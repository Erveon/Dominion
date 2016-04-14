package net.ultradev.dominion.game;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;
import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.local.LocalGame;

public class GameManager {
	
	GameServer gs;
	private Map<HttpSession, LocalGame> games = new HashMap<>();
	
	public GameManager(GameServer gs) {
		this.gs = gs;
	}
	
	public GameServer getGameServer() {
		return gs;
	}
	
	public LocalGame getGame(HttpSession session) {
		if(games.containsKey(session))
			return games.get(session);
		return null;
	}
	
	// If null, it's a java front-end game
	public LocalGame createGame(HttpSession session) {
		LocalGame game = new LocalGame(getGameServer());
		games.put(session, game);
		return game;
	}
	
	public void destroyFor(HttpSession session) {
		if(!games.containsKey(session))
			return;
		games.remove(session);
		System.gc(); // Free the memory!!
		getGameServer().getUtils().debug("A local game has been destroyed");
	}
	
	public JSONObject handleLocalRequest(Map<String, String> map) {
		return handleLocalRequest(map, null, null);
	}
	
	/**
	 * Java Front-End support
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
				|| action.equals("selectcard")) {
			if(g == null)
				return getInvalid("No game running");
		}
		
		switch(action) {
			case "create":
				createGame(session);
				return response.accumulate("response", "OK");
			case "destroy":
				destroyFor(session);
				return response.accumulate("response", "OK");
			case "start":
				if(g.getPlayers().size() < 2)
					return getInvalid("You need at least 2 players to start a game");
				g.start();
				return response.accumulate("response", "OK");
			case "info":
				return response
						.accumulate("response", "OK")
						.accumulate("game", g.getAsJson());
			case "setconfig":
				if(!map.containsKey("key") || !map.containsKey("value"))
					return getInvalid("No key & value pair given for config");
				String key = map.get("key");
				if(g.getConfig().handle(key, map.get("value")))
					return response.accumulate("response", "OK");
				else
					return getInvalid("Invalid key in setconfig: " + key);
			case "addplayer":
				if(!map.containsKey("name")) {
					return getInvalid("Need a name to add the player");
				}
				String name = map.get("name");
				g.addPlayer(name);
				return response.accumulate("response", "OK");
			case "endphase":
				g.endPhase();
				return response.accumulate("response", "OK");
			case "playcard":
				if(!map.containsKey("card"))
					return getInvalid("Card parameter doesn't exist");
				return g.getTurn().playCard(map.get("card"));
			case "buycard":
				if(!map.containsKey("card"))
					return getInvalid("Card parameter doesn't exist");
				return g.getTurn().buyCard(map.get("card"));
			case "selectcard":
				if(!map.containsKey("card"))
					return getInvalid("Card parameter doesn't exist");
				if(g.getTurn().getSubTurn() != null)
					return g.getTurn().getSubTurn().selectCard(map.get("card"));
				return getInvalid("Cannot select card, not handled in the game's current state");
			default:
				return getInvalid("Action not recognized: " + action);
		}
	}
	
	public JSONObject getInvalid(String reason) {
		return new JSONObject()
				.accumulate("response", "invalid")
				.accumulate("reason", reason);
	}
	
}
