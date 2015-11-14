package coup.cards;

import coup.Block;
import coup.actions.Action;
import coup.actions.Exchange;

public class Ambassador extends Card {

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
			public Card getCard() {
				return Ambassador.this;
			}
		};
	}

}
