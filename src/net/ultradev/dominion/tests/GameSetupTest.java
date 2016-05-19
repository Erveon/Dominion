package net.ultradev.dominion.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.Board;
import net.ultradev.dominion.game.Board.SupplyType;
import net.ultradev.dominion.game.Game;
import net.ultradev.dominion.game.GameConfig;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.CardManager;
import net.ultradev.dominion.game.local.LocalGame;

public class GameSetupTest {
	
	private GameServer gameServer;
	private Game game;
	private Board board;
	private GameConfig config;
	
	private CardManager cardManager;
	
	@Before
	public void init() {
		gameServer = new GameServer();
		cardManager = gameServer.getCardManager();
		game = new LocalGame(gameServer);
		board = game.getBoard();
		config = game.getConfig();
		
		game.addPlayer("Bob");
		game.addPlayer("Jos");
	}
	
	@Test
	public void testAmountOfActionCards() {
		board.addActionCard(cardManager.get("chapel"));
		int chapelCount = board.getSupply(SupplyType.ACTION).getCards().get(cardManager.get("chapel"));
		assertEquals("Chapel does not have the desired amount on the board", 10, chapelCount);
	}
	
	@Test
	public void testGardenCards() {
		Card gardens = cardManager.get("gardens");
		board.addActionCard(gardens);
		for(int speler = 2; speler <= 4; speler++) {
			int gardensCount = board.getSupply(SupplyType.ACTION).getCards().get(gardens);
			int amount = game.getPlayers().size() == 2 ? 8 : 12;
			assertEquals("Garden count is not correct", amount, gardensCount);
			game.addPlayer("Player " + speler);
			board.addActionCard(gardens);
		}
	}
	
	@Test
	public void testCardset() {
		config.setCardset("test");
		assertTrue("Cardset doesn't have the correct cards", config.getActionCards().contains("militia"));
	}
	
	@Test
	public void testAddTreasures() {
		int playerCount = game.getPlayers().size();
		board.initSupplies();
		int desiredCoppers = 60 - (7 * playerCount);
		int coppers = board.getSupply(SupplyType.TREASURE).getCards().get(board.getGame().getGameServer().getCardManager().get("copper"));
		assertEquals("Does not have the correct amount of coppers", desiredCoppers, coppers);
	}
	
	@Test
	public void testVictorySupply() {
		int playerCount = game.getPlayers().size();
		board.initSupplies();
		String[] vicType = new String[]{"estate", "duchy", "province"};
		for(String type : vicType) {
			int amount = board.getSupply(SupplyType.VICTORY).getCards().get(board.getGame().getGameServer().getCardManager().get(type));
			int desired = playerCount == 2 ? 8 : 12;
			assertEquals("Does not have the correct amount of victory cards", desired, amount);
		}
	}
	
	@Test
	public void testCurseSupply() {
		int desired = 10 * (game.getPlayers().size() - 1);
		board.initSupplies();
		int curseCount = board.getSupply(SupplyType.CURSE).getCards().get(board.getGame().getGameServer().getCardManager().get("curse"));
		assertEquals("Does not have the correct amount of curse cards", desired, curseCount);
	}
	
}