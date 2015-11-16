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
import coup.characters.Ambassador;
import coup.characters.Assassin;
import coup.characters.Captain;
import coup.characters.Character;
import coup.characters.Contessa;
import coup.characters.Duke;

public class Utilities {
	
	public static final int MAX_QUEUE_SIZE = 2000;
	public static final Action[] ACTIONS = {new Income(), new ForeignAid(), new Coup()};
	public static final Character[] CARDS = {new Duke(), new Assassin(), new Ambassador(), new Captain(), new Contessa()};
	
	public static <T> Stack<T> copyStack(Stack<T> originalStack) {
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
	
	public static <T> T xthLastItemOfStack(Stack<T> originalStack, int x) {
		Stack<T> copyStack = Utilities.copyStack(originalStack);
		
		while(copyStack.size() > x)
			copyStack.pop();
		
		return copyStack.pop();
	}
	
	public static Step performMove(Player agent, Game game) {
		ConcurrentLinkedQueue<Game> q = new ConcurrentLinkedQueue<Game>();
		q.add(game);
		Game g1 = performMove(q, game, agent).root();		
		g1.restoreStepStack();
		Step actionStep = Utilities.xthLastItemOfStack(g1.stepStack, 1);
		return actionStep;
	}
	
	public static int depth = 0;
	public static int counter = 0;
	
	public static Game performMove(ConcurrentLinkedQueue<Game> q, Game origGame, Player agent) {
		
		while (!q.isEmpty()) {
			Game g = q.poll();			
			int d = g.depth();
			
			if (g.winner() != null && g.winner().equals(agent)) {
				System.out.println("Found goal state");
				Game copy = new Game(g);
				copy.parentGame = g.parentGame;
				copy.restoreStepStack();
				if (testMove(copy))
					return g;
				else
					continue; // skip this game; it is invalid
			}
			else if (g.winner() != null && !g.winner().equals(agent)) // another player won; don't expand
				continue; // analyze another state
			else if (d != depth && q.size() >= MAX_QUEUE_SIZE) {
				return performMoveWithHeuristic(q, g, origGame, agent);
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
					ArrayList<Game> games = action.theorize(null, p, null, agent, null, g);
					for (Game game : games) {
						boolean c = true;
						while (!game.stepStack.isEmpty() && c) {
							//System.out.println(action.getDescription());
							game.backupStack();
							//game.describeStack();
							Step step = game.stepStack.pop();
							c = step.effect.execute(step.instigator, step.victim, agent, step.cardsToChallenge, game, true);
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
				
				for (Character card : CARDS) {
					Action action = card.getAction();
					if (action != null) {
						ArrayList<Game> games = action.theorize(null, p, null, agent, null, g);
						for (Game game : games) {
							boolean c = true;
							while (!game.stepStack.isEmpty() && c) {
								//System.out.println(card.getName());
								//game.describeStack();
								game.backupStack();
								Step step = game.stepStack.pop();
								//System.out.println(step.effect.getDescription());
								c = step.effect.execute(step.instigator, step.victim, agent, step.cardsToChallenge, game, true);
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
	
	public static Game performMoveWithHeuristic(ConcurrentLinkedQueue<Game> q, Game g, Game origGame, Player agent) {
		//System.out.printf("%s is calculating finding the best solution with a heuristic\n", this.name);
		TreeMap<Integer, ArrayList<Game>> games = new TreeMap<Integer, ArrayList<Game>>();
		int count = 0;
		
		while(!q.isEmpty()) {
			count++;
			
			//System.out.print(count);
			//System.out.print("\r");
			
			int heuristicValue = g.calculateHeuristic(agent, origGame);
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
		//int maxNumIndicies = 5;
		//Game[] bestGames = new Game[maxNumIndicies];
		//int k = 0;
		
		Random rand = new Random();
		
		for (int i = keyArr.length - 1; i >= 0; i--) {
			int key = keyArr[i];
			ArrayList<Game> list = games.get(key);
			boolean validMove = false;
			
			while (validMove == false && list.size() > 0) {
				int index = rand.nextInt(list.size());
				Game bestMove = list.get(index);
				validMove = testMove(bestMove);
				
				if (validMove == false) {
					list.remove(index);
					//System.out.println("Trying another game...");
				}
				else {
					//System.out.printf("\n%s found the best move\n", this.name);
					
					if (bestMove == null)
						throw new IllegalStateException();
					
					return bestMove;
				}
			}
		}
		
		throw new IllegalStateException();
	}

	
	public static boolean testMove(Game move) {
		Game copy = new Game(move);
		copy.restoreStepStack();
		return copy.executeSteps();
	}
	
	public static int revealCard(Player player, Game game) {
		ConcurrentLinkedQueue<Game> q = new ConcurrentLinkedQueue<Game>();
		Character[] possibleCardsToAssasinate = getPossibleCardsToAssasinate(game, player);
		
		for (Character possibleCard : possibleCardsToAssasinate) {
			Character[] cardsToExchange = new Character[1];
			cardsToExchange[0] = possibleCard;
			q.addAll(game.stepStack.peek().effect.theorize(null, game.players[game.currentPlayer], player, player, cardsToExchange, game));
		}
		
		Game g1 = Utilities.performMove(q, game, player);
		
		if (g1 != null) {
			while (g1 != null && g1.parentGame != game)
				g1 = g1.parentGame;
			
			if (g1 != null) {
				for (Step step : g1.stepStack) {
					if (step.effect instanceof Action) {
						return player.cards.indexOf(step.cardsToChallenge[0]) + 1; // returns 1 or 2
					}
				}
			}
		}
		
		if (player.cards.size() > 0) {
			Random random = new Random();
			return random.nextInt(player.cards.size()); // returns 1 or 2
		}
		else
			return -1;
	}
	
	public static Character[] getPossibleCardsToAssasinate(Game game, Player player) {
		if (game.players[game.currentPlayer].equals(player))
			return player.cards.toArray(new Character[player.cards.size()]);
		else
			return Utilities.CARDS;
	}
	
	public static Game blockMove(Step move, Player player, Game game, Player source, Player target) {
		if (move.effect instanceof Action) {
			Action action = (Action) move.effect;
			Character[] blocks = action.getPossibleBlocks();
			ArrayList<Game> list = new ArrayList<Game>();
			ConcurrentLinkedQueue<Game> q = new ConcurrentLinkedQueue<Game>();
			
			for (Character counter : blocks) {
				Game gameCopy = new Game(game);
				gameCopy.clearStacks();
				gameCopy.stepStack.add(move);
				list.addAll(counter.getCounteraction().theorize(counter.getCounteraction(), source, move.instigator, 
						player, move.cardsToChallenge, gameCopy));
			}
			
			// ===========================================================================================================
			// THIS CODE SHOULD NOT APPEAR IN THE LISP VERSION. HOWEVER, BECAUSE I CONSIDER A CHALLENGE A TYPE OF
			// COUNTERACTION, WE NEED THIS CODE IN ORDER FOR THE GAME TO END. OTHERWISE, THE GAME WILL NEVER END!
			// ===========================================================================================================
			Challenge challenge = new Challenge();
			Game gameCopy = new Game(game);
			gameCopy.clearStacks();
			gameCopy.stepStack.add(move);
			list.addAll(challenge.theorize(null, source, move.instigator, player, move.cardsToChallenge, gameCopy));
			// ===========================================================================================================
			// ===========================================================================================================
			
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
			
			Game g1 = Utilities.performMove(q, game, player);
			
			if (g1 == null)
				return null;
			else
				return g1.root();
		}
		else
			throw new IllegalStateException("The Effect object in actionStep must be of type Action");
	}
}