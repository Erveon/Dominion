package net.ultradev.dominion.game.card.action.actions;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.ActionResult;

public class GainBuysAction extends Action {
	
	int amount;

	public GainBuysAction(String identifier, String description, ActionTarget target, int amount) {
		super(identifier, description, target);
		this.amount = amount;
	}

	@Override
	public JSONObject play(Turn turn) {
		JSONObject response = new JSONObject().accumulate("response", "OK");
		turn.addBuys(this.amount);
		response.accumulate("result", ActionResult.DONE);
		return response;
	}

}
