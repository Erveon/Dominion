package net.ultradev.dominion.tests;

import net.ultradev.dominion.game.Board;

public class BoardTest {
	
	public BoardTest(int players){
		Board b = new Board();
		b.initSupplies(players);
	}
}
