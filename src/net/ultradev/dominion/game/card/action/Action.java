package net.ultradev.dominion.game.card.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Turn;

public abstract class Action {

	List<Action> callbacks;
	private String identifier, description;
	
	public Action(String identifier, String description) {
		this.identifier = identifier;
		this.description = description;
		this.callbacks = new ArrayList<>();
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
	
	public void addCallback(Action action) {
		callbacks.add(action);
	}
	
	public abstract JSONObject play(Turn turn, HttpSession session);
	
	
}
