package net.ultradev.dominion.game; 

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.Card.CardType;
import net.ultradev.dominion.game.card.CardManager;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.ActionResult;
import net.ultradev.dominion.game.card.action.actions.RemoveCardAction;
import net.ultradev.dominion.game.player.Player; 

public class Turn {
	
	public enum Phase { ACTION, BUY, CLEANUP };
	private enum BuyResponse { CANTAFFORD, BOUGHT };
	
	private Game game;
	private Player player;
	private int buycount;
	private int actioncount;
	private int buypower;
	private int buypowerMultiplier;
	private Phase phase;
	
	Card activeCard;
	Action activeAction;

	public Turn(Game game, Player player) {
		this.game = game;
		this.player = player;
		this.buycount = 1;
		this.actioncount = 1;
		this.buypower = 0;
		this.buypowerMultiplier = 1;
		this.phase = Phase.ACTION;
	}
	
	public Game getGame() {
		return game;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Phase getPhase() {
		return phase;
	}
	
	public void setPhase(Phase phase) {
		this.phase = phase;
		if(phase.equals(Phase.CLEANUP))
			getGame().getBoard().cleanup(getPlayer());
	}
	
	public void endPhase() {
		switch(getPhase()) {
			case ACTION:
				setPhase(Phase.BUY);
				break;
			case BUY:
				end();
				setPhase(Phase.CLEANUP);
				break;
			case CLEANUP:
			default:
				break;
		}
	}
	
	public void addBuys(int amount) {
		this.buycount += amount;
	}
	
	public void removeBuy() {
		this.buycount--;
		if(this.buycount == 0)
			endPhase();
	}
	
	public void removeAction() {
		this.actioncount--;
		if(this.actioncount == 0)
			endPhase();
	}
	
	public void addActions(int amount) {
		this.actioncount += amount;
	}
	
	public void addBuypower(int amount) {
		this.buypower += amount;
	}
	
	public void addMultiplierBuypower(int amount) {
		this.buypowerMultiplier += amount;
	}
	
	public void removeBuypower(int amount) {
		this.buypower -= amount;
	}
	
	public int getBuypower() {
		return buypower * buypowerMultiplier;
	}
	
	public int getBuys() {
		return buycount;
	}
	
	public int getActions() {
		return actioncount;
	}
	
	/**
	 * Gets the next player clockwise.
	 * Checks every player for the player we want the next from, and returns the one after that.
	 * If none is returned, then it means the next is the first player in the array.
	 * @param p The player we want the next from
	 * @return The next player
	 */
	public Player getNext() {
		boolean found = false;
		for(Player pl : getGame().getPlayers()) {
			if(found)
				return pl;
			if(getPlayer().equals(pl))
				found = true;
		}
		return getGame().getPlayers().get(0);
	}
	
	public Turn getNextTurn() {
		return new Turn(getGame(), getNext());
	}

	public void end() {
		Player p = getPlayer();
		p.discardHand();
		for(int i = 0; i < 5; i++)
			p.drawCardFromDeck();
	}
	
	public JSONObject buyCard(String cardid) {
		if(!phase.equals(Phase.BUY) || !isValidCard(cardid))
			return getGame().getGameServer().getGameManager()
					.getInvalid("Unable to perform purchase. (Not in the right phase ("+phase.toString()+") or card '"+cardid+"' is invalid");
		
		JSONObject response = new JSONObject().accumulate("response", "OK");
		CardManager cm = getGame().getGameServer().getCardManager();
		Card card = cm.get(cardid);
		
		if(getBuypower() >= card.getCost()) {
			getPlayer().getDeck().add(card);
			removeBuy();
			removeBuypower(card.getCost());
			getGame().getBoard().getSupply(CardType.ACTION).removeOne(card);
			return response.accumulate("result", BuyResponse.BOUGHT);
		}
		// In other cases
		return response.accumulate("result", BuyResponse.CANTAFFORD);
	}
	
	public JSONObject playCard(String cardid) {
		GameManager gm = getGame().getGameServer().getGameManager();
		if(!canPlay(getPhase(), cardid)) {
			return gm.getInvalid("Unable to perform action. (Not in the right phase ("+phase.toString()+") or card '"+cardid+"' is invalid)");
		}
		
		Card card = getGame().getGameServer().getCardManager().get(cardid);
		
		if(!getPlayer().getHand().contains(card)) {
			return gm.getInvalid("Player doesn't have the selected card in their hand");
		}

		getGame().getGameServer().getUtils().debug("Card played: " + card.getName());
		getPlayer().getHand().remove(card);
		if(getPhase().equals(Phase.ACTION)) {
			removeAction();
			if(getActions() == 0) {
				endPhase();
			}
		}
		getGame().getBoard().addPlayedCard(card);
		JSONObject response = playActions(card);
		return response;
	}
	
	private boolean isValidCard(String cardid) {
		return getGame().getGameServer().getCardManager().exists(cardid);
	}
	
	private boolean canPlay(Phase phase, String cardid) {
		CardManager cm = getGame().getGameServer().getCardManager();
		if(!cm.exists(cardid))
			return false;
		Card card = cm.get(cardid);
		if(card.getType().equals(CardType.VICTORY)
				|| cm.get(cardid).getType().equals(CardType.CURSE))
			return false;
		else if(card.getType().equals(CardType.TREASURE))
			return phase.equals(Phase.BUY);
		else if(card.getType().equals(CardType.ACTION))
			return phase.equals(Phase.ACTION);
		return true;
	}
	
	private JSONObject playActions(Card card) {
		this.activeCard = card;
		for(Action action : card.getActions()) {
			JSONObject actionResponse = action.play(this);
			ActionResult result = ActionResult.valueOf(actionResponse.get("result").toString());
			if(!result.equals(ActionResult.DONE)) {
				return actionResponse;
			}
		}
		return new JSONObject().accumulate("response", "OK")
							   .accumulate("result", ActionResult.DONE);
	}
	
	/**
	 * Only used when in a sub action where a player has to select a card
	 * @param cardid
	 * @return response
	 */
	public JSONObject selectCard(String cardid) {
		Action action = getActiveAction();
		GameManager gm = getGame().getGameServer().getGameManager();
		if(!phase.equals(Phase.ACTION) || !isValidCard(cardid))
			return gm.getInvalid("Unable to perform action. (Not in the right phase or card '"+cardid+"' is invalid)");
		
		Card card = getGame().getGameServer().getCardManager().get(cardid);
		
		if(action == null)
			return gm.getInvalid("Unable to select card, no active action");
		JSONObject response = handleCardSelection(card, action);
		return response;
	}
	
	private JSONObject handleCardSelection(Card card, Action action) {
		getGame().getGameServer().getUtils().debug("Card selected: " + card.getName());
		if(action instanceof RemoveCardAction) {
			RemoveCardAction tca = (RemoveCardAction) action;
			return tca.selectCard(this, card);
		}
		return getGame().getGameServer().getGameManager().getInvalid("Action '"+ action.getIdentifier() +"' does not handle card selections");
	}
	
	public Card getActiveCard() {
		return activeCard;
	}
	
	public Action getActiveAction() {
		return activeAction;
	}
	
	public JSONObject getAsJson() {
		return new JSONObject()
				.accumulate("player", getPlayer().getDisplayname())
				.accumulate("buysleft", getBuys())
				.accumulate("actionsleft", getActions())
				.accumulate("buypower", getBuypower())
				.accumulate("phase", getPhase().toString());
	}
	
}
