package coup.cards;

import coup.Block;
import coup.actions.Action;

public class Contessa extends Card {

	@Override
	public String getName() {
		return "Contessa";
	}

	@Override
	public Action getAction() {
		return null;
	}

	@Override
	public Block getCounteraction() {
		return new Block() {
			
			@Override
			public String getDescription() {
				return "Blocks Assasination";
			}
			
			@Override
			public Card getCard() {
				return Contessa.this;
			}
		};
	}

}
