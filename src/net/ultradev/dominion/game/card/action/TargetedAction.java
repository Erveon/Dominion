package net.ultradev.dominion.game.card.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.ultradev.dominion.game.Game;
import net.ultradev.dominion.game.card.action.Action.ActionTarget;
import net.ultradev.dominion.game.player.Player;

public class TargetedAction {
	
	private Player player;
	private ActionTarget target;
	private Action action;
	
	private Game game;
	private List<Player> todo;
	private Player currentPlayer;
	
	boolean isDone;
	
	public TargetedAction(Player player, Action action) {
		this.player = player;
		this.action = action;
		this.target = action.getTarget();
		
		this.game = player.getGame();
		this.todo = new ArrayList<>();
		this.todo.addAll(player.getGame().getPlayers());
		
		switch(target) {
			case EVERYONE:
				this.currentPlayer = getNextPlayer(player);
				break;
			case OTHERS:
				this.currentPlayer = getNextPlayer(player);
				this.todo.remove(player);
				break;
			case SELF:
				this.todo = Arrays.asList(player);
				this.currentPlayer = player;
				break;
			default:
				break;
		}
		
		List<Player> immume = new ArrayList<>();
		for(Player p : todo) {
			if(p.hasReaction()) {
				immume.add(p);
			}
		}
		
		todo.removeAll(immume);
		if(todo.isEmpty()) {
			isDone = true;
		}
	}
	
	public void setPlayers(List<Player> todo) {
		this.todo = todo;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	public Action getAction() {
		return action;
	}
	
	public ActionTarget getTarget() {
		return target;
	}
	
	public Player getNextPlayer() {
		return getNextPlayer(currentPlayer);
	}
	
	public boolean isDone() {
		return isDone;
	}
	
	private Player getNextPlayer(Player player) {
		boolean found = false;
		for(Player p : todo) {
			if(!found && p.equals(player)) {
				found = true;
			} else if(found) {
				return p;
			}
		}
		// If it's not found, it means 'player' is last in the list
		// which results in the first player being the next relative player
		return game.getPlayers().get(0);
	}
	
	public List<Player> getPlayers() {
		return todo;
	}
	
	public void completeForCurrentPlayer() {
		Player current = currentPlayer;
		if(todo.contains(current)) {
			this.currentPlayer = getNextPlayer(current);
			todo.remove(current);
			if(todo.isEmpty()) {
				this.isDone = true;
			}
		}
	}

}
