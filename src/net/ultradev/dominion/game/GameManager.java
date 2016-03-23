package net.ultradev.dominion.game;

import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

public class GameManager {

	public static JSONObject handleLocalRequest(Map<String, String> map) {
		return handleLocalRequest(map, null, null);
	}
	
	/**
	 * Java Front-End support
	 * @param map Parameters
	 * @param g Game, may be null
	 * @return Response
	 */
	public static JSONObject handleLocalRequest(Map<String, String> map, LocalGame g) {
		return handleLocalRequest(map, g, null);
	}
	
	public static JSONObject handleLocalRequest(Map<String, String> map, LocalGame g, HttpSession session) {
		JSONObject response = new JSONObject();
		String action = map.get("action").toLowerCase();
		
		// Parameters that need a game to be running
		if(action.equals("info") || action.equals("setconfig") || action.equals("addplayer") || action.equals("removeplayer") || action.equals("start")) {
			if(g == null)
				return response
						.accumulate("response", "invalid")
						.accumulate("reason", "No game running");
		}
		
		switch(action) {
			case "create":
				LocalGame.createGame(session);
				return response.accumulate("response", "OK");
			case "destroy":
				LocalGame.destroyFor(session);
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
			default:
				return getInvalid("Action not recognized: " + action);
		}
	}
	
	public static JSONObject getInvalid(String reason) {
		return new JSONObject()
				.accumulate("response", "invalid")
				.accumulate("reason", reason);
	}
	
}
