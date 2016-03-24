package net.ultradev.dominion.game.card.action.actions;

import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.action.Action;

public class GainBuysAction extends Action {
	
	int amount;

	public GainBuysAction(String identifier, String description, int amount) {
		super(identifier, description);
		this.amount = amount;
	}

	@Override
	public void play(Turn turn) {
		turn.addBuys(this.amount);
	}

}
