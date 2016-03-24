package net.ultradev.dominion.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;
import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.card.Card;

public class Board {
	
	// Define all of those with the same type
	public Map<Card, Integer> actionsupply, victorysupply, treasuresupply, cursesupply;
	private GameServer gs;
	
	public Board(GameServer gs) {
		this.gs = gs;
		actionsupply = new HashMap<>();
		victorysupply = new HashMap<>();
		treasuresupply = new HashMap<>();
		cursesupply = new HashMap<>();
	}
	
	public GameServer getGameServer() {
		return gs;
	}
	
	public boolean hasEndCondition() {
		int emptyActionPiles = 0;
		for(int count : actionsupply.values()) {
			if(count == 0)
				emptyActionPiles++;
		}
		if(emptyActionPiles >= 3)
			return true;
		// If there's enough piles left, whether the province supply ran out
		// or not will determine if there's an end condition. If it's not empty, the game goes on. (false)
		return victorysupply.get(getGameServer().getCardManager().get("province")) == 0;
	}
	
	/**
	 * Called when the game has been configured (the playercount is known)
	 * @param playercount
	 */
	public void initSupplies(int playercount) {
		// Treasure supply (Coppers - 7 per speler)
		int coppers = 60 - (7 * playercount);
		treasuresupply.put(getGameServer().getCardManager().get("copper"), coppers);
		treasuresupply.put(getGameServer().getCardManager().get("silver"), 40);
		treasuresupply.put(getGameServer().getCardManager().get("gold"), 30);
		
		// Victory supply (is the playercount 2? have 8, else 12)
		int victoryamount = (playercount == 2 ? 8 : 12);
		victorysupply.put(getGameServer().getCardManager().get("estate"), victoryamount);
		victorysupply.put(getGameServer().getCardManager().get("duchy"), victoryamount);
		victorysupply.put(getGameServer().getCardManager().get("province"), victoryamount);
		
		// Curse supply (2 = 10, 3 = 20, 4 = 30)
		int curseamount = (Math.max(playercount, 2) - 1) * 10;
		cursesupply.put(getGameServer().getCardManager().get("curse"), curseamount);
	}
	
	// Kingdom cards
	public void addActionCard(Card card) {
		actionsupply.put(card, 10);
	}
	
	/**
	 * Removes a single card from a supply
	 * @param supply that will be altered
	 * @param card
	 * @return The updated supply
	 */
	public Map<Card, Integer> removeOneFrom(Map<Card, Integer> supply, Card card) {
		if(!supply.containsKey(card))
			return supply;
		supply.put(card, supply.get(card) - 1);
		return supply;
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
