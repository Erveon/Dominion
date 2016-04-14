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
	
	/**
	 * @param key
	 * @param value
	 * @return whether the handle is valid or not
	 */
	public boolean handle(String key, String value) {
		Option option = null;
		try { 
			option = Option.valueOf(key.toUpperCase()); 
		} catch(Exception ignored) { }
		if(option == null)
			return false;
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
		return true;
	}
	
	public void addActionCard(String actionCard) {
		if(!actionCardTypes.contains(actionCard) && actionCardTypes.size() < 10)
			actionCardTypes.add(actionCard);
	}
	
	public void removeActionCard(String actionCard) {
		if(actionCardTypes.contains(actionCard))
			actionCardTypes.remove(actionCard);
	}
	
	public List<String> getActionCards() {
		return actionCardTypes;
	}
	
	public JSONObject getAsJson() {
		return new JSONObject()
				.accumulate("actionCards", getActionCards());
	}

}