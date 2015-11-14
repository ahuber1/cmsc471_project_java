package coup.cards;

import coup.Block;
import coup.actions.Action;
import coup.actions.Assassinate;

public class Assassin extends Card {

	@Override
	public String getName() {
		return "Assassin";
	}

	@Override
	public Action getAction() {
		return new Assassinate();
	}

	@Override
	public Block getCounteraction() {
		return null;
	}

}
