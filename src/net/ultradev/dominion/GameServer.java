package net.ultradev.dominion;

import net.ultradev.dominion.game.GameManager;
import net.ultradev.dominion.game.card.CardManager;
import net.ultradev.dominion.game.utils.Utils;
import net.ultradev.dominion.persistence.Database;

public class GameServer {
	
	private static GameServer instance;
	
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
	
	/**
	 * Static because the 2 APIs initialised by Tomcat need access to the server
	 * It is synchronized just in case it's using multiple threads, to avoid accidental fuckups
	 * @return
	 */
	synchronized public static GameServer get() {
		if(instance == null) {
			instance = new GameServer();
		}
		return instance;
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
