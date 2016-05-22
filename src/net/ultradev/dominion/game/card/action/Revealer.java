package net.ultradev.dominion.game.card.action;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.sf.json.JSONObject;
import net.ultradev.dominion.game.card.Card;

public class Revealer {
	
	List<Card> toReveal;
	
	public Revealer() {
		toReveal = new ArrayList<>();
	}
	
	public Revealer(List<Card> cards) {
		toReveal = new ArrayList<>();
		toReveal.addAll(cards);
	}
	
	public Revealer(Card card) {
		toReveal = new ArrayList<>();
		toReveal.add(card);
	}
	
	public void addCard(Card card) {
		toReveal.add(card);
	}
	
	public void addCards(List<Card> cards) {
		toReveal.addAll(cards);
	}
	
	/**
	 * @return the card list mapped to its JSON data
	 */
	public List<JSONObject> get() {
		return toReveal.stream().map(Card::getAsJson).collect(Collectors.toList());
	}

}
