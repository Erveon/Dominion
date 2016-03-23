package net.ultradev.dominion.game.card;

public class CardNotFoundException extends RuntimeException {
	
	//Generated
	private static final long serialVersionUID = 1L;
	
	public CardNotFoundException(String card) {
		super("Card not found: " + card);
	}

}