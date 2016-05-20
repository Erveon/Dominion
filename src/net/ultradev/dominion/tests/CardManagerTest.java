package net.ultradev.dominion.tests;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.card.CardManager;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.Action.ActionTarget;
import net.ultradev.dominion.game.card.action.actions.DrawCardAction;
import net.ultradev.dominion.game.card.action.actions.GainActionsAction;
import net.ultradev.dominion.game.card.action.actions.GainBuypowerAction;
import net.ultradev.dominion.game.card.action.actions.GainBuypowerAction.GainBuypowerType;
import net.ultradev.dominion.game.card.action.actions.GainBuysAction;
import net.ultradev.dominion.game.card.action.actions.RemoveCardAction;
import net.ultradev.dominion.game.card.action.actions.RemoveCardAction.AmountType;
import net.ultradev.dominion.game.card.action.actions.RemoveCardAction.RemoveType;

public class CardManagerTest {
	
	private GameServer gameserver;
	private CardManager cardmanager;
	
	@Before
	public void init() {
		gameserver = new GameServer();
		cardmanager = new CardManager(gameserver);
		}

	@Test
	public void testParseDrawCards() {
		Action parsedAction = cardmanager.parseDrawCards("Draw a card","Tests parsing the database by drawing a card", ActionTarget.SELF,"1");
		assertTrue("Failed to parse a drawCard string", parsedAction instanceof DrawCardAction);

	}
	
	@Test
	public void testParseAddActions() {
		Action parsedAction = cardmanager.parseAddActions("Add an action","Tests parsing the database by adding an action",ActionTarget.SELF, "1");
		assertTrue("Failed to parse a AddAction string", parsedAction instanceof GainActionsAction);
	}
	
	@Test
	public void testParseAddBuypower() {
		Action parsedAction = cardmanager.parseAddBuypower("Add buypower", "Tests parsing the database by adding buypower", ActionTarget.SELF, "1", GainBuypowerType.ADD);
		assertTrue("Failed to parse a AddBuypower string", parsedAction instanceof GainBuypowerAction);
	}
	
	@Test
	public void testParseAddBuys() {
		Action parsedAction = cardmanager.parseAddBuys("Add a buycount", "Tests parsing the database by adding buycount", ActionTarget.SELF, "1");
		assertTrue("Failed to parse a AddBuycount string", parsedAction instanceof GainBuysAction);
	}
	
	@Test
	public void testParseRemove() {
		Map<String, String> parameters = cardmanager.getMappedVariables("trash_range", "min=0;max=4");
		Action parsedAction = cardmanager.parseRemove(gameserver, "Remove a card", "Tests parsing the database by adding a removeCard", parameters, ActionTarget.SELF, AmountType.RANGE, RemoveType.DISCARD);
		assertTrue("Failed to parse a RemoveCard string", parsedAction instanceof RemoveCardAction);
	}

}
