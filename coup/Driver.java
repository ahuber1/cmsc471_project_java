package coup;

public class Driver {

	public static void main(String[] args) {
		Game game = new Game(new Agent("Agent 1"), new Agent("Agent 2"));
		game.dealCards();
		game.giveCoinsToAllPlayers(2);
		
		while (game.winner() == null) {
			game = nextIteration(game);
		}
	}
	
	public static Game nextIteration(Game game) {
		Game nextMove = game.players[game.currentPlayer].nextMove(game);
		nextMove.restoreStepStack();
		while(!nextMove.stepStack.isEmpty()) {
			Step step = nextMove.stepStack.pop();
			step.effect.execute(step.instigator, step.victim, step.ai, step.cardsToChallenge, nextMove);
		}
		nextMove.incrementPlayer();
		nextMove.giveCoinsToAllPlayers(2);
		return nextMove;
	}
}
