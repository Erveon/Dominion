package net.ultradev.dominion.tests;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.ultradev.dominion.game.*;
import net.ultradev.dominion.game.card.*;


public class GameTest {
	
	public Board b;
		
	public void makeBoard(int players) {
		CardManager.setup();
		b = new Board();
		b.initSupplies(players);
	}
	
	public void testAanmakenCoppers() {
		int playerAmount = 2;	
		makeBoard(playerAmount);
		int coppers = b.treasuresupply.get(CardManager.get("copper"));
		if(coppers != 60 - 7 * playerAmount)
			System.out.println("Error: aanmakenCoppers op lijn 21");
	}
	
	
	
	
	
	public static void main(String[] args){
		System.out.println("Starting test");
		GameTest gt = new GameTest();
		gt.testAanmakenCoppers();
		System.out.println("Test ended");
	}
}
