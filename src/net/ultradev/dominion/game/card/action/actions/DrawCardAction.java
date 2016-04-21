package net.ultradev.dominion.game.card.action.actions;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.ActionResult;

public class DrawCardAction extends Action {
	
	int amount;
	
	public DrawCardAction(String identifier, String description, int amount) {
		super(identifier, description);
		this.amount = amount;
	}

	@Override
	public JSONObject play(Turn turn, HttpSession session) {
		JSONObject response = new JSONObject().accumulate("response", "OK");
		for(int i = 0; i < amount; i++)
			turn.getPlayer().drawCardFromDeck();
		response.accumulate("result", ActionResult.DONE);
		return response;
	}
	
}