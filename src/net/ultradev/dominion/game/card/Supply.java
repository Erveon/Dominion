package net.ultradev.dominion.game.card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

public class Supply {

	Map<Card, Integer> cards;
	
	public Supply() {
		cards = new HashMap<>();
	}
	
	public Map<Card, Integer> getCards() {
		return cards;
	}
	
	public void add(Card card, int amount) {
		cards.put(card, amount);
	}
	
	public void removeOne(Card card) {
		if(cards.containsKey(card)) {
			int amount = cards.get(card) - 1;
			if(amount != 0) {
				cards.put(card, amount);
			}
		}
	}
	
	public List<JSONObject> getAsJson() {
		List<JSONObject> json = new ArrayList<>();
		for(Entry<Card, Integer> pile : getCards().entrySet()) {
			json.add(pile.getKey().getAsJson().accumulate("amount", pile.getValue()));
		}
		return json;
	}
	
}
