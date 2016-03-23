package net.ultradev.dominion.game.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Board;
import net.ultradev.dominion.game.GameConfig;
import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.player.Player;
import net.ultradev.dominion.game.utils.Utils;

public class LocalGame {
	
	public static Map<HttpSession, LocalGame> games = new HashMap<>();
	
	private GameConfig config;
	private List<Player> players;
	private Board board = null;
	
	private List<Card> trash;
	private Turn turn;
	
	//In case there is a tie, this will determine who wins (least amount of turns)
	private Player started;
	
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
		Player starter = p.get(new Random().nextInt(p.size()));
		this.started = starter;
		setTurn(new Turn(this, starter));
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
	
	public Turn getTurn() {
		return turn;
	}
	
	public void endTurn() {
		getTurn().getPlayer().increaseRounds();
		if(getBoard().hasEndCondition())
			endGame();
		else {
			getTurn().end();
			setTurn(getTurn().getNextTurn());
		}
	}
	
	public void setTurn(Turn turn) {
		this.turn = turn;
	}
	
	public Player getWhoStarted() {
		return this.started;
	}
	
	public void endGame() {
		List<Player> winnerlist = getWinningPlayer();
		if(winnerlist.size() > 1) { // We've got a tie on our hands
			//TODO tie
		} else {
			//Player winner = winnerlist.get(0);
			//TODO win!
		}
	}
	
	public List<Player> getWinningPlayer() {
		List<Player> winnerList = new ArrayList<>();
		Player winner = getPlayers().get(0);
		for(Player p : getPlayers()) {
			if(p.equals(winner))
				continue;
			int pVic = p.getVictoryPoints();
			int wVic = winner.getVictoryPoints();
			if(pVic > wVic)
				winner = p;
			else if(pVic == wVic) {
				if(p.getRounds() == winner.getRounds()) { // Tie, multiple winners
					if(!winnerList.contains(winner))
						winnerList.add(winner);
					if(!winnerList.contains(p))
						winnerList.add(p);
					winner = p; // Just in case it's a fucking threeway tie..
				} else { // p is set to winner because they have played less rounds
					if(p.getRounds() < winner.getRounds())
						p = winner;
				}
			}
		}
		return winnerList;
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
				.accumulate("board", getBoard().getAsJson())
				.accumulate("turn", getTurn() == null ? "null" : getTurn().getAsJson());
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
