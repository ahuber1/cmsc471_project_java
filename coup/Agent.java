package coup;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import coup.actions.Action;
import coup.cards.Card;

public class Agent extends Player {
	
	public Agent(String name) {
		super(name);
	}

	@Override
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
	
	@Override
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

	@Override
	public Card revealCard(Game game, Agent ai, Effect effect) {
		ConcurrentLinkedQueue<Game> q = new ConcurrentLinkedQueue<Game>();
		Card[] possibleCardsToAssasinate = getPossibleCardsToAssasinate(game, ai);
		
		for (Card possibleCard : possibleCardsToAssasinate) {
			Card[] cardsToExchange = new Card[1];
			cardsToExchange[0] = possibleCard;
			q.addAll(effect.theorize(null, game.players[game.currentPlayer], this, ai, cardsToExchange, game));
		}
		
		Game g1 = Utilities.performMove(q, game, this);
		
		if (g1 != null) {
			while (g1 != null && g1.parentGame != game)
				g1 = g1.parentGame;
			
			if (g1 != null) {
				for (Step step : g1.stepStack) {
					if (step.effect instanceof Action) {
						return step.cardsToChallenge[0];
					}
				}
			}
		}
		
		Random random = new Random();
		return cards.get(random.nextInt(cards.size())); // return random card from hand; we're going to lose anyway
	}
	
	@Override
	public Card[] getPossibleCardsToAssasinate(Game game, Agent ai) {
		if (this.equals(ai))
			return this.cards.toArray(new Card[this.cards.size()]);
		else
			return Utilities.CARDS;
	}
	
	@Override
	public Player copy() {
		Agent newAgent = new Agent(this.name);
		newAgent.cards = new ArrayList<Card>(this.cards);
		newAgent.numCoins = this.numCoins;
		newAgent.lost = this.lost;
		return newAgent;
	}
}