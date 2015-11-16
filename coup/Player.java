package coup;

import java.util.ArrayList;

import coup.cards.Card;

public abstract class Player {

	public ArrayList<Card> cards;
	public int numCoins;
	public String name;
	public boolean lost;
	
	public Player(String name) {
		this.cards = new ArrayList<Card>();
		this.numCoins = 0;
		this.name = name;
		this.lost = false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || (obj instanceof Player) == false)
			return false;
		else
			return this.name.equals(((Player) obj).name);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public abstract Card revealCard(Game game, Agent ai, Effect effect);
	public abstract Player copy();
	public abstract Card[] getPossibleCardsToAssasinate(Game game, Agent ai);
	public abstract Game requestCounteraction(Step actionStep, Game game, Player instigatorOfCounteraction);
	public abstract Game requestChallenge(Step counteractionStep, Game game, Player instigatorOfChallenge);

	public void looses() {
		//this.cards.clear();
		//this.numCoins = 0;
		this.lost = true;
	}

	public boolean lost() {
		return lost;
	}

	public void stealCoins(int numCoins) {
		if (this.numCoins > numCoins) // only deduct coins if it will not go to a negative number
			this.numCoins -= numCoins;
	}
}
