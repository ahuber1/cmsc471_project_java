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
		this.stepStack = Utilities.copyStack(game.stepStack); 
		this.backupStepStack = new Stack<Step>();
		this.backupStepStack = Utilities.copyStack(game.backupStepStack);
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
			if (!player.lost()) {
				player.numCoins += numCoins;
			}
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
		ArrayList<Card> unshuffledDeck = new ArrayList<Card>();
		
		for (Card card : cards) {
			for (int i = 0; i < 3; i++) {
				unshuffledDeck.add(card);
			}
		}
		
		ArrayList<Card> shuffledDeck = null;
		Random random = new Random();
		
		for (int i = 0; i < 10; i++) {
			shuffledDeck = new ArrayList<Card>();
			while(!unshuffledDeck.isEmpty()) {
				shuffledDeck.add(unshuffledDeck.remove(random.nextInt(unshuffledDeck.size())));
			}
			unshuffledDeck = shuffledDeck;
		}
		
		deckOfCards.addAll(shuffledDeck);
	}

	public void incrementPlayer() {
		this.currentPlayer++;
		
		if (this.currentPlayer == this.players.length)
			this.currentPlayer = 0;
		
		if (this.players[this.currentPlayer].lost())
			incrementPlayer(); // increment the player again
	}

	public int depth() {
		Game game = this;
		int depth = 0;
		
		while (game != null) {
			depth++;
			game = game.parentGame;
		}
		
		return depth - 1;
	}

	public Player[] getOtherPlayersExcept(Player target) {
		ArrayList<Player> others = new ArrayList<Player>();
		
		for (Player player : players) {
			if (!player.equals(target) && !player.lost()) {
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
		backupStepStack = Utilities.copyStack(stepStack);
	}

	public void restoreStepStack() {
		this.stepStack.clear();
		stepStack = Utilities.copyStack(backupStepStack);
	}
	
	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("Depth: %d\n", depth()));
		builder.append(String.format("Current Player: %s\n", players[currentPlayer]));
		builder.append("Players: \n");
		int activePlayers = 0;
		for (Player player : players) {
			if (!player.lost()) {
				builder.append(String.format("\tPlayer Name: %s\n", player.name));
				builder.append(String.format("\tNum Coins: %d\n", player.numCoins));
				builder.append("\tCards:\n");
				
				for (Card card : player.cards) {
					builder.append(String.format("\t\t%s\n", card.getName()));
				}
				
				builder.append("\n");
				activePlayers++;
			}
		}
		
		builder.append("Deck:\n");
		
		for (Card card : deckOfCards) {
			builder.append(String.format("\t%s\n", card.getName()));
		}
		
		builder.append("Step Stack: \n");
		
		for (Step step : stepStack) {
			builder.append(String.format("\tInstigator: %s\n", step.instigator));
			builder.append(String.format("\tVictim: %s\n", step.victim));
			builder.append(String.format("\tAI: %s\n", step.ai.name));
			builder.append(String.format("\tEffect: %s\n", step.effect.getDescription()));
			builder.append("\tCards to Exchange:\n");
			
			if (step.cardsToChallenge != null) {
				for(Card card : step.cardsToChallenge) {
					builder.append(String.format("\t\t%s\n", card.getName()));
				}
				builder.append("\n");
			}
			builder.append("\n");
		}
		
		builder.append("Backup: \n");
		
		for (Step step : backupStepStack) {
			builder.append(String.format("\tInstigator: %s\n", step.instigator));
			builder.append(String.format("\tVictim: %s\n", step.victim));
			builder.append(String.format("\tAI: %s\n", step.ai.name));
			builder.append(String.format("\tEffect: %s\n", step.effect.getDescription()));
			builder.append("\tCards to Exchange:\n");
			
			if (step.cardsToChallenge != null) {
				for(Card card : step.cardsToChallenge) {
					builder.append(String.format("\t\t%s\n", card.getName()));
				}
				builder.append("\n");
			}
			builder.append("\n");
		}		
		
		builder.append(String.format("Active Players: %d", activePlayers));
		
		return builder.toString();
	}

	public int calculateHeuristic(Player inquirer, Game origGame) {
		int x = 0;
		
		for (Player player : players) {
			
			int coinsGained = player.numCoins - origGame.findPlayer(player).numCoins;
			int cardsLost = origGame.findPlayer(player).cards.size() - player.cards.size();
			
			if (player.equals(inquirer)) {
				x += (players.length * players.length * coinsGained) + cardsLost;
			}
			else {
				x += coinsGained + (players.length * players.length * cardsLost);
			}
		}
		
		return x;
	}

	public Game root() {
		if (parentGame == null)
			return this;
		else {
			Game game = this;
			while (game.parentGame.parentGame != null) {
				game = game.parentGame;
			}
			return game;
		}
	}

	public void clearStacks() {
		backupStepStack.clear();
		stepStack.clear();
	}
}