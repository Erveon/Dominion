package net.ultradev.dominion.game.local;

import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.Game;

public class LocalGame extends Game {
	
	public LocalGame(GameServer gs) {
		super(gs);
		getGameServer().getUtils().debug("A local game has been made");
	}

}
