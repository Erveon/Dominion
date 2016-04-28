package net.ultradev.dominion.tests;


import static org.junit.Assert.fail;
import org.junit.Test;
import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.local.LocalGame;


public class LocalGameTest {

	private GameServer gs;
	private LocalGame lg;
	
	@Test
	public void setupGame() {
		gs = new GameServer();
		lg = new LocalGame(gs);
		lg.addPlayer("Ruben");
		lg.addPlayer("Tim");
	}
	
	@Test
	public void startGame() {
		lg.init();
		lg.start();
	}

}
