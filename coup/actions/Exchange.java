package coup.actions;

import java.util.ArrayList;

import coup.Agent;
import coup.Effect;
import coup.Game;
import coup.Player;
import coup.cards.Ambassador;
import coup.cards.Card;

public class Exchange extends Action {

	@Override
	public boolean execute(Player instigator, Player victim, Agent ai, Card[] cardsToExchange, Game game) {
		instigator = game.findPlayer(instigator);
		
		if (game.deckOfCards.size() >= 2) {
			game.deckOfCards.add(cardsToExchange[0]);
			game.deckOfCards.add(cardsToExchange[1]);
			instigator.cards.remove(cardsToExchange[0]);
			instigator.cards.remove(cardsToExchange[1]);
			instigator.cards.add(game.deckOfCards.poll());
			instigator.cards.add(game.deckOfCards.poll());
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
	public Card getCard() {
		return new Ambassador();
	}

	@Override
	public Card[] getPossibleBlocks() {
		return new Card[0];
	}
	
	@Override
	public ArrayList<Game> theorize(Effect parent, Player instigator, Player victim, Agent ai, Card[] cardsToExchange,
			Game game) {
		ArrayList<Game> list = new ArrayList<Game>();
		Player[] otherPlayers = game.getOtherPlayersExcept(instigator);
		if (game.deckOfCards.size() >= 2) {
			for (Player otherPlayer : otherPlayers) {
				for (int i = 0; i < instigator.cards.size(); i++) {
					for (int j = i + 1; j < instigator.cards.size(); j++) {
						cardsToExchange = new Card[2];
						cardsToExchange[0] = instigator.cards.get(i);
						cardsToExchange[1] = instigator.cards.get(j);
						list.addAll(super.theorize(this, instigator, otherPlayer, ai, cardsToExchange, game));
					}
				}
			}
		}
		return list;
	}

}
