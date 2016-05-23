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
		
	private CardType type;
	private int coins;
	
	private Card card;
	private CardDestination destination;
	
	public GainCardAction(String identifier, String description, ActionTarget target, int cost) {
		super(identifier, description, target);
		this.coins = cost;
	}
	
	// If they can choose a card and get a budget (specific type)
	public GainCardAction(String identifier, String description, ActionTarget target, int cost, CardType type) {
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
		return destination == null ? CardDestination.DECK : destination;
	}

	@Override
	public JSONObject play(Turn turn, Card card) {
		if(this.card != null) {
			if(getTarget().equals(ActionTarget.SELF)) {
				turn.buyCard(card.getName(), true, getDestinaton());
			} else if(getTarget().equals(ActionTarget.OTHERS)) {
				for(Player p : turn.getGame().getPlayers()) {
					if(!p.equals(turn.getPlayer())) {
						turn.gainCard(p, card, getDestinaton());
					}
				}
			}
			return new JSONObject()
					.accumulate("response", "OK")
					.accumulate("result", ActionResult.DONE);
		} else {
			return new JSONObject()
					.accumulate("response", "OK")
					.accumulate("result", ActionResult.SELECT_CARD_BOARD)
					.accumulate("force", false)
					.accumulate("type", type == null ? "ANY" : type)
					.accumulate("player", turn.getPlayer().getDisplayname())
					.accumulate("cost", getCost(turn.getPlayer()))
					.accumulate("message", getDescripton())
					.accumulate("min", 0)
					.accumulate("max", 0);
		}
	}
	
	@Override
	public JSONObject selectCard(Turn turn, Card card) {
		if(!isSelectable(card)) {
			return turn.getGame().getGameServer().getGameManager().getInvalid("Can't select that card");
		} else if(card.getCost() <= getCost(turn.getPlayer())) {
			turn.buyCard(card.getName(), true, getDestinaton());
			return turn.stopAction();
		}
		return turn.getGame().getGameServer().getGameManager().getInvalid("Card is too expensive");
	}
	
	public boolean isSelectable(Card card) {
		if(type == null) {
			return true;
		} else {
			return card.getType().equals(type);
		}
	}
	
	/**
	 * @param player
	 * @return The cost the gained card may have
	 */
	public int getCost(Player player) {
		return hasTrigger(player) ? getTrigger(player).getCost() + coins : coins;
	}
	
}
