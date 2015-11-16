package coup;

import java.util.Arrays;

import coup.cards.Card;

public class Step {
	public Effect effect;
	public Player instigator; 
	public Player victim;
	public Player ai;
	public Card[] cardsToChallenge; 
	
	public Step(Effect effect, Player instigator, Player victim, Player ai, Card[] cardsToChallenge) {
		this.effect = effect;
		this.instigator = instigator;
		this.victim = victim;
		this.ai = ai;
		this.cardsToChallenge = cardsToChallenge;
	}
	
	@Override
	public String toString() {
		return effect.getDescription();
	}
}