package net.ultradev.dominion.game;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Turn.Phase;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.actions.TrashCardAction;
import net.ultradev.dominion.game.player.Player;

public class SubTurn {
	
	private Player player;
	private Turn turn;
	private Action action;
	
	public SubTurn(Turn turn, Action action) {
		this.turn = turn;
		this.player = turn.getPlayer();
		this.action = action;
	}
	
	public Turn getTurn() {
		return turn;
	}
	
	public Action getAction() {
		return action;
	}
	
	public Player getPlayer() {
		return player;
	}

	/**
	 * Only used when in a sub action where a player has to select a card
	 * @param cardid
	 * @return response
	 */
	public JSONObject selectCard(String cardid) {
		GameManager gm = getTurn().getGame().getGameServer().getGameManager();
		if(!getTurn().canPerform(Phase.ACTION, cardid))
			return gm.getInvalid("Unable to perform action. (Not in the right phase or card '"+cardid+"' is invalid)");
		
		Card card = getTurn().getGame().getGameServer().getCardManager().get(cardid);
		
		if(getAction() == null)
			return gm.getInvalid("Unable to select card, no active action");
		JSONObject response = handleCardSelection(card);
		
		return response;
	}
	
	private JSONObject handleCardSelection(Card card) {
		if(getAction() instanceof TrashCardAction) {
			TrashCardAction tca = (TrashCardAction) getAction();
			return tca.selectCard(this, card);
		}
		return getTurn().getGame().getGameServer().getGameManager().getInvalid("Action '"+ action.getIdentifier() +"' does not handle card selections");
	}
	
}
