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
	public void testEndConditionNoProvinces() {
		Card province = gameServer.getCardManager().get("province");
		for(int players = 2; players <= 4; players++) {
			int playerCount = game.getPlayers().size();
			int amountOfCards = playerCount == 2 ? 8 : 12;
			for(int i = 0; i < amountOfCards; i++){
				int desiredSupplyAmount = amountOfCards - (i + 1);
				board.getSupply(SupplyType.VICTORY).removeOne(province);
				int amountOfProvinces = board.getSupply(SupplyType.VICTORY).getCards().get(province);
				assertEquals("not the right amount of provinces...", desiredSupplyAmount, amountOfProvinces);
			}
			assertTrue("Game doesn't end when there are " + amountOfCards + " provinces removed", board.hasEndCondition());
			String newPlayer = "player " + players;
			game.addPlayer(newPlayer);
			board.initSupplies();
		}
	}
	
	@Test
	public void testEndConditonThreeEmptyPiles() {
		Card curse = gameServer.getCardManager().get("curse");
		Card estate = gameServer.getCardManager().get("estate");
		Card duchy = gameServer.getCardManager().get("duchy");
		for(int players = 2; players <= 4; players++) {
			int playerCount = game.getPlayers().size();
			int amountOfCards = playerCount == 2 ? 8 : 12;
			for(int i = 0; i < amountOfCards; i++) {
				board.getSupply(SupplyType.CURSE).removeOne(gameServer.getCardManager().getCards().get(curse));
				board.getSupply(SupplyType.VICTORY).removeOne(gameServer.getCardManager().getCards().get(estate));
				board.getSupply(SupplyType.VICTORY).removeOne(gameServer.getCardManager().getCards().get(duchy));
				System.out.println(board.getSupply(SupplyType.CURSE).getCards().get(curse));
			}
			assertTrue("Game doesn't end when there are " + amountOfCards + " removed from 3 piles", board.hasEndCondition());
		}
	}
}
