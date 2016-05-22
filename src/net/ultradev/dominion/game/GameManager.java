package net.ultradev.dominion.game;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

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
		if(games.containsKey(session)) {
			return games.get(session);
		}
		return null;
	}
	
	// If null, it's a java GUI game
	public LocalGame createLocalGame(HttpSession session) {
		LocalGame game = new LocalGame(getGameServer());
		games.put(session, game);
		return game;
	}
	
	public void destroyFor(HttpSession session) {
		if(games.containsKey(session)) {
			games.remove(session);
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
				Stream.of(players).forEach(game::addPlayer);
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
	
	public JSONObject getInvalid(String reason) {
		return new JSONObject()
				.accumulate("response", "invalid")
				.accumulate("reason", reason);
	}
	
}
