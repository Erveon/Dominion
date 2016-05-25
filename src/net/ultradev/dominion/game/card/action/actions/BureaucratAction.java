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
import net.ultradev.dominion.game.card.action.ActionResult;
import net.ultradev.dominion.game.card.action.Revealer;
import net.ultradev.dominion.game.card.action.TargetedAction;
import net.ultradev.dominion.game.player.Player;
import net.ultradev.dominion.game.player.Player.Pile;

public class BureaucratAction extends Action {

	private Map<Game, TargetedAction> targeted;
	
	public BureaucratAction(String identifier, String description, ActionTarget target) {
		super(identifier, description, target);
		this.targeted = new HashMap<>();
	}

	@Override
	public JSONObject play(Turn turn, Card card) {
		TargetedAction ta = new TargetedAction(turn.getPlayer(), this);
		List<Player> affected = new ArrayList<>();
		for(Player p : turn.getGame().getPlayers()) {
			if(!p.equals(turn.getPlayer())) {
				for(Card c : p.getPile(Pile.HAND)) {
					if(c.getType().equals(CardType.VICTORY)) {
						System.out.println(p.getDisplayname() + " has a " + c.getName());
						affected.add(p);
						continue;
					}
				}
			}
		}
		ta.setPlayers(affected);
		targeted.put(turn.getGame(), ta);
		return finish(turn);
	}
	
	@Override
	public JSONObject selectCard(Turn turn, Card card) {
		if(!card.getType().equals(CardType.VICTORY)) {
			return turn.getGame().getGameServer().getGameManager().getInvalid("Card is not a victory card");
		}
		TargetedAction ta = getTargeted(turn.getGame());
		Player target = ta.getCurrentPlayer();
		target.getPile(Pile.HAND).remove(card);
		target.getPile(Pile.DECK).add(0, card);
		ta.completeForCurrentPlayer();
		return new JSONObject()
				.accumulate("response", "OK")
				.accumulate("result", ActionResult.REVEAL)
				.accumulate("reveal", new Revealer(card).get())
				.accumulate("player", target.getDisplayname())
				.accumulate("force", false)
				.accumulate("min", 0)
				.accumulate("max", 0)
				.accumulate("type", "ALL")
				.accumulate("message", "Reveal " + target.getDisplayname() + "'s card(s) to everyone");
	}
	
	@Override
	public boolean isCompleted(Turn turn) {
		TargetedAction ta = getTargeted(turn.getGame());
		return ta == null || ta.isDone();
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
			return new JSONObject()
					.accumulate("response", "OK")
					.accumulate("result", ActionResult.SELECT_CARD_HAND)
					.accumulate("type", CardType.VICTORY)
					.accumulate("force", true)
					.accumulate("player", ta.getCurrentPlayer().getDisplayname())
					.accumulate("min", 0)
					.accumulate("max", 1)
					.accumulate("message", getDescripton());
		}
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
