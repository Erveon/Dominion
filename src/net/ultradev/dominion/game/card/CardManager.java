package net.ultradev.dominion.game.card;

import java.util.HashMap;
import java.util.Map;

import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.Turn.CardDestination;
import net.ultradev.dominion.game.card.Card.CardType;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.Action.ActionTarget;
import net.ultradev.dominion.game.card.action.Action.AmountType;
import net.ultradev.dominion.game.card.action.IllegalActionVariableException;
import net.ultradev.dominion.game.card.action.MissingVariableException;
import net.ultradev.dominion.game.card.action.actions.AdventurerAction;
import net.ultradev.dominion.game.card.action.actions.BureaucratAction;
import net.ultradev.dominion.game.card.action.actions.DrawCardAction;
import net.ultradev.dominion.game.card.action.actions.GainActionsAction;
import net.ultradev.dominion.game.card.action.actions.GainBuypowerAction;
import net.ultradev.dominion.game.card.action.actions.GainBuypowerAction.GainBuypowerType;
import net.ultradev.dominion.game.card.action.actions.GainBuysAction;
import net.ultradev.dominion.game.card.action.actions.GainCardAction;
import net.ultradev.dominion.game.card.action.actions.MultipleActionsAction;
import net.ultradev.dominion.game.card.action.actions.RemoveCardAction;
import net.ultradev.dominion.game.card.action.actions.RemoveCardAction.RemoveType;
import net.ultradev.dominion.game.card.action.actions.SpyAction;
import net.ultradev.dominion.game.card.action.actions.ThiefAction;
import net.ultradev.dominion.game.card.action.actions.TransferPileAction;
import net.ultradev.dominion.game.player.Player;
import net.ultradev.dominion.game.player.Player.Pile;
import net.ultradev.dominion.persistence.Database;
import net.ultradev.dominion.persistence.Fallback;

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
		getCards().put("curse", new Card("curse", "A curse placed on your victory points", 1, CardType.CURSE));
		getCards().put("gardens", new Card("gardens", "Worth 1 Victory Point for every 10 cards in your deck (rounded down).", 4, CardType.VICTORY));
		addActions();
		
		Database db = getGameServer().getDatabase();
		if(db.hasConnection()) {
			db.loadCards();
			db.loadActions();
			addSubTypes();
		} else {
            new Fallback(getGameServer());
		}
	}
	
	// Happens after card creation because some actions rely on other cards
	public void addActions() {
		Card copper = getCards().get("copper");
		copper.addAction(parseAction("add_buypower", "Adds 1 coin to your turn", "amount=1"));
		Card silver = getCards().get("silver");
		silver.addAction(parseAction("add_buypower", "Adds 2 coins to your turn", "amount=2"));
		Card gold = getCards().get("gold");
		gold.addAction(parseAction("add_buypower", "Adds 3 coins to your turn", "amount=3"));
	}
	
	public void addSubTypes() {
		Card moat = getCards().get("moat");
		moat.addType("REACTION");
		Card witch = getCards().get("witch");
		witch.addType("ATTACK");
	}
	
	/**
	 * Converts database string input to an action
	 * @param identifier
	 * @param description
	 * @param variables
	 * @return an action
	 */
	public Action parseAction(String identifier, String description, String variables) {
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
					AmountType type = AmountType.SPECIFIC_AMOUNT;
					if(params.containsKey("type")) {
						type = AmountType.valueOf(params.get("type").toUpperCase());
					}
					return parseDrawCards(identifier, description, target, params.get("amount"), type);
				}
				break;
			case "trash_specific":
				if(containsKeys(params, identifier, "amount")) {
					return parseRemove(getGameServer(), identifier, description, params, target, AmountType.SPECIFIC_AMOUNT, RemoveType.TRASH);
				}
				break;
			case "trash_choose":
				if(containsKeys(params, identifier)) {
					return parseRemove(getGameServer(), identifier, description, params, target, AmountType.CHOOSE_AMOUNT, RemoveType.TRASH);
				}
				break;
			case "trash_range":
				if(containsKeys(params, identifier, "min", "max")) {
					return parseRemove(getGameServer(), identifier, description, params, target, AmountType.RANGE, RemoveType.TRASH);
				}
				break;
			case "trash_min":
				if(containsKeys(params, identifier, "min")) {
					return parseRemove(getGameServer(), identifier, description, params, target, AmountType.RANGE, RemoveType.TRASH);
				}
				break;
			case "trash_max":
				if(containsKeys(params, identifier, "max")) {
					return parseRemove(getGameServer(), identifier, description, params, target, AmountType.RANGE, RemoveType.TRASH);
				}
				break;
			case "trash_self":
				return parseRemove(getGameServer(), identifier, description, params, target, AmountType.SELF, RemoveType.TRASH);
			case "discard_specific":
				if(containsKeys(params, identifier, "amount")) {
					return parseRemove(getGameServer(), identifier, description, params, target, AmountType.SPECIFIC_AMOUNT, RemoveType.DISCARD);
				}
				break;
			case "discard_choose":
				return parseRemove(getGameServer(), identifier, description, params, target, AmountType.CHOOSE_AMOUNT, RemoveType.DISCARD);
			case "discard_range":
				if(containsKeys(params, identifier, "min", "max")) {
					return parseRemove(getGameServer(), identifier, description, params, target, AmountType.RANGE, RemoveType.DISCARD);
				}
				break;
			case "discard_min":
				if(containsKeys(params, identifier, "min")) {
					return parseRemove(getGameServer(), identifier, description, params, target, AmountType.RANGE, RemoveType.DISCARD);
				}
				break;
			case "discard_max":
				if(containsKeys(params, identifier, "max")) {
					return parseRemove(getGameServer(), identifier, description, params, target, AmountType.RANGE, RemoveType.DISCARD);
				}
				break;
			case "discard_until":
				if(containsKeys(params, identifier, "amount")) {
					return parseRemove(getGameServer(), identifier, description, params, target, AmountType.UNTIL, RemoveType.DISCARD);
				}
				break;
			case "add_actions":
				if(containsKeys(params, identifier, "amount")) {
					return parseAddActions(identifier, description, target, params.get("amount"));
				}
			case "add_buys":
				if(containsKeys(params, identifier, "amount")) {
					return parseAddBuys(identifier, description, target, params.get("amount"));
				}
				break;
			case "add_buypower":
				if(containsKeys(params, identifier, "amount")) {
					return parseAddBuypower(identifier, description, target, params.get("amount"), GainBuypowerType.ADD);
				}
				break;
			case "gain_card":
				if(containsKeys(params, identifier, "cost")) {
					if(params.containsKey("type")) {
						CardType gainCardType = CardType.valueOf(params.get("type").toUpperCase());
						return new GainCardAction(identifier, description, target, Integer.valueOf(params.get("cost")), gainCardType);
					} else {
						return new GainCardAction(identifier, description, target, Integer.valueOf(params.get("cost")));
					}
				}
				break;
			case "gain_specific_card":
				if(containsKeys(params, identifier, "card")) {
					Card card = get(params.get("card"));
					GainCardAction gainSpecificCard = new GainCardAction(identifier, description, target, card);
					CardDestination destination = CardDestination.DISCARD;
					if(params.containsKey("to")) {
						destination = CardDestination.valueOf(params.get("to").toUpperCase());
						if(destination != null) {
							gainSpecificCard.setDestination(destination);
						}
					}
					return gainSpecificCard;
				}
				break;
			case "adventurer":
				return new AdventurerAction(identifier, description, target);
			case "bureaucrat":
				return new BureaucratAction(identifier, description, target);
			case "transferpile":
				if(containsKeys(params, identifier, "from", "to")) {
					Pile from = Pile.valueOf(params.get("from").toUpperCase());
					Pile to = Pile.valueOf(params.get("to").toUpperCase());
					return new TransferPileAction(identifier, description, target, from, to);
				}
			case "multiaction":
				if(containsKeys(params, identifier, "times")) {
					int times = Integer.parseInt(params.get("times"));
					return new MultipleActionsAction(identifier, description, target, times);
				}
			case "thief":
				return new ThiefAction(identifier, description, target);
			case "spy":
				return new SpyAction(identifier, description, target);
		}
		throw new IllegalArgumentException("That action does not exist");
	}
	
	/*************
	 * 
	 *  START PARSERS
	 * 
	 *************/
	
	public Action parseDrawCards(String identifier, String description, ActionTarget target, String amountVar, AmountType amountType) {
		int amount = getGameServer().getUtils().parseInt(amountVar, 1);
		return new DrawCardAction(identifier, description, target, amount, amountType);
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
	
	public Action parseRemove(GameServer gs, String identifier, String description, Map<String, String> params, ActionTarget target,  AmountType count, RemoveType type) {
		RemoveCardAction action = null;
		switch(count) {
			case CHOOSE_AMOUNT:
				action = new RemoveCardAction(target, type, identifier, description);
				break;
			case RANGE:
				int min = 0;
				int max = 0;
				if(params.containsKey("min")) {
					min = getGameServer().getUtils().parseInt(params.get("min"), 0);
				}
				if(params.containsKey("max")) {
					max = getGameServer().getUtils().parseInt(params.get("max"), 4);
				}
				action = new RemoveCardAction(target, type, identifier, description, min, max);
				break;
			case UNTIL:
			case SPECIFIC_AMOUNT:
				int amount = getGameServer().getUtils().parseInt(params.get("amount"), 1);
				action = new RemoveCardAction(target, type, identifier, description, count, amount);
				break;
			case SELF:
				action = new RemoveCardAction(target, type, identifier, description, AmountType.SELF);
				break;
			default:
				action = new RemoveCardAction(target, type, identifier, description);
				break;
		}
		if(params.containsKey("restricttype")) {
			CardType restricttype = CardType.valueOf(params.get("restricttype").toUpperCase());
			for(Card c : getCards().values()) {
				if(c.getType().equals(restricttype)) {
					action.addPermitted(c);
				}
			}
		} else if(params.containsKey("restrict")) {
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
		if(!variables.isEmpty() && !variables.equals("")) {
			String[] vars = variables.split(";");
			for(String var : vars) {
				if(!var.contains("=") || var.split("=").length != 2) {
					throw new IllegalActionVariableException(identifier, variables);
				}
				String[] keyvalue = var.split("=");
				mappedVariables.put(keyvalue[0].toLowerCase(), keyvalue[1].toLowerCase());
			}
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
			case "curse":
				return -1;
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
