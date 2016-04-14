package net.ultradev.dominion.tests;

import java.util.List;

import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.actions.GainBuypowerAction;
import net.ultradev.dominion.game.card.action.actions.GainBuypowerAction.GainBuypowerType;

public class CardTest {
	
	public Card c1;
	public Card c2;
	public boolean Testing = true;
	
	public CardTest() {
		System.out.println("Starting test");
		initTest();
		testAddAction();
		testAddType();
		testGetTypesFormatted();
		System.out.println("\nTest ended");
	}
	
	public void initTest(){
		//public Card(String name, String description, int cost)
		c1 = new Card("name","description",99);
		c2 = new Card("name2","description2",98);
		if(Testing){
			System.out.println("Functie in de main toegevoegd?\n");
		}
	}
	
	public void testAddAction(){
		Action action = new GainBuypowerAction("test_game","test something",9001, GainBuypowerType.ADD);
		c1.addAction(action);
		List<Action> actions = c1.getActions();
		if(!(actions.contains(action))) {
			System.out.println("Error: testAddAction");
		};
	}
	
	public void testAddType(){
		String type = "TestCard";
		c1.addType(type);
		List<String> types = c1.getTypes();
		if(!(types.contains(type))) {
			System.out.println("Error: testAddType");
		}
	}
	
	public void testGetTypesFormatted() {
		String type = "anotherType";
		String type2 = "yetAnotherType";
		c2.addType(type);
		c2.addType(type2);
		String arrangedTypes = c2.getTypesFormatted();
		if(!(arrangedTypes.equals("anotherType - yetAnotherType"))) {   //Strings nooit vergelijken met "==" maar met [STRING].equals([STRING])
			System.out.println("Error: testGetTypesFormatted");
		}
	}
	
	
	
	
	public static void main(String[] args){
		new CardTest();
	}

}
