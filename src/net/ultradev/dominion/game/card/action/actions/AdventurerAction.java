package net.ultradev.dominion.game.card.action.actions;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.Card.CardType;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.ActionResult;
import net.ultradev.dominion.game.card.action.Revealer;
import net.ultradev.dominion.game.player.Player;
import net.ultradev.dominion.game.player.Player.Pile;

public class AdventurerAction extends Action {

	public AdventurerAction(String identifier, String description, ActionTarget target) {
		super(identifier, description, target);
	}

	@Override
	public JSONObject play(Turn turn, Card card) {
		Player p = turn.getPlayer();
		
		List<Card> toReveal = new ArrayList<>();
		List<Card> treasures = new ArrayList<>();
		
		checkDeck(p, toReveal, treasures);
		
		// If there's not enough in the deck, go to the discard pile and do the same thing
		if(treasures.size() < 2) {
			p.transferDiscardToDeck();
			checkDeck(p, toReveal, treasures);
		}
		
		for(Card treasure : treasures) {
			p.getPile(Pile.HAND).add(treasure);
		}
		
		return new JSONObject()
				.accumulate("response", "OK")
				.accumulate("result", ActionResult.REVEAL)
				.accumulate("reveal", new Revealer(toReveal).get())
				.accumulate("player", turn.getPlayer().getDisplayname())
				.accumulate("force", false)
				.accumulate("min", 0)
				.accumulate("max", 0)
				.accumulate("type", "ALL")
				.accumulate("message", "Reveal " + turn.getPlayer().getDisplayname() + "'s card(s) to everyone");
	}
	
	public void checkDeck(Player p, List<Card> toReveal, List<Card> treasures) {
		// Looking for treasures :))
		for(Card card : p.getPile(Pile.DECK)) {
			if(treasures.size() < 2) {
				if(card.getType().equals(CardType.TREASURE)) {
					treasures.add(card);
				} else {
					toReveal.add(card);
				}
			}
		}
		p.getPile(Pile.DECK).removeAll(toReveal);
		p.getPile(Pile.DECK).removeAll(treasures);
	}

}
