package net.ultradev.dominion.game;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.ultradev.dominion.game.utils.Utils;

public class LocalGame {
	
	//AJAX CALLS
		// Create game > ?action=create&type=local
		// Check if a game is running > ?action=info&type=local
		// Set config > ?action=setconfig&type=local&key=players&value=4
	
	//TODO voor AJAX
		// > Set config properties
	
	public static Map<HttpSession, LocalGame> games = new HashMap<>();
	
	private GameConfig config;
	
	public LocalGame() {
		this.config = new GameConfig();
		Utils.debug("A local game has been made");
	}
	
	public void destroy() {
		Utils.debug("A local game has been destroyed");
	}
	
	public GameConfig getConfig() {
		return config;
	}
	
	public static LocalGame getGame(HttpSession session) {
		if(games.containsKey(session))
			return games.get(session);
		return null;
	}
	
	public static void createGame(HttpSession session) {
		games.put(session, new LocalGame());
	}

}
