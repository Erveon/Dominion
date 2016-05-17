package net.ultradev.dominion.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.Card.CardType;
import net.ultradev.dominion.game.card.action.Action;
import net.ultradev.dominion.game.card.action.Action.ActionTarget;
import net.ultradev.dominion.game.card.action.actions.GainActionsAction;

public class CardTest {
	
	@Test
	public void testAddAction() {
		Card card = new Card("Test card", "This is a test card", 824);
		Action action = new GainActionsAction("gain_actions", "Grants 99 actions", ActionTarget.SELF, 99);
		card.addAction(action);
		assertTrue("Couldn't add action to card", card.getActions().contains(action));
	}
	
	@Test
	public void testAddType(){
		Card card = new Card("Test card", "This is a test card", 824);
		String type = "Testing card";
		card.addType(type);
		assertTrue("The type wasn't added to the card", card.getTypes().contains(type));	
	}
	
	@Test
	public void testGetTypesFormatted() {
		Card card = new Card("Test card", "This is a test card", 824, CardType.ACTION);
		String extraType = "Reaction";
		card.addType(extraType);
		String desired = CardType.ACTION.toString() + " - " + extraType;
		assertEquals("Card types were not formatted correctly", desired, card.getTypesFormatted());
	}
}