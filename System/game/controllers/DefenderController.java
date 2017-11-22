package game.controllers;

import game.models.Game;

/*
 * Interface that Enemies controllers must implement. The only method that is
 * required is getActions(-), which returns the direction to be taken: 
 * Up - Right - Down - Left -> 0 - 1 - 2 - 3
 * Any other number is considered to be a lack of action (Neutral). 
 */
public interface DefenderController
{
	public void init(Game game);
	public int[] update(Game game, long timeDue);
	//Takes the position of the enemy and uses that to determine where to go next?
	//Probably wouldn't be efficient enough to just chase after the attacker, so maybe somehow we can program the defenders to corner it?
	public void shutdown(Game game);
}