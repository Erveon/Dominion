package net.ultradev.dominion.game.card.action.actions;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.Turn.CardDestination;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.Card.CardType;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.ActionResult;
import net.ultradev.dominion.game.player.Player;

public class GainCardAction extends Action {
	
	public enum GainCardType { TREASURE, ANY }
	
	private GainCardType type;
	private int coins;
	
	private Card card;
	private CardDestination destination;
	
	// If they can choose a card and get a budget
	public GainCardAction(String identifier, String description, ActionTarget target, int cost, GainCardType type) {
		super(identifier, description, target);
		this.coins = cost;
		this.type = type;
	}
	
	// If they get that specific card for free
	public GainCardAction(String identifier, String description, ActionTarget target, Card card) {
		super(identifier, description, target);
		this.card = card;
	}
	
	public void setDestination(CardDestination destination) {
		this.destination = destination;
	}
	
	public CardDestination getDestinaton() {
		return destination;
	}

	@Override
	public JSONObject play(Turn turn, Card card) {
		if(this.card != null) {
			turn.buyCard(card.getName(), true, getDestinaton());
			return new JSONObject()
					.accumulate("response", "OK")
					.accumulate("result", ActionResult.DONE);
		} else {
			return new JSONObject()
					.accumulate("response", "OK")
					.accumulate("result", ActionResult.SELECT_CARD_BOARD)
					.accumulate("force", false)
					.accumulate("type", type)
					.accumulate("player", turn.getPlayer())
					.accumulate("cost", getCost(turn.getPlayer()));
		}
	}
	
	@Override
	public JSONObject selectCard(Turn turn, Card card) {
		if(!isSelectable(card)) {
			return turn.getGame().getGameServer().getGameManager().getInvalid("Can't select that card");
		} else if(card.getCost() <= getCost(turn.getPlayer())) {
			turn.buyCard(card.getName(), true, getDestinaton());
			if(hasMaster(turn.getPlayer())) {
				return turn.getActiveAction().finish(turn);
			} else {
				return new JSONObject().accumulate("response", "OK")
									   .accumulate("result", ActionResult.DONE);
			}
		}			
		return turn.getGame().getGameServer().getGameManager().getInvalid("Card is too expensive");
	}
	
	public boolean isSelectable(Card card) {
		switch(type) {
			case TREASURE:
				return card.getType().equals(CardType.TREASURE);
			case ANY:
			default:
				return true;
		}
	}
	
	/**
	 * @param player
	 * @return The cost the gained card may have
	 */
	public int getCost(Player player) {
		return hasMaster(player) ? getMaster(player).getCost() + coins : coins;
	}
	
}
