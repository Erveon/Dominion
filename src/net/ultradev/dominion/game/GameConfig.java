package net.ultradev.dominion.game;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

public class GameConfig {
	
	public enum Option { ADDCARD, REMOVECARD };
	
	List<String> actionCardTypes;
	
	public GameConfig() {
		//Default values
		this.actionCardTypes = new ArrayList<>();
	}
	
	public void handle(String key, String value) {
		Option option = Option.valueOf(key.toUpperCase());
		switch(option) {
			case ADDCARD:
				addActionCard(value);
				break;
			case REMOVECARD:
				removeActionCard(value);
				break;
			default:
				break;
		}
	}
	
	public void addActionCard(String actionCard) {
		if(actionCardTypes.size() < 10)
			actionCardTypes.add(actionCard);
	}
	
	public void removeActionCard(String actionCard) {
		if(actionCardTypes.contains(actionCard))
			actionCardTypes.remove(actionCard);
	}
	
	public void assignRandomActionCards() {
		//TODO Geef 10 random kaart types vanuit db
	}
	
	public List<String> getActionCards() {
		return actionCardTypes;
	}
	
	public JSONObject getAsJson() {
		return new JSONObject()
				.accumulate("actionCards", getActionCards());
	}

}