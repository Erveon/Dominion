package net.ultradev.dominion.game.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.websocket.Session;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.Game;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.CardManager;

public class Player {

	public enum Pile { HAND, DECK, DISCARD }
	
	private String displayname;
	
	private List<Card> discard;
	private List<Card> deck;
	private List<Card> hand;
	
	private int rounds;
	private Game g;
	
	private Session session;
	
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
	 * The online equivalent of a player
	 */
	public Player(Game game, String displayname, Session session) {
		this(game, displayname);
		setSession(session);
	}
	
	public void setSession(Session session) {
		this.session = session;
	}
	
	public Session getSession() {
		return session;
	}
	
	/**
	 * @return The game the player is in
	 */
	public Game getGame() {
		return g;
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
	 * @param pile Which pile to return
	 * @return A list of cards in that pile
	 */
	public List<Card> getPile(Pile pile) {
		switch(pile) {
			case DECK:
				return deck;
			case DISCARD:
				return discard;
			case HAND:
				return hand;
		}
		return null;
	}
	
	/**
	 * @return All the cards a player has
	 */
	public List<Card> getCards() {
		List<Card> cards = new ArrayList<Card>();
		cards.addAll(getPile(Pile.DECK));
		cards.addAll(getPile(Pile.HAND));
		cards.addAll(getPile(Pile.DISCARD));
		return cards;
	}
	
	/**
	 * Sets the player up, giving them the starter cards
	 */
	public void setup() {
		for(int i = 0; i < 7; i++) {
			getPile(Pile.DECK).add(getGame().getGameServer().getCardManager().get("copper"));
		}
		for(int i = 0; i < 3; i++) {
			getPile(Pile.DECK).add(getGame().getGameServer().getCardManager().get("estate"));
		}
		this.deck = shuffle(getPile(Pile.DECK));
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
		if(getPile(Pile.DECK).size() == 0 && getPile(Pile.DISCARD).size() != 0) {
			transferDiscardToDeck();
		}
		Card c = getPile(Pile.DECK).remove(0);
		getPile(Pile.HAND).add(c);
	}
	
	/**
	 * Shuffles the discard pile and moves it to the deck
	 */
	public void transferDiscardToDeck() {
		this.deck = shuffle(getPile(Pile.DISCARD));
		getPile(Pile.DISCARD).clear();
	}
	
	/**
	 * Maps the sum of the victory points for this player
	 * @return The player's victory points
	 */
	public int getVictoryPoints() {
		int points = 0;
		CardManager cm = getGame().getGameServer().getCardManager();
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
		getPile(Pile.DISCARD).addAll(getPile(Pile.HAND));
		getPile(Pile.HAND).clear();
	}
	
	/**
	 * @param cards To map to JSON
	 * @return the specified list of cards as JSON
	 */
	private List<JSONObject> getCardsAsJson(List<Card> cards) {
		List<JSONObject> json = new ArrayList<>();
		List<Card> toAdd = new ArrayList<>(cards); // Prevent concurrent modification exception
		for(Card card : toAdd) {
			json.add(card.getAsJson());
		}
		return json;
	}
	
	/**
	 * Trashes a card
	 * @param card To be trashed
	 */
	public void trashCard(Card card) {
		if(getPile(Pile.HAND).contains(card)) {
			getPile(Pile.HAND).remove(card);
			getGame().getBoard().addTrash(card);
		}
	}
	
	public boolean hasReaction() {
		Card moat = getGame().getGameServer().getCardManager().get("moat");
		if(getPile(Pile.HAND).contains(moat)) {
			return true;
		}
		return false;
	}

	/**
	 * Discards a card
	 * @param card To be discarded
	 */
	public void discardCard(Card card) {
		getPile(Pile.DISCARD).add(card);
		getPile(Pile.HAND).remove(card);
	}
	
	/**
	 * @return The necessary player info as JSON
	 */
	public JSONObject getAsJson() {
		return new JSONObject()
				.accumulate("displayname", getDisplayname())
				.accumulate("deck", getCardsAsJson(getPile(Pile.DECK)))
				.accumulate("hand", getCardsAsJson(getPile(Pile.HAND)))
				.accumulate("discard", getCardsAsJson(getPile(Pile.DISCARD)))
				.accumulate("victorypoints", getVictoryPoints());
	}
	
}
