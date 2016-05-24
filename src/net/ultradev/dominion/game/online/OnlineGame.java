package net.ultradev.dominion.game.online;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.websocket.Session;

import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.Game;
import net.ultradev.dominion.game.player.Player;

public class OnlineGame extends Game {

	private Map<Session, Player> players;
	
	public OnlineGame(GameServer gs) {
		super(gs);
	}

	@Override
	public boolean isOnline() {
		return true;
	}

	/**
	 * Converts the player values to an ArrayList from a collection
	 */
	@Override
	public List<Player> getPlayers() {
		return new ArrayList<>(players.values());
	}
	
	@Override
	public void addPlayer(String name, Session session) {
		players.put(session, new Player(this, getValidNameFor(name)));
		getGameServer().getUtils().debug("A player named " + name + " has been added to an online game");
	}

}
