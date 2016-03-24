package net.ultradev.dominion.game.card.action.actions;

import net.ultradev.dominion.game.Turn;
import net.ultradev.dominion.game.card.action.Action;

public class DrawCardAction extends Action {
	
	int amount;
	
	public DrawCardAction(String identifier, String description, int amount) {
		super(identifier, description);
		this.amount = amount;
	}

	@Override
	public void play(Turn turn) {
		for(int i = 0; i < amount; i++)
			turn.getPlayer().drawCardFromDeck();
	}
	
}