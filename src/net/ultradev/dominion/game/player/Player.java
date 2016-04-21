package net.ultradev.dominion.game.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.json.JSONObject;
import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.local.LocalGame;

public class Player {
	
	private String displayname;
	
	private List<Card> discard;
	private List<Card> deck;
	private List<Card> hand;
	
	private int rounds;
	private LocalGame g;
	
	public Player(LocalGame g, String displayname) {
		this.g = g;
		this.displayname = displayname;
		this.discard = new ArrayList<>();
		this.deck = new ArrayList<>();
		this.hand = new ArrayList<>();
		this.rounds = 0;
	}
	
	public LocalGame getGame() {
		return g;
	}
	
	public GameServer getGameServer() {
		return getGame().getGameServer();
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
			getDeck().add(getGameServer().getCardManager().get("copper"));
		for(int i = 0; i < 3; i++)
			getDeck().add(getGameServer().getCardManager().get("estate"));
		this.deck = shuffle(getDeck());
		
		for(int i = 0; i < 5; i++)
			drawCardFromDeck();
	}
	
	public List<Card> shuffle(List<Card> cards) {
		Collections.shuffle(cards);
		return cards;
	}
	
	/**
	 * @return if the draw works
	 */
	public void drawCardFromDeck() {
		if(getDeck().size() == 0 && getDiscard().size() != 0) {
			transferDiscardToDeck();
			drawCardFromDeck();
		}
		Card c = getDeck().remove(0);
		getHand().add(c);
	}
	
	public void transferDiscardToDeck() {
		this.deck = shuffle(getDiscard());
		getDiscard().clear();
	}
	
	public int getVictoryPoints() {
		int points = 0;
		for(Card c : getHand())
			points += getGameServer().getCardManager().getVictoryPointsFor(c, this);
		return points;
	}
	
	public int getTotalCardCount() {
		return getHand().size() + getDeck().size() + getDiscard().size();
	}

	public void increaseRounds() {
		this.rounds++;
	}
	
	public int getRounds() {
		return rounds;
	}
	
	public void discardHand() {
		getDiscard().addAll(getHand());
		getHand().clear();
	}
	
	private List<JSONObject> getCardsAsJson(List<Card> cards) {
		List<JSONObject> json = new ArrayList<>();
		for(Card c : cards)
			json.add(c.getAsJson());
		return json;
	}
	
	public void trashCard(Card card) {
		if(getHand().contains(card)) {
			getHand().remove(card);
			getGame().getBoard().addTrash(card);
		}
	}

	public void discardCard(Card card) {
		getDiscard().add(card);
		getHand().remove(card);
	}
	
	public JSONObject getAsJson() {
		return new JSONObject()
				.accumulate("displayname", getDisplayname())
				.accumulate("deck", getCardsAsJson(getDeck()))
				.accumulate("hand", getCardsAsJson(getHand()))
				.accumulate("discard", getCardsAsJson(getDiscard()));
	}
	
}
