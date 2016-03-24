package net.ultradev.dominion.tests;

import net.ultradev.dominion.game.card.*;

public class CardTest {
	
	public Card c1;
	public boolean Testing = true;
	
	public void initTest(){
		//public Card(String name, String description, int cost)
		c1 = new Card("name","description",99);
		if(Testing){
			System.out.println("Functie in de main toegevoegd?");
		}
	}
	/*
	public void testAddAction(){
		//woodcutter.addAction(parseAction("add_buys", "Adds 1 buy to your turn", "amount=1"));
		c1.addAction("test_game","Test the game","test=over9000");
	}
	*/
	
	
	
	public static void main(String[] args){
		System.out.println("Starting test");
		//CardTest ct = new CardTest();
		System.out.println("Test ended");
	}

}
