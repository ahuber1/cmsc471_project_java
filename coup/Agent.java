package coup;

import java.util.ArrayList;
import java.util.Stack;
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
	
	public static final Action[] ACTIONS = {new Income(), new ForeignAid(), new Coup()};
	public static final Card[] CARDS = {new Duke(), new Assassin(), new Ambassador(), new Captain(), new Contessa()};
	
	public Agent(String name) {
		super(name);
	}
	
	public Game nextMove(Game game) {
		ConcurrentLinkedQueue<Game> q = new ConcurrentLinkedQueue<Game>();
		q.add(game);
		Game g1 = nextMove(q);
		System.out.println("----------------------------------------------------------------------");
		System.out.printf("%s found a a goal state %d move(s) in advance\n", this.name, g1.depth());
		Stack<Game> stack = new Stack<Game>();
		
		while (g1.parentGame != game) {
			stack.add(g1);
			g1 = g1.parentGame;
		}
		
		while (!stack.isEmpty()) {
			System.out.printf(stack.pop().toString());
			if (!stack.isEmpty()) {
				System.out.println("                                  ***                                 ");
			}
		}
		
		return g1;
	}
	
	private static int depth = 0;
	private static int counter = 0;

	private Game nextMove(ConcurrentLinkedQueue<Game> q) {
		
		while (!q.isEmpty()) {
			Game g = q.poll();
			int d = g.depth();
			
			if (d != depth) {
				System.out.printf("\n%s is considering possible moves %d move(s) in advance\n", this.name, d);
				depth = d;
				counter = 1;
			}
			else {
				counter++;
				System.out.print(counter);
				System.out.print("\r");
			}
			
			
			if (g.winner() != null && g.winner().equals(this))
				return g;
			else if (g.winner() != null && !g.winner().equals(this)) // another player won; don't expand
				continue; // analyze another state
			else {
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
	
	private void printHandSize(Game game) {
		for (Player player : game.players) {
			System.out.println(player.cards.size());
		}
		
		System.out.println();
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
		
		Game g1 = nextMove(q);
		
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
