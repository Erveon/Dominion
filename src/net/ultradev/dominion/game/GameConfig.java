package net.ultradev.dominion.game;

import java.util.ArrayList;
import java.util.List;

public class GameConfig {
	
	int players;
	List<String> actionCardTypes;
	
	public GameConfig() {
		//Default values
		this.players = 2;
		this.actionCardTypes = new ArrayList<>();
	}
	
	public void setPlayers(int players) {
		this.players = players;
	}
	
	public void addActionCard(String actionCard) {
		if(actionCardTypes.size() <= 10)
			actionCardTypes.add(actionCard);
	}
	
	public void removeActionCard(String actionCard) {
		if(actionCardTypes.contains(actionCard))
			actionCardTypes.remove(actionCard);
	}
	
	public void assignRandomActionCards() {
		//TODO Geef 10 random kaart types vanuit db
	}
	
	public List<String> getActionCards() {
		return actionCardTypes;
	}

}