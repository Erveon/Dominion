package net.ultradev.dominion.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.player.Player;
import net.ultradev.dominion.game.utils.Utils;

public class LocalGame {
	
	public static Map<HttpSession, LocalGame> games = new HashMap<>();
	
	private GameConfig config;
	private List<Player> players;
	
	private List<Card> trash;
	
	public LocalGame() {
		this.config = new GameConfig();
		this.players = new ArrayList<>();
		this.trash = new ArrayList<>();
		Utils.debug("A local game has been made");
	}
	
	//We're also returning the player because we'll be feeding the profile back to the front-end
	//It would be somewhat counter productive to split this particular case up
	public void addPlayer(String name) {
		Player p = new Player(name);
		getPlayers().add(p);
	}
	
	public void removePlayer(Player p) {
		players.remove(p);
	}
	
	public Player getPlayerByName(String name) {
		for(Player p : getPlayers()) {
			if(p.getDisplayname().equalsIgnoreCase(name))
				return p;
		}
		return null;
	}
	
	public List<Player> getPlayers() {
		return players;
	}
	
	public static void destroyFor(HttpSession session) {
		//TODO create an ajax call to destroy the game
		if(!games.containsKey(session))
			return;
		games.remove(session);
		System.gc();
		Utils.debug("A local game has been destroyed");
	}
	
	public GameConfig getConfig() {
		return config;
	}
	
	public List<JSONObject> getPlayersAsJson() {
		List<JSONObject> objs = new ArrayList<>();
		for(Player p : players) 
			objs.add(p.getAsJson());
		return objs;
	}
	
	public JSONObject getAsJson() {
		return new JSONObject()
				.accumulate("config", getConfig().getAsJson())
				.accumulate("players", getPlayersAsJson());
	}
	
	public static LocalGame getGame(HttpSession session) {
		if(games.containsKey(session))
			return games.get(session);
		return null;
	}
	
	// If null, it's a java front-end game
	public static void createGame(HttpSession session) {
		games.put(session, new LocalGame());
	}
	
	public List<Card> getTrash() {
		return trash;
	}

}
