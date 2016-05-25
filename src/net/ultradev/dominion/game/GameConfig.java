package net.ultradev.dominion.game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import net.sf.json.JSONObject;

public class GameConfig {
	
	public enum CardSet { TEST, FIRSTGAME, BIGMONEY, INTERACTION, SIZEDISTORTION, VILLAGESQUARE }
	public enum Option { ADDCARD, REMOVECARD, SETCARDSET };
	
	private CardSet cardset;
	private List<String> actionCardTypes;
	private Game game;
	
	public GameConfig(Game game) {
		this.game = game;
		this.cardset = CardSet.FIRSTGAME;
		this.actionCardTypes = new ArrayList<>();
	}
	
	public Game getGame() {
		return game;
	}
	
	public boolean hasValidActionCards() {
		return actionCardTypes.size() == 10;
	}
	
	public void setCardset(String cardSet) {
		getGame().getGameServer().getUtils().debug("A cardset has been chosen");
		actionCardTypes.clear();
		CardSet set;
		try  {
			set = CardSet.valueOf(cardSet.toUpperCase());
		} catch(Exception ignored) { 
			set = CardSet.FIRSTGAME;
		}
		this.cardset = set;
		switch(set) {
			case TEST:
				addActionCards("chapel", 
						"village", 
						"woodcutter", 
						"moneylender", 
						"cellar", 
						"market", 
						"militia", 
						"mine", 
						"moat", 
						"remodel");
				break;
			case FIRSTGAME:
				addActionCards("cellar", 
						"market", 
						"militia", 
						"mine", 
						"moat", 
						"remodel", 
						"smithy", 
						"village", 
						"woodcutter", 
						"workshop");
				break;
			case BIGMONEY:
				addActionCards("adventurer", 
						"bureaucrat", 
						"chancellor", 
						"chapel", 
						"feast", 
						"laboratory", 
						"market", 
						"mine", 
						"moneylender", 
						"throne_room");
				break;
			case INTERACTION:
				addActionCards("bureaucrat", 
						"chancellor", 
						"council_room", 
						"festival", 
						"library", 
						"militia", 
						"moat", 
						"spy", 
						"thief", 
						"village");
				break;
			case SIZEDISTORTION:
				addActionCards("cellar", 
						"chapel", 
						"feast", 
						"gardens", 
						"laboratory", 
						"thief", 
						"village", 
						"witch", 
						"woodcutter", 
						"workshop");
				break;
			case VILLAGESQUARE:
				addActionCards("bureaucrat", 
						"cellar", 
						"festival", 
						"library", 
						"market", 
						"remodel", 
						"smithy", 
						"throne_room", 
						"village", 
						"woodcutter");
				break;
			default:
				break;
		}
	}
	
	public CardSet getCardset() {
		return cardset;
	}
	
	public void addActionCards(String... cards) {
		Stream.of(cards).forEach(card -> addActionCard(card));
	}
	
	public void addActionCard(String actionCard) {
		if(getGame().getGameServer().getCardManager().exists(actionCard)) {
			if(!actionCardTypes.contains(actionCard) && actionCardTypes.size() < 10) {
				actionCardTypes.add(actionCard);
			}
		}
	}
	
	public List<String> getActionCards() {
		return actionCardTypes;
	}
	
	public JSONObject getAsJson() {
		return new JSONObject()
				.accumulate("actionCards", getActionCards());
	}

}