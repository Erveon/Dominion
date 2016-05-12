package net.ultradev.dominion.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.Supply;
import net.ultradev.dominion.game.player.Player;

public class Board {
	
	public enum SupplyType { ACTION, VICTORY, CURSE, TREASURE }
		
	// Define all of those with the same type
	public Map<SupplyType, Supply> supplies;
	private List<Card> trash;
	private Game game;
	private List<Card> playedcards;
	
	public Board(Game game) {
		this.game = game;
		supplies = new HashMap<>();
		Stream.of(SupplyType.values()).forEach(type -> supplies.put(type, new Supply()));
		trash = new ArrayList<>();
		playedcards = new ArrayList<>();
	}
	
	public Game getGame() {
		return game;
	}
	
	public void addTrash(Card card) {
		trash.add(card);
	}
	
	public Supply getSupply(SupplyType type) {
		return supplies.get(type);
	}
	
	public SupplyType getSupplyTypeForCard(Card card) {
		// Gardens is a victory card that's actually an action card
		if(card.getName().equalsIgnoreCase("gardens")) {
			return SupplyType.ACTION;
		}
		return SupplyType.valueOf(card.getType().toString());
	}
	
	public boolean hasEndCondition() {
		int emptyActionPiles = 0;
		for(int count : getSupply(SupplyType.ACTION).getCards().values()) {
			if(count == 0) {
				emptyActionPiles++;
			}
		}
		if(emptyActionPiles >= 3) {
			return true;
		}
		// If there's enough piles left, whether the province supply ran out
		// or not will determine if there's an end condition. If it's not empty, the game goes on. (false)
		return getSupply(SupplyType.VICTORY).getCards().get(getGame().getGameServer().getCardManager().get("province")) == 0;
	}
	
	/**
	 * Called when the game has been configured (the playercount is known)
	 * @param playercount
	 */
	public void initSupplies(int playercount) {
		// Treasure supply (Coppers - 7 per speler)
		int coppers = 60 - (7 * playercount);
		getSupply(SupplyType.TREASURE).add(getGame().getGameServer().getCardManager().get("copper"), coppers);
		getSupply(SupplyType.TREASURE).add(getGame().getGameServer().getCardManager().get("silver"), 40);
		getSupply(SupplyType.TREASURE).add(getGame().getGameServer().getCardManager().get("gold"), 30);
		
		// Victory supply (is the playercount 2? have 8, else 12)
		int victoryamount = (playercount == 2 ? 8 : 12);
		getSupply(SupplyType.VICTORY).add(getGame().getGameServer().getCardManager().get("estate"), victoryamount);
		getSupply(SupplyType.VICTORY).add(getGame().getGameServer().getCardManager().get("duchy"), victoryamount);
		getSupply(SupplyType.VICTORY).add(getGame().getGameServer().getCardManager().get("province"), victoryamount);
		
		// Curse supply (2 = 10, 3 = 20, 4 = 30)
		int curseamount = (Math.max(playercount, 2) - 1) * 10;
		getSupply(SupplyType.CURSE).add(getGame().getGameServer().getCardManager().get("curse"), curseamount);
		
		// Action supply
		for(String cardid : getGame().getConfig().getActionCards()) {
			Card c = getGame().getGameServer().getCardManager().get(cardid);
			getSupply(SupplyType.ACTION).add(c, 10);
		}
	}
	
	// Kingdom cards
	public void addActionCard(Card card) {
		getSupply(SupplyType.ACTION).add(card, 10);
	}
	
	public void removeFromSupply(Card card, SupplyType which) {
		getSupply(which).removeOne(card);
	}

	public List<JSONObject> getPlayedCards() {
		List<JSONObject> json = new ArrayList<>();
		playedcards.forEach(card -> json.add(card.getAsJson()));
		return json;
	}
	
	public void cleanup(Player p) {
		playedcards.forEach(card -> p.getDiscard().add(card));
		playedcards.clear();
	}
	
	public void addPlayedCard(Card c) {
		playedcards.add(c);
	}
	
	public JSONObject getAsJson() {
		return new JSONObject()
				.accumulate("action", getSupply(SupplyType.ACTION).getAsJson())
				.accumulate("treasure", getSupply(SupplyType.TREASURE).getAsJson())
				.accumulate("victory", getSupply(SupplyType.VICTORY).getAsJson())
				.accumulate("curse", getSupply(SupplyType.CURSE).getAsJson())
				.accumulate("trash", trash)
				.accumulate("playedcards", getPlayedCards());
	}

}
