package net.ultradev.dominion.game.card.action.actions;

import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.ActionResult;

public class GainActionsAction extends Action {
	
	int amount;

	public GainActionsAction(String identifier, String description, int amount) {
		super(identifier, description);
		this.amount = amount;
	}

	@Override
	public ActionResult play(Turn turn) {
		turn.addActions(this.amount);
		return ActionResult.DONE;
	}

}
