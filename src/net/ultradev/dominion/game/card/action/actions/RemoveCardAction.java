package net.ultradev.dominion.game.card.action.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.ActionResult;
import net.ultradev.dominion.game.player.Player;

public class RemoveCardAction extends Action {
	
	public enum RemoveCount { CHOOSE_AMOUNT, SPECIFIC_AMOUNT, RANGE, MINIMUM, MAXIMUM };
	public enum RemoveType { TRASH, DISCARD };
	
	int amount;
	int min, max;
	String special;
	RemoveCount countType;
	RemoveType type;
	
	Map<Player, Integer> cardsRemoved;
	
	List<Card> restriction;
	
	public RemoveCardAction(ActionTarget target, RemoveType type, String identifier, String description) {
		super(identifier, description, target);
		this.cardsRemoved = new HashMap<>();
		this.countType = RemoveCount.CHOOSE_AMOUNT;
		this.restriction = new ArrayList<>();
		this.type = type;
	}
	
	public RemoveCardAction(ActionTarget target, RemoveType type, String identifier, String description, int amount) {
		super(identifier, description, target);
		this.cardsRemoved = new HashMap<>();
		this.amount = amount;
		this.countType = RemoveCount.SPECIFIC_AMOUNT;
		this.restriction = new ArrayList<>();
		this.type = type;
	}
	
	public RemoveCardAction(ActionTarget target, RemoveType type, String identifier, String description, int min, int max) {
		super(identifier, description, target);
		this.cardsRemoved = new HashMap<>();
		this.min = min;
		this.max = max;
		this.countType = RemoveCount.RANGE;
		this.restriction = new ArrayList<>();
		this.type = type;
	}
	
	public RemoveCardAction(ActionTarget target, RemoveType type, String identifier, String description, int amount, boolean minimum) {
		super(identifier, description, target);
		this.cardsRemoved = new HashMap<>();
		if(minimum) {
			this.min = amount;
			this.countType = RemoveCount.MINIMUM;
		} else {
			this.max = amount;
			this.countType = RemoveCount.MAXIMUM;
		}
		this.restriction = new ArrayList<>();
		this.type = type;
	}
	
	/**
	 * If anything special that can't be parsed through the actions has to happen
	 * to a card, the card is defined in here and handled by the class when necessary
	 * @param String the name of the special card
	 */
	public void setSpecial(String name) {
		this.special = name;
	}
	
	public void addRestriction(Card card) {
		restriction.add(card);
	}
	
	public boolean isRestricted() {
		return restriction.size() == 0;
	}
	
	public List<Card> getRestriction() {
		return restriction;
	}

	@Override
	public JSONObject play(Turn turn) {
		return getResponse(turn);
	}
	
	public JSONObject selectCard(Turn turn, Card card) {
		Player player = turn.getPlayer();
		
		switch(type) {
			case DISCARD:
				turn.getPlayer().discardCard(card);
				break;
			case TRASH:
				turn.getPlayer().trashCard(card);
				break;
			default:
				break;
		}
		
		// Handles special card actions that can't be parsed
		if(special != null) {
			if(special.equalsIgnoreCase("mine")) {
				return getResponse(turn).accumulate("after", new JSONObject()
																.accumulate("type", "buy_treasure")
																.accumulate("max_cost", card.getCost() + 3));
			}
		}
		
		getCallbacks().forEach(action -> action.play(turn));
		cardsRemoved.put(turn.getPlayer(), getRemovedCards(player) + 1);
		return getResponse(turn);
	}
	
	public int getRemovedCards(Player player) {
		if(cardsRemoved.containsKey(player)) {
			return cardsRemoved.get(player);
		}
		return 0;
	}
	
	public boolean hasForceSelect(Player player) {
		switch(countType) {
			case CHOOSE_AMOUNT:
				return false;
			case RANGE:
				return getRemovedCards(player) < this.min;
			case SPECIFIC_AMOUNT:
				return getRemovedCards(player) == this.amount;
			default:
				return false;
		}
	}
	
	public boolean canSelectMore(Player player) {
		switch(countType) {
			case CHOOSE_AMOUNT:
				return true;
			case RANGE:
				return getRemovedCards(player) < this.max;
			case SPECIFIC_AMOUNT:
				return getRemovedCards(player) != this.amount;
			default:
				return false;
		}
	}
	
	public JSONObject getResponse(Turn turn) {
		JSONObject response = new JSONObject().accumulate("response", "OK");
		if(canSelectMore(turn.getPlayer())) {
			response.accumulate("result", ActionResult.SELECT_CARD);
			response.accumulate("force", hasForceSelect(turn.getPlayer()));
		} else {
			response.accumulate("result", ActionResult.DONE);
		}
		return response;
	}
	
}