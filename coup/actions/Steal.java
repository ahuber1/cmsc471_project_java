package coup.actions;

import java.util.ArrayList;

import coup.Agent;
import coup.Effect;
import coup.Game;
import coup.Player;
import coup.cards.Ambassador;
import coup.cards.Captain;
import coup.cards.Card;

public class Steal extends Action {
	
	private static final int NUM_COINS = 2;

	@Override
	public boolean execute(Player instigator, Player victim, Agent ai, Card[] cardsToExchange, Game game, boolean theorizing) {
		
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
	public Card getCard() {
		return new Captain();
	}

	@Override
	public Card[] getPossibleBlocks() {
		Card[] cards = {new Captain(), new Ambassador()};
		return cards;
	}
	
	@Override
	public ArrayList<Game> theorize(Effect parent, Player instigator, Player victim, Agent ai, Card[] cardsToChallenge,
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