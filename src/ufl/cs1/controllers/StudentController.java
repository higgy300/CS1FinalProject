package ufl.cs1.controllers;

import game.controllers.DefenderController;
import game.models.Defender;
import game.models.Game;

import java.util.List;

public final class StudentController implements DefenderController
{
	Game newState, currentState;

	// This is how many gators are supposed to exist in the game
	private final int Gator1 = 0;
	private final int Gator2 = 1;
	private final int Gator3 = 2;
	private final int Gator4 = 3;

	// init(), shutdown(), update() are mandatory from the DefenderController interface
	public void init(Game game) { }
	public void shutdown(Game game) { }
	public int[] update(Game game,long timeDue)
	{
		int[] actions = new int[Game.NUM_DEFENDER];
		List<Defender> enemies = game.getDefenders();
		
		//Chooses a random LEGAL action if required. Could be much simpler by simply returning
		//any random number of all of the ghosts
		for(int i = 0; i < actions.length; i++)
		{
			Defender defender = enemies.get(i);
			List<Integer> possibleDirs = defender.getPossibleDirs();
			if (possibleDirs.size() != 0)
				actions[i]=possibleDirs.get(Game.rng.nextInt(possibleDirs.size()));
			else
				actions[i] = -1;
		}
		return actions;
	}


	// Created this interface to check current and future state
	private interface CheckState {
		void updateTimer(long newTime);

		StudentController.stateType getCurrentState();
		StudentController.stateType getFutureState(int nxtState);
		int getNextDirection(int dir);
		void reset();

	}

	// This state defines when the gator is chashing the attacker
	private class KillState implements StudentController.CheckState {
		private int switchOutOfExplore;
		private long timer, futureTime, previousTime;

		// default constructor initializes everything to ZERO
		private KillState() {
			this.switchOutOfExplore = 0;
			this.timer = 0L;
			this.futureTime = 0L;
			this.previousTime = 0L;
		}

		// This will reset all attributes of this class
		public void reset() {
			this.switchOutOfExplore = 0;
			this.timer = 0L;
			this.futureTime = 0L;
			this.previousTime = 0L;
		}
		public StudentController.stateType getCurrentState() {
			return stateType.Kill;
		}
		public StudentController.stateType getFutureState(int whichGator) {
			if (this.switchOutOfExplore < 3 && this.timer >= 20000L) {
				++this.switchOutOfExplore;
				return stateType.explore;
			}
			// This will check if gator can be killed. If true, will change state to run away.
			else if(StudentController.this.currentState.getDefender(whichGator).isVulnerable()) {
				return stateType.runAway;
			}
			// If nothing changes:
			else {
				return stateType.unchanged;
			}
		}

		@Override
		public int getNextDirection(int dir) {
			// FIX ME
			return 0;
		}

		public void updateTimer(long timeNow) {
			this.futureTime = 40L;
			this.timer += this.futureTime;
			this.previousTime = timeNow;
		}

		// This method will
		public int getMovingPossibilities(int whichGator) {
			// FIX ME
			return 0;
		}
	}

	static enum stateType { Kill, runAway, explore, Jailed, unchanged;
		private stateType(){}}


}