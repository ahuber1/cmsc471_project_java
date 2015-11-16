package coup.characters;

import coup.Block;
import coup.actions.Action;

public abstract class Character {
	
	public abstract String getName();
	public abstract Action getAction();
	public abstract Block getCounteraction();
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Character) {
			Character otherCard = (Character) obj;
			return this.getName().equals(otherCard.getName());
		}
		else
			return false;
	}
}