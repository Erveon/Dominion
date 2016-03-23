package net.ultradev.dominion.game.card.action.actions;

import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.action.Action;

public class DrawCardAction implements Action {
	
	String identifier;
	String description;
	int amount;
	
	public DrawCardAction(String identifier, String description, int amount) {
		this.identifier = identifier;
		this.description = description;
		this.amount = amount;
	}

	public String getIdentifier() {
		return identifier;
	};
	
	public String getDescripton() { 
		return description; 
	}

	public void play(Turn turn) {
		for(int i = 0; i < amount; i++)
			turn.getPlayer().drawCardFromDeck();
	}
	
}