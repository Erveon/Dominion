package net.ultradev.dominion.tests;


import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.Board;
import net.ultradev.dominion.game.GameConfig;


public class GameSetupTest {
	
	private int playerAmount;
	private GameServer gs = new GameServer();
	private Board b = new Board(gs);
	private Board b2 = new Board(gs);
	private GameConfig gc = new GameConfig();
	private GameConfig gc2 = new GameConfig();
	private boolean databaseLive = false; //zet op true indien er een DB is, om te testen op kaartspecifieke dingen

	@Test
	public void testOnce() {
		testAmountOfActionCards();
		testHandleAddActionCards();
		testHandleRemoveActionCards();
		//TODO add functions
	}
	
	@Test
	public void testForPlayerAmount() {
		for(int s = 2; s <= 4; s++) {
			playerAmount = s;
			testAmountOfGardenCards(playerAmount);
			testAddTreasures(playerAmount);
			testAddVictory(playerAmount);
			//TODO add functions
		}
	}
	
	public void testAmountOfActionCards() {
		b.addActionCard(b.getGameServer().getCardManager().get("chapel"));
		int chapelCount = b.actionsupply.get(b.getGameServer().getCardManager().get("chapel"));
		if(!(chapelCount == 10)) {
			fail("testAmountOfActionCards failed:\n");
		}
	}
	
	public void testAmountOfGardenCards(int playerCount) {
		if(databaseLive) {
			b.addActionCard(b.getGameServer().getCardManager().get("gardens"));
			int gardensCount = b.actionsupply.get(b.getGameServer().getCardManager().get("gardens"));
			if(!((playerCount == 2 && gardensCount == 8) || gardensCount == 12)) {
				fail("Actual error for\ntestAmountOfGardenCards failed:\nPlayer count: " + playerCount + " and amount of cards: " + gardensCount);
			}
		}
	}
	
	public void testHandleAddActionCards() {
		List<String> desiredResult = new ArrayList<>();
		for(int i = 1; i <= 10; i++) {
			String val = "card" + Integer.toString(i);
			gc.handle("addCard", val);
			desiredResult.add(val);
		}
		List<String> actionCards = gc.getActionCards();
		if(!(actionCards.equals(desiredResult))) {
			fail("testHandleAddActionCards failed:\n   Added  cards: " + actionCards + "\nDesired result: " + desiredResult);
		}
	}
	
	public void testHandleRemoveActionCards() {		
		String add = "addCard";
		String rem = "removeCard";
		String val = "card";
		List<String> desiredResult = new ArrayList<>();
		for(int i = 1; i < 10; i++) {
			val = "card" + Integer.toString(i);
			gc2.handle(add, val);
			desiredResult.add(val);
		}
		gc2.handle(rem, val);
		desiredResult.remove(val);
		List<String> actionCards = gc2.getActionCards();
		if(!(actionCards.equals(desiredResult))) {
			fail("testHandleRemoveActionCards failed:\n   Added cards: " + actionCards + "\nDesired Result: " + desiredResult);
		}
		
	}
	
	/*
	
	@Test			// Remove this, and add to the funcion on top
	public void testAddSameCardTwice() {			// execute once
		String card = "chapel";
		b2.addActionCard(b2.getGameServer().getCardManager().get(card));
		//try {
			b2.addActionCard(b2.getGameServer().getCardManager().get(card));
		//}
		//catch () {
						//TODO this logic has to be added
		//}
		fail("Under construction....");
	}
	
	*/
	
	public void testAddTreasures(int playerCount) {
		b.initSupplies(playerCount);
		int desiredCoppers = 60 - (7*playerCount);
		int desiredCurses = (playerCount * 10) - 10;
		int coppers = b.treasuresupply.get(b.getGameServer().getCardManager().get("copper"));
		int curses = b.cursesupply.get(b.getGameServer().getCardManager().get("curse"));
		if(!(coppers == desiredCoppers)) {
			fail("testAddTreasures failed:\n" + coppers + " instead of " + desiredCoppers + "coppers\n" + curses + " instead of " + desiredCurses);
		}
	}
	
	public void testAddVictory(int playerCount) {
		b2.initSupplies(playerCount);
		String[] vicType = new String[]{"estate","duchy","province"};
		for(String type : vicType) {
			int amount = b2.victorysupply.get(b2.getGameServer().getCardManager().get(type));
			if(!( (amount == 8 && playerCount == 2) || (amount == 12 && playerCount > 2) )) {
				fail("testAddVictory failed:\ntype: " + type + " amount: " + amount + " players: " + playerCount);
			}
		}
	}
}












