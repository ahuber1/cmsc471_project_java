package coup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;

public class Driver {

	public static void main(String[] args) {
		
		long timeDifferences = 0;
		File file = new File("output.txt");
		PrintStream stream = null;
		try {
			stream = new PrintStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-2);
		}
		System.setOut(stream);
		for (int i = 0; i < 10; i++) {
			long startTime = System.currentTimeMillis();
			Game game = new Game(new Agent("Agent 1"), 
					new Agent("Agent 2"), 
					new Agent("Agent 3"), 
					new Agent("Agent 4"), 
					new Agent("Agent 5"), 
					new Agent("Agent 6"));
			
			game.dealCards();
			game.giveCoinsToAllPlayers(2);
			
			while (game.winner() == null) {
				game = nextIteration(game);
			}
			
			System.out.printf("%s won!\n", game.winner().name);
			System.out.println("----------------------------------------------------------------");
			long endTime = System.currentTimeMillis();
			timeDifferences = timeDifferences + (endTime - startTime);
		}
		
		double average = (timeDifferences * 0.001) / 10.0;
		
		System.out.printf("Average time elapsed per game (in seconds): %.2f\n", average);
		
		stream.close();
	}
	
	public static Game nextIteration(Game game) {
		System.out.printf("It is %s's turn\n", game.players[game.currentPlayer].name);
		Game action = game.players[game.currentPlayer].nextMove(game);
		action.restoreStepStack();
		Step actionStep = Utilities.xthLastItemOfStack(action.stepStack, 1);
		game.stepStack.push(actionStep);
		Player[] otherPlayers = action.getOtherPlayersExcept(game.players[game.currentPlayer]);
		ArrayList<Game> counteractions = new ArrayList<Game>();
		
		for (Player other : otherPlayers) {
			Game counteraction = other.requestCounteraction(actionStep, game, other);
			
			if (counteraction != null)
				counteractions.add(counteraction);
		}
		
		if (counteractions.size() > 0) {
			Random random = new Random();
			Game counteraction = counteractions.get(random.nextInt(counteractions.size()));
			counteraction.restoreStepStack();
			Step counteractionStep = Utilities.xthLastItemOfStack(counteraction.stepStack, 2);
			
			if (counteractionStep.effect instanceof Block || counteractionStep.effect instanceof Challenge) {
				game.stepStack.push(counteractionStep);
				Game challenge = game.players[game.currentPlayer].requestChallenge(counteractionStep, game, game.players[game.currentPlayer]);
				
				if (challenge != null) {
					challenge.restoreStepStack();
					Step challengeStep = challenge.stepStack.peek();
					game.stepStack.push(challengeStep);
				}
			}
		}	
		
		System.out.println(game);
		System.out.println();
		
		while(game.stepStack.size() > 0) {
			Step step = game.stepStack.pop();
			step.effect.execute(step.instigator, step.victim, step.ai, step.cardsToChallenge, game, false);
		}
		
		game.incrementPlayer();
		game.giveCoinsToAllPlayers(2);
		return game;
	}
}
