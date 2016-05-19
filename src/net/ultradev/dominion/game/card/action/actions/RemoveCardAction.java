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
import net.ultradev.dominion.game.card.action.TargetedAction;
import net.ultradev.dominion.game.player.Player;

public class RemoveCardAction extends Action {
	
	public enum RemoveCount { CHOOSE_AMOUNT, SPECIFIC_AMOUNT, RANGE, MINIMUM, MAXIMUM };
	public enum RemoveType { TRASH, DISCARD };
	
	private int amount;
	private int min, max;
	private RemoveCount countType;
	private RemoveType type;
	
	private Map<Player, Integer> cardsRemoved;
	
	private List<Card> permitted;
	private TargetedAction targeted;
	
	public RemoveCardAction(ActionTarget target, RemoveType type, String identifier, String description) {
		super(identifier, description, target);
		this.countType = RemoveCount.CHOOSE_AMOUNT;
		init(type);
	}
	
	public RemoveCardAction(ActionTarget target, RemoveType type, String identifier, String description, int amount) {
		super(identifier, description, target);
		this.amount = amount;
		this.countType = RemoveCount.SPECIFIC_AMOUNT;
		init(type);
	}
	
	public RemoveCardAction(ActionTarget target, RemoveType type, String identifier, String description, int min, int max) {
		super(identifier, description, target);
		this.min = min;
		this.max = max;
		this.countType = RemoveCount.RANGE;
		init(type);
	}
	
	public RemoveCardAction(ActionTarget target, RemoveType type, String identifier, String description, int amount, boolean minimum) {
		super(identifier, description, target);
		if(minimum) {
			this.min = amount;
			this.countType = RemoveCount.MINIMUM;
		} else {
			this.max = amount;
			this.countType = RemoveCount.MAXIMUM;
		}
		init(type);
	}
	
	public void init(RemoveType type) {
		this.permitted = new ArrayList<>();
		this.type = type;
		this.cardsRemoved = new HashMap<>();
	}
	
	public void addPermitted(Card card) {
		permitted.add(card);
	}
	
	public boolean isRestricted() {
		return permitted.size() != 0;
	}
	
	public List<Card> getPermitted() {
		return permitted;
	}

	@Override
	public JSONObject play(Turn turn) {
		cardsRemoved.put(turn.getPlayer(), 0);
		
		// If the action affects more than the person that played the card
		if(getTarget().equals(ActionTarget.EVERYONE) || getTarget().equals(ActionTarget.OTHERS)) {
			targeted = new TargetedAction(turn.getPlayer(), this);
			for(Player p : targeted.getPlayers()) {
				cardsRemoved.put(p, 0);
			}
		}
		
		return getResponse(turn);
	}
	
	@Override
	public JSONObject selectCard(Turn turn, Card card) {
		return selectCard(turn, card, turn.getPlayer());
	}
	
	public JSONObject selectCard(Turn turn, Card card, Player player) {
		if(isRestricted() && !getPermitted().contains(card)) {
			return turn.getGame().getGameServer().getGameManager().getInvalid("Cannot select that card, it is resricted");
		}
		
		switch(type) {
			case DISCARD:
				player.discardCard(card);
				break;
			case TRASH:
				player.trashCard(card);
				break;
			default:
				break;
		}
		
		for(Action action : getCallbacks()) {
			action.setMaster(player, card);
			JSONObject played = action.play(turn);
			if(action instanceof GainCardAction) {
				return played;
			}
		}
		
		return finish(turn, player);
	}
	
	@Override
	public JSONObject finish(Turn turn) {
		if(isMultiTargeted()) {
			return finish(turn, targeted.getCurrentPlayer());
		} else {
			return finish(turn, turn.getPlayer());
		}
	}
	
	@Override
	public JSONObject finish(Turn turn, Player player) {
		int removedCards = getRemovedCards(player) + 1;
		cardsRemoved.put(player, removedCards);
		
		if(targeted != null) {
			targeted.completeForCurrentPlayer();
		}
		
		if(max != 0 && removedCards >= max && isCompleted()) {
			return turn.stopAction();
		}
		
		return getResponse(turn);
	}
	
	@Override
	public boolean isCompleted() {
		boolean completed = false;
		if(targeted == null) {
			completed = true;
		} else {
			if(targeted.isDone()) {
				completed = true;
			}
		}
		return completed;
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
		
		Player player = turn.getPlayer();
		if(isMultiTargeted()) {
			player = targeted.isDone() ? null : targeted.getCurrentPlayer();
		}
		
		if(player != null && canSelectMore(player)) {
			response.accumulate("result", ActionResult.SELECT_CARD_HAND);
			response.accumulate("force", hasForceSelect(player));
			response.accumulate("player", player.getDisplayname());
		} else {
			response.accumulate("result", ActionResult.DONE);
		}
		return response;
	}
	
}