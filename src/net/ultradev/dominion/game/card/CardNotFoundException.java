package net.ultradev.dominion.game.card;

@SuppressWarnings("serial")
public class CardNotFoundException extends RuntimeException {
	
	public CardNotFoundException(String card) {
		super("Card not found: " + card);
	}

}