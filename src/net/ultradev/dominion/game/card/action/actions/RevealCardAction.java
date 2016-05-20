package net.ultradev.dominion.game.card.action.actions;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.action.Action;

public class RevealCardAction extends Action {
	
	public RevealCardAction(String identifier, String description, ActionTarget target) {
		super(identifier, description, target);
	}

	@Override
	public JSONObject play(Turn turn) {
		return null;
	}

}
