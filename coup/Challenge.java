package coup;

import java.util.ArrayList;

import coup.cards.Card;

public class Challenge extends Effect {

	@Override
	public boolean execute(Player instigator, Player victim, Agent ai, Card[] cardsToExchange, Game game, boolean theorizing) {
		instigator = game.findPlayer(instigator);
		victim = game.findPlayer(victim);
		
		Player temp = game.findPlayer(ai);
		ai = temp == null ? null : (Agent) temp;
		
		if (theorizing) { // if we are theorizing, assume the worst-case scenario
			ai.cards.clear(); // assume worst-case scenario happens and the agent looses the game
		}
		else { // if we are not theorizing, investigate who would actually lose
			if (victim.cards.contains(game.stepStack.peek().effect.getCard()))
				victim.cards.clear();
			else
				instigator.cards.clear();
		}
		
		game.stepStack.clear(); // clear the step stack; nothing else needs to be done; this challenge determines the future state of the game
		
		return true;
	}

	@Override
	public String getDescription() {
		return "Challenge";
	}

	
	// Work in progress
	@Override
	public ArrayList<Game> theorize(Effect parent, Player instigator, Player victim, Agent ai, Card[] cardsToChallenge, Game game) {
		ArrayList<Game> list = new ArrayList<Game>();
		if (game.stepStack.peek().effect.getCard() != null) {
			if (game.players[game.currentPlayer].equals(ai)) {
				if (game.players[game.currentPlayer].cards.contains(game.stepStack.peek().effect.getCard())) { // If the current player contains the card that was previously played
					Game gameCopy = new Game(game);
					gameCopy.stepStack.add(new Step(this, instigator, victim, ai, cardsToChallenge));
					list.add(gameCopy);
				}
			}
			else {
				Game gameCopy = new Game(game);
				gameCopy.stepStack.add(new Step(this, instigator, victim, ai, cardsToChallenge));
				list.add(gameCopy);
			}
		}
		
		Game gameCopy = new Game(game);
		list.add(gameCopy);
		return list;
	}
	
	@Override
	public Card getCard() {
		return null;
	}

}
