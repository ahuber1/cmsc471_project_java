package coup.characters;

import coup.Block;
import coup.actions.Action;

public class Contessa extends Character {

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
			public Character getCard() {
				return Contessa.this;
			}
		};
	}

}
