package net.ultradev.dominion.game.local;

import java.util.ArrayList;
import java.util.List;

import javax.websocket.Session;

import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.Game;
import net.ultradev.dominion.game.player.Player;

public class LocalGame extends Game {
	
	private List<Player> players;
	
	/**
	 * @param gs
	 */
	public LocalGame(GameServer gs) {
		super(gs);
		this.players = new ArrayList<>();
		getGameServer().getUtils().debug("A local game has been made");
	}

	@Override
	public boolean isOnline() {
		return false;
	}

	@Override
	public List<Player> getPlayers() {
		return players;
	}
	
	@Override
	public void addPlayer(String name, Session ignored) {
		getPlayers().add(new Player(this, getValidNameFor(name)));
		getGameServer().getUtils().debug("A player named " + name + " has been added to the game");
	}

}
