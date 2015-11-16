package coup.actions;

import java.util.ArrayList;

import coup.Player;
import coup.Challenge;
import coup.Effect;
import coup.Game;
import coup.Player;
import coup.Step;
import coup.characters.Character;

public abstract class Action extends Effect {
	
	public abstract Character[] getPossibleBlocks();
	
	public static void theorize(Effect parent, Player instigator, Player victim, Player ai, Character[] cardsToChallenge, Game game, ArrayList<Game> list) {
		if (parent instanceof Action) {
			Action parentAction = (Action) parent;
			Character[] counteractions = parentAction.getPossibleBlocks();
			for (Character counter : counteractions) {
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
		}
		else {
			throw new IllegalArgumentException("parent must be a parent of Action");
		}
	}
	
	@Override
	public ArrayList<Game> theorize(Effect parent, Player instigator, Player victim, Player ai,
			Character[] cardsToChallenge, Game game) {
		ArrayList<Game> list = new ArrayList<Game>();
		Action.theorize(parent, instigator, victim, ai, cardsToChallenge, game, list);
		return list;
	}
}
