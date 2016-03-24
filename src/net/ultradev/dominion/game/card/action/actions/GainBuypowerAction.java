package net.ultradev.dominion.game.card.action.actions;

import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.action.Action;

public class GainBuypowerAction extends Action {

	public enum GainBuypowerType { ADD, MULTIPLIER };
	
	int amount;
	GainBuypowerType type;

	public GainBuypowerAction(String identifier, String description, int amount, GainBuypowerType type) {
		super(identifier, description);
		this.amount = amount;
		this.type = type;
	}

	@Override
	public void play(Turn turn) {
		switch(type) {
			case ADD:
				turn.addBuypower(this.amount);
				break;
			case MULTIPLIER: // Default multiplier is 1, adding 1 will make it double buypower
				turn.addMultiplierBuypower(this.amount);
				break;
		}
	}
	
}
