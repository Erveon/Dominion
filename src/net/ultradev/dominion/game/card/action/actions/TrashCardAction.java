package net.ultradev.dominion.game.card.action.actions;

import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.action.Action;

public class TrashCardAction implements Action {
	
	String identifier;
	String description;
	int amount;
	int min, max;
	boolean chooseAmount;
	boolean rangeAmount;
	
	public TrashCardAction(String identifier, String description) {
		this.identifier = identifier;
		this.description = description;
		this.chooseAmount = true;
	}
	
	public TrashCardAction(String identifier, String description, int amount) {
		this.identifier = identifier;
		this.description = description;
		this.amount = amount;
	}
	
	public TrashCardAction(String identifier, String description, int min, int max) {
		this.identifier = identifier;
		this.description = description;
		this.min = min;
		this.max = max;
		this.rangeAmount = true;
	}

	public String getIdentifier() {
		return identifier;
	}
	
	public String getDescripton() { 
		return description; 
	}

	public void play(Turn turn) {
		//TODO trash 'amount' cards
	}
	
}