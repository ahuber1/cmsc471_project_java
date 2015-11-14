package coup.cards;

import coup.Block;
import coup.actions.Action;

public abstract class Card {
	
	public abstract String getName();
	public abstract Action getAction();
	public abstract Block getCounteraction();
	
	@Override
	public String toString() {
		return getName();
	}
}