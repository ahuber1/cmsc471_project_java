package coup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import coup.actions.Action;
import coup.actions.Coup;
import coup.actions.ForeignAid;
import coup.actions.Income;
import coup.cards.Ambassador;
import coup.cards.Assassin;
import coup.cards.Captain;
import coup.cards.Card;
import coup.cards.Contessa;
import coup.cards.Duke;

public class Agent extends Player {
	
	public static final int MAX_QUEUE_SIZE = 2000;
	public static final Action[] ACTIONS = {new Income(), new ForeignAid(), new Coup()};
	public static final Card[] CARDS = {new Duke(), new Assassin(), new Ambassador(), new Captain(), new Contessa()};
	
	public Agent(String name) {
		super(name);
	}
	
	public Game nextMove(Game game) {
		ConcurrentLinkedQueue<Game> q = new ConcurrentLinkedQueue<Game>();
		q.add(game);
		Game g1 = nextMove(q, game).root();
		//System.out.println("----------------------------------------------------------------------");
		//System.out.printf("%s found a a goal state %d move(s) in advance\n", this.name, g1.depth());
		
//		while(true) {
//			try {
//				Thread.sleep(500);
//				break;
//			} catch (InterruptedException e) {
//				// Keep trying...
//			}
//		}
		
		return g1;
	}
	
	private static int depth = 0;
	private static int counter = 0;

	private Game nextMove(ConcurrentLinkedQueue<Game> q, Game origGame) {
		
		while (!q.isEmpty()) {
			Game g = q.poll();
			
			int d = g.depth();
			
			if (g.winner() != null && g.winner().equals(this))
				return g;
			else if (g.winner() != null && !g.winner().equals(this)) // another player won; don't expand
				continue; // analyze another state
			else if (d != depth && q.size() >= MAX_QUEUE_SIZE) {
				//System.out.printf("%s is calculating finding the best solution with a heuristic\n", this.name);
				TreeMap<Integer, ArrayList<Game>> games = new TreeMap<Integer, ArrayList<Game>>();
				int count = 0;
				
				while(!q.isEmpty()) {
					count++;
					
					//System.out.print(count);
					//System.out.print("\r");
					
					int heuristicValue = g.calculateHeuristic(this, origGame);
					ArrayList<Game> list = games.get(heuristicValue);
					
					if (list == null)
						list = new ArrayList<Game>();
					
					list.add(g);
					games.put(heuristicValue, list);
					g = q.poll();
				}
				
				Set<Integer> keySet = games.keySet();
				Integer[] keyArr = keySet.toArray(new Integer[keySet.size()]);
				Arrays.sort(keyArr);
				int maxNumIndicies = 5;
				Game[] bestGames = new Game[maxNumIndicies];
				int k = 0;
				
				for (int i = keyArr.length - 1; i >= 0; i--) {
					int key = keyArr[i];
					ArrayList<Game> list = games.get(key);
					for (int j = 0; j < list.size() && k < bestGames.length; j++) {
						Game game = list.get(j);
						if (k == 0) {
							bestGames[k] = game;
							k++;
						}
						else {
							Game root1 = bestGames[k - 1].root();
							Game root2 = game.root();
							Stack<Step> stack1 = Utilities.copyStack(root1.backupStepStack);
							Stack<Step> stack2 = Utilities.copyStack(root2.backupStepStack);
							Step step1 = null;
							Step step2 = null;
							
							while(!stack1.isEmpty())
								step1 = stack1.pop();
							
							while(!stack2.isEmpty())
								step2 = stack2.pop();
							
							if(!step1.effect.equals(step2.effect)) {
								bestGames[k] = game;
								k++;
							}
							
						}
					}
				}
				
				Random rand = new Random();
				Game bestMove = null;
				
				while (bestMove == null)
					bestMove = bestGames[rand.nextInt(bestGames.length)];
				
				//System.out.printf("\n%s found the best move\n", this.name);
				
				return bestMove;
			}
			else {	
				
				if (d != depth) {
					//System.out.printf("\n%s is considering possible moves %d move(s) in advance\n", this.name, d);
					depth = d;
					counter = 1;
				}
				else {
					counter++;
					//System.out.print(counter);
					//System.out.print("\r");
				}
				
				Player p = g.players[g.currentPlayer];
				
				for (Action action : ACTIONS) {
					ArrayList<Game> games = action.theorize(null, p, null, this, null, g);
					for (Game game : games) {
						boolean c = true;
						while (!game.stepStack.isEmpty() && c) {
							//System.out.println(action.getDescription());
							game.backupStack();
							//game.describeStack();
							Step step = game.stepStack.pop();
							c = step.effect.execute(step.instigator, step.victim, this, step.cardsToChallenge, game, true);
						}
						if (c) { // if all steps were successfully completed
							game.incrementPlayer();
							game.giveCoinsToAllPlayers(2);
							game.parentGame = g;
							//System.out.printf("stack-size = %d\tbackup-size = %d\n", game.stepStack.size(), game.backupStepStack.size());
							q.add(game);
						}
					}
				}
				
				for (Card card : CARDS) {
					Action action = card.getAction();
					if (action != null) {
						ArrayList<Game> games = action.theorize(null, p, null, this, null, g);
						for (Game game : games) {
							boolean c = true;
							while (!game.stepStack.isEmpty() && c) {
								//System.out.println(card.getName());
								//game.describeStack();
								game.backupStack();
								Step step = game.stepStack.pop();
								c = step.effect.execute(step.instigator, step.victim, this, step.cardsToChallenge, game, true);
							}
							if (c) { // if all steps were successfully completed
								game.incrementPlayer();
								game.giveCoinsToAllPlayers(2);
								assert game.stepStack.isEmpty();
								game.parentGame = g;
								//System.out.printf("stack-size = %d\tbackup-size = %d\n", game.stepStack.size(), game.backupStepStack.size());
								q.add(game);
							}
						}
					}
				}
			}
		}
		return null;
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
			
			Game g1 = nextMove(q, game);
			
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
			
			Game g1 = nextMove(q, game);
			
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
		
		Game g1 = nextMove(q, game);
		
		while (g1.parentGame != game)
			g1 = g1.parentGame;
		
		for (Step step : g1.stepStack) {
			if (step.effect instanceof Action) {
				return step.cardsToChallenge[0];
			}
		}
		
		return null;
	}
	
	@Override
	public Card[] getPossibleCardsToAssasinate(Game game, Agent ai) {
		if (this.equals(ai))
			return this.cards.toArray(new Card[this.cards.size()]);
		else
			return CARDS;
	}
	
	@Override
	public Player copy() {
		Agent newAgent = new Agent(this.name);
		newAgent.cards = new ArrayList<Card>(this.cards);
		newAgent.numCoins = this.numCoins;
		return newAgent;
	}
}
