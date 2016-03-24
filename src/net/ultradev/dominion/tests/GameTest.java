package net.ultradev.dominion.tests;

import net.ultradev.dominion.game.*;
import net.ultradev.dominion.game.card.*;

public class GameTest {
	
	public int playerAmount1;
	public Board b1;
	public boolean Testing = true;
	
	public void initTest() {
		makeBoard(playerAmount1);
		if(Testing){
			System.out.println("Functie in de main toegevoegd?");
		}
	}
	
	public void initLoop() {
		makeBoard(playerAmount1);
	}
		
	public void makeBoard(int players) {
		CardManager.setup();
		b1 = new Board();
		b1.initSupplies(players);
	}
	
	public void testAanmakenCoppers() {
		int coppers = b1.treasuresupply.get(CardManager.get("copper"));
		int desiredCoppers = 60 - 7 * playerAmount1;
		if (coppers != desiredCoppers){
			System.out.println("Error: aanmakenCoppers playerAmount="+playerAmount1);
			System.out.println("coppers = " + coppers + ", and should be = " + desiredCoppers);
		}
	}
	
	public void testAantalProvinces() {
		int provinces = b1.victorysupply.get(CardManager.get("province"));
		if ( (provinces != 8 && playerAmount1 == 2) || (provinces != 12 && playerAmount1 > 2 && playerAmount1 <= 4) ) {
			System.out.println("Error: testAantalProvinces playerAmount="+playerAmount1);
		}
	}
	
	public void testAantalDuchies() {
		int duchies = b1.victorysupply.get(CardManager.get("duchy"));
		if ( (duchies != 8 && playerAmount1 == 2) || (duchies != 12 && playerAmount1 > 2 && playerAmount1 <= 4) ) {
			System.out.println("Error: testAantalDuchies playerAmount="+playerAmount1);
		}
	}
	
	public void testAantalEstates() {
		int estates = b1.victorysupply.get(CardManager.get("estate"));
		if ( (estates != 8 && playerAmount1 == 2) || (estates != 12 && playerAmount1 > 2 && playerAmount1 <= 4) ) {
			System.out.println("Error: testAantalEstates playerAmount="+playerAmount1);
		}
	}
	
	public void testAantalCurses() {
		int curses = b1.cursesupply.get(CardManager.get("curse"));
		if ( (curses + 10) / playerAmount1 != 10) {
			System.out.println("Error: testAantalCurses playerAmount="+playerAmount1);
		}
	}
	
	public void testAmountOfActionCards() {
		//Card chapel = new Card("chapel", "test card", 1);
		b1.addActionCard(CardManager.get("chapel"));
		int chapelCount = b1.actionsupply.get(CardManager.get("chapel"));
		if (chapelCount != 10) {
			System.out.println("Error: testAmountOfActionCards");
		}
	}

	
	
	
	

	public static void main(String[] args){
		System.out.println("Starting test");
		GameTest gt = new GameTest();
		//Test alle functies die niet afhankelijk zijn van het aantal spelers
		gt.initTest();
		gt.testAmountOfActionCards();
		//Test alle functies die afhankelijk zijn van het aantal spelers
		for(int i = 2;  i <= 4; i++) {
			gt.playerAmount1 = i;
			gt.initLoop();
			gt.testAanmakenCoppers();
			gt.testAantalEstates();
			gt.testAantalDuchies();
			gt.testAantalProvinces();
			gt.testAantalCurses();
		}
		System.out.println("Test ended");
	}
}
