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
		String action = map.get("action");
		switch(action) {
			case "create":
				LocalGame.createGame(session);
				return response.accumulate("response", "OK");
			case "info":
				return response
						.accumulate("response", "OK")
						.accumulate("game", g == null ? "null" : g.getAsJson());
			case "setconfig":
				if(!map.containsKey("key") || !map.containsKey("value"))
					return response
							.accumulate("response", "invalid")
							.accumulate("reason", "No key & value pair given for config");
				if(g == null) {
					return response
							.accumulate("response", "invalid")
							.accumulate("reason", "No game running");
				} else {
					g.getConfig().handle(map.get("key"), map.get("value"));
					return response.accumulate("response", "OK");
				}
			default:
				return response
						.accumulate("response", "invalid")
						.accumulate("reason", "Action not recognized: " + action);
		}
	}
	
}
