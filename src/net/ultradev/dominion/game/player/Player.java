package net.ultradev.dominion.game.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.CardManager;

public class Player {
	
	private String displayname;
	
	private List<Card> discard;
	private List<Card> deck;
	private List<Card> hand;
	
	public Player(String displayname) {
		this.displayname = displayname;
		this.discard = new ArrayList<>();
		this.deck = new ArrayList<>();
		this.hand = new ArrayList<>();
	}
	
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
	
	public String getDisplayname() {
		return displayname;
	}
	
	public List<Card> getDeck() {
		return deck;
	}
	
	public List<Card> getHand() {
		return hand;
	}
	
	public List<Card> getDiscard() {
		return discard;
	}
	
	public void setup() {
		for(int i = 0; i < 7; i++)
			getDeck().add(CardManager.get("copper"));
		for(int i = 0; i < 3; i++)
			getDeck().add(CardManager.get("estate"));
		this.deck = shuffle(getDeck());
		
		for(int i = 0; i < 5; i++)
			drawCardFromDeck();
	}
	
	public List<Card> shuffle(List<Card> cards) {
		Collections.shuffle(cards);
		return cards;
	}
	
	public void drawCardFromDeck() {
		Card c = getDeck().remove(0);
		getHand().add(c);
	}
	
	private List<JSONObject> getCardsAsJson(List<Card> cards) {
		List<JSONObject> json = new ArrayList<>();
		for(Card c : cards)
			json.add(c.getAsJson());
		return json;
	}
	
	public JSONObject getAsJson() {
		return new JSONObject()
				.accumulate("displayname", getDisplayname())
				.accumulate("deck", getCardsAsJson(getDeck()))
				.accumulate("hand", getCardsAsJson(getHand()))
				.accumulate("discard", getCardsAsJson(getDiscard()));
	}
	
}
