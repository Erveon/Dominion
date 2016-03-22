package net.ultradev.dominion.game.player;

import net.sf.json.JSONObject;

public class Player {
	
	String displayname;
	
	public Player(String displayname) {
		this.displayname = displayname;
	}
	
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
	
	public String getDisplayname() {
		return displayname;
	}
	
	public JSONObject getAsJson() {
		return new JSONObject()
				.accumulate("displayname", getDisplayname());
	}
	
}
