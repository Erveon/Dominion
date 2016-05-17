package net.ultradev.dominion.game.utils;

public class Utils {
	
	public boolean debugging;
	
	public void setDebugging(boolean debugging) {
		this.debugging = debugging;
	}
	
	public void debug(String s) {
		if(debugging) {
			System.out.println(s);
		}
	}
	
	/**
	 * Returns the toParse string as an integer if possible, if not, it'll output an error and use the fallback integer
	 * @param toParse
	 * @param fallback
	 * @return
	 */
	public int parseInt(String toParse, int fallback) {
		try {
			fallback = Integer.parseInt(toParse);
		} catch(Exception e) { e.printStackTrace(); }
		return fallback;
	}

}
