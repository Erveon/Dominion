package net.ultradev.dominion.game.card;

import java.util.HashMap;
import java.util.Map;

public class CardManager {
	
	// Static because we only need 1 instance of these
	private static Map<String, Card> cards;
	
	public static void setup() {
		cards = new HashMap<>();
		//TODO init cards (fetch from db)
		
		//Temporary cards to make the board work:
		getCards().put("copper", new Card("copper", "test card", 1));
		getCards().put("silver", new Card("silver", "test card", 1));
		getCards().put("gold", new Card("gold", "test card", 1));

		getCards().put("estate", new Card("estate", "test card", 1));
		getCards().put("duchy", new Card("duchy", "test card", 1));
		getCards().put("province", new Card("province", "test card", 1));

		getCards().put("curse", new Card("curse", "test card", 1));
	}
	
	private static Map<String, Card> getCards() {
		return cards;
	}
	
	public static Card get(String identifier) {
		if(cards.containsKey(identifier))
			return getCards().get(identifier);
		throw new CardNotFoundException(identifier);
	}

}
