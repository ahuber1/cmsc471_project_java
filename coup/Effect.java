package coup;

import java.util.ArrayList;

import coup.characters.Character;

public abstract class Effect{

	public abstract boolean execute(Player instigator, Player victim, Player ai, Character[] cardsToExchange, Game game, boolean theorizing);
	public abstract String getDescription();
	public abstract ArrayList<Game> theorize(Effect parent, Player instigator, Player victim, Player ai, Character[] cardsToExchange, Game game);
	public abstract Character getCard();
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		else
			return obj.toString().equals(toString());
	}
	
	@Override
	public String toString() {
		return getDescription();
	}
}
