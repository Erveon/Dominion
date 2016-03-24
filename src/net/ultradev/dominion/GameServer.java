package net.ultradev.dominion;

import net.ultradev.dominion.game.GameManager;
import net.ultradev.dominion.game.card.CardManager;
import net.ultradev.dominion.game.utils.Utils;

public class GameServer {
	
	private CardManager cm;
	private GameManager gm;
	private Utils utils;
	
	public GameServer() {
		this.utils = new Utils();
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
	
	public Utils getUtils() {
		return utils;
	}

}
