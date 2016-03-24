package net.ultradev.dominion.game.card.action;

import net.ultradev.dominion.game.Turn;

public abstract class Action {

	private String identifier, description;
	
	public Action(String identifier, String description) {
		this.identifier = identifier;
		this.description = description;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public String getDescripton() {
		return description;
	}
	
	public abstract void play(Turn turn);
	
}
