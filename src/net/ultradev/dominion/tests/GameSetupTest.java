package net.ultradev.dominion.tests;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.Board;
import net.ultradev.dominion.game.Game;
import net.ultradev.dominion.game.GameConfig;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.local.LocalGame;
import net.ultradev.dominion.game.card.CardManager;


public class GameSetupTest {
	
	private int playerAmount;
	private GameServer gs = new GameServer();
	private Game g1 = new LocalGame(gs);
	private Game g2 = new LocalGame(gs);
	private Board b = new Board(g1);
	private Board b2 = new Board(g2);
	private GameConfig gc = new GameConfig(g1);
	private GameConfig gc2 = new GameConfig(g2);
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
		b.addActionCard(b.getGame().getGameServer().getCardManager().get("chapel"));
		int chapelCount = b.actionsupply.get(b.getGame().getGameServer().getCardManager().get("chapel"));
		if(!(chapelCount == 10)) {
			fail("testAmountOfActionCards failed:\n");
		}
		//assertEquals("testAmountOfActionCards failed",10,chapelCount);
	}
	
	public void testAmountOfGardenCards(int playerCount) {
		if(databaseLive) {
			Card gardens = b.getGame().getGameServer().getCardManager().get("gardens");
			b.addActionCard(gardens);
			int gardensCount = b.actionsupply.get(gardens);
			if(!((playerCount == 2 && gardensCount == 8) || gardensCount == 12)) {
				fail("Actual error for\ntestAmountOfGardenCards failed:\nPlayer count: " + playerCount + " and amount of cards: " + gardensCount);
			}
		}
	}
	
	public void testHandleAddActionCards() { //TODO FIX BUG
		List<String> desiredResult = new ArrayList<>();
		for(int i = 1; i <= 10; i++) {
			String val = "card" + Integer.toString(i);
			Card testCard = new Card(val, "Test stuff", 2);
			 //access cardManager, then execute getCards.put(val,testCard);
			b.
			gc.handle("addCard", val);
			desiredResult.add(val);
		}
		List<String> actionCards = gc.getActionCards();
		if(!(actionCards.equals(desiredResult))) {
			fail("testHandleAddActionCards failed:\nAdded cards:   " + actionCards + "\nDesired result: " + desiredResult);
		}
		/*Card chapel = new Card("chapel", "Trash up to 4 cards from your hand.", 2);
		getCards().put("chapel", chapel);*/
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
		int coppers = b.treasuresupply.get(b.getGame().getGameServer().getCardManager().get("copper"));
		int curses = b.cursesupply.get(b.getGame().getGameServer().getCardManager().get("curse"));
		if(!(coppers == desiredCoppers)) {
			fail("testAddTreasures failed:\n" + coppers + " instead of " + desiredCoppers + "coppers\n" + curses + " instead of " + desiredCurses);
		}
	}
	
	public void testAddVictory(int playerCount) {
		b2.initSupplies(playerCount);
		String[] vicType = new String[]{"estate","duchy","province"};
		for(String type : vicType) {
			int amount = b2.victorysupply.get(b2.getGame().getGameServer().getCardManager().get(type));
			if(!( (amount == 8 && playerCount == 2) || (amount == 12 && playerCount > 2) )) {
				fail("testAddVictory failed:\ntype: " + type + " amount: " + amount + " players: " + playerCount);
			}
		}
	}
}












