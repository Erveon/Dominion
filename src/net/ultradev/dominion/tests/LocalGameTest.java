package net.ultradev.dominion.tests;


import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.json.JSONObject;
import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.Game;
import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.local.LocalGame;
import net.ultradev.dominion.game.player.Player;


public class LocalGameTest {

	private GameServer gs = new GameServer();
	private Game lg = new LocalGame(gs);
	
	@Test
	public void setupGame() {
		lg.addPlayer("Ruben");
		lg.addPlayer("Tim");
		lg.start();
	}
	
	@Test
	public void testPlayTreasureInActionPhase () {
		setupGame();
		JSONObject response = lg.getTurn().playCard("copper");
		String desired =  "{\"response\":\"invalid\",\"reason\":\"Unable to perform action. (Not in the right phase (ACTION) or card 'copper' is invalid)\"}";
		assertEquals("played a copper in action phase", desired, response.toString());
	}
	
	@Test		//TODO should be changed when lg.endPhase() happens automatically
	public void testPlayTwoCopper () {
		testPlayTreasureInActionPhase();
		lg.endPhase();
		JSONObject answer = null;
		String desired = "{\"response\":\"OK\",\"result\":\"DONE\"}";
		for(int i = 0; i < 2; i++)
		{
			answer = lg.getTurn().playCard("copper");
		}
		assertEquals("couldn't play two copper", desired, answer.toString());
	}
	
	@Test
	public void testBuyChapel () {
		testPlayTwoCopper();
		JSONObject answer = lg.getTurn().buyCard("chapel");
		String desired = "{\"response\":\"OK\",\"result\":\"BOUGHT\"}";
		assertEquals("Couldn't buy chapel (cost 2)",desired , answer.toString());
		// TODO phase should end automatically => phase doesn't end, but you can't do anything except ending
		lg.getTurn().end();
	}
	
	@Test
	public void testBuyTooExpensiveCard () {
		testPlayTwoCopper();
		JSONObject answer = lg.getTurn().buyCard("market");
		String desired = "{\"response\":\"OK\",\"result\":\"CANTAFFORD\"}";
		assertEquals("No \"too expensive\" error", desired, answer.toString());
	}
	
	@Test
	public void testNextTurn () {
		testBuyChapel();
		Player nextPlayer = lg.getTurn().getNextPlayer();
		Turn turn = new Turn(lg, nextPlayer);
		lg.setTurn(turn);
		testBuyChapel();
	}

}
