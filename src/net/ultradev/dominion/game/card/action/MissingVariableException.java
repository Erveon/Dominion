package net.ultradev.dominion.game.card.action;

@SuppressWarnings("serial")
public class MissingVariableException extends RuntimeException {
	
	public MissingVariableException(String identifier, String variable) {
		super(identifier +" is missing variable: " + variable);
	}

}
