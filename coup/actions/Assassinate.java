package coup.actions;

import java.util.ArrayList;

import coup.Agent;
import coup.Effect;
import coup.Game;
import coup.Player;
import coup.cards.Assassin;
import coup.cards.Card;
import coup.cards.Contessa;

public class Assassinate extends Action {

	private static final int NUM_COINS = 3;
	
	@Override
	public boolean execute(Player instigator, Player victim, Agent ai, Card[] cardsToExchange, Game game) {
		
		instigator = game.findPlayer(instigator);
		victim = game.findPlayer(victim);
		
		Player temp = game.findPlayer(ai);
		ai = temp == null ? null : (Agent) temp;
		
		if (instigator.numCoins >= NUM_COINS) {
			instigator.numCoins -= NUM_COINS;
			Card revealedCard;
			if (cardsToExchange == null || cardsToExchange.length > 1)
				revealedCard = victim.revealCard(game, ai, this);
			else 
				revealedCard = cardsToExchange[0];
			
			victim.cards.remove(revealedCard);
			return true;
		}
		else
			return false;
	}

	@Override
	public String getDescription() {
		return "Assassinate";
	}

	@Override
	public Card[] getPossibleBlocks() {
		Card[] cards = {new Contessa()};
		return cards;
	}
	
	@Override
	public ArrayList<Game> theorize(Effect parent, Player instigator, Player victim, Agent ai, Card[] cardsToExchange,
			Game game) {
		ArrayList<Game> list = new ArrayList<Game>();
		
		if (instigator.numCoins >= NUM_COINS) {
			Player[] otherPlayers = game.getOtherPlayersExcept(instigator);
			for (Player otherPlayer : otherPlayers) {
				Card[] possibleCardsToAssasinate;
				if (cardsToExchange == null || cardsToExchange.length > 1) {
					possibleCardsToAssasinate = otherPlayer.getPossibleCardsToAssasinate(game, ai);
				}
				else {
					possibleCardsToAssasinate = cardsToExchange;
				}
				
				for (Card possibleCard : possibleCardsToAssasinate) {
					cardsToExchange = new Card[1];
					cardsToExchange[0] = possibleCard;
					list.addAll(super.theorize(this, instigator, otherPlayer, ai, cardsToExchange, game));
				}
			}
		}
		
		return list;
	}

	@Override
	public Card getCard() {
		return new Assassin();
	}

}