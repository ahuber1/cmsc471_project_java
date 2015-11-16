package coup.actions;

import java.util.ArrayList;

import coup.Player;
import coup.characters.Character;
import coup.Effect;
import coup.Game;
import coup.Player;

public class Coup extends Action {
	
	private final static int NUM_COINS = 7;

	@Override
	public boolean execute(Player instigator, Player victim, Player ai, Character[] cardsToExchange, Game game, boolean theorizing) {
		return Assassinate.execute(this, NUM_COINS, instigator, victim, ai, cardsToExchange, game, theorizing);
	}

	@Override
	public String getDescription() {
		return "Coup";
	}

	@Override
	public Character[] getPossibleBlocks() {
		return new Character[0];
	}
	
	@Override
	public ArrayList<Game> theorize(Effect parent, Player instigator, Player victim, Player ai, Character[] cardsToExchange, Game game) {
		ArrayList<Game> list = new ArrayList<Game>();
		Assassinate.theorize(this, instigator, victim, ai, cardsToExchange, game, NUM_COINS, list);
		return list;
	}
	
	@Override
	public Character getCard() {
		return null;
	}

}
