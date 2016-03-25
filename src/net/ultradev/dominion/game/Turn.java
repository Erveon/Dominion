package net.ultradev.dominion.game;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.local.LocalGame;
import net.ultradev.dominion.game.player.Player;

public class Turn {
	
	public enum Phase { ACTION, BUY, CLEANUP };
	
	private LocalGame game;
	private Player player;
	private int buycount;
	private int actioncount;
	private int buypower;
	private int buypowerMultiplier;
	private Phase phase;

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
				.accumulate("actionsleft", getActions())
				.accumulate("buypower", getBuypower())
				.accumulate("phase", getPhase().toString());
	}
	
}
