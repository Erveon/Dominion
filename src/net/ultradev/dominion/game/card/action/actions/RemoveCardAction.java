package net.ultradev.dominion.game.card.action.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Game;
import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.ActionProgress;
import net.ultradev.dominion.game.card.action.ActionResult;
import net.ultradev.dominion.game.card.action.TargetedAction;
import net.ultradev.dominion.game.player.Player;

public class RemoveCardAction extends Action {
	
	public enum AmountType { CHOOSE_AMOUNT, SPECIFIC_AMOUNT, UNTIL, RANGE };
	public enum RemoveType { TRASH, DISCARD };
	
	private int min, max;
	private AmountType amountType;
	private RemoveType type;
	
	private Map<Player, ActionProgress> progress;
	
	private List<Card> permitted;
	private Map<Game, TargetedAction> targeted;
	
	public RemoveCardAction(ActionTarget target, RemoveType type, String identifier, String description) {
		super(identifier, description, target);
		this.amountType = AmountType.CHOOSE_AMOUNT;
		init(type);
	}
	
	// Until & specific amount
	public RemoveCardAction(ActionTarget target, RemoveType type, String identifier, String description, AmountType amountType, int amount) {
		super(identifier, description, target);
		this.min = amount;
		this.max = amount;
		this.amountType = amountType;
		init(type);
	}
	
	public RemoveCardAction(ActionTarget target, RemoveType type, String identifier, String description, int min, int max) {
		super(identifier, description, target);
		this.min = min;
		this.max = max;
		this.amountType = AmountType.RANGE;
		init(type);
	}
	
	public void init(RemoveType type) {
		this.permitted = new ArrayList<>();
		this.type = type;
		this.progress = new HashMap<>();
		this.targeted = new HashMap<>();
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
	
	/**
	 * Calculates the minimum amount of cards to be removed for a player.
	 * This will actively decrease when cards are removed opposed to the 'min' variable
	 * @param player
	 */
	public void calculateRemovalAmount(Player player) {
		progress.get(player).set("forceremovecount", min);
		// If they already have that amount or less, don't force
		if(amountType.equals(AmountType.UNTIL)) {
			int amount = min - player.getHand().size();
			amount = amount > 0 ? 0 : Math.abs(amount);
			progress.get(player).set("forceremovecount", amount);
			player.getGame().getGameServer().getUtils().debug(player.getDisplayname() + " has to discard " + amount + " cards");
		}
	}

	@Override
	public JSONObject play(Turn turn) {
		progress.put(turn.getPlayer(), new ActionProgress());
		progress.get(turn.getPlayer()).set("removed", 0);
		calculateRemovalAmount(turn.getPlayer());
		
		// If the action affects more people than the person that played the card
		if(getTarget().equals(ActionTarget.EVERYONE) || getTarget().equals(ActionTarget.OTHERS)) {
			turn.getGame().getGameServer().getUtils().debug("Playing multi-target card");
			targeted.put(turn.getGame(), new TargetedAction(turn.getPlayer(), this));
			for(Player p : getTargetedAction(turn).getPlayers()) {
				progress.put(p, new ActionProgress());
				progress.get(p).set("removed", 0);
				calculateRemovalAmount(p);
			}
		}
		return getResponse(turn);
	}
	
	@Override
	public JSONObject selectCard(Turn turn, Card card) {
		Player player = getTargetedAction(turn) == null ? turn.getPlayer() : getTargetedAction(turn).getCurrentPlayer();
		return selectCard(turn, card, player);
	}
	
	public TargetedAction getTargetedAction(Turn turn) {
		if(targeted.containsKey(turn.getGame())) {
			return targeted.get(turn.getGame());
		}
		return null;
	}
	
	public JSONObject selectCard(Turn turn, Card card, Player player) {
		if(isRestricted() && !getPermitted().contains(card)) {
			return turn.getGame().getGameServer().getGameManager().getInvalid("Cannot select that card, it is resricted");
		}
		turn.getGame().getGameServer().getUtils().debug("Player " + player.getDisplayname() + " selected " + card.getName());
		removeCard(player, card);
		for(Action action : getCallbacks()) {
			action.setMaster(player, card);
			JSONObject played = action.play(turn);
			if(!action.isCompleted(turn)) {
				turn.getGame().getGameServer().getUtils().debug("Card remove is going into a sub action");
				return played;
			}
		}
		return finish(turn, player);
	}
	
	@Override
	public JSONObject finish(Turn turn) {
		if(getTargetedAction(turn) != null) {
			return finish(turn, getTargetedAction(turn).getCurrentPlayer());
		}
		return finish(turn, turn.getPlayer());
	}
	
	@Override
	public JSONObject finish(Turn turn, Player player) {
		if(getTargetedAction(turn) != null && !canSelectMore(player)) {
			getTargetedAction(turn).completeForCurrentPlayer();
			turn.getGame().getGameServer().getUtils().debug("The subturn for " + player.getDisplayname() + " has been completed");
		}
		if(max != 0 && getRemovedCards(player) >= max && isCompleted(turn)) {
			turn.getGame().getGameServer().getUtils().debug("The action has been fully completed");
			return turn.stopAction();
		}
		return getResponse(turn);
	}
	
	@Override
	public boolean isCompleted(Turn turn) {
		boolean completed = false;
		if(getTargetedAction(turn) == null) {
			completed = true;
		} else {
			if(getTargetedAction(turn).isDone()) {
				completed = true;
			}
		}
		return completed;
	}
	
	public void removeCard(Player player, Card card) {
		switch(type) {
			case TRASH:
				player.trashCard(card);
				break;
			case DISCARD:
			default:
				player.discardCard(card);
				break;
		}
		progress.get(player).set("removed", (getRemovedCards(player) + 1));
	}
	
	public int getRemovedCards(Player player) {
		return progress.get(player).getInteger("removed");
	}
	
	public boolean hasForceSelect(Player player) {
		return progress.get(player).getInteger("forceremovecount") > 0;
	}
	
	public boolean canSelectMore(Player player) {
		if(amountType.equals(AmountType.UNTIL)) {
			player.getGame().getGameServer().getUtils().debug(player.getDisplayname() + " has removed "+ getRemovedCards(player) +" of minimum " + progress.get(player).getInteger("forceremovecount") + " cards");
			return getRemovedCards(player) < progress.get(player).getInteger("forceremovecount");
		}		
		player.getGame().getGameServer().getUtils().debug(player.getDisplayname() + " has removed " + getRemovedCards(player) + " of " + max + " allowed cards");
		return this.max == 0 || getRemovedCards(player) <= this.max;
	}
	
	public JSONObject getResponse(Turn turn) {
		JSONObject response = new JSONObject().accumulate("response", "OK");
		Player player = turn.getPlayer();
		if(getTargetedAction(turn) != null) {
			player = getTargetedAction(turn).isDone() ? null : getTargetedAction(turn).getCurrentPlayer();
			turn.getGame().getGameServer().getUtils().debug("Multi targeted action, player is: " + player == null ? "null" : player.getDisplayname());
		}
		
		if(player != null && canSelectMore(player)) {
			response.accumulate("result", ActionResult.SELECT_CARD_HAND);
			response.accumulate("force", hasForceSelect(player));
			response.accumulate("min", progress.get(player).getInteger("forceremovecount"));
			response.accumulate("max", amountType.equals(AmountType.UNTIL) ? progress.get(player).getInteger("forceremovecount") : max);
			response.accumulate("player", player.getDisplayname());
			response.accumulate("message", getDescripton());
		} else {
			response.accumulate("result", ActionResult.DONE);
		}
		return response;
	}
	
}