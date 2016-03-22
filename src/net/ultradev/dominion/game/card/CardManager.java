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
		getCards().put("copper", new Card("copper", new String[] {"test card"}, 1));
		getCards().put("silver", new Card("silver", new String[] {"test card"}, 1));
		getCards().put("gold", new Card("gold", new String[] {"test card"}, 1));

		getCards().put("estate", new Card("estate", new String[] {"test card"}, 1));
		getCards().put("duchy", new Card("duchy", new String[] {"test card"}, 1));
		getCards().put("province", new Card("province", new String[] {"test card"}, 1));

		getCards().put("curse", new Card("curse", new String[] {"test card"}, 1));
	}
	
	private static Map<String, Card> getCards() {
		return cards;
	}
	
	public static Card get(String identifier) {
		if(cards.containsKey(identifier))
			return getCards().get(identifier);
		return null;
	}

}
