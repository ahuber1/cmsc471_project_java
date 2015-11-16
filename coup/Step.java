package coup;

import java.util.Arrays;

import coup.characters.Character;

public class Step {
	public Effect effect;
	public Player instigator; 
	public Player victim;
	public Player ai;
	public Character[] cardsToChallenge; 
	
	public Step(Effect effect, Player instigator, Player victim, Player ai, Character[] cardsToChallenge) {
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