package net.ultradev.dominion.game; 

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.CardManager;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.ActionResult;
import net.ultradev.dominion.game.card.action.actions.RemoveCardAction;
import net.ultradev.dominion.game.local.LocalGame;
import net.ultradev.dominion.game.player.Player; 

public class Turn {
	
	public enum Phase { ACTION, BUY, CLEANUP };
	private enum BuyResponse { CANTAFFORD, BOUGHT };
	
	private LocalGame game;
	private Player player;
	private int buycount;
	private int actioncount;
	private int buypower;
	private int buypowerMultiplier;
	private Phase phase;
	
	Card activeCard;
	Action activeAction;

	public Turn(LocalGame game, Player player) {
		this.game = game;
		this.player = player;
		this.buycount = 1;
		this.actioncount = 1;
		this.buypower = 0;
		this.buypowerMultiplier = 1;
		this.phase = Phase.ACTION;
	}
	
	public LocalGame getGame() {
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
	}
	
	public void endPhase() {
		switch(getPhase()) {
			case ACTION:
				this.actioncount = 0;
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
		if(!canPerform(Phase.BUY, cardid))
			return getGame().getGameServer().getGameManager()
					.getInvalid("Unable to perform purchase. (Not in the right phase ("+phase.toString()+") or card '"+cardid+"' is invalid");
		
		JSONObject response = new JSONObject().accumulate("response", "OK");
		CardManager cm = getGame().getGameServer().getCardManager();
		Card card = cm.get(cardid);
		
		if(getBuypower() >= card.getCost()) {
			getPlayer().getDeck().add(card);
			removeBuy();
			return response.accumulate("result", BuyResponse.BOUGHT);
		}
		// In other cases
		return response.accumulate("result", BuyResponse.CANTAFFORD);
	}
	
	public JSONObject playCard(String cardid) {
		if(!canPerform(Phase.ACTION, cardid))
			return getGame().getGameServer().getGameManager()
					.getInvalid("Unable to perform action. (Not in the right phase ("+phase.toString()+") or card '"+cardid+"' is invalid)");
		
		Card card = getGame().getGameServer().getCardManager().get(cardid);
		JSONObject response = playActions(card);
		
		return response;
	}
	
	protected boolean canPerform(Phase phase, String cardid) {
		if(!getPhase().equals(phase))
			return false;
		CardManager cm = getGame().getGameServer().getCardManager();
		if(!cm.exists(cardid))
			return false;
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
		this.phase = Phase.ACTION;
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
		if(!canPerform(Phase.ACTION, cardid))
			return gm.getInvalid("Unable to perform action. (Not in the right phase or card '"+cardid+"' is invalid)");
		
		Card card = getGame().getGameServer().getCardManager().get(cardid);
		
		if(action == null)
			return gm.getInvalid("Unable to select card, no active action");
		JSONObject response = handleCardSelection(card, action);
		return response;
	}
	
	private JSONObject handleCardSelection(Card card, Action action) {
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
