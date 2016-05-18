package net.ultradev.dominion.game.card;

import java.util.HashMap;
import java.util.Map;

import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.card.Card.CardType;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.Action.ActionTarget;
import net.ultradev.dominion.game.card.action.IllegalActionVariableException;
import net.ultradev.dominion.game.card.action.MissingVariableException;
import net.ultradev.dominion.game.card.action.actions.DrawCardAction;
import net.ultradev.dominion.game.card.action.actions.GainActionsAction;
import net.ultradev.dominion.game.card.action.actions.GainBuypowerAction;
import net.ultradev.dominion.game.card.action.actions.GainBuypowerAction.GainBuypowerType;
import net.ultradev.dominion.game.card.action.actions.GainBuysAction;
import net.ultradev.dominion.game.card.action.actions.GainCardAction;
import net.ultradev.dominion.game.card.action.actions.GainCardAction.GainCardType;
import net.ultradev.dominion.game.card.action.actions.RemoveCardAction;
import net.ultradev.dominion.game.card.action.actions.RemoveCardAction.RemoveCount;
import net.ultradev.dominion.game.card.action.actions.RemoveCardAction.RemoveType;
import net.ultradev.dominion.game.player.Player;

public class CardManager {
	
	private Map<String, Card> cards;
	private GameServer gs;
	
	public CardManager(GameServer gs) {
		this.gs = gs;
	}
	
	public GameServer getGameServer() {
		return gs;
	}
	
	public void setup() {
		cards = new HashMap<>();
		
		getCards().put("copper", new Card("copper", "A copper coin", 0, CardType.TREASURE));
		getCards().put("silver", new Card("silver", "A silver coin", 3, CardType.TREASURE));
		getCards().put("gold", new Card("gold", "A golden coin", 6, CardType.TREASURE));

		getCards().put("estate", new Card("estate", "An estate, worth 1 victory point", 2, CardType.VICTORY));
		getCards().put("duchy", new Card("duchy", "A duchy, worth 3 victory points", 5, CardType.VICTORY));
		getCards().put("province", new Card("province", "A province, worth 6 victory points", 8, CardType.VICTORY));

		getCards().put("curse", new Card("curse", "A curse placed on your victory points", 1, CardType.VICTORY));
		getCards().put("gardens", new Card("gardens", "Worth 1 Victory Point for every 10 cards in your deck (rounded down).", 4, CardType.VICTORY));

		//TODO fetch from db
		//Temporary cards to make the board work:
		Card chapel = new Card("chapel", "Trash up to 4 cards from your hand.", 2);
		getCards().put("chapel", chapel);
		
		Card village = new Card("village", "+1 Card; +2 Actions.", 3);
		getCards().put("village", village);
		
		Card woodcutter = new Card("woodcutter", "+1 Buy; +2 Coins.", 3);
		getCards().put("woodcutter", woodcutter);
		
		Card moneylender = new Card("moneylender", "Trash a Copper from your hand. If you do, +3 coins.", 3);
		getCards().put("moneylender", moneylender);
		
		Card cellar = new Card("cellar", "+1 Action. Discard any number of cards, +1 Card per card discarded.", 2);
		getCards().put("cellar", cellar);
		
		Card market = new Card("market", "+1 Card. +1 Action. +1 Buy. +1 coin.", 5);
		getCards().put("market", market);
		
		Card militia = new Card("militia", "+2 coins. Each player discards down to 3 cards in his hand.", 4);
		getCards().put("militia", militia);
		
		Card mine = new Card("mine", "Trash a Treasure card from your hand. Gain a Treasure card costing up to 3 coins more; put it into your hand.", 5);
		getCards().put("mine", mine);
		
		Card moat = new Card("moat", "+2 Cards. When another player plays an Attack card, you may reveal this from your hand. If you do, you are unaffected by that Attack.", 2);
		moat.addType("REACTION");
		getCards().put("moat", moat);
		
		Card remodel = new Card("remodel", "Trash a card from your hand. Gain a card costing up to 2 coins more than the trashed card.", 4);
		getCards().put("remodel", remodel);
		
		Card smithy = new Card("smithy", "+3 Cards.", 4);
		getCards().put("smithy", smithy);
		
		Card workshop = new Card("workshop", "Gain a card costing up to 4 coins.", 3);
		getCards().put("workshop", workshop);
		
		Card adventurer = new Card("adventurer", "Reveal cards from your deck until you reveal 2 Treasure cards. Put those Treasure cards into your hand and discard the other revealed cards.", 3);
		getCards().put("adventurer", adventurer);
		
		addActions();
	}
	
	// Happens after card creation because some actions rely on other cards
	public void addActions() {
		Card copper = getCards().get("copper");
		copper.addAction(parseAction("add_buypower", "Adds 1 coin to your turn", "amount=1"));

		Card silver = getCards().get("silver");
		silver.addAction(parseAction("add_buypower", "Adds 2 coins to your turn", "amount=2"));

		Card gold = getCards().get("gold");
		gold.addAction(parseAction("add_buypower", "Adds 3 coins to your turn", "amount=3"));
		
		//TODO FETCH FROM DB
		Card chapel = getCards().get("chapel");
		chapel.addAction(parseAction("trash_range", "Trash up to 4 cards from your hand.", "min=0;max=4"));
		
		Card village = getCards().get("village");
		village.addAction(parseAction("draw_cards", "Draw 1 card", "amount=1"));
		village.addAction(parseAction("add_actions", "Adds 2 actions to your turn", "amount=2"));
		
		Card woodcutter = getCards().get("woodcutter");
		woodcutter.addAction(parseAction("add_buys", "Adds 1 buy to your turn", "amount=1"));
		woodcutter.addAction(parseAction("add_buypower", "Adds 2 coins to your turn", "amount=2"));
		
		Card moneylender = getCards().get("moneylender");
		Action moneylenderAction = parseAction("trash_range", "Ability to trash a single copper for $3", "min=0;max=1;restrict=copper");
		moneylenderAction.addCallback(parseAction("add_buypower", "Grants 3 buypower", "amount=3"));
		moneylender.addAction(moneylenderAction);
		
		
		//TODO fetch callbacks for cards from db
		Card cellar = getCards().get("cellar");
		cellar.addAction(parseAction("add_actions", "Adds 1 action to your turn", "amount=1"));
		Action cellarAddcard = parseAction("discard_choose", "Discard any number of cards. +1 Card per card discarded.", "");
		cellarAddcard.addCallback(parseAction("draw_cards", "Draw 1 card", "amount=1"));
		cellar.addAction(cellarAddcard);
		
		Card market = getCards().get("market");
		market.addAction(parseAction("draw_cards", "Draw 1 card", "amount=1"));
		market.addAction(parseAction("add_actions", "Adds 1 action to your turn", "amount=1"));
		market.addAction(parseAction("add_buys", "Adds 1 buy to your turn", "amount=1"));
		market.addAction(parseAction("add_buypower", "Adds 1 coin to your turn", "amount=1"));

		Card militia = getCards().get("militia");
		militia.addAction(parseAction("add_buypower", "Adds 2 coins to your turn", "amount=2"));
		militia.addAction(parseAction("trash_min", "Each other player discards down to 3 cards in his hand.", "min=3;for=others"));

		Card mine = getCards().get("mine");
		Action discardTreasureMine = parseAction("trash_specific", "Trash a treasure card from your hand & gain a treasure card costing up to 3 coins more", "amount=1;restrict=gold,copper,silver");
		discardTreasureMine.addCallback(parseAction("gain_card", "gain a treasure card costing up to 3 coins more", "cost=3;type=treasure"));
		mine.addAction(discardTreasureMine);
		
		Card moat = getCards().get("moat");
		moat.addAction(parseAction("draw_cards", "Draw 2 cards", "amount=2"));
		
		Card remodel = getCards().get("remodel");
		Action discardTreasureRemodel = parseAction("trash_specific", "Trash a card from your hand. Gain a card costing up to 2 Coins more than the trashed card.", "amount=1");
		discardTreasureRemodel.addCallback(parseAction("gain_card", "gain a treasure card costing up to 2 coins more", "cost=2"));
		remodel.addAction(discardTreasureRemodel);
		
		Card smithy = getCards().get("smithy");
		smithy.addAction(parseAction("draw_cards", "Draw 3 cards", "amount=3"));
		
		Card workshop = getCards().get("workshop");
		workshop.addAction(parseAction("gain_card", "Gain a card costing up to 4 coins", "cost=4"));
	}
	
	/**
	 * Converts database string input to an action
	 * @param identifier
	 * @param description
	 * @param variables
	 * @return an action
	 */
	private Action parseAction(String identifier, String description, String variables) {
		Map<String, String> params = getMappedVariables(identifier, variables);
		
		ActionTarget target = ActionTarget.SELF;
		if(params.containsKey("for")) {
			try {
				target = ActionTarget.valueOf(params.get("for").toUpperCase());
			} catch(Exception ignored) { }
		}
		
		switch(identifier.toLowerCase()) {
			case "draw_cards":
				if(containsKeys(params, identifier, "amount")) {
					return parseDrawCards(identifier, description, target, params.get("amount"));
				}
			case "trash_specific":
				if(containsKeys(params, identifier, "amount")) {
					return parseRemove(getGameServer(), identifier, description, params, target, RemoveCount.SPECIFIC_AMOUNT, RemoveType.TRASH);
				}
			case "trash_choose":
				if(containsKeys(params, identifier)) {
					return parseRemove(getGameServer(), identifier, description, params, target, RemoveCount.CHOOSE_AMOUNT, RemoveType.TRASH);
				}
			case "trash_range":
				if(containsKeys(params, identifier, "min", "max")) {
					return parseRemove(getGameServer(), identifier, description, params, target, RemoveCount.RANGE, RemoveType.TRASH);
				}
			case "trash_min":
				if(containsKeys(params, identifier, "min")) {
					return parseRemove(getGameServer(), identifier, description, params, target, RemoveCount.MINIMUM, RemoveType.TRASH);
				}
			case "trash_max":
				if(containsKeys(params, identifier, "max")) {
					return parseRemove(getGameServer(), identifier, description, params, target, RemoveCount.MAXIMUM, RemoveType.TRASH);
				}
			case "discard_specific":
				if(containsKeys(params, identifier, "amount")) {
					return parseRemove(getGameServer(), identifier, description, params, target, RemoveCount.SPECIFIC_AMOUNT, RemoveType.DISCARD);
				}
			case "discard_choose":
				return parseRemove(getGameServer(), identifier, description, params, target, RemoveCount.CHOOSE_AMOUNT, RemoveType.DISCARD);
			case "discard_range":
				if(containsKeys(params, identifier, "min", "max")) {
					return parseRemove(getGameServer(), identifier, description, params, target, RemoveCount.RANGE, RemoveType.DISCARD);
				}
			case "discard_min":
				if(containsKeys(params, identifier, "min")) {
					return parseRemove(getGameServer(), identifier, description, params, target, RemoveCount.MINIMUM, RemoveType.DISCARD);
				}
			case "discard_max":
				if(containsKeys(params, identifier, "max")) {
					return parseRemove(getGameServer(), identifier, description, params, target, RemoveCount.MAXIMUM, RemoveType.DISCARD);
				}
			case "add_actions":
				if(containsKeys(params, identifier, "amount")) {
					return parseAddActions(identifier, description, target, params.get("amount"));
				}
			case "add_buys":
				if(containsKeys(params, identifier, "amount")) {
					return parseAddBuys(identifier, description, target, params.get("amount"));
				}
			case "add_buypower":
				if(containsKeys(params, identifier, "amount")) {
					return parseAddBuypower(identifier, description, target, params.get("amount"), GainBuypowerType.ADD);
				}
			case "gain_card":
				if(containsKeys(params, identifier, "cost")) {
					GainCardType gainType = GainCardType.ANY;
					if(params.containsKey("type")) {
						gainType = GainCardType.valueOf(params.get("type").toUpperCase());
					}
					return new GainCardAction(identifier, description, ActionTarget.SELF, Integer.valueOf(params.get("cost")), gainType);
				}
		}
		throw new IllegalArgumentException();
	}
	
	/*************
	 * 
	 *  START PARSERS
	 * 
	 *************/
	
	public Action parseDrawCards(String identifier, String description, ActionTarget target, String amountVar) {
		int amount = getGameServer().getUtils().parseInt(amountVar, 1);
		return new DrawCardAction(identifier, description, target, amount);
	}
	
	public Action parseAddActions(String identifier, String description, ActionTarget target, String amountVar) {
		int amount = getGameServer().getUtils().parseInt(amountVar, 1);
		return new GainActionsAction(identifier, description, target, amount);
	}
	
	public Action parseAddBuypower(String identifier, String description, ActionTarget target, String amountVar, GainBuypowerType type) {
		int amount = getGameServer().getUtils().parseInt(amountVar, 1);
		return new GainBuypowerAction(identifier, description, target, amount, type);
	}
	
	public Action parseAddBuys(String identifier, String description, ActionTarget target, String amountVar) {
		int amount = getGameServer().getUtils().parseInt(amountVar, 1);
		return new GainBuysAction(identifier, description, target, amount);
	}
	
	public Action parseRemove(GameServer gs, String identifier, String description, Map<String, String> params, ActionTarget target,  RemoveCount count, RemoveType type) {
		RemoveCardAction action = null;
		switch(count) {
			case CHOOSE_AMOUNT:
				action = new RemoveCardAction(target, type, identifier, description);
				break;
			case RANGE:
				int minrange = getGameServer().getUtils().parseInt(params.get("min"), 0);
				int maxrange = getGameServer().getUtils().parseInt(params.get("max"), 4);
				action = new RemoveCardAction(target, type, identifier, description, minrange, maxrange);
				break;
			case MINIMUM:
				int min = getGameServer().getUtils().parseInt(params.get("min"), 0);
				action = new RemoveCardAction(target, type, identifier, description, min, true);
				break;
			case MAXIMUM:
				int max = getGameServer().getUtils().parseInt(params.get("max"), 4);
				action = new RemoveCardAction(target, type, identifier, description, max, false);
				break;
			case SPECIFIC_AMOUNT:
				int amount = getGameServer().getUtils().parseInt(params.get("amount"), 1);
				action = new RemoveCardAction(target, type, identifier, description, amount);
				break;
			default:
				action = new RemoveCardAction(target, type, identifier, description);
				break;
		}
		if(params.containsKey("restrict")) {
			String[] toPermit = params.get("restrict").split(",");
			for(String permit : toPermit) {
				action.addPermitted(gs.getCardManager().get(permit));
			}
		}
		return action;
	}
	
	/*************
	 * 
	 *  END PARSERS
	 * 
	 *************/
	
	private boolean containsKeys(Map<String, String> params, String identifier, String... variables) {
		for(String var : variables) {
			if(!params.containsKey(var)) {
				throw new MissingVariableException(identifier, var);
			}
		}
		return true;
	}
	
	public Map<String, String> getMappedVariables(String identifier, String variables) {
		Map<String, String> mappedVariables = new HashMap<>();
		if(variables.isEmpty() || variables.equals("")) {
			return mappedVariables;
		}
		
		String[] vars = variables.split(";");
		for(String var : vars) {
			if(!var.contains("=") || var.split("=").length != 2) {
				throw new IllegalActionVariableException(identifier, variables);
			}
			String[] keyvalue = var.split("=");
			mappedVariables.put(keyvalue[0].toLowerCase(), keyvalue[1].toLowerCase());
		}
		
		return mappedVariables;
	}
	
	public Map<String, Card> getCards() {
		return cards;
	}
	
	public boolean exists(String identifier) {
		return cards.containsKey(identifier);
	}
	
	public Card get(String identifier) {
		if(cards.containsKey(identifier)) {
			return getCards().get(identifier);
		} else {
			throw new CardNotFoundException(identifier);
		}
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
