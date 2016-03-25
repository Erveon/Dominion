package net.ultradev.dominion.game.card.action.actions;

import java.util.ArrayList;
import java.util.List;
import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.ActionResult;

public class TrashCardAction extends Action {
	
	public enum TrashType { CHOOSE_AMOUNT, SPECIFIC_AMOUNT, RANGE };
	
	int amount;
	int min, max;
	TrashType type;
	
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
	public ActionResult play(Turn turn) {
		//TODO trash 'amount' cards
		return ActionResult.SELECT_CARDS;
	}
	
}