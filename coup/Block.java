package coup;

import java.util.ArrayList;

import coup.cards.Card;

public abstract class Block extends Effect {
	
	@Override
	public boolean execute(Player instigator, Player victim, Agent ai, Card[] cardsToExchange, Game game) {
		game.stepStack.clear(); // clear stack to action is not executed
		return true;
	}

	@Override
	public ArrayList<Game> theorize(Effect parent, Player instigator, Player victim, Agent ai, Card[] cardsToChallenge, Game game) {
		ArrayList<Game> list = new ArrayList<Game>();
		Game gameCopy = new Game(game);
		gameCopy.stepStack.add(new Step(parent, instigator, victim, ai, cardsToChallenge));
		Challenge challenge = new Challenge();
		list.addAll(challenge.theorize(null, victim, instigator, ai, cardsToChallenge, gameCopy));
		//gameCopy = new Game(game);
		//gameCopy.stepStack.add(new Step(parent, instigator, victim, ai, cardsToChallenge));
		//list.add(gameCopy);
		return list;
	}
}
