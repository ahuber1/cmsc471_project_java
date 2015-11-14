package coup.actions;

import java.util.ArrayList;

import coup.Agent;
import coup.Challenge;
import coup.Effect;
import coup.Game;
import coup.Player;
import coup.Step;
import coup.cards.Card;

public abstract class Action extends Effect {
	
	public abstract Card[] getPossibleBlocks();
	
	@Override
	public ArrayList<Game> theorize(Effect parent, Player instigator, Player victim, Agent ai,
			Card[] cardsToChallenge, Game game) {
		ArrayList<Game> list = new ArrayList<Game>();
		if (parent instanceof Action) {
			Action parentAction = (Action) parent;
			Card[] counteractions = parentAction.getPossibleBlocks();
			for (Card counter : counteractions) {
				Game gameCopy = new Game(game);
				gameCopy.stepStack.add(new Step(parentAction, instigator, victim, ai, cardsToChallenge));
				list.addAll(counter.getCounteraction().theorize(counter.getCounteraction(), victim, instigator, ai, cardsToChallenge, gameCopy));
			}
			
			if (parent.getCard() != null) {
				Challenge challenge = new Challenge();
				Game gameCopy = new Game(game);
				gameCopy.stepStack.add(new Step(parent, instigator, victim, ai, cardsToChallenge));
				list.addAll(challenge.theorize(null, victim, instigator, ai, cardsToChallenge, gameCopy));
			}
			else {
				Game gameCopy = new Game(game);
				gameCopy.stepStack.add(new Step(parent, instigator, victim, ai, cardsToChallenge));
				list.add(gameCopy);
			}
			
			return list;
		}
		else {
			throw new IllegalArgumentException("parent must be a parent of Action");
		}
	}
}
