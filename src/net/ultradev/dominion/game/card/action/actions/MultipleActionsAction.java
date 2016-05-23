package net.ultradev.dominion.game.card.action.actions;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.Card.CardType;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.ActionProgress;
import net.ultradev.dominion.game.card.action.ActionResult;
import net.ultradev.dominion.game.player.Player;
import net.ultradev.dominion.game.player.Player.Pile;

public class MultipleActionsAction extends Action {

	private int times;
	private Map<Player, ActionProgress> progress;
	
	public MultipleActionsAction(String identifier, String description, ActionTarget target, int times) {
		super(identifier, description, target);
		this.times = times;
		this.progress = new HashMap<>();
	}

	@Override
	public JSONObject play(Turn turn, Card card) {
		turn.getGame().getGameServer().getUtils().debug(turn.getPlayer().getDisplayname() + " is playing a multi action card");
		setProgress(turn.getPlayer(), 0);
		return new JSONObject().accumulate("response", "OK")
				.accumulate("result", ActionResult.SELECT_CARD_HAND)
				.accumulate("player", turn.getPlayer().getDisplayname())
				.accumulate("type", CardType.ACTION)
				.accumulate("message", getDescripton())
				.accumulate("min", 0)
				.accumulate("max", 0)
				.accumulate("force", false);
	}

	@Override
	public JSONObject selectCard(Turn turn, Card card) {
		if(!card.getType().equals(CardType.ACTION)) {
			return turn.getGame().getGameServer().getGameManager().getInvalid("Selected card is not an action card");
		}
		turn.getPlayer().getPile(Pile.HAND).remove(card);
		turn.getGame().getBoard().addPlayedCard(card);
		turn.getGame().getGameServer().getUtils().debug(turn.getPlayer().getDisplayname() + " selected " + card.getName() + " for the multi action card");
		setCard(turn.getPlayer(), card);
		return finish(turn);
	}
	
	@Override
	public boolean isCompleted(Turn turn) {
		return getProgress(turn.getPlayer()) == times;
	}
	
	@Override
	public JSONObject finish(Turn turn) {
		Card card = getCard(turn.getPlayer());
		if(isCompleted(turn)) {
			turn.getGame().getGameServer().getUtils().debug(turn.getPlayer().getDisplayname() + " finished playing multi action card");
			turn.stopAction();
			progress.remove(turn.getPlayer());
			return new JSONObject()
					.accumulate("response", "OK")
					.accumulate("result", ActionResult.DONE);
		} else {
			turn.getGame().getGameServer().getUtils().debug(turn.getPlayer().getDisplayname() + " is playing '"+card.getName()+"' for the multi action card");
			turn.addActions(1);
			setProgress(turn.getPlayer(), getProgress(turn.getPlayer()) + 1);
			JSONObject response = turn.playCard(card.getName(), true);
			if(!response.get("result").equals(ActionResult.DONE)) {
				return response;
			} else {
				return finish(turn);
			}
		}
	}
	
	public void setCard(Player player, Card card) {
		if(!progress.containsKey(player)) {
			progress.put(player, new ActionProgress());
		}
		progress.get(player).set("card", card);
	}
	
	public void setProgress(Player player, int times) {
		if(!progress.containsKey(player)) {
			progress.put(player, new ActionProgress());
		}
		progress.get(player).set("times", times);
	}
	
	public int getProgress(Player player) {
		if(progress.containsKey(player)) {
			return progress.get(player).getInteger("times");
		}
		return 0;
	}
	
	public Card getCard(Player player) {
		if(progress.containsKey(player)) {
			if(progress.get(player).contains("card")) {
				return (Card) progress.get(player).get("card");
			}
		}
		return null;
	}
	
}