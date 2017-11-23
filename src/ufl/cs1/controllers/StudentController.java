package ufl.cs1.controllers;

import game.controllers.DefenderController;
import game.controllers.example.OriginalDefenders;
import game.models.Defender;
import game.models.Game;
import game.models.Node;

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
		int getMovingPossibilities(int dir);
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
		// This returns the current type of state
		public StudentController.stateType getCurrentState() {
			return stateType.Kill;
		}
		// This retrieves what the defender will do
		public StudentController.stateType getFutureState(int whichGator) {
		    // If there is no rush of time
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

		// This update makes sure the movements of the defender stay in sync with the game
		public void updateTimer(long timeNow) {
			this.futureTime = 40L;
			this.timer += this.futureTime;
			this.previousTime = timeNow;
		}

		// This method will retrieve what are the possible movements for the defender based
        // off which gater is the one retrieving
		public int getMovingPossibilities(int whichGator) {
			// FIX ME
            switch(whichGator) {
                case 0:
                    return StudentController.this.currentState.getDefender(whichGator)
                            .getNextDir(StudentController.this.currentState.getAttacker()
                            .getLocation(), true);
                case 1:
                case 3:
                    int nodeCount = 4;
                    if (whichGator == 3) {
                        nodeCount = 2;
                    }

                    int wherePacmanIsHeading = StudentController.this.
                            currentState.getAttacker().getDirection();
                    Node target = StudentController.this.
                            currentState.getAttacker().getLocation();

                    for(int iLeft = 0; iLeft < nodeCount; ++iLeft) {
                        if (target != null) {
                            target = target.getNeighbor(wherePacmanIsHeading);
                        }
                    }

                    if (wherePacmanIsHeading == 0) {
                        for(int iLeft = 0; iLeft < nodeCount; ++iLeft) {
                            if (target != null) {
                                target = target.getNeighbor(3);
                            }
                        }
                    }
                case 2:
                    Node wherePacmanIs = StudentController.this.currentState.
                            getAttacker().getLocation();
                    List<Node> powerPillLocations = StudentController.this.
                            currentState.getCurMaze().getPowerPillNodes();
                    Node exploreTarget = (Node)powerPillLocations.get(2);
                    if (StudentController.this.currentState.
                            getDefender(whichGator).getLocation().
                            getPathDistance(wherePacmanIs) > 40) {
                        return StudentController.this.currentState.
                                getDefender(whichGator).getNextDir(StudentController.
                                this.currentState.getAttacker().
                                getLocation(), true);
                    }

                    return StudentController.this.currentState.getDefender(whichGator).
                            getNextDir(exploreTarget, true);
                default:
                    return StudentController.this.currentState.getDefender(whichGator)
                            .getNextDir(StudentController.this.currentState.getAttacker()
                            .getLocation(), true);
            }
		}
	}

	static enum stateType { Kill, runAway, explore, Jailed, unchanged;
		private stateType(){}}


}