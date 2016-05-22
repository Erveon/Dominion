package net.ultradev.dominion.game.card.action.actions;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.ActionResult;

public class GainBuypowerAction extends Action {

	public enum GainBuypowerType { ADD, MULTIPLIER };
	
	int amount;
	GainBuypowerType type;

	public GainBuypowerAction(String identifier, String description, ActionTarget target, int amount, GainBuypowerType type) {
		super(identifier, description, target);
		this.amount = amount;
		this.type = type;
	}

	@Override
	public JSONObject play(Turn turn, Card card) {
		JSONObject response = new JSONObject().accumulate("response", "OK");
		switch(type) {
			case ADD:
				turn.addBuypower(this.amount);
				break;
			case MULTIPLIER: // Default multiplier is 1, adding 1 will make it double buypower
				turn.addMultiplierBuypower(this.amount);
				break;
		}
		response.accumulate("result", ActionResult.DONE);
		return response;
	}
	
}
