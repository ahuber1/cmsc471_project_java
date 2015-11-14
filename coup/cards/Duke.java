package coup.cards;

import coup.Block;
import coup.actions.Action;
import coup.actions.Tax;

public class Duke extends Card {

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
			public Card getCard() {
				return Duke.this;
			}
		};
	}

}
