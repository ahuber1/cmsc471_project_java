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
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Card) {
			Card otherCard = (Card) obj;
			return this.getName().equals(otherCard.getName());
		}
		else
			return false;
	}
}