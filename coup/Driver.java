package coup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

public class Driver {
	
	public static TreeMap<String, Integer> effectCounter = new TreeMap<String, Integer>();

	public static void main(String[] args) {
		
		long timeDifferences = 0;
//		File file = new File("output.txt");
//		PrintStream stream = null;
//		try {
//			stream = new PrintStream(file);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			System.exit(-2);
//		}
//		System.setOut(stream);
		for (int i = 0; i < 3; i++) {
			System.out.println(i + 1);
			Random random = new Random();
			int numPlayers =  6; //random.nextInt(5) + 2; // new random int from 2 to 6
			Player[] players = new Player[numPlayers];
			
			for (int j = 0; j < players.length; j++)
				players[j] = new Player(String.format("Agent %d", j + 1));
			
			long startTime = System.currentTimeMillis();
			Game game = new Game(players);
			
			game.dealCards();
			game.giveCoinsToAllPlayers(2);
			
			int counter = 1;
			while (game.winner() == null) {
				game = nextIteration(game);
				//System.out.printf("Iteration Counter: %d\n", counter);
				counter++;
			}
			
			long endTime = System.currentTimeMillis();
			timeDifferences = timeDifferences + (endTime - startTime);
			System.out.printf("%s won!\n", game.winner().name);
			System.out.println(((endTime - startTime) * 0.001) / 60.0);
			//System.out.println("----------------------------------------------------------------");
		}
		
		double average = (timeDifferences * 0.001) / 10.0;
		
		System.out.printf("Average time elapsed per game (in seconds): %.2f\n", average);
		
		Set<String> keySet = effectCounter.keySet();
		String[] keyArr = keySet.toArray(new String[keySet.size()]);
		Arrays.sort(keyArr);
		
		for (String key : keyArr) {
			System.out.printf("%50s     %d\n", key, effectCounter.get(key).intValue());
		}
	}
	
	public static Game nextIteration(Game game) {
		//System.out.printf("It is %s's turn\n", game.players[game.currentPlayer].name);
		Game action = Utilities.performMove(game.players[game.currentPlayer], game);
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
				
				if (challenge != null)
					challenge.restoreStepStack();
				
				// If the current player found a state he/she desires and it will challenge (i.e., the step stack has 3 items in it)
				if (challenge != null && challenge.stepStack.size() == 3) { 
					//challenge.restoreStepStack();
					Step challengeStep = challenge.stepStack.peek();
					game.stepStack.push(challengeStep);
				}
			}
		}	
		
		//System.out.println(game);
		//System.out.println();
		
		for (Step step : game.stepStack) {
			Integer counter = effectCounter.get(step.effect.getDescription());
			counter = (counter == null ? 1 : counter + 1);
			effectCounter.put(step.effect.getDescription(), counter);
		}
		
		game.executeSteps();		
		game.incrementPlayer();
		game.giveCoinsToAllPlayers(2);
		return game;
	}
}
