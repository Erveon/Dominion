package net.ultradev.dominion.game.card;

public interface Action {

	public String getIdentifier();
	public String getDescripton();
	public void play();
	
	// DRAW CARD ACTION
	public class DrawCardAction implements Action {
		
		String identifier;
		String description;
		int amount;
		
		public DrawCardAction(String identifier, String description, int amount) {
			this.identifier = identifier;
			this.description = description;
			this.amount = amount;
		}

		@Override
		public String getIdentifier() {
			return identifier;
		};
		
		@Override 
		public String getDescripton() { 
			return description; 
		}

		@Override
		public void play() {
			//TODO draw 'amount' cards
		}
		
	}
	
	// TRASH CARD ACTION
	public class TrashCardAction implements Action {
		
		String identifier;
		String description;
		int amount;
		int min, max;
		boolean chooseAmount;
		boolean rangeAmount;
		
		public TrashCardAction(String identifier, String description) {
			this.identifier = identifier;
			this.description = description;
			this.chooseAmount = true;
		}
		
		public TrashCardAction(String identifier, String description, int amount) {
			this.identifier = identifier;
			this.description = description;
			this.amount = amount;
		}
		
		public TrashCardAction(String identifier, String description, int min, int max) {
			this.identifier = identifier;
			this.description = description;
			this.min = min;
			this.max = max;
			this.rangeAmount = true;
		}

		@Override
		public String getIdentifier() {
			return identifier;
		}
		
		@Override 
		public String getDescripton() { 
			return description; 
		}

		@Override
		public void play() {
			//TODO trash 'amount' cards
		}
		
	}
	
}
