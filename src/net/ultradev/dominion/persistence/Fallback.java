package net.ultradev.dominion.persistence;

import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.CardManager;
import net.ultradev.dominion.game.card.action.Action;

/**
 * In case the database doesn't work
 */
public class Fallback {

	public Fallback(GameServer gs) { 
		init(gs);
	}
	
	public void init(GameServer gs) {
		addCards(gs.getCardManager());
	}
	
	public void addCards(CardManager cm) {
		Card chapel = new Card("chapel", "Trash up to 4 cards from your hand", 2);
		cm.getCards().put("chapel", chapel);
		
		Card village = new Card("village", "+1 card, +2 actions", 3);
		cm.getCards().put("village", village);
		
		Card woodcutter = new Card("woodcutter", "+1 buy, +2 coins", 3);
		cm.getCards().put("woodcutter", woodcutter);
		
		Card moneylender = new Card("moneylender", "Trash a copper from your hand. If you do, +3 coins", 3);
		cm.getCards().put("moneylender", moneylender);
		
		Card cellar = new Card("cellar", "+1 action. Discard any number of cards, +1 Card per card discarded", 2);
		cm.getCards().put("cellar", cellar);
		
		Card market = new Card("market", "+1 card, +1 action, +1 buy, +1 coin", 5);
		cm.getCards().put("market", market);
		
		Card militia = new Card("militia", "+2 coins, each other player discards down to 3 cards in his hand", 4);
		cm.getCards().put("militia", militia);
		
		Card mine = new Card("mine", "Trash a treasure card from your hand. Gain a treasure card costing up to 3 coins more, put it into your hand", 5);
		cm.getCards().put("mine", mine);
		
		Card moat = new Card("moat", "+2 Cards. When another player plays an attack card, you may reveal this from your hand. If you do, you are unaffected by that attack", 2);
		moat.addType("REACTION");
		cm.getCards().put("moat", moat);
		
		Card remodel = new Card("remodel", "Trash a card from your hand. Gain a card costing up to 2 coins more than the trashed card", 4);
		cm.getCards().put("remodel", remodel);
		
		Card smithy = new Card("smithy", "+3 cards", 4);
		cm.getCards().put("smithy", smithy);
		
		Card workshop = new Card("workshop", "Gain a card costing up to 4 coins", 3);
		cm.getCards().put("workshop", workshop);
		
		Card adventurer = new Card("adventurer", "Reveal cards from your deck until you reveal 2 treasure cards. Put those treasure cards into your hand and discard the other revealed cards", 6);
		cm.getCards().put("adventurer", adventurer);
		
		Card bureaucrat = new Card("bureaucrat", "Gain a silver card, put it on top of your deck. Each other player reveals a victory card from his hand and puts it on his deck", 4);
		cm.getCards().put("bureaucrat", bureaucrat);
		
		Card chancellor = new Card("chancellor", "+2 coins, you immediately put your deck into your discard pile", 3);
		cm.getCards().put("chancellor", chancellor);
		
		Card feast = new Card("feast", "Trash this card. Gain a card costing up to 5 coins", 4);
		cm.getCards().put("feast", feast);
		
		Card laboratory = new Card("laboratory", "+2 cards, +1 action", 5);
		cm.getCards().put("laboratory", laboratory);
		
		Card throne_room = new Card("throne_room", "Choose an action card in your hand. Play it twice", 4);
		cm.getCards().put("throne_room", throne_room);
		
		Card council_room = new Card("council_room", "+4 cards, +1 buy, each other player draws a card", 5);
		cm.getCards().put("council_room", council_room);
		
		Card festival = new Card("festival", "+2 actions, +1 buy, +2 coins", 5);
		cm.getCards().put("festival", festival);
		
		Card witch = new Card("witch", "+2 cards, each other player draws a curse card", 5);
		witch.addType("ATTACK");
		cm.getCards().put("witch", witch);
		
		Card library = new Card("library", "Draw until you have 7 cards in hand. You may set aside any action cards drawn this way, as you draw them, discard the set aside cards after you finish drawing", 5);
		cm.getCards().put("library", library);
		
		Card thief = new Card("thief", "Each other player reveals the top 2 cards of his deck. If they revealed any treasure cards, you gain all of those cards. They discard the other revealed cards", 4);
		cm.getCards().put("thief", thief);
		
		Card spy = new Card("spy", "+1 card, +1 action, each player (including you) reveals the top card of his deck and either discards it or puts it back, your choice", 4);
		cm.getCards().put("spy", spy);
		
		addActions(cm);
	}
	
	public void addActions(CardManager cm) {
		Card chapel = cm.getCards().get("chapel");
		chapel.addAction(cm.parseAction("trash_range", "Trash up to 4 cards from your hand", "min=0;max=4"));
		
		Card village = cm.getCards().get("village");
		village.addAction(cm.parseAction("draw_cards", "Draw 1 card", "amount=1"));
		village.addAction(cm.parseAction("add_actions", "Adds 2 actions to your turn", "amount=2"));
		
		Card woodcutter = cm.getCards().get("woodcutter");
		woodcutter.addAction(cm.parseAction("add_buys", "Adds 1 buy to your turn", "amount=1"));
		woodcutter.addAction(cm.parseAction("add_buypower", "Adds 2 coins to your turn", "amount=2"));
		
		Card moneylender = cm.getCards().get("moneylender");
		Action moneylenderAction = cm.parseAction("trash_range", "Ability to trash a single copper for $3", "min=0;max=1;restrict=copper");
		moneylenderAction.addCallback(cm.parseAction("add_buypower", "Grants 3 buypower", "amount=3"));
		moneylender.addAction(moneylenderAction);
		
		Card cellar = cm.getCards().get("cellar");
		cellar.addAction(cm.parseAction("add_actions", "Adds 1 action to your turn", "amount=1"));
		Action cellarAddcard = cm.parseAction("discard_choose", "Discard any number of cards. +1 Card per card discarded", "");
		cellarAddcard.addCallback(cm.parseAction("draw_cards", "Draw 1 card", "amount=1"));
		cellar.addAction(cellarAddcard);
		
		Card market = cm.getCards().get("market");
		market.addAction(cm.parseAction("draw_cards", "Draw 1 card", "amount=1"));
		market.addAction(cm.parseAction("add_actions", "Adds 1 action to your turn", "amount=1"));
		market.addAction(cm.parseAction("add_buys", "Adds 1 buy to your turn", "amount=1"));
		market.addAction(cm.parseAction("add_buypower", "Adds 1 coin to your turn", "amount=1"));

		Card militia = cm.getCards().get("militia");
		militia.addAction(cm.parseAction("add_buypower", "Adds 2 coins to your turn", "amount=2"));
		militia.addAction(cm.parseAction("discard_until", "Discard down to 3 cards in your hand", "amount=3;for=others"));

		Card mine = cm.getCards().get("mine");
		Action discardTreasureMine = cm.parseAction("trash_specific", "Trash a treasure card from your hand & gain a treasure card costing up to 3 coins more", "amount=1;restrict=gold,copper,silver");
		discardTreasureMine.addCallback(cm.parseAction("gain_card", "gain a treasure card costing up to 3 coins more", "cost=3;type=treasure"));
		mine.addAction(discardTreasureMine);
		
		Card moat = cm.getCards().get("moat");
		moat.addAction(cm.parseAction("draw_cards", "Draw 2 cards", "amount=2"));
		
		Card remodel = cm.getCards().get("remodel");
		Action discardTreasureRemodel = cm.parseAction("trash_specific", "Trash a card from your hand. Gain a card costing up to 2 Coins more than the trashed card.", "amount=1");
		discardTreasureRemodel.addCallback(cm.parseAction("gain_card", "gain a card costing up to 2 coins more", "cost=2"));
		remodel.addAction(discardTreasureRemodel);
		
		Card smithy = cm.getCards().get("smithy");
		smithy.addAction(cm.parseAction("draw_cards", "Draw 3 cards", "amount=3"));
		
		Card workshop = cm.getCards().get("workshop");
		workshop.addAction(cm.parseAction("gain_card", "Gain a card costing up to 4 coins", "cost=4"));
		
		Card adventurer = cm.getCards().get("adventurer");
		adventurer.addAction(cm.parseAction("adventurer", "Reveal cards from your deck until you reveal 2 Treasure cards. Put those Treasure cards into your hand and discard the other revealed cards.", ""));
		
		Card bureaucrat = cm.getCards().get("bureaucrat");
		bureaucrat.addAction(cm.parseAction("gain_specific_card", "Gain a Silver card; put it on top of your deck", "card=silver;to=top_deck"));
		bureaucrat.addAction(cm.parseAction("bureaucrat", "Each other player reveals a Victory card from his hand and puts it on his deck", "for=others"));
		
		Card chancellor = cm.getCards().get("chancellor");
		chancellor.addAction(cm.parseAction("add_buypower", "Adds 2 coins to your turn", "amount=2"));
		chancellor.addAction(cm.parseAction("transferpile", "You immediately put your deck into your discard pile.", "from=deck;to=discard"));
		
		Card feast = cm.getCards().get("feast");
		feast.addAction(cm.parseAction("trash_self", "Trash this card", ""));
		feast.addAction(cm.parseAction("gain_card", "Gain a card costing up to 5 coins", "cost=5"));

		Card laboratory = cm.getCards().get("laboratory");
		laboratory.addAction(cm.parseAction("draw_cards", "Draw 2 cards", "amount=2"));
		laboratory.addAction(cm.parseAction("add_actions", "Adds 1 action to your turn", "amount=1"));
		
		Card throne_room = cm.getCards().get("throne_room");
		throne_room.addAction(cm.parseAction("multiaction", "Choose an Action card in your hand. Play it twice.", "times=2"));

		Card council_room = cm.getCards().get("council_room");
		council_room.addAction(cm.parseAction("draw_cards", "Draw 4 cards", "amount=4"));
		council_room.addAction(cm.parseAction("add_buys", "Adds 1 buy to your turn", "amount=1"));
		council_room.addAction(cm.parseAction("draw_cards", "Every other player draws 1 card", "amount=1;for=others"));

		Card festival = cm.getCards().get("festival");
		festival.addAction(cm.parseAction("add_actions", "Adds 2 actions to your turn", "amount=2"));
		festival.addAction(cm.parseAction("add_buys", "Adds 1 buy to your turn", "amount=1"));
		festival.addAction(cm.parseAction("add_buypower", "Adds 2 coins to your turn", "amount=2"));
		
		Card witch = cm.getCards().get("witch");
		witch.addAction(cm.parseAction("draw_cards", "Draw 2 cards", "amount=2"));
		witch.addAction(cm.parseAction("gain_specific_card", "Draw a curse card", "card=curse;for=others;to=hand"));
		
		Card library = cm.getCards().get("library");
		library.addAction(cm.parseAction("draw_cards", "Draw until you have 7 cards", "amount=7;type=until"));
		library.addAction(cm.parseAction("discard_choose", "Discard any number of action cards.", "restricttype=action"));
		
		Card thief = cm.getCards().get("thief");
		thief.addAction(cm.parseAction("thief", "You reveal the top 2 cards from your deck, the thief grabs all the treasure.", "for=others"));
		
		Card spy = cm.getCards().get("spy");
		spy.addAction(cm.parseAction("draw_cards", "Draw 1 card", "amount=1"));
		spy.addAction(cm.parseAction("add_actions", "Adds 1 action to your turn", "amount=1"));
		spy.addAction(cm.parseAction("spy", "Reveal the top card of your deck, discard or put it back. Your choice.", "for=everyone"));
	}
	
}