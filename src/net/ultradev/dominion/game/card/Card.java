package net.ultradev.dominion.game.card;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.card.action.Action;

public class Card {
	
	/***********
	 * 
	 * CREATE:
	 * 
	 * actioncard
	 * treasure
	 * victory
	 * curse
	 * 
	 ************/
	
	String name;
	String description;
	int cost;
	List<Action> actions;
	List<String> types;
	
	public Card(String name, String description, int cost) {
		this.name = name;
		this.cost = cost;
		this.description = description;
		this.actions = new ArrayList<>();
		this.types = new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}
	
	public int getCost() {
		return cost;
	}
	
	public String getDescription() {
		return description;
	}
	
	public List<Action> getActions() {
		return actions;
	}
	
	public void addAction(Action action) {
		this.actions.add(action);
	}
	
	public List<String> getTypes() {
		return types;
	}
	
	public void addType(String type) {
		this.types.add(type);
	}
	
	public String getTypesFormatted() {
		boolean first = true;
		StringBuilder sb = new StringBuilder();
		for(String s : getTypes()) {
			if(first) {
				sb.append(s);
				first = false;
			} else
				sb.append(" - " + s);
		}
		return sb.toString();
	}
	
	private List<String> getActionDescriptions() {
		List<String> desc = new ArrayList<>();
		for(Action action : getActions())
			desc.add(action.getDescripton());
		return desc;
	}
	
	public JSONObject getAsJson() {
		return new JSONObject()
				.accumulate("name", getName())
				.accumulate("cost", getCost())
				.accumulate("type", getTypesFormatted())
				.accumulate("description", getDescription())
				.accumulate("actions", getActionDescriptions());
	}

}
