package net.ultradev.dominion;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import net.ultradev.dominion.game.local.LocalGame;

public class Dominion {
	
	public enum State { ADDPLAYERS, SETCARDPACK };
	
	private GameServer gs;
	private State state;
	private LocalGame game;
	
	private String inputMessage = "";
	
	public static void main(String[] args) {
		new Dominion();
	}

	public Dominion() {
		gs = new GameServer();
        gs.getUtils().setDebugging(false);
		game = gs.getGameManager().createLocalGame(null);
		
		setState(State.ADDPLAYERS);
		display();
		
		handleCommand(askInput("Command"));
	}
	
	public void display() {
		// Clear console
		try {
			new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
		switch(getState()) {
			case ADDPLAYERS:
				printLogo();
				print();
				print("Welcome to Dominion,");
				print("Add players with 'add (name)'");
				print("Use 'done' when you're done adding players");
				print("Use the 'help' command if you're stuck");
				print();
				break;
			case SETCARDPACK:
				printLogo();
				print();
				print("Great! Let's pick the card set we'll be using.");
				print("The available packs are:");
				print("    0) Test");
				print("    1) First Game");
				print("    2) Big Money");
				print("    3) Interaction");
				print("    4) Size distortion");
				print("    5) Village Square");
				print();
				print("Pick one of those by typing its number");
				print("For more info about a set, use 'info (number)'");
				print();
				break;
			default:
				print("Invalid menu");
				break;
		}
	}
	
	public void printLogo() {
		print("________                 .__       .__                ");
		print("\\______ \\   ____   _____ |__| ____ |__| ____   ____   ");
		print(" |    |  \\ /  _ \\ /     \\|  |/    \\|  |/  _ \\ /    \\  ");
		print(" |    `   (  <_> )  Y Y  \\  |   |  \\  (  <_> )   |  \\ ");
		print("/_______  /\\____/|__|_|  /__|___|  /__|\\____/|___|  / ");
		print("        \\/             \\/        \\/               \\/ ");
	}
	
	public void setState(State state) {
		this.state = state;
	}
	
	public State getState() {
		return state;
	}
	
	public void handleCommand(String command) {
		String[] cmds = command.split(" ");
		String cmd = cmds[0];
		String[] args = Arrays.copyOfRange(command.split(" "), 1, cmds.length);
		if(getState().equals(State.SETCARDPACK)) {
			if(command.startsWith("info")) {
				command = command.replace("info", "");
				if(isNumberBetween(command, 0, 6)) {
					inputComplete("Info about the cardset");
				}
				return;
			} else if(command.startsWith("pick")) {
				command = command.replace("pick", "");
				if(isNumberBetween(command, 0, 6)) {
					inputComplete("Picked card");
				}
				return;
			}
		}
		switch(cmd) {
			case "help":
				help();
				break;
			case "exit":
				System.exit(0);
				break;
			case "done":
				done();
				break;
			case "add":
				if(getState().equals(State.ADDPLAYERS)) {
					add(args);
				}
				break;
			default:
				print("That command was not recognized. Use 'help' if you need help.");
				handleCommand(askInput(getInputMessage()));
				break;
		}
	}
	
	public boolean isNumberBetween(String input, int min, int max) {
		try {
			int nr = Integer.parseInt(input.trim());
			if(nr >= min && nr < max) {
				return true;
			} else {
				inputComplete("Please provide a valid number");
			}
		} catch(Exception ignored) { 
			inputComplete("That's not a number, silly");
		}
		return false;
	}
	
	public void add(String[] args) {
		String conc = String.join(" ", args);
		switch(getState()) {
			case ADDPLAYERS:
				if(getGame().getPlayers().size() < 4) {
					if(getGame().getPlayerByName(conc) != null) {
						inputComplete("There's a player named " + conc + " already!");
						break;
					}
					getGame().addPlayer(conc);
					print("Added " + conc + " to the players!");
					handleCommand(askInput(getInputMessage()));
				} else {
					inputComplete("You can only play with up to 4 players");
				}
				break;
			case SETCARDPACK:
				break;
			default:
				break;
		}
	}
	
	public void done() {
		switch(getState()) {
			case ADDPLAYERS:
				if(game.getPlayers().size() < 2) {
					inputComplete("You need at least 2 players to start a game");
					break;
				}
				setState(State.SETCARDPACK);
				display();
				handleCommand(askInput("Pick a cardset"));
				break;
			case SETCARDPACK:
				break;
			default:
				break;
		}
	}
	
	public LocalGame getGame() {
		return game;
	}
	
	public void help() {
		print("Just testing");
		handleCommand(askInput(getInputMessage()));
	}
	
	public void print() {
		System.out.println("");
	}
	
	public void print(String message) {
		System.out.println(message);
	}
	
	public String getInputMessage() {
		return inputMessage;
	}
	
	public void inputComplete(String message) {
		print(message);
		handleCommand(askInput(getInputMessage()));
	}
	
	@SuppressWarnings("resource")
	public String askInput(String message) {
		String in;
		this.inputMessage = message;
		System.out.print(message + " > ");
		Scanner input = new Scanner(System.in);
	    in = input.nextLine();
	    input.reset();
	    return in;
	}
	
	public Map<String, String> params(String... params) {
		Map<String, String> map = new HashMap<>();
		for(String param : params) {
			String[] par = param.split("=");
			if(par.length == 2)
				map.put(par[0], par[1]);
		}
		return map;
	}

}
