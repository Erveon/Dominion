package net.ultradev.dominion.game;

import net.ultradev.dominion.game.card.Action;
import net.ultradev.dominion.game.player.Player;

public class SubTurn {
	
	private Player player;
	private Turn turn;
	private Action action;
	
	public SubTurn(Turn turn, Action action) {
		this.turn = turn;
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
	
	public void end() {
		
	}

}
