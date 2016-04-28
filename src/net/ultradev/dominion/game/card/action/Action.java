package net.ultradev.dominion.game.card.action;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Turn;

public abstract class Action {

	public enum ActionTarget { EVERYONE, OTHERS, SELF };
	
	private List<Action> callbacks;
	private String identifier, description;
	private ActionTarget target;
	
	public Action(String identifier, String description, ActionTarget target) {
		this.identifier = identifier;
		this.description = description;
		this.callbacks = new ArrayList<>();
		this.target = target;
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
	
	public abstract JSONObject play(Turn turn);
	
	
}
