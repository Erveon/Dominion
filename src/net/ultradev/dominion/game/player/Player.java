package net.ultradev.dominion.game.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.json.JSONObject;
import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.Game;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.CardManager;

public class Player {
	
	private String displayname;
	
	private List<Card> discard;
	private List<Card> deck;
	private List<Card> hand;
	
	private int rounds;
	private Game g;
	
	/**
	 * An object that represents the player in a game
	 * @param game The game the player will be playing in
	 * @param displayname The name this player will have in this game
	 */
	public Player(Game game, String displayname) {
		this.g = game;
		this.displayname = displayname;
		this.discard = new ArrayList<>();
		this.deck = new ArrayList<>();
		this.hand = new ArrayList<>();
		this.rounds = 0;
	}
	
	/**
	 * @return The game the player is in
	 */
	public Game getGame() {
		return g;
	}
	
	/**
	 * @return The game server the player is on
	 */
	public GameServer getGameServer() {
		return getGame().getGameServer();
	}
	
	/**
	 * Sets the player's displayname
	 * @param displayname
	 */
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
	
	/**
	 * @return The player's displayname
	 */
	public String getDisplayname() {
		return displayname;
	}

	/**
	 * @return The deck pile
	 */
	public List<Card> getDeck() {
		return deck;
	}

	/**
	 * @return The hand pile
	 */
	public List<Card> getHand() {
		return hand;
	}
	
	/**
	 * @return The discard pile
	 */
	public List<Card> getDiscard() {
		return discard;
	}
	
	/**
	 * @return All the cards a player has
	 */
	public List<Card> getCards() {
		List<Card> cards = new ArrayList<Card>();
		cards.addAll(getDeck());
		cards.addAll(getHand());
		cards.addAll(getDiscard());
		return cards;
	}
	
	/**
	 * Sets the player up, giving them the starter cards
	 */
	public void setup() {
		for(int i = 0; i < 7; i++) {
			getDeck().add(getGameServer().getCardManager().get("copper"));
		}
		for(int i = 0; i < 3; i++) {
			getDeck().add(getGameServer().getCardManager().get("estate"));
		}
		this.deck = shuffle(getDeck());
		for(int i = 0; i < 5; i++) {
			drawCardFromDeck();
		}
	}
	
	/**
	 * @param cards The cards to shuffle
	 * @return The shuffled cards
	 */
	public List<Card> shuffle(List<Card> cards) {
		List<Card> shuffled = new ArrayList<>(cards);
		Collections.shuffle(shuffled);
		return shuffled;
	}
	
	/**
	 * Draws a card from the player's deck
	 */
	public void drawCardFromDeck() {
		if(getDeck().size() == 0 && getDiscard().size() != 0) {
			transferDiscardToDeck();
		}
		Card c = getDeck().remove(0);
		getHand().add(c);
	}
	
	/**
	 * Shuffles the discard pile and moves it to the deck
	 */
	public void transferDiscardToDeck() {
		this.deck = shuffle(getDiscard());
		getDiscard().clear();
	}
	
	/**
	 * Maps the sum of the victory points for this player
	 * @return The player's victory points
	 */
	public int getVictoryPoints() {
		int points = 0;
		CardManager cm = getGameServer().getCardManager();
		for(Card card : getCards()) {
			points += cm.getVictoryPointsFor(card, this);
		}
		return points;
	}
	
	/**
	 * @return The total amount of cards a player has
	 */
	public int getTotalCardCount() {
		return getCards().size();
	}

	/**
	 * Increases the amount of rounds by 1
	 */
	public void increaseRounds() {
		this.rounds++;
	}
	
	/**
	 * @return The amount of rounds that have passed
	 */
	public int getRounds() {
		return rounds;
	}
	
	/**
	 * Discards the player's hand
	 */
	public void discardHand() {
		getDiscard().addAll(getHand());
		getHand().clear();
	}
	
	/**
	 * @param cards To map to JSON
	 * @return the specified list of cards as JSON
	 */
	private List<JSONObject> getCardsAsJson(List<Card> cards) {
		List<JSONObject> json = new ArrayList<>();
		cards.forEach(card -> json.add(card.getAsJson()));
		return json;
	}
	
	/**
	 * Trashes a card
	 * @param card To be trashed
	 */
	public void trashCard(Card card) {
		if(getHand().contains(card)) {
			getHand().remove(card);
			getGame().getBoard().addTrash(card);
		}
	}

	/**
	 * Discards a card
	 * @param card To be discarded
	 */
	public void discardCard(Card card) {
		getDiscard().add(card);
		getHand().remove(card);
	}
	
	/**
	 * @return The necessary player info as JSON
	 */
	public JSONObject getAsJson() {
		return new JSONObject()
				.accumulate("displayname", getDisplayname())
				.accumulate("deck", getCardsAsJson(getDeck()))
				.accumulate("hand", getCardsAsJson(getHand()))
				.accumulate("discard", getCardsAsJson(getDiscard()));
	}
	
}
