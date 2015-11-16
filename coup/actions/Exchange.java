package coup.actions;

import java.util.ArrayList;

import coup.Player;
import coup.Effect;
import coup.Game;
import coup.Player;
import coup.Utilities;
import coup.characters.Ambassador;
import coup.characters.Character;

public class Exchange extends Action {

	@Override
	public boolean execute(Player instigator, Player victim, Player ai, Character[] cardsToExchange, Game game, boolean theorizing) {
		instigator = game.findPlayer(instigator);
		
		if (game.deckOfCards.size() >= 2) {
			game.deckOfCards.add(instigator.cards.remove(0));
			game.deckOfCards.add(instigator.cards.remove(0));
			
			if (theorizing) {
				instigator.cards.add(cardsToExchange[0]);
				instigator.cards.add(cardsToExchange[1]);
			}
			else {
				instigator.cards.add(game.deckOfCards.poll());
				instigator.cards.add(game.deckOfCards.poll());
			}
			
			return true;
		}
		else
			return false;
	}

	@Override
	public String getDescription() {
		return "Exchange";
	}

	@Override
	public Character getCard() {
		return new Ambassador();
	}

	@Override
	public Character[] getPossibleBlocks() {
		return new Character[0];
	}
	
	@Override
	public ArrayList<Game> theorize(Effect parent, Player instigator, Player victim, Player ai, Character[] cardsToExchange,
			Game game) {
		ArrayList<Game> list = new ArrayList<Game>();
		Player[] otherPlayers = game.getOtherPlayersExcept(instigator);
		if (game.deckOfCards.size() >= 2 && instigator.cards.size() == 2) {
			for (Player otherPlayer : otherPlayers) {
				for (int i = 0; i < Utilities.CARDS.length; i++) {
					for (int j = 0; j < Utilities.CARDS.length; j++) {
						cardsToExchange = new Character[2];
						cardsToExchange[0] = Utilities.CARDS[i];
						cardsToExchange[1] = Utilities.CARDS[j];
						list.addAll(super.theorize(this, instigator, otherPlayer, ai, cardsToExchange, game));
					}
				}
			}
		}
		return list;
	}

}
