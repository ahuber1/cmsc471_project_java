package coup;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import coup.actions.Action;
import coup.cards.Card;

public class Player {
	
	public ArrayList<Card> cards;
	public int numCoins;
	public String name;
	public boolean lost;
	
	public Player(String name) {
		this.cards = new ArrayList<Card>();
		this.numCoins = 0;
		this.name = name;
		this.lost = false;
	}

	public Game requestCounteraction(Step actionStep, Game game, Player instigatorOfCounteraction) {
		if (actionStep.effect instanceof Action) {
			Action action = (Action) actionStep.effect;
			Card[] blocks = action.getPossibleBlocks();
			ArrayList<Game> list = new ArrayList<Game>();
			ConcurrentLinkedQueue<Game> q = new ConcurrentLinkedQueue<Game>();
			
			for (Card counter : blocks) {
				Game gameCopy = new Game(game);
				gameCopy.clearStacks();
				gameCopy.stepStack.add(actionStep);
				list.addAll(counter.getCounteraction().theorize(counter.getCounteraction(), instigatorOfCounteraction, actionStep.instigator, 
						this, actionStep.cardsToChallenge, gameCopy));
			}
			Challenge challenge = new Challenge();
			Game gameCopy = new Game(game);
			gameCopy.clearStacks();
			gameCopy.stepStack.add(actionStep);
			list.addAll(challenge.theorize(null, instigatorOfCounteraction, actionStep.instigator, this, actionStep.cardsToChallenge, gameCopy));
			
//			Make all the copy games reference the original game
//			Make copies of all the games
//			Increment the player in the copies
//			Distribute coins in the copies
//			Add the copies to the queue
//			RUN!
			
			for (Game copy : list) {
				Game copyOfCopy = new Game(copy);
				copy.parentGame = game;
				copyOfCopy.parentGame = copy;
				copy.backupStack();
				copyOfCopy.incrementPlayer();
				copyOfCopy.giveCoinsToAllPlayers(2);
				copyOfCopy.clearStacks();
				q.add(copyOfCopy);
			}
			
			Game g1 = Utilities.performMove(q, game, this);
			
			return g1.root();
		}
		else
			throw new IllegalStateException("The Effect object in actionStep must be of type Action");
	}
	
	public Game requestChallenge(Step counteractionStep, Game game, Player instigatorOfChallenge) {
		if (counteractionStep.effect instanceof Block) {
			Block block = (Block) counteractionStep.effect;
			Challenge challenge = new Challenge();
			ArrayList<Game> list = new ArrayList<Game>();
			ConcurrentLinkedQueue<Game> q = new ConcurrentLinkedQueue<Game>();
			list.addAll(challenge.theorize(block, instigatorOfChallenge, counteractionStep.instigator, this, 
				counteractionStep.cardsToChallenge, game));
			
//			Make all the copy games reference the original game
//			Make copies of all the games
//			Increment the player in the copies
//			Distribute coins in the copies
//			Add the copies to the queue
//			RUN!
			
			for (Game copy : list) {
				Game copyOfCopy = new Game(copy);
				copy.parentGame = game;
				copyOfCopy.parentGame = copy;
				copy.backupStack();
				copyOfCopy.incrementPlayer();
				copyOfCopy.giveCoinsToAllPlayers(2);
				copyOfCopy.clearStacks();
				q.add(copyOfCopy);
			}
			
			Game g1 = Utilities.performMove(q, game, this);
			
			return g1.root();
		}
		else if (counteractionStep.effect instanceof Challenge) {
			return null; // You cannot challenge a challenge
		}
		else {
			throw new IllegalStateException("The Effect object in counteractionStep must be of type Block or Challenge");
		}
	}
	
	public Player copy() {
		Player newAgent = new Player(this.name);
		newAgent.cards = new ArrayList<Card>(this.cards);
		newAgent.numCoins = this.numCoins;
		newAgent.lost = this.lost;
		return newAgent;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || (obj instanceof Player) == false)
			return false;
		else
			return this.name.equals(((Player) obj).name);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public void looses() {
		//this.cards.clear();
		//this.numCoins = 0;
		this.lost = true;
	}

	public boolean lost() {
		return lost;
	}

	public void stealCoins(int numCoins) {
		if (this.numCoins > numCoins) // only deduct coins if it will not go to a negative number
			this.numCoins -= numCoins;
	}
}