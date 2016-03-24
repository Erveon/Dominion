package net.ultradev.dominion.game.card.action;

@SuppressWarnings("serial")
public class IllegalActionVariableException extends RuntimeException {

	public IllegalActionVariableException(String identifier, String variable) {
		super("The following action variable is not valid for action '"+ identifier +"': " + variable);
	}
	
}
