package net.ultradev.dominion;

import net.ultradev.dominion.game.GameManager;
import net.ultradev.dominion.game.card.CardManager;

public class GameServer {
	
	private CardManager cm;
	private GameManager gm;
	
	public GameServer() {
		this.cm = new CardManager(this);
		this.gm = new GameManager(this);
		setup();
	}
	
	public void setup() {
		getCardManager().setup();
	}
	
	public CardManager getCardManager() {
		return cm;
	}
	
	public GameManager getGameManager() {
		return gm;
	}

}
