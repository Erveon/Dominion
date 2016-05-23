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
import net.ultradev.dominion.game.player.Player.Pile;

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
		trash = new ArrayList<>();
		playedcards = new ArrayList<>();
		// Make a new supply for every type
		Stream.of(SupplyType.values()).forEach(type -> supplies.put(type, new Supply()));
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
		SupplyType type = SupplyType.valueOf(card.getType().toString());
		if(card.getName().equalsIgnoreCase("gardens")) {
			type = SupplyType.ACTION;
		}
		return type;
	}
	
	public boolean hasEndCondition() {
		int emptyActionPiles = 0;
		for(int cardAmount : getSupply(SupplyType.ACTION).getCards().values()) {
			if(cardAmount == 0) {
				emptyActionPiles++;
			}
		}
		if(emptyActionPiles >= 3) {
			return true;
		}
		// If there's enough piles left, whether the province supply ran out
		// or not will determine if there's an end condition. If it's not empty, the game goes on. (false)
		return getSupply(SupplyType.VICTORY).getCards().get(getCard("province")) == 0;
	}
	
	/**
	 * Called when the game has been configured (the playercount is known)
	 * @param playercount
	 */
	public void initSupplies() {
		int playercount = getGame().getPlayers().size();
		
		// Treasure supply (Coppers - 7 per speler)
		int coppers = 60 - (7 * playercount);
		getSupply(SupplyType.TREASURE).add(getCard("copper"), coppers);
		getSupply(SupplyType.TREASURE).add(getCard("silver"), 40);
		getSupply(SupplyType.TREASURE).add(getCard("gold"), 30);
		
		// Victory supply (is the playercount 2? have 8, else 12)
		int victoryamount = (playercount == 2 ? 8 : 12);
		getSupply(SupplyType.VICTORY).add(getCard("estate"), victoryamount);
		getSupply(SupplyType.VICTORY).add(getCard("duchy"), victoryamount);
		getSupply(SupplyType.VICTORY).add(getCard("province"), victoryamount);
		
		// Curse supply (2 = 10, 3 = 20, 4 = 30)
		int curseamount = (Math.max(playercount, 2) - 1) * 10;
		getSupply(SupplyType.CURSE).add(getCard("curse"), curseamount);
		
		// Action supply
		for(String cardid : getGame().getConfig().getActionCards()) {
			getSupply(SupplyType.ACTION).add(getCard(cardid), 10);
		}
	}
	
	// Kingdom cards
	public void addActionCard(Card card) {
		if(card.getName().equalsIgnoreCase("gardens")) {
			int amount = getGame().getPlayers().size() == 2 ? 8 : 12;
			getSupply(SupplyType.ACTION).add(card, amount);
		} else {
			getSupply(SupplyType.ACTION).add(card, 10);
		}
	}

	public List<JSONObject> getPlayedCards() {
		List<JSONObject> json = new ArrayList<>();
		playedcards.forEach(card -> json.add(card.getAsJson()));
		return json;
	}
	
	public void cleanup(Player p) {
		playedcards.forEach(card -> p.getPile(Pile.DISCARD).add(card));
		playedcards.clear();
	}
	
	public void addPlayedCard(Card c) {
		playedcards.add(c);
	}
	
	public Card getCard(String cardid) {
		return getGame().getGameServer().getCardManager().get(cardid);
	}
	
	public JSONObject getAsJson() {
		JSONObject response = new JSONObject();
		for(SupplyType type : SupplyType.values()) {
			response.accumulate(type.toString().toLowerCase(), getSupply(type).getAsJson());
		}
		return response
				.accumulate("trash", trash)
				.accumulate("playedcards", getPlayedCards());
	}

}
