package net.ultradev.dominion.game.online;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.websocket.Session;

import net.sf.json.JSONObject;
import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.Game;
import net.ultradev.dominion.game.player.Player;

public class OnlineGame extends Game {

	private Map<Session, Player> players;
	private String name;
	private UUID uuid;
	private Session creator;
	
	public OnlineGame(GameServer gs, UUID uuid) {
		super(gs);
		this.uuid = uuid;
		this.players = new HashMap<>();
	}
	
	public void setCreator(Player player) {
		players.put(player.getSession(), player);
		getGameServer().getGameManager().setOnlineGameFor(player.getSession(), this);
		creator = player.getSession();
	}
	
	public Session getCreator() {
		return creator;
	}
	
	public void leave(Session session) {
		if(players.containsKey(session)) {
			players.remove(session);
			if(hasStarted()) {
				end();
			} else {
				updateGameInfo();
				updateLobby();
			}
		}
	}
	
	public void sendChatMessage(Session session, String message) {
		if(players.containsKey(session)) {
			Player from = players.get(session);
			JSONObject chatmessage = new JSONObject()
					.accumulate("type", "chat")
					.accumulate("username", from.getDisplayname())
					.accumulate("message", message);
			broadcast(chatmessage);
		}
	}
	
	@Override
	public void start() {
		super.start();
		broadcast(new JSONObject().accumulate("type", "startgame"));
		broadcast(getAsJson().accumulate("type", "game"));
		updateLobby();
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
		getGameServer().getGameManager().setOnlineGameFor(session, this);
		updateLobby();
		updateGameInfo();
		getGameServer().getUtils().debug("A player named " + name + " has been added to an online game");
	}
	
	public void broadcast(JSONObject message) {
		for(Session sess : players.keySet()) {
			try {
				if(sess.isOpen()) {
					sess.getBasicRemote().sendText(message.toString());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendToHost(JSONObject message) {
		try {
			if(creator.isOpen()) {
			creator.getBasicRemote().sendText(message.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void broadcastAllButHost(JSONObject message) {
		for(Session sess : players.keySet()) {
			try {
				if(sess.isOpen() && !sess.equals(creator)) {
					sess.getBasicRemote().sendText(message.toString());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setName(String name) {
		this.name = name;
		updateGameInfo();
	}
	
	public String getName() {
		return name;
	}
	
	public void end() {
		JSONObject message = new JSONObject()
				.accumulate("type", "endgame");
		broadcast(message);
		getGameServer().getGameManager().removeOnlineGame(getUniqueId());
	}
	
	public void updateGameInfo() {
		JSONObject message = new JSONObject()
				.accumulate("type", "gameinfo")
				.accumulate("game", getGameInfo());
		broadcastAllButHost(message.accumulate("host", false));
		sendToHost(message.accumulate("host", true));
	}
	
	public void updateLobby() {
		JSONObject message = new JSONObject()
				.accumulate("type", "updatelobby")
				.accumulate("lobby", getLobbyInfo());
		broadcast(message);
	}
	
	public JSONObject getGameInfo() {
		return new JSONObject()
				.accumulate("id", getUniqueId().toString())
				.accumulate("cardset", getConfig().getCardset())
				.accumulate("name", name)
				.accumulate("players", players.values().stream().map(Player::getDisplayname).collect(Collectors.toList()));
	}
	
	public JSONObject getLobbyInfo() {
		return new JSONObject()
				.accumulate("id", getUniqueId().toString())
				.accumulate("name", name)
				.accumulate("players", getPlayers().size())
				.accumulate("canjoin", !hasStarted());
	}

}
