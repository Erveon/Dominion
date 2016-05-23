package net.ultradev.dominion;

import net.ultradev.dominion.game.GameManager;
import net.ultradev.dominion.game.card.CardManager;
import net.ultradev.dominion.game.utils.Utils;
import net.ultradev.dominion.persistence.Database;

public class GameServer {
	
	private CardManager cm;
	private GameManager gm;
	private Database database;
	private Utils utils;
	
	public GameServer() {
		this.database = new Database(this, "localhost", "3306", "Dominion", "root", "password");
		this.utils = new Utils();
		getUtils().setDebugging(true);
		this.cm = new CardManager(this);
		this.gm = new GameManager(this);
		getDatabase().openConnection();
		setup();
	}
	
	private void setup() {
		getCardManager().setup();
	}
	
	public Database getDatabase() {
		return database;
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
