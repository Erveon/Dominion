package net.ultradev.dominion.game.card.action.actions;

import java.util.List;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.ActionResult;
import net.ultradev.dominion.game.player.Player.Pile;

public class TransferPileAction extends Action {
	
	private Pile from, to;
	
	public TransferPileAction(String identifier, String description, ActionTarget target, Pile from, Pile to) {
		super(identifier, description, target);
		this.from = from;
		this.to = to;
	}

	@Override
	public JSONObject play(Turn turn, Card card) {
		List<Card> cards = turn.getPlayer().getPile(from);
		turn.getPlayer().getPile(to).addAll(cards);
		cards.clear();
		return new JSONObject().accumulate("response", "OK").accumulate("result", ActionResult.DONE);
	}

}
