package net.ultradev.dominion.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.ultradev.dominion.GameServer;

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
    
    public boolean checkConnection() {
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
   
}
