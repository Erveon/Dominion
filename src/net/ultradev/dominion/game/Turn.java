package net.ultradev.dominion.game;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.local.LocalGame;
import net.ultradev.dominion.game.player.Player;

public class Turn {
	
	private LocalGame game;
	private Player player;
	private int buycount;
	private int actioncount;
	
	public Turn(LocalGame game, Player player) {
		this.game = game;
		this.player = player;
		this.buycount = 1;
		this.actioncount = 1;
	}
	
	public LocalGame getGame() {
		return game;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void addBuys(int amount) {
		this.buycount += amount;
	}
	
	public void addActions(int amount) {
		this.actioncount += amount;
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
		p.getDiscard().addAll(p.getHand());
		p.getHand().clear();
		for(int i = 0; i < 5; i++) {
			// Draws a card and fires when the deck is empty
			if(!p.drawCardFromDeck()) {
				p.transferDiscardToDeck();
				p.drawCardFromDeck();
			}
		}
	}
	
	/**
	 * When an action has to be performed by a player, resulting from an actioncard played
	 */
	public void createSubturns() {
		//TODO Maybe define which action as parameter?
	}
	
	public JSONObject getAsJson() {
		return new JSONObject()
				.accumulate("player", getPlayer().getDisplayname())
				.accumulate("buysleft", getBuys())
				.accumulate("actionsleft", getActions());
	}
	
}
