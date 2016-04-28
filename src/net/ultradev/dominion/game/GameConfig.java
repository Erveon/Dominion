package net.ultradev.dominion.game;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

public class GameConfig {
	
	public enum CardSet { TEST }
	public enum Option { ADDCARD, REMOVECARD, SETCARDSET };
	
	List<String> actionCardTypes;
	
	public GameConfig() {
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
			case SETCARDSET:
				setCardset(value);
				break;
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
	
	public boolean hasValidActionCards() {
		return actionCardTypes.size() == 10;
	}
	
	public void setCardset(String cardSet) {
		actionCardTypes.clear();
		CardSet set;
		try  {
			set = CardSet.valueOf(cardSet);
		} catch(Exception ignored) { 
			set = CardSet.TEST;
		}
		switch(set) {
			case TEST:
				addActionCards("chapel", 
						"village", 
						"woodcutter", 
						"moneylender", 
						"cellar", 
						"market", 
						"militia", 
						"mine", 
						"moat", 
						"remodel");
				break;
			default:
				break;
		}
	}
	
	public void addActionCards(String... cards) {
		for(String card : cards)
			addActionCard(card);
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