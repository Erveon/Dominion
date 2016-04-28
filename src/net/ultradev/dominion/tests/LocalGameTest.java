package net.ultradev.dominion.tests;


import static org.junit.Assert.fail;
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
		String notDesired =  "{\"response\":\"OK\",\"result\":\"DONE\"}";
		if(response.toString().equals(notDesired) ){
			//TODO fail("\nYou shouldn't be able to play coppers in the action phase, now should you?");
			System.err.println("you failed!");
		}
	}
	
	@Test		//TODO should be changed when lg.endPhase() happens automatically
	public void testPlayThreeCopper() {
		testPlayTreasureInActionPhase();
		lg.endPhase();
		JSONObject answer = null;
		String desired = "{\"response\":\"OK\",\"result\":\"DONE\"}";
		for(int i = 0; i < 3; i++)
		{
			answer = lg.getTurn().playCard("copper");			
		}
		if(!(answer.toString().equals(desired))) {
			fail("\nresponse was: " + answer + " and should have been " + desired);
		}
	}
	
	@Test
	public void testBuyACard() {
		testPlayThreeCopper();
		lg.getTurn().buyCard("village");
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
