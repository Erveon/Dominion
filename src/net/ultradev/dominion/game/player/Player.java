package net.ultradev.dominion.game.player;

import java.util.List;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.card.Card;

public class Player {
	
	private String displayname;
	
	private List<Card> discard;
	private List<Card> deck;
	private List<Card> hand;
	
	public Player(String displayname) {
		this.displayname = displayname;
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
	
	public JSONObject getAsJson() {
		return new JSONObject()
				.accumulate("displayname", getDisplayname());
	}
	
}
