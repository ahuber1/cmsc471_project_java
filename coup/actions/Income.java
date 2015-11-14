package coup.actions;

import java.util.ArrayList;

import coup.Agent;
import coup.Effect;
import coup.Game;
import coup.Player;
import coup.cards.Card;

public class Income extends Action {
	
	private static final int NUM_COINS = 1;

	@Override
	public boolean execute(Player instigator, Player victim, Agent ai, Card[] cardsToExchange, Game game, boolean theorizing) {
		instigator = game.findPlayer(instigator);
		victim = game.findPlayer(victim);
		
		Player temp = game.findPlayer(ai);
		ai = temp == null ? null : (Agent) temp;
		if (victim.numCoins >= NUM_COINS) {
			victim.numCoins -= NUM_COINS;
			instigator.numCoins += NUM_COINS;
			return true;
		}
		else
			return false;
	}

	@Override
	public String getDescription() {
		return "Income";
	}

	@Override
	public Card getCard() {
		return null;
	}

	@Override
	public Card[] getPossibleBlocks() {
		return new Card[0];
	}
	
	@Override
	public ArrayList<Game> theorize(Effect parent, Player instigator, Player victim, Agent ai, Card[] cardsToChallenge,
			Game game) {
		Player[] otherPlayers = game.getOtherPlayersExcept(instigator);
		ArrayList<Game> list = new ArrayList<Game>();
		
		for (Player otherPlayer : otherPlayers) {
			if (otherPlayer.numCoins >= NUM_COINS)
				list.addAll(super.theorize(this, instigator, otherPlayer, ai, null, game));
		}
		
		return list;
	}
}