package net.ultradev.dominion.game.card.action.actions;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.ActionResult;

public class DrawCardAction extends Action {
	
	int amount;
	
	public DrawCardAction(String identifier, String description, ActionTarget target, int amount) {
		super(identifier, description, target);
		this.amount = amount;
	}

	@Override
	public JSONObject play(Turn turn, Card card) {
		JSONObject response = new JSONObject().accumulate("response", "OK");
		for(int i = 0; i < amount; i++) {
			turn.getPlayer().drawCardFromDeck();
		}
		response.accumulate("result", ActionResult.DONE);
		return response;
	}
	
}