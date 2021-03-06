package coup.actions;

import java.util.ArrayList;

import coup.Player;
import coup.characters.Character;
import coup.characters.Duke;
import coup.Effect;
import coup.Game;
import coup.Player;

public class ForeignAid extends Action {
	
	private static final int NUM_COINS = 2;

	@Override
	public boolean execute(Player instigator, Player victim, Player ai, Character[] cardsToExchange, Game game, boolean theorizing) {
		instigator = game.findPlayer(instigator);
//		victim = game.findPlayer(victim);
//		
//		Player temp = game.findPlayer(ai);
//		ai = temp == null ? null : (Agent) temp;
		instigator.numCoins += NUM_COINS;
		return true;
	}

	@Override
	public String getDescription() {
		return "Foreign Aid";
	}

	@Override
	public Character getCard() {
		return null;
	}

	@Override
	public Character[] getPossibleBlocks() {
		Character[] cards = {new Duke()};
		return cards;
	}
	
	@Override
	public ArrayList<Game> theorize(Effect parent, Player instigator, Player victim, Player ai, Character[] cardsToChallenge,
			Game game) {
		Player[] otherPlayers = game.getOtherPlayersExcept(instigator);
		ArrayList<Game> list = new ArrayList<Game>();
		
		for (Player player : otherPlayers) {
			if (!player.lost()) {
				list.addAll(super.theorize(this, instigator, player, ai, cardsToChallenge, game));
			}
		}
		
		return list;
	}
}