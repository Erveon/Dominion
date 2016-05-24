package net.ultradev.dominion.game.online;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.websocket.Session;

import net.sf.json.JSONObject;
import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.Game;
import net.ultradev.dominion.game.player.Player;

public class OnlineGame extends Game {

	private Map<Session, Player> players;
	private String name;
	private UUID uuid;
	
	public OnlineGame(GameServer gs, UUID uuid) {
		super(gs);
		this.uuid = uuid;
		this.players = new HashMap<>();
	}

	@Override
	public boolean isOnline() {
		return true;
	}
	
	public UUID getUniqueId() {
		return uuid;
	}

	/**
	 * Converts the player values to an ArrayList from a collection
	 */
	@Override
	public List<Player> getPlayers() {
		return new ArrayList<>(players.values());
	}
	
	@Override
	public void addPlayer(String name, Session session) {
		players.put(session, new Player(this, getValidNameFor(name)));
		getGameServer().getUtils().debug("A player named " + name + " has been added to an online game");
	}
	
	public void broadcast(JSONObject message) {
		String toSend = new JSONObject()
							.accumulate("action", "chat")
							.accumulate("message", message.toString()).toString();
		for(Session sess : players.keySet()) {
			try {
				sess.getBasicRemote().sendText(toSend);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public JSONObject getLobbyInfo() {
		return new JSONObject()
				.accumulate("id", getUniqueId().toString())
				.accumulate("name", name)
				.accumulate("players", getPlayers().size())
				.accumulate("canjoin", !hasStarted());
	}

}
