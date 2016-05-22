package net.ultradev.dominion.game.local;

import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.Game;

public class LocalGame extends Game {
	
	/**
	 * Local game specific methods come here
	 * @param gs
	 */
	public LocalGame(GameServer gs) {
		super(gs);
		getGameServer().getUtils().debug("A local game has been made");
	}

}
