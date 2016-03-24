package net.ultradev.dominion.game.card.action;

public class IllegalActionVariableException extends RuntimeException {

	//Generated
	private static final long serialVersionUID = 1L;

	public IllegalActionVariableException(String identifier, String variable) {
		super("The following action variable is not valid for action '"+ identifier +"': " + variable);
	}
	
}
