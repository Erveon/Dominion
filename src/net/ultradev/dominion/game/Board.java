package net.ultradev.dominion.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.CardManager;

public class Board {
	
	// Define all of those with the same type
	public Map<Card, Integer> actionsupply, victorysupply, treasuresupply, cursesupply;
	
	public Board() {
		actionsupply = new HashMap<>();
		victorysupply = new HashMap<>();
		treasuresupply = new HashMap<>();
		cursesupply = new HashMap<>();
	}
	
	/**
	 * Called when the game has been configured (the playercount is known)
	 * @param playercount
	 */
	public void initSupplies(int playercount) {
		// Treasure supply
		treasuresupply.put(CardManager.get("copper"), 60);
		treasuresupply.put(CardManager.get("silver"), 40);
		treasuresupply.put(CardManager.get("gold"), 30);
		
		// Victory supply (is the playercount 2? have 8, else 12)
		int victoryamount = (playercount == 2 ? 8 : 12);
		victorysupply.put(CardManager.get("estate"), victoryamount);
		victorysupply.put(CardManager.get("duchy"), victoryamount);
		victorysupply.put(CardManager.get("province"), victoryamount);
		
		// Curse supply (2 = 10, 3 = 20, 4 = 30)
		int curseamount = (Math.max(playercount, 2) - 1) * 10;
		cursesupply.put(CardManager.get("curse"), curseamount);
	}
	
	// Kingdom cards
	public void addActionCard(Card card) {
		actionsupply.put(card, 25);
	}
	
	private JSONObject getSupplyAsJson(String which) {
		JSONObject json = new JSONObject();
		Map<Card, Integer> supply;
		switch(which.toLowerCase()) {
			case "action":
				supply = actionsupply;
				break;
			case "treasure":
				supply = treasuresupply;
				break;
			case "victory":
				supply = victorysupply;
				break;
			case "curse":
				supply = cursesupply;
				break;
			default:
				return json;
		}
		for(Entry<Card, Integer> pile : supply.entrySet())
			json.accumulate(pile.getKey().getName(), pile.getValue());
		return json;
	}
	
	public JSONObject getAsJson() {
		return new JSONObject()
				.accumulate("action", getSupplyAsJson("action"))
				.accumulate("treasure", getSupplyAsJson("treasure"))
				.accumulate("victory", getSupplyAsJson("victory"))
				.accumulate("curse", getSupplyAsJson("curse"));
	}

}
