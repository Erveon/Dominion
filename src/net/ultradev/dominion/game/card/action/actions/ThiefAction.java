package net.ultradev.dominion.game.card.action.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Game;
import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.Card.CardType;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.ActionProgress;
import net.ultradev.dominion.game.card.action.ActionResult;
import net.ultradev.dominion.game.card.action.Revealer;
import net.ultradev.dominion.game.card.action.TargetedAction;
import net.ultradev.dominion.game.player.Player;
import net.ultradev.dominion.game.player.Player.Pile;

public class ThiefAction extends Action {

	private Map<Game, TargetedAction> targeted;
	private Map<Player, ActionProgress> progress;
	
	public ThiefAction(String identifier, String description, ActionTarget target) {
		super(identifier, description, target);
		this.targeted = new HashMap<>();
		this.progress = new HashMap<>();
	}

	@Override
	public JSONObject play(Turn turn, Card card) {
		targeted.put(turn.getGame(), new TargetedAction(turn.getPlayer(), this));
		for(Player p : getTargeted(turn.getGame()).getPlayers()) {
			ActionProgress ap = new ActionProgress();
			ap.set("revealed", false);
			progress.put(p, ap);
		}
		return finish(turn);
	}
	
	@Override
	public JSONObject finish(Turn turn) {
		TargetedAction ta = getTargeted(turn.getGame());
		if(ta == null || ta.isDone()) {
			if(ta != null) {
				removeTargeted(turn.getGame());
			}
			return new JSONObject()
					.accumulate("response", "OK")
					.accumulate("result", ActionResult.DONE);
		} else {
			Player p = ta.getCurrentPlayer();
			return affect(p, turn);
		}
	}
	
	public JSONObject affect(Player p, Turn turn) {
		List<Card> toReveal = new ArrayList<>();
		for(int i = 0; i < 2; i++) {
			toReveal.add(p.getPile(Pile.DECK).get(i));
		}
		if(progress.get(p).getBoolean("revealed")) {
			return endFor(p, turn, toReveal);
		} else {
			progress.get(p).set("revealed", true);
			return new JSONObject()
					.accumulate("response", "OK")
					.accumulate("result", ActionResult.REVEAL)
					.accumulate("reveal", new Revealer(toReveal).get())
					.accumulate("force", false)
					.accumulate("player", p.getDisplayname())
					.accumulate("min", 0)
					.accumulate("max", 0)
					.accumulate("message", "Reveal " + p.getDisplayname() + "'s card(s) to everyone")
					.accumulate("type", "ANY");
		}
	}
	
	public JSONObject endFor(Player p, Turn turn, List<Card> toReveal) {
		TargetedAction ta = getTargeted(turn.getGame());
		for(Card c : toReveal) {
			p.getPile(Pile.DECK).remove(c);
			// Treasures go to the thief's discard, others to the same player
			if(c.getType().equals(CardType.TREASURE)) {
				turn.getPlayer().getPile(Pile.DISCARD).add(c);
			} else {
				p.getPile(Pile.DISCARD).add(c);
			}
		}
		progress.remove(p);
		// Finishes for the current player and moves on to the next
		// if there's no other player, we're done
		ta.completeForCurrentPlayer();
		return finish(turn);
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
