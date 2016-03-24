package net.ultradev.dominion.tests;

import net.ultradev.dominion.game.card.*;

public class CardTest {
	
	public Card c1;
	public boolean Testing = true;
	
	public void initTest(){
		//public Card(String name, String description, int cost)
		Card c1 = new Card("name","description",99);
		if(Testing){
			System.out.println("Functie in de main toegevoegd?");
		}
	}
	
	public void testAddAction(){
		c1.addAction();
	}
	
	
	
	
	public static void main(string[] args){
		System.out.println("Starting test");
		CardTest ct = new CardTest();
		System.out.println("Test ended");
	}

}
