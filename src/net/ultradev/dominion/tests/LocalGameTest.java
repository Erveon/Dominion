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
	public void testPlayTreasureInActionPhase() {
		setupGame();
		JSONObject response = lg.getTurn().playCard("copper");
		String Desired =  "{\"response\":\"invalid\",\"reason\":\"Unable to perform action. (Not in the right phase (ACTION) or card 'copper' is invalid)\"}";
		assertEquals("played a copper in action phase", Desired, response.toString());
	}
	
	@Test		//TODO should be changed when lg.endPhase() happens automatically
	public void testPlayTwoCopper() {
		testPlayTreasureInActionPhase();
		lg.endPhase();
		JSONObject answer = null;
		String desired = "{\"response\":\"OK\",\"result\":\"DONE\"}";
		for(int i = 0; i < 2; i++)
		{
			answer = lg.getTurn().playCard("copper");
			System.out.println("function: testPlayTwoCopper\nresponse: " + answer.toString() + "\namount already completed: " + i + "\nTurn: " + lg.getTurn().getPhase() + "\n\n");
		}
		if(!(answer.toString().equals(desired))) {
			fail("\nresponse was: " + answer + " and should have been " + desired);
		}
	}
	
	@Test
	public void testBuyACard() {
		testPlayTwoCopper();
		lg.getTurn().buyCard("chapel");
		//phase should end automatically => phase doesn't end, but you can't do anything except ending
		lg.getTurn().end();
		//TODO end turn?
	}
	
	@Test
	public void testNextTurn() {
		testBuyACard();
		//TODO get next player
		Player nextPlayer = lg.getTurn().getNext();
		//TODO initiate his turn
		Turn turn = new Turn(lg, nextPlayer);
		lg.setTurn(turn);
		testBuyACard();
	}

}
