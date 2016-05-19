package net.ultradev.dominion.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.sf.json.JSONObject;
import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.Game;
import net.ultradev.dominion.game.local.LocalGame;

public class EngineTest {

	private GameServer gameServer;
	private Game game;
	
	@Before
	public void init() {
		gameServer = new GameServer();
		game = new LocalGame(gameServer);
		game.addPlayer("Bob");
		game.addPlayer("Jos");
		game.start();
	}
	
	@Test
	public void testPlayTreasureInActionPhase () {
		JSONObject response = game.getTurn().playCard("copper");
		assertTrue("Played treasure in action phase", response.toString().contains("invalid"));
	}

}
