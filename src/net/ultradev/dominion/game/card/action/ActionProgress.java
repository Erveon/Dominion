package net.ultradev.dominion.game.card.action;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps track of the progress within an action.
 * This is necessary to keep actions confined to a single instance 
 * and waste the least amount of memory possible
 */
public class ActionProgress {

	private Map<String, String> progress;

	public ActionProgress() {
		progress = new HashMap<>();
	}
	
	public void set(String key, String value) {
		progress.put(key, value);
	}
	
	public void set(String key, int value) {
		progress.put(key, String.valueOf(value));
	}
	
	public int getInteger(String key) {
		if(progress.containsKey(key)) {
			return Integer.valueOf(progress.get(key));
		}
		throw new IllegalArgumentException("The key does not exist (" + key + ")");
	}
	
	public String getString(String key) {
		if(progress.containsKey(key)) {
			return progress.get(key);
		}
		throw new IllegalArgumentException("The key does not exist (" + key + ")");
	}

}
