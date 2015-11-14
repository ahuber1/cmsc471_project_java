package coup;

public class Driver {

	public static void main(String[] args) {
		Game game = new Game(new Agent("Agent 1"), new Agent("Agent 2"));
		game.dealCards();
		game.giveCoinsToAllPlayers(2);
		
		while (game.winner() == null) {
			game = nextIteration(game);
		}
		
		System.out.printf("%d won!\n", game.winner().name);
	}
	
	public static Game nextIteration(Game game) {
		System.out.printf("It is %s's turn\n", game.players[game.currentPlayer].name);
		Game nextMove = game.players[game.currentPlayer].nextMove(game);
		nextMove.restoreStepStack();
		while(!nextMove.stepStack.isEmpty()) {
			Step step = nextMove.stepStack.pop();
			step.effect.execute(step.instigator, step.victim, step.ai, step.cardsToChallenge, game, false);
		}
		game.incrementPlayer();
		game.giveCoinsToAllPlayers(2);
		return game;
	}
}
