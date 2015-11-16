package coup.actions;

import java.util.ArrayList;

import coup.Player;
import coup.Utilities;
import coup.characters.Assassin;
import coup.characters.Character;
import coup.characters.Contessa;
import coup.Effect;
import coup.Game;
import coup.Player;

public class Assassinate extends Action {

	private static final int NUM_COINS = 3;
	
	public static void theorize(Effect parent, Player instigator, Player victim, Player ai, Character[] cardsToExchange,
			Game game, int numCoins, ArrayList<Game> list) {		
		if (instigator.numCoins >= numCoins) {
			Player[] otherPlayers = game.getOtherPlayersExcept(instigator);
			for (Player otherPlayer : otherPlayers) {
				Character[] possibleCardsToAssasinate;
				if (cardsToExchange == null || cardsToExchange.length > 1) {
					possibleCardsToAssasinate = Utilities.getPossibleCardsToAssasinate(game, ai);
				}
				else {
					possibleCardsToAssasinate = cardsToExchange;
				}
				
				for (Character possibleCard : possibleCardsToAssasinate) {
					cardsToExchange = new Character[1];
					cardsToExchange[0] = possibleCard;
					Action.theorize(parent, instigator, otherPlayer, ai, cardsToExchange, game, list);
				}
			}
		}
	}
	
	public static boolean execute(Effect effect, int numCoins, Player instigator, Player victim, Player ai, Character[] cardsToExchange, Game game, boolean theorizing) {
		
		if(theorizing == false)
			System.out.printf("");
		
		instigator = game.findPlayer(instigator);
		victim = game.findPlayer(victim);
		
		Player temp = game.findPlayer(ai);
		ai = temp == null ? null : (Player) temp;
		
		if (instigator.numCoins >= numCoins) {
			instigator.numCoins -= numCoins;
			boolean isVictimAgent = victim.equals(ai);
			
			if (isVictimAgent && !(theorizing)) {
				
				int index = Utilities.revealCard(victim, game);
				if (index > 0) {
					Character cardToReveal = victim.cards.get(Utilities.revealCard(victim, game));				
					return victim.cards.remove(cardToReveal);
				}
				else {
					return false;
				}
			}
			else if (isVictimAgent) {
				return victim.cards.contains(cardsToExchange[0]);
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
	public boolean execute(Player instigator, Player victim, Player ai, Character[] cardsToExchange, Game game, boolean theorizing) {
		return Assassinate.execute(this, NUM_COINS, instigator, victim, ai, cardsToExchange, game, theorizing);
	}

	@Override
	public String getDescription() {
		return "Assassinate";
	}

	@Override
	public Character[] getPossibleBlocks() {
		Character[] cards = {new Contessa()};
		return cards;
	}
	
	@Override
	public ArrayList<Game> theorize(Effect parent, Player instigator, Player victim, Player ai, Character[] cardsToExchange,
			Game game) {
		ArrayList<Game> list = new ArrayList<Game>();
		Assassinate.theorize(this, instigator, victim, ai, cardsToExchange, game, NUM_COINS, list);
		return list;
	}

	@Override
	public Character getCard() {
		return new Assassin();
	}

}