package coup;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import coup.cards.Ambassador;
import coup.cards.Assassin;
import coup.cards.Captain;
import coup.cards.Card;
import coup.cards.Contessa;
import coup.cards.Duke;

public class Game {
	
	public static enum ExecutionType {ACTION, COUNTERACTION, CHALLENGE};
	
	public Player[] players;
	public ConcurrentLinkedQueue<Card> deckOfCards;
	public Game parentGame;
	public int currentPlayer;
	public Stack<Step> stepStack;
	public Stack<Step> backupStepStack;
	
	public Game(Player ... players) {
		this.players = players;
		this.parentGame = null;
		this.currentPlayer = 0;
		this.stepStack = new Stack<Step>();
		this.backupStepStack = new Stack<Step>();
		makeDeck();
	}
	
	public Game(Game game) {
		this.players = copyPlayers(game.players);
		this.currentPlayer = game.currentPlayer;
		this.deckOfCards = new ConcurrentLinkedQueue<Card>(game.deckOfCards);
		this.stepStack = copyStack(game.stepStack); 
		this.backupStepStack = new Stack<Step>();
		this.backupStepStack = copyStack(game.backupStepStack);
	}
	
	private static <T> Stack<T> copyStack(Stack<T> originalStack) {
		Stack<T> tempStack = new Stack<T>();
		Stack<T> newStack = new Stack<T>();
		
		while(!originalStack.isEmpty()) {
			tempStack.add(originalStack.pop());
		}
		
		while(!tempStack.isEmpty()) {
			T data = tempStack.pop();
			originalStack.add(data);
			newStack.add(data);
		}
		
		return newStack;
	}
	
	private Player[] copyPlayers(Player[] originals) {
		Player[] copies = new Player[originals.length];
		
		for (int i = 0; i < originals.length; i++) 
			copies[i] = originals[i].copy();
		
		return copies;
	}

	public void dealCards() {
		for (int i = 0; i < 2; i++) {
			for (Player player : players) {
				player.cards.add(deckOfCards.poll());
			}
		}
	}
	
	public void giveCoinsToAllPlayers(int numCoins) {
		for (Player player : players) {
			player.numCoins += numCoins;
		}
	}
	
	public Player winner() {
		Player winner = null;
		int counter = 0;
		for (Player player : players) {
			if (player.cards.size() > 0) {
				winner = player;
				counter++;
			}
		}
		if (counter == 1)
			return winner;
		else
			return null;
	}

	private void makeDeck() {
		deckOfCards = new ConcurrentLinkedQueue<Card>();
		
		Card[] cards = {new Ambassador(), new Assassin(), new Captain(), new Contessa(), new Duke()};
		int[] counter = new int[cards.length];
		final int MAX = counter.length * 3;
		Random rand = new Random();
		for (int i = 0; i < MAX; ) {
			int index = rand.nextInt(cards.length);
			if(counter[index] < 3) {
				counter[index]++;
				deckOfCards.add(cards[index]);
				i++;
			}
		}
	}

	public void incrementPlayer() {
		this.currentPlayer++;
		
		if (this.currentPlayer == this.players.length)
			this.currentPlayer = 0;
	}

	public int depth() {
		Game game = this;
		int depth = 0;
		
		while (game != null) {
			depth++;
			game = game.parentGame;
		}
		
		return depth;
	}

	public Player[] getOtherPlayersExcept(Player target) {
		ArrayList<Player> others = new ArrayList<Player>();
		
		for (Player player : players) {
			if (player != target) {
				others.add(player);
			}
		}
		
		return others.toArray(new Player[others.size()]);
	}

	public Player findPlayer(Player player) {
		if (player == null)
			return null;
		else {
			for (Player p : this.players) {
				if (p.name.equals(player.name))
					return p;
			}
			
			return null;
		}
	}

	public void describeStack() {
		for (Step step : stepStack) {
			System.out.println(step.toString());
		}
		System.out.println();
	}

	public void backupStack() {
		backupStepStack.clear();
		backupStepStack = copyStack(stepStack);
	}

	public void restoreStepStack() {
		this.stepStack.clear();
		stepStack = copyStack(backupStepStack);
	}
}