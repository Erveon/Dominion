package net.ultradev.dominion.game.card;

import java.util.HashMap;
import java.util.Map;

import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.IllegalActionVariableException;
import net.ultradev.dominion.game.card.action.MissingVariableException;
import net.ultradev.dominion.game.card.action.actions.DrawCardAction;
import net.ultradev.dominion.game.card.action.actions.GainActionsAction;
import net.ultradev.dominion.game.card.action.actions.GainBuypowerAction;
import net.ultradev.dominion.game.card.action.actions.GainBuypowerAction.GainBuypowerType;
import net.ultradev.dominion.game.card.action.actions.GainBuysAction;
import net.ultradev.dominion.game.card.action.actions.TrashCardAction;
import net.ultradev.dominion.game.card.action.actions.TrashCardAction.TrashType;
import net.ultradev.dominion.game.player.Player;

public class CardManager {
	
	private GameServer gs;
	
	public CardManager(GameServer gs) {
		this.gs = gs;
	}
	
	public GameServer getGameServer() {
		return gs;
	}
	
	// Static because we only need 1 instance of these
	private static Map<String, Card> cards;
	
	public void setup() {
		cards = new HashMap<>();
		//TODO init cards (fetch from db)
		
		getCards().put("copper", new Card("copper", "A copper coin", 0));
		getCards().put("silver", new Card("silver", "A silver coin", 3));
		getCards().put("gold", new Card("gold", "A golden coin", 6));

		getCards().put("estate", new Card("estate", "An estate, worth 1 victory point", 2));
		getCards().put("duchy", new Card("duchy", "A duchy, worth 3 victory points", 5));
		getCards().put("province", new Card("province", "A province, worth 6 victory points", 8));

		getCards().put("curse", new Card("curse", "A curse placed on your victory points", 1));

		//Temporary cards to make the board work:
		Card chapel = new Card("chapel", "Trash up to 4 cards from your hand.", 2);
		chapel.addAction(parseAction("trash_range", "Trash up to 4 cards from your hand.", "min=0;max=4"));
		getCards().put("chapel", chapel);
		
		Card village = new Card("village", "+1 Card; +2 Actions.", 3);
		village.addAction(parseAction("draw_cards", "Draw 1 card", "amount=1"));
		village.addAction(parseAction("add_actions", "Adds 1 action to your turn", "amount=1"));
		getCards().put("village", village);
		
		Card woodcutter = new Card("woodcutter", "+1 Card; +2 Actions.", 3);
		woodcutter.addAction(parseAction("add_buys", "Adds 1 buy to your turn", "amount=1"));
		woodcutter.addAction(parseAction("add_buypower", "Adds 2 coins to your turn", "amount=2"));
		getCards().put("woodcutter", woodcutter);
		
		Card moneylender = new Card("moneylender", "Trash a Copper from your hand. If you do, +$3.", 3);
		moneylender.addAction(parseAction("trash_range", "Ability to trash a single copper for $3", "min=0;max=1;restrict=copper"));
		getCards().put("moneylender", moneylender);
	}
	
	private Action parseAction(String identifier, String description, String variables) {
		Map<String, String> params = getMappedVariables(identifier, variables);
		switch(identifier.toLowerCase()) {
			case "draw_cards":
				if(containsKeys(params, identifier, "amount"))
					return DrawCardAction.parse(identifier, description, params.get("amount"));
			case "trash_specific":
				if(containsKeys(params, identifier, "amount"))
					return TrashCardAction.parse(getGameServer(), identifier, description, params, TrashType.SPECIFIC_AMOUNT);
			case "trash_choose":
				// No parameters
				return TrashCardAction.parse(getGameServer(), identifier, description, params, TrashType.CHOOSE_AMOUNT);
			case "trash_range":
				if(containsKeys(params, identifier, "min", "max"))
					return TrashCardAction.parse(getGameServer(), identifier, description, params, TrashType.RANGE);
			case "add_actions":
				if(containsKeys(params, identifier, "amount"))
					return GainActionsAction.parse(identifier, description, params.get("amount"));
			case "add_buys":
				if(containsKeys(params, identifier, "amount"))
					return GainBuysAction.parse(identifier, description, params.get("amount"));
			case "add_buypower":
				if(containsKeys(params, identifier, "amount"))
					return GainBuypowerAction.parse(identifier, description, params.get("amount"), GainBuypowerType.ADD);
		}
		return null;
	}
	
	private boolean containsKeys(Map<String, String> params, String identifier, String... variables) {
		for(String var : variables) {
			if(!params.containsKey(var))
				throw new MissingVariableException(identifier, var);
		}
		return true;
	}
	
	public Map<String, String> getMappedVariables(String identifier, String variables) {
		Map<String, String> mappedVariables = new HashMap<>();
		if(variables.isEmpty() || variables.equals(""))
			return mappedVariables;
		
		String[] vars = variables.split(";");
		for(String var : vars) {
			if(!var.contains("=") || var.split("=").length != 2)
				throw new IllegalActionVariableException(identifier, variables);
			String[] keyvalue = var.split("=");
			mappedVariables.put(keyvalue[0].toLowerCase(), keyvalue[1].toLowerCase());
		}
		
		return mappedVariables;
	}
	
	private Map<String, Card> getCards() {
		return cards;
	}
	
	public Card get(String identifier) {
		if(cards.containsKey(identifier))
			return getCards().get(identifier);
		throw new CardNotFoundException(identifier);
	}
	
	public int getVictoryPointsFor(Card c, Player p) {
		switch(c.getName().toLowerCase()) {
			case "estate":
				return 1;
			case "duchy":
				return 3;
			case "province":
				return 6;
			case "gardens": // Every 10 cards is worth 1 point, rounded down
				return (int) Math.floor(p.getTotalCardCount() / 10);
		}
		return 0;
	}

}
