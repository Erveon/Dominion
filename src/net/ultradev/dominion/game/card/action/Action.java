package net.ultradev.dominion.game.card.action;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Turn;

public abstract class Action {

	private String identifier, description;
	
	public Action(String identifier, String description) {
		this.identifier = identifier;
		this.description = description;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public String getDescripton() {
		return description;
	}
	
	public abstract JSONObject play(Turn turn, HttpSession session);
	
}
