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
		game.addPlayer("Bob", null);
		game.addPlayer("Jos", null);
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
			game.addPlayer(newPlayer, null);
			board.initSupplies();
		}
	}

	@Test
	public void testEndConditionThreeEmptyPiles() {
		// since we're testing with 2 players, there will be 10 of these cards
		Card curse = gameServer.getCardManager().get("curse");
		Card cellar = gameServer.getCardManager().get("cellar");
		Card moat = gameServer.getCardManager().get("moat");
		for(int i = 0; i < 10; i++) {
			board.getSupply(SupplyType.CURSE).removeOne(curse);
			board.getSupply(SupplyType.ACTION).removeOne(cellar);
			board.getSupply(SupplyType.ACTION).removeOne(moat);
		}
		assertTrue("game doesn't end when there are 3 empty piles", board.hasEndCondition());
	}
}





















