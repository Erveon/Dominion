package net.ultradev.dominion.game.card.action.actions;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.ActionResult;

public class BureaucratAction extends Action {

	public BureaucratAction(String identifier, String description, ActionTarget target) {
		super(identifier, description, target);
	}

	@Override
	public JSONObject play(Turn turn, Card card) {
		//TODO make
		return new JSONObject().accumulate("response", "OK").accumulate("result", ActionResult.DONE);
	}

}
