package net.ultradev.dominion.game.card.action;

import net.ultradev.dominion.game.Turn;

public interface Action {

	public String getIdentifier();
	public String getDescripton();
	public void play(Turn turn);
	
}
