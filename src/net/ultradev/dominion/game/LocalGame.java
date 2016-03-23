package net.ultradev.dominion.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.player.Player;
import net.ultradev.dominion.game.utils.Utils;

public class LocalGame {
	
	public static Map<HttpSession, LocalGame> games = new HashMap<>();
	
	private GameConfig config;
	private List<Player> players;
	private Board board = null;
	
	private List<Card> trash;
	
	public LocalGame() {
		this.config = new GameConfig();
		this.players = new ArrayList<>();
		this.trash = new ArrayList<>();
		this.board = new Board();
		Utils.debug("A local game has been made");
	}

	/**
	 * Start the game. Done with an ajax call after all the settings have been configured.
	 * Player null means a random player starts
	 */
	public void start() {
		start(getPlayers());
	}
	
	/**
	 * Loser of the previous game starts the next one
	 * Player(s) to start is an array because a tie is possible
	 * @param p Eligible to start
	 */
	public void start(List<Player> p) {
		init();
		Player start = p.get(new Random().nextInt(p.size()));
		//TODO start game with the 'start' player
	}
	
	/**
	 * Set variables when the game has been configured
	 */
	public void init() {
		getBoard().initSupplies(getPlayers().size());
		for(Player p : getPlayers())
			p.setup();
	}
	
	public Board getBoard() {
		return board;
	}
	
	//We're also returning the player because we'll be feeding the profile back to the front-end
	//It would be somewhat counter productive to split this particular case up
	public void addPlayer(String name) {
		Player p = new Player(name);
		getPlayers().add(p);
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
				.accumulate("players", getPlayersAsJson())
				.accumulate("board", getBoard().getAsJson());
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
