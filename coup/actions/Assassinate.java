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
	
	public static boolean execute(Effect effect, int numCoins, Player instigator, Player victim, Agent ai, Card[] cardsToExchange, Game game, boolean theorizing) {
		instigator = game.findPlayer(instigator);
		victim = game.findPlayer(victim);
		
		Player temp = game.findPlayer(ai);
		ai = temp == null ? null : (Agent) temp;
		
		if (instigator.numCoins >= numCoins) {
			boolean isVictimAgent = victim.equals(ai);
			
			if (isVictimAgent || !(theorizing)) {
				
				Card cardToReveal;
				
				if (cardsToExchange != null && cardsToExchange.length == 1) { // if we are contemplating the possibility of removing a card
					cardToReveal = cardsToExchange[0];
				}
				else {
					cardToReveal = victim.revealCard(game, ai, effect);
				}
				
				victim.cards.remove(cardToReveal);
				return true;
			}
			else {
				if (victim.cards.size() > 0) {
					victim.cards.remove(0); // remove a card (blindly)
					return true;
				}
				else {
					return false;
				}
			}
		}
		else
			return false;
	}
	
	@Override
	public boolean execute(Player instigator, Player victim, Agent ai, Card[] cardsToExchange, Game game, boolean theorizing) {
		return Assassinate.execute(this, NUM_COINS, instigator, victim, ai, cardsToExchange, game, theorizing);
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