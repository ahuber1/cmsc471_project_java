package coup.cards;

import coup.Block;
import coup.actions.Action;
import coup.actions.Steal;

public class Captain extends Card {

	@Override
	public String getName() {
		return "Captain";
	}

	@Override
	public Action getAction() {
		return new Steal();
	}

	@Override
	public Block getCounteraction() {
		return new Block() {
			
			@Override
			public String getDescription() {
				return "Blocks Stealing (Captain)";
			}
			
			@Override
			public Card getCard() {
				return Captain.this;
			}
		};
	}

}
