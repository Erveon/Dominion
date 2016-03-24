package net.ultradev.dominion.game.card.action;

public class MissingVariableException extends RuntimeException {

	//Generated
	private static final long serialVersionUID = 1L;
	
	public MissingVariableException(String identifier, String variable) {
		super(identifier +" is missing variable: " + variable);
	}

}
