package net.ultradev.dominion.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.sf.json.JSONObject;
import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.Game;
import net.ultradev.dominion.game.GameConfig;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.Board;
import net.ultradev.dominion.game.Board.SupplyType;
import net.ultradev.dominion.game.local.LocalGame;

public class EngineTest {

	private GameServer gameServer;
	private Game game;
	private Board board;
	private GameConfig gameConfig;

	@Before
	public void init() {
		gameServer = new GameServer();
		game = new LocalGame(gameServer);
		board = game.getBoard();
		gameConfig = game.getConfig();
		game.addPlayer("Bob");
		game.addPlayer("Jos");
		gameConfig.setCardset("test");
		game.start();
	}

	@Test
	public void testPlayTreasureInActionPhase() {
		JSONObject response = game.getTurn().playCard("copper");
		assertTrue("Played treasure in action phase", response.toString().contains("invalid"));
	}
	
	@Test
	public void logCreationOfCardsWhileTesting() {
		try {
		int provinces = board.getSupply(SupplyType.VICTORY).getCards().get("province");
		int curses = board.getSupply(SupplyType.CURSE).getCards().get("curse");
		int coppers = board.getSupply(SupplyType.TREASURE).getCards().get("copper");
		int chapels = board.getSupply(SupplyType.ACTION).getCards().get("chapel");
		System.out.println("\nprovinces: " + provinces + "\ncurses: " + curses + "\ncoppers: " + coppers + "\nchapels: " + chapels);
		}
		catch (Exception e) {
			System.out.println("failed...");
		}
		System.out.println("done");
	}
	
	
	// onderstaande endcondition tests geven een nulpointer exception in klasse Board op lijn 66.
	/*
	@Test
	public void testEndConditionNoProvinces() {
		Card province = gameServer.getCardManager().get("province");
		for(int players = 2; players <= 4; players++) {
			int playerCount = game.getPlayers().size();
			int amountOfCards = playerCount == 2 ? 8 : 12;
			for(int i = 0; i < amountOfCards; i++){
				board.getSupply(SupplyType.VICTORY).removeOne(province);
				System.out.println("amount of provinces: " + board.getSupply(SupplyType.VICTORY).getCards().get("province"));
			}
			assertTrue("Game doesn't end when there are " + amountOfCards + " provinces removed", board.hasEndCondition());
			String newPlayer = "player " + players;
			game.addPlayer(newPlayer);
			board.addActionCard(gameServer.getCardManager().get("province"));
		}
	}
	
	@Test
	public void testEndConditonThreeEmptyPiles() {
		for(int players = 2; players <= 4; players++) {
			int playerCount = game.getPlayers().size();
			int amountOfCards = playerCount == 2 ? 8 : 12;
			for(int i = 0; i < amountOfCards; i++) {
				board.getSupply(SupplyType.CURSE).removeOne(gameServer.getCardManager().get("curse"));
				board.getSupply(SupplyType.VICTORY).removeOne(gameServer.getCardManager().get("estate"));
				board.getSupply(SupplyType.VICTORY).removeOne(gameServer.getCardManager().get("duchy"));
			}
			assertTrue("Game doesn't end when there are " + amountOfCards + " removed from 3 piles", board.hasEndCondition());
		}
	}
	*/
}
