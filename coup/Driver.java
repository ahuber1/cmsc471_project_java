package coup;

import java.util.ArrayList;
import java.util.Random;

public class Driver {

	public static void main(String[] args) {
		for (int i = 0; i < 1000; i++) {
			Game game = new Game(new Agent("Agent 1"), new Agent("Agent 2"));
			game.dealCards();
			game.giveCoinsToAllPlayers(2);
			
			while (game.winner() == null) {
				game = nextIteration(game);
			}
			
			System.out.printf("%s won!\n", game.winner().name);
			System.out.println("----------------------------------------------------------------");
		}
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
