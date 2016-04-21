package net.ultradev.dominion.game.card.action.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.SubTurn;
import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.ActionResult;

public class RemoveCardAction extends Action {
	
	public enum RemoveCount { CHOOSE_AMOUNT, SPECIFIC_AMOUNT, RANGE, MINIMUM, MAXIMUM };
	public enum RemoveType { TRASH, DISCARD }
	
	int amount;
	int min, max;
	RemoveCount countType;
	RemoveType type;
	
	Map<HttpSession, Integer> cardsRemoved;
	
	List<Card> restriction;
	
	public RemoveCardAction(RemoveType type, String identifier, String description) {
		super(identifier, description);
		this.cardsRemoved = new HashMap<>();
		this.countType = RemoveCount.CHOOSE_AMOUNT;
		this.restriction = new ArrayList<>();
		this.type = type;
	}
	
	public RemoveCardAction(RemoveType type, String identifier, String description, int amount) {
		super(identifier, description);
		this.cardsRemoved = new HashMap<>();
		this.amount = amount;
		this.countType = RemoveCount.SPECIFIC_AMOUNT;
		this.restriction = new ArrayList<>();
		this.type = type;
	}
	
	public RemoveCardAction(RemoveType type, String identifier, String description, int min, int max) {
		super(identifier, description);
		this.cardsRemoved = new HashMap<>();
		this.min = min;
		this.max = max;
		this.countType = RemoveCount.RANGE;
		this.restriction = new ArrayList<>();
		this.type = type;
	}
	
	public RemoveCardAction(RemoveType type, String identifier, String description, int amount, boolean minimum) {
		super(identifier, description);
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
	public JSONObject play(Turn turn, HttpSession session) {
		return getResponse(turn, session);
	}
	
	public JSONObject selectCard(SubTurn subturn, Card card, HttpSession session) {
		switch(type) {
			case DISCARD:
				subturn.getPlayer().discardCard(card);
				break;
			case TRASH:
				subturn.getPlayer().trashCard(card);
				break;
			default:
				break;
		}
		cardsRemoved.put(session, removedCards(session) + 1);
		return getResponse(subturn.getTurn(), session);
	}
	
	public int removedCards(HttpSession session) {
		if(cardsRemoved.containsKey(session))
			return cardsRemoved.get(session);
		return 0;
	}
	
	public boolean hasForceSelect(HttpSession session) {
		switch(countType) {
			case CHOOSE_AMOUNT:
				return false;
			case RANGE:
				return removedCards(session) < this.min;
			case SPECIFIC_AMOUNT:
				return removedCards(session) == this.amount;
			default:
				return false;
		}
	}
	
	public boolean canSelectMore(HttpSession session) {
		switch(countType) {
			case CHOOSE_AMOUNT:
				return true;
			case RANGE:
				return removedCards(session) < this.max;
			case SPECIFIC_AMOUNT:
				return removedCards(session) != this.amount;
			default:
				return false;
		}
	}
	
	public JSONObject getResponse(Turn turn, HttpSession session) {
		JSONObject response = new JSONObject().accumulate("response", "OK");
		if(canSelectMore(session)) {
			response.accumulate("result", ActionResult.SELECT_CARD);
			response.accumulate("force", hasForceSelect(session));
		} else {
			return turn.playCard(turn.getActiveCard().getName(), session);
		}
		return response;
	}
	
}