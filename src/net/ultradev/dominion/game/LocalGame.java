package net.ultradev.dominion.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.player.Player;
import net.ultradev.dominion.game.utils.Utils;

public class LocalGame {
	
	//AJAX CALLS
		// Create game > ?action=create&type=local
		// Check if a game is running > ?action=info&type=local
		// Set config > ?action=setconfig&type=local&key=players&value=4
	
	public String[] RandomNames = {
			"Albert", "Fernando", "Pedro", "Alfred", "Mohammed", "Freddy", "André", "Maria", "Annemie", 
			"Lieselot", "Brecht", "Lara", "Dirk", "Jill", "Ethan", "Spongebob", "Sandy", "Sarah",
			"Lisa", "Gertrude", "Homer", "Bart", "Marge", "Maggie", "Ahmed", "Nick", "Ruben", "Gilles", "Tim",
			"Gengis Khan", "Joseph", "Adolf", "Joe", "Patrick", "KillerRobot89", "Marshmallow", "Koekje",
			"BlueJ", "Bernard", "Kristien", "Corneel", "Nemo", "Enum", "Bob de Bouwer", "Gert", "Samson"
	};
	
	public static Map<HttpSession, LocalGame> games = new HashMap<>();
	
	private GameConfig config;
	private List<Player> players;
	
	public LocalGame() {
		this.config = new GameConfig();
		this.players = new ArrayList<>();
		Utils.debug("A local game has been made");
	}
	
	//We're also returning the player because we'll be feeding the profile back to the front-end
	public Player addPlayer() {
		String randName = RandomNames[new Random().nextInt(RandomNames.length)];
		Player p = new Player(randName);
		players.add(p);
		return p;
	}
	
	public void removePlayerById(int id) {
		if(players.size() > id)
			players.remove(id);
	}
	
	public Player getPlayerById(int id) {
		if(players.size() > id)
			return players.get(id);
		return null;
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
	
	public static void createGame(HttpSession session) {
		games.put(session, new LocalGame());
	}

}
