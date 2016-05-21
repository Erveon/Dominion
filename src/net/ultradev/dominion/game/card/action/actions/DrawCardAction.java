package net.ultradev.dominion.game.card.action.actions;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.ActionResult;
import net.ultradev.dominion.game.player.Player;

public class DrawCardAction extends Action {
	
	int amount;
	
	public DrawCardAction(String identifier, String description, ActionTarget target, int amount) {
		super(identifier, description, target);
		this.amount = amount;
	}

	@Override
	public JSONObject play(Turn turn, Card card) {
		if(getTarget().equals(ActionTarget.SELF)) {
			for(int i = 0; i < amount; i++) {
				turn.getPlayer().drawCardFromDeck();
			}
		} else if(getTarget().equals(ActionTarget.OTHERS)) {
			for(Player p : turn.getGame().getPlayers()) {
				if(!p.equals(turn.getPlayer())) {
					for(int i = 0; i < amount; i++) {
						p.drawCardFromDeck();
					}
				}
			}
		}
		return new JSONObject()
				.accumulate("response", "OK")
				.accumulate("result", ActionResult.DONE);
	}
	
}