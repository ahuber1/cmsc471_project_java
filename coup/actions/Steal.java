package coup.actions;

import java.util.ArrayList;

import coup.Player;
import coup.characters.Ambassador;
import coup.characters.Captain;
import coup.characters.Character;
import coup.Effect;
import coup.Game;
import coup.Player;

public class Steal extends Action {
	
	private static final int NUM_COINS = 2;

	@Override
	public boolean execute(Player instigator, Player victim, Player ai, Character[] cardsToExchange, Game game, boolean theorizing) {
		
		instigator = game.findPlayer(instigator);
		victim = game.findPlayer(victim);
		
		if (victim.numCoins >= NUM_COINS) {
			victim.stealCoins(NUM_COINS);
			instigator.numCoins += NUM_COINS;
			return true;
		}
		else
			return false;
	}

	@Override
	public String getDescription() {
		return "Steal";
	}

	@Override
	public Character getCard() {
		return new Captain();
	}

	@Override
	public Character[] getPossibleBlocks() {
		Character[] cards = {new Captain(), new Ambassador()};
		return cards;
	}
	
	@Override
	public ArrayList<Game> theorize(Effect parent, Player instigator, Player victim, Player ai, Character[] cardsToChallenge,
			Game game) {
		Player[] otherPlayers = game.getOtherPlayersExcept(instigator);
		ArrayList<Game> list = new ArrayList<Game>();
		
		for (Player otherPlayer : otherPlayers) {
			if (otherPlayer.numCoins >= NUM_COINS)
				list.addAll(super.theorize(this, instigator, otherPlayer, ai, cardsToChallenge, game));
		}
		
		return list;
	}
}