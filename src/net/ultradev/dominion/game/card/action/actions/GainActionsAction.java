package net.ultradev.dominion.game.card.action.actions;

import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.utils.Utils;

public class GainActionsAction extends Action {
	
	int amount;

	public GainActionsAction(String identifier, String description, int amount) {
		super(identifier, description);
		this.amount = amount;
	}

	@Override
	public void play(Turn turn) {
		turn.addActions(this.amount);
	}
	
	public static Action parse(String identifier, String description, String amountVar) {
		int amount = Utils.parseInt(amountVar, 1);
		return new GainActionsAction(identifier, description, amount);
	}

}
