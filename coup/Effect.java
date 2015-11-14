package coup;

import java.util.ArrayList;

import coup.cards.Card;

public abstract class Effect{

	public abstract boolean execute(Player instigator, Player victim, Agent ai, Card[] cardsToExchange, Game game);
	public abstract String getDescription();
	public abstract ArrayList<Game> theorize(Effect parent, Player instigator, Player victim, Agent ai, Card[] cardsToExchange, Game game);
	public abstract Card getCard();
	
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
