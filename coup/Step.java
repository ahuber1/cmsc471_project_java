package coup;

import java.util.Arrays;

import coup.cards.Card;

public class Step {
	public Effect effect;
	public Player instigator; 
	public Player victim;
	public Agent ai;
	public Card[] cardsToChallenge; 
	
	public Step(Effect effect, Player instigator, Player victim, Agent ai, Card[] cardsToChallenge) {
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