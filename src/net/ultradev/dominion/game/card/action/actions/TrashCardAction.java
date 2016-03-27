package net.ultradev.dominion.game.card.action.actions;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.SubTurn;
import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.ActionResult;

public class TrashCardAction extends Action {
	
	public enum TrashType { CHOOSE_AMOUNT, SPECIFIC_AMOUNT, RANGE };
	
	int amount;
	int min, max;
	TrashType type;
	
	int cardsTrashed;
	
	List<Card> restriction;
	
	public TrashCardAction(String identifier, String description) {
		super(identifier, description);
		this.type = TrashType.CHOOSE_AMOUNT;
		this.restriction = new ArrayList<>();
	}
	
	public TrashCardAction(String identifier, String description, int amount) {
		super(identifier, description);
		this.amount = amount;
		this.type = TrashType.SPECIFIC_AMOUNT;
		this.restriction = new ArrayList<>();
	}
	
	public TrashCardAction(String identifier, String description, int min, int max) {
		super(identifier, description);
		this.min = min;
		this.max = max;
		this.type = TrashType.RANGE;
		this.restriction = new ArrayList<>();
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
		// Reset trashed card amount from possible previous trash performed by the same kind of card
		this.cardsTrashed = 0;
		return getResponse(turn);
	}
	
	public JSONObject selectCard(SubTurn subturn, Card card) {
		subturn.getPlayer().trashCard(card);
		this.cardsTrashed++;
		return getResponse(subturn.getTurn());
	}
	
	public boolean hasForceSelect() {
		switch(type) {
			case CHOOSE_AMOUNT:
				return false;
			case RANGE:
				return this.cardsTrashed < this.min;
			case SPECIFIC_AMOUNT:
				return this.cardsTrashed == this.amount;
			default:
				return false;
		}
	}
	
	public boolean canSelectMore() {
		switch(type) {
			case CHOOSE_AMOUNT:
				return true;
			case RANGE:
				return this.cardsTrashed < this.max;
			case SPECIFIC_AMOUNT:
				return this.cardsTrashed != this.amount;
			default:
				return false;
		}
	}
	
	public JSONObject getResponse(Turn turn) {
		JSONObject response = new JSONObject().accumulate("response", "OK");
		if(canSelectMore()) {
			response.accumulate("result", ActionResult.SELECT_CARD);
			response.accumulate("force", hasForceSelect());
		} else {
			return turn.playCard(turn.getActiveCard().getName());
		}
		return response;
	}
	
}