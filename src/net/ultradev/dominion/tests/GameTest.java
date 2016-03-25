package net.ultradev.dominion.tests;

import java.util.ArrayList;
import java.util.List;

import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.Board;
import net.ultradev.dominion.game.GameConfig;

public class GameTest {
	
	private GameServer gs;
	private int playerAmount1;
	private Board b1;
	private GameConfig gc;
	private GameConfig gc2;
	private GameConfig gc3;
	private boolean Testing = true;
	
	public GameTest() {
		gs = new GameServer();
		System.out.println("Starting test\n");
		
		//Test alle functies die niet afhankelijk zijn van het aantal spelers
		initTest();
		testAmountOfActionCards();
		testActionCardTypes();
		testHandleRemove();
		
		//Test alle functies die afhankelijk zijn van het aantal spelers
		for(int i = 2;  i <= 4; i++) {
			playerAmount1 = i;
			initLoop();
			testMakeCoppers();
			testAmountEstates();
			testAmountDuchies();
			testAmountProvinces();
			testAmountCurses();
			testHandleAdd();
		}
		System.out.println("\nTest ended");
	}
	
	public GameServer getGameServer() {
		return gs;
	}
	
	public void initTest() {
		makeBoard(playerAmount1);
		makeGameConfig();
		if(Testing){
			System.out.println("Functie in de main toegevoegd?");
		}
	}
	
	public void initLoop() {
		makeBoard(playerAmount1);
	}
		
	public void makeBoard(int players) {
		getGameServer().getCardManager().setup();
		b1 = new Board(getGameServer());
		b1.initSupplies(players);
	}
	
	public void makeGameConfig() {
		gc = new GameConfig();
		gc2 = new GameConfig();
		gc3 = new GameConfig();
	}
	
	
	
	
	
	public void testMakeCoppers() {
		int coppers = b1.treasuresupply.get(getGameServer().getCardManager().get("copper"));
		int desiredCoppers = 60 - 7 * playerAmount1;
		if (coppers != desiredCoppers){
			System.out.println("Error: testMakeCoppers playerAmount="+playerAmount1);
			System.out.println("coppers = " + coppers + ", and should be = " + desiredCoppers);
		}
	}
	
	public void testAmountProvinces() {
		int provinces = b1.victorysupply.get(getGameServer().getCardManager().get("province"));
		if ( (provinces != 8 && playerAmount1 == 2) || (provinces != 12 && playerAmount1 > 2 && playerAmount1 <= 4) ) {
			System.out.println("Error: testAmountProvinces playerAmount="+playerAmount1);
		}
	}
	
	public void testAmountDuchies() {
		int duchies = b1.victorysupply.get(getGameServer().getCardManager().get("duchy"));
		if ( (duchies != 8 && playerAmount1 == 2) || (duchies != 12 && playerAmount1 > 2 && playerAmount1 <= 4) ) {
			System.out.println("Error: testAmountDuchies playerAmount="+playerAmount1);
		}
	}
	
	public void testAmountEstates() {
		int estates = b1.victorysupply.get(getGameServer().getCardManager().get("estate"));
		if ( (estates != 8 && playerAmount1 == 2) || (estates != 12 && playerAmount1 > 2 && playerAmount1 <= 4) ) {
			System.out.println("Error: testAmountEstates playerAmount="+playerAmount1);
		}
	}
	
	public void testAmountCurses() {
		int curses = b1.cursesupply.get(getGameServer().getCardManager().get("curse"));
		if ( (curses + 10) / playerAmount1 != 10) {
			System.out.println("Error: testAmountCurses playerAmount="+playerAmount1);
		}
	}
	
	public void testAmountOfActionCards() {
		//Card chapel = new Card("chapel", "test card", 1);
		b1.addActionCard(getGameServer().getCardManager().get("chapel"));
		int chapelCount = b1.actionsupply.get(getGameServer().getCardManager().get("chapel"));
		if (chapelCount != 10) {
			System.out.println("Error: testAmountOfActionCards");
		}
	}
	
	public void testHandleAdd() {
		String key = "ADDCARD";
		String value = "Garden";
		boolean succes = gc.handle(key,value);
		if(!succes) {
			System.out.println("Error: testHandleAdd");
		}
	}
	
	public void testActionCardTypes() {
		String value = null;
		String key = "ADDCARD";
		List<String> actionCards;
		List<String> desiredResult = new ArrayList<>();
		boolean succes = true;
		for(int i = 0; i < 10; i++) {
			desiredResult.add(Integer.toString(i));
		}
		for(int i = 0; i <= 11 && succes; i++) {
			value = Integer.toString(i);
			succes = gc2.handle(key, value);
		}
		actionCards = gc2.getActionCards();
		if(!succes && actionCards == desiredResult) {
			System.out.println("Error testActionCardTypes & cycle = " + value);
		}
	}
	
	public void testHandleRemove() {
		String key = "addcard";
		List<String> actionCards;
		List<String> desiredResult = new ArrayList<>();
		boolean succes = true;
		String val = null;
		for(int i = 0; i < 9; i++) {
			val = Integer.toString(i);
			desiredResult.add(val);
		}
		desiredResult.add("11");
		for(int i = 0; i < 10 && succes; i++) {
			val = Integer.toString(i);
			succes = gc3.handle(key, val);
		}
		succes = gc3.handle("removecard", val);
		val = "11";
		succes = gc3.handle(key, val);
		actionCards = gc3.getActionCards();
		//System.out.println(actionCards);
		if(!(actionCards == desiredResult || succes)) {
			System.out.println("Error: testHandleRemove");
		}
	}

	
	
	
	

	public static void main(String[] args){
		new GameTest();
	}
}
