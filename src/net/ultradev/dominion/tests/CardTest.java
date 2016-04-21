package net.ultradev.dominion.tests;

import static org.junit.Assert.*;
import org.junit.Test;
import net.ultradev.dominion.game.card.*;
import net.ultradev.dominion.game.card.action.*;
import net.ultradev.dominion.game.card.action.actions.GainActionsAction;

import java.util.List;

public class CardTest {
	
	private Card c;
	private boolean Testing = true;

	@Test
	public void initTest() {
		if(Testing){
			System.out.println("Functie in de main toegevoegd?\n");
		}
	}
	
	@Test
	public void testAddAction() {
		c = new Card("Test card","This is a test card",824);
		Action action = new GainActionsAction("Gain_Actions","I Gained 99 actions...",99);
		c.addAction(action);
		List<Action> actions = c.getActions();
		if(!(actions.contains(action))) {
			fail("testAddAction failed:\n"+ actions + " doesn't contain " + "action");
		}
	}
	
	@Test
	public void testAddType(){
		c = new Card("Test card","This is a test card",824);
		String type = "Testing card";
		c.addType(type);
		List<String> types = c.getTypes();
		if(!(types.contains(type))) {
			fail("testAddType failed:\n" + types + " doesn't contain " + type);
		}
		
	}
	
	@Test
	public void testGetTypesFormatted() {
		c = new Card("Test card","This is a test card",824);
		String type1 = "Test-it";
		String type2 = "Testing card";
		String format = type1 + " - " + type2;
		c.addType(type1);
		c.addType(type2);
		String formatted = c.getTypesFormatted();
		if(!(formatted.equals(format))) {
			fail("testGetTypesFormatted failed:\n" + formatted + "\nIs not equal to:\n" + format);
		}
	}
}








