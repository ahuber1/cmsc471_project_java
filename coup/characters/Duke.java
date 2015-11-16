package coup.characters;

import coup.Block;
import coup.actions.Action;
import coup.actions.Tax;

public class Duke extends Character {

	@Override
	public String getName() {
		return "Duke";
	}

	@Override
	public Action getAction() {
		return new Tax();
	}

	@Override
	public Block getCounteraction() {
		return new Block() {
			@Override
			public String getDescription() {
				return "Blocks Foreign Aid";
			}
			
			@Override
			public Character getCard() {
				return Duke.this;
			}
		};
	}

}
