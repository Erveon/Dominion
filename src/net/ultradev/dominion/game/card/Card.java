package net.ultradev.dominion.game.card;

import java.util.ArrayList;
import java.util.List;

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
	int cost;
	String[] description;
	List<Action> actions;
	List<String> subtypes;
	
	public Card(String name, String[] description, int cost) {
		this.name = name;
		this.description = description;
		this.cost = cost;
		this.actions = new ArrayList<>();
		this.subtypes = new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}
	
	public int getCost() {
		return cost;
	}
	
	public String[] getDescription() {
		return description;
	}
	
	public List<Action> getActions() {
		return actions;
	}
	
	public void addAction(Action action) {
		this.actions.add(action);
	}
	
	public List<String> getSubtypes() {
		return subtypes;
	}
	
	public void addSubtype(String subtype) {
		this.subtypes.add(subtype);
	}

}
