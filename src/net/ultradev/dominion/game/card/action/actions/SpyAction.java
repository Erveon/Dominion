package net.ultradev.dominion.game.card.action.actions;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Game;
import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.ActionResult;
import net.ultradev.dominion.game.card.action.Revealer;
import net.ultradev.dominion.game.card.action.TargetedAction;
import net.ultradev.dominion.game.player.Player;
import net.ultradev.dominion.game.player.Player.Pile;

public class SpyAction extends Action {
	
	private Map<Game, TargetedAction> targeted;
	
	public SpyAction(String identifier, String description, ActionTarget target) {
		super(identifier, description, target);
		this.targeted = new HashMap<>();
	}

	@Override
	public JSONObject play(Turn turn, Card card) {
		targeted.put(turn.getGame(), new TargetedAction(turn.getPlayer(), this));
		return continueAction(turn);
	}

	/**
	 * In this case, when the card is 'selected', the card is kept
	 */
	@Override
	public JSONObject selectCard(Turn turn, Card card) {
		getTargeted(turn.getGame()).completeForCurrentPlayer();
		return continueAction(turn);
	}
	
	/**
	 * In this case, when the action is finished, the card is discarded
	 */
	@Override
	public JSONObject finish(Turn turn) {
		Player target = getTargeted(turn.getGame()).getCurrentPlayer();
		Card card = getCard(target);
		if(card != null) {
			target.getPile(Pile.DECK).remove(0);
			target.getPile(Pile.DISCARD).add(card);
		}
		getTargeted(turn.getGame()).completeForCurrentPlayer();
		return continueAction(turn);
	}
	
	public JSONObject continueAction(Turn turn) {
		if(getTargeted(turn.getGame()).isDone()) {
			return new JSONObject().accumulate("response", "OK").accumulate("result", ActionResult.DONE);
		} else {
			Player target = getTargeted(turn.getGame()).getCurrentPlayer();
			return new JSONObject()
					.accumulate("response", "OK")
					.accumulate("result", ActionResult.REVEAL)
					.accumulate("reveal", new Revealer(getCard(target)).get())
					.accumulate("force", false)
					.accumulate("player", target.getDisplayname())
					.accumulate("min", 0)
					.accumulate("max", 0)
					.accumulate("message", "Reveal " + target.getDisplayname() + "'s card(s) to everyone")
					.accumulate("type", "ANY");
		}
	}
	
	public Card getCard(Player p) {
		if(p.getPile(Pile.DECK).size() > 0) {
			return p.getPile(Pile.DECK).get(0);
		}
		return null;
	}
	
	@Override
	public boolean isCompleted(Turn turn) {
		TargetedAction ta = getTargeted(turn.getGame());
		return ta == null || ta.isDone();
	}
	
	public TargetedAction getTargeted(Game g) {
		if(targeted.containsKey(g)) {
			return targeted.get(g);
		}
		return null;
	}
	
	public void removeTargeted(Game g) {
		if(targeted.containsKey(g)) {
			targeted.remove(g);
		}
	}

}
