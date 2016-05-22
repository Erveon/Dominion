package net.ultradev.dominion.game.card.action.actions;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.ActionResult;
import net.ultradev.dominion.game.player.Player;
import net.ultradev.dominion.game.player.Player.Pile;

public class DrawCardAction extends Action {
	
	AmountType amountType;
	int amount;
	
	public DrawCardAction(String identifier, String description, ActionTarget target, int amount, AmountType amountType) {
		super(identifier, description, target);
		this.amount = amount;
		this.amountType = amountType;
	}

	@Override
	public JSONObject play(Turn turn, Card card) {
		if(getTarget().equals(ActionTarget.SELF)) {
			drawCards(turn.getPlayer());
		} else if(getTarget().equals(ActionTarget.OTHERS)) {
			for(Player p : turn.getGame().getPlayers()) {
				if(!p.equals(turn.getPlayer())) {
					drawCards(p);
				}
			}
		}
		return new JSONObject()
				.accumulate("response", "OK")
				.accumulate("result", ActionResult.DONE);
	}
	
	public void drawCards(Player p) {
		if(amountType.equals(AmountType.UNTIL)) {
			int current = p.getPile(Pile.HAND).size();
			// The amount is how many they have to draw (in negative form)
			// If less than 0 then they have to draw cards
			int amount = this.amount - current;
			if(amount < 0) {
				for(int i = 0; i < Math.abs(amount); i++) {
					p.drawCardFromDeck();
				}
			}
		} else {
			for(int i = 0; i < amount; i++) {
				p.drawCardFromDeck();
			}
		}
	}
	
}