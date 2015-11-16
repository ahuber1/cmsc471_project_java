package coup.characters;

import coup.Block;
import coup.actions.Action;
import coup.actions.Exchange;

public class Ambassador extends Character {

	@Override
	public String getName() {
		return "Ambassador";
	}

	@Override
	public Action getAction() {
		return new Exchange();
	}

	@Override
	public Block getCounteraction() {
		return new Block() {
			
			@Override
			public String getDescription() {
				return "Blocks Stealing (Ambassador)";
			}
			
			@Override
			public Character getCard() {
				return Ambassador.this;
			}
		};
	}

}
