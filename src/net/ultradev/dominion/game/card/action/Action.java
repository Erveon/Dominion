package net.ultradev.dominion.game.card.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.player.Player;

public abstract class Action {

	public enum ActionTarget { EVERYONE, OTHERS, SELF };
	
	private List<Action> callbacks;
	private String identifier, description;
	private ActionTarget target;
	
	/**
	 * Keeps track of which card triggered the action for subactions
	 */
	private Map<Player, Card> masters;
	
	public Action(String identifier, String description, ActionTarget target) {
		this.identifier = identifier;
		this.description = description;
		this.callbacks = new ArrayList<>();
		this.target = target;
		this.masters = new HashMap<>();
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public String getDescripton() {
		return description;
	}
	
	public List<Action> getCallbacks() {
		return callbacks;
	}
	
	public ActionTarget getTarget() {
		return target;
	}
	
	public void addCallback(Action action) {
		callbacks.add(action);
	}

	public void setMaster(Player player, Card card) {
		masters.put(player, card);
	}
	
	public boolean hasMaster(Player player) {
		return masters.containsKey(player);
	}
	
	public Card getMaster(Player player) {
		return masters.get(player);
	}
	
	public void removeMaster(Player player) {
		if(hasMaster(player)) {
			masters.remove(player);
		}
	}
	
	/**
	 * @param turn Used when breaking out of a slave action
	 * @return 
	 */
	public JSONObject finish(Turn turn) { 
		return new JSONObject()
					.accumulate("response", "OK")
					.accumulate("result", ActionResult.DONE);
	}
		
	public abstract JSONObject play(Turn turn);
	
	
}
