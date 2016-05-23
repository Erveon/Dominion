package net.ultradev.dominion.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.card.Card;
import net.ultradev.dominion.game.card.CardManager;
import net.ultradev.dominion.game.card.action.Action;

public class Database {
	
    private final String user;
    private final String database;
    private final String password;
    private final String port;
    private final String hostname;
	private GameServer gs;
    private Connection connection;
    
    public Database(GameServer gs, final String hostname, final String port, final String database, final String username, final String password) {
    	this.gs = gs;
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.user = username;
        this.password = password;
        this.connection = null;
    }
    
    private GameServer getGameServer() {
    	return gs;
    }
    
    public Connection openConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database, this.user, this.password);
        } catch (SQLException e) {
            System.out.println("Could not connect to MySQL server! because: " + e.getMessage());
            return null;
        } catch (ClassNotFoundException e2) {
        	System.out.println("JDBC Driver not found!");
            return null;
        }
        getGameServer().getUtils().debug("Connected to the database!");
        return this.connection;
    }
    
    public boolean hasConnection() {
        return this.connection != null;
    }
    
    public Connection getConnection() {
        return this.connection;
    }
    
    public void closeConnection() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
            	System.out.println("Error closing the MySQL Connection!");
                e.printStackTrace();
            }
        }
    }
    
    public void loadCards() {
    	CardManager cm = getGameServer().getCardManager();
		try {
	    	Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM `Cards`");
			String card_id;
			String description;
			int cost;
			while(rs.next()) {
				card_id = rs.getString(1);
				description = rs.getString(2);
				cost = rs.getInt(3);
				Card card = new Card(card_id, description, cost);
				cm.getCards().put(card_id, card);
				getGameServer().getUtils().debug("(Card) Added card " + card.getName());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Variables are defined beforehand to be less stressful on the memory
     */
    public void loadActions() {
    	CardManager cm = getGameServer().getCardManager();
    	try {
	    	Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM `Actions`");
			ResultSet rscallback;
			// action
			Card card;
			Action action;
			String card_id, action_id, description, params;
			// callback
			String callback_action, callback_description, callback_params;
			while(rs.next()) {
				card_id = rs.getString(1);
				action_id = rs.getString(2);
				description = rs.getString(3);
				params = rs.getString(4);
				card = cm.getCards().get(card_id);
				action = cm.parseAction(action_id, description, params);
				getGameServer().getUtils().debug("(Action) Added " + action.getIdentifier() + " action for " + card.getName());
		    	Statement substatement = connection.createStatement();
				rscallback = substatement.executeQuery("SELECT * FROM `ActionCallbacks` "
																+ "WHERE `card_id` = '"+ card_id +"'"
																+ "AND `action_id` = '"+ action_id +"'");
				while(rscallback.next()) {
					callback_action = rscallback.getString(3);
					callback_description = rscallback.getString(4);
					callback_params = rscallback.getString(5);
					getGameServer().getUtils().debug("(Callback) Added callback " +callback_action + " for " + action.getIdentifier() + " action in " + card.getName());
					action.addCallback(cm.parseAction(callback_action, callback_description, callback_params));
				}
				card.addAction(action);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
   
}
