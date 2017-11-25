package ufl.cs1.controllers;

import game.controllers.DefenderController;
import game.models.Defender;
import game.models.Game;
import game.models.Node;
import java.util.List;

public final class StudentController implements DefenderController
{
	Game lastGameState, currentGameState;

	// This is how many gators are supposed to exist in the game
	private final int Gator1 = 0;
	private final int Gator2 = 1;
	private final int Gator3 = 2;
	private final int Gator4 = 3;

	// This initializes an array of each state type for each gator
	private StudentController.killState[] killStates = new StudentController.
            killState[]{new StudentController.
            killState(), new StudentController.killState(),
            new StudentController.killState(), new StudentController.killState()};
	private StudentController.exploreState[] exploreStates = new StudentController.
            exploreState[]{new StudentController.
            exploreState(), new StudentController.exploreState(),
            new StudentController.exploreState(), new StudentController.exploreState()};
	private StudentController.runAwayState[] runAwayStates = new StudentController.
            runAwayState[]{new StudentController.
            runAwayState(), new StudentController.runAwayState(),
            new StudentController.runAwayState(), new StudentController.runAwayState()};
	private StudentController.jailedState[] jailedStates = new StudentController.
            jailedState[]{new StudentController.
            jailedState(), new StudentController.jailedState(),
            new StudentController.jailedState(), new StudentController.jailedState()};

	// This initializes an array of gator states
	private StudentController.CheckState[] lastGatorState =
            new StudentController.CheckState[] {null, null, null, null};
	private StudentController.CheckState[] currentGatorState;

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

	// This state defines when the gator is chasing the attacker
	private class killState implements StudentController.CheckState {
		private int switchOutOfExplore;
		private long timer, futureTime, previousTime;

		// default constructor initializes everything to ZERO
		private killState() {
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
				return stateType.Explore;
			}
			// This will check if gator can be killed. If true, will change state to run away.
			else if(StudentController.this.currentGameState.getDefender(whichGator).isVulnerable()) {
				return stateType.runAway;
			}
			// If nothing changes:
			else {
				return stateType.Unchanged;
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
            switch(whichGator) {
                case 0:
                    return StudentController.this.currentGameState.getDefender(whichGator)
                            .getNextDir(StudentController.this.currentGameState.getAttacker()
                            .getLocation(), true);
                case 1:
                case 3:
                    int nodeCount = 4;
                    if (whichGator == 3) {
                        nodeCount = 2;
                    }

                    int wherePacmanIsHeading = StudentController.this.
                            currentGameState.getAttacker().getDirection();
                    Node target = StudentController.this.
                            currentGameState.getAttacker().getLocation();

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
                    Node wherePacmanIs = StudentController.this.currentGameState.
                            getAttacker().getLocation();
                    List<Node> powerPillLocations = StudentController.this.
                            currentGameState.getCurMaze().getPowerPillNodes();
                    Node exploreTarget = (Node)powerPillLocations.get(2);
                    if (StudentController.this.currentGameState.
                            getDefender(whichGator).getLocation().
                            getPathDistance(wherePacmanIs) > 40) {
                        return StudentController.this.currentGameState.
                                getDefender(whichGator).getNextDir(StudentController.
                                this.currentGameState.getAttacker().
                                getLocation(), true);
                    }

                    return StudentController.this.currentGameState.getDefender(whichGator).
                            getNextDir(exploreTarget, true);
                default:
                    return StudentController.this.currentGameState.getDefender(whichGator)
                            .getNextDir(StudentController.this.currentGameState.getAttacker()
                            .getLocation(), true);
            }
		}
	}

	// This state defines when the gator is running away from the attacker
    private class runAwayState implements StudentController.CheckState {
        private runAwayState() {
        }

        public void updateTimer(long timeNow) {
        }

        public StudentController.stateType getFutureState(int whichGator) {
            if (StudentController.this.currentGameState.
                    getDefender(whichGator).getLairTime() > 0) {
                return stateType.Jailed;
            } else {
                return !StudentController.this.currentGameState.
                        getDefender(whichGator).isVulnerable() ?
                        StudentController.this.lastGatorState[whichGator].
                                getCurrentState() : stateType.Unchanged;
            }
        }

        public StudentController.stateType getCurrentState() {
            return stateType.runAway;
        }

        public int getMovingPossibilities(int whichGator) {
            List<Node> powerPillLocations = StudentController.this.
                    currentGameState.getCurMaze().getPowerPillNodes();
            if (whichGator == 0) {
                return StudentController.this.currentGameState.getDefender(whichGator).
                        getNextDir((Node)powerPillLocations.get(1), true);
            } else if (whichGator == 1) {
                return StudentController.this.currentGameState.getDefender(whichGator).
                        getNextDir((Node)powerPillLocations.get(0), true);
            } else if (whichGator == 3) {
                return StudentController.this.currentGameState.getDefender(whichGator).
                        getNextDir((Node)powerPillLocations.get(3), true);
            } else {
                return whichGator == 2 ? StudentController.this.currentGameState.
                        getDefender(whichGator).getNextDir((Node)powerPillLocations.
                        get(2), true) : -1;
            }
        }

        public void reset() {
        }
    }

    // This state defines when the gator is exploring the maze
    private class exploreState implements StudentController.CheckState {
        private int switchToKill;
	    private long lastTime, futureTime, timer;

        private exploreState() {
            this.lastTime = 0L;
            this.futureTime = 0L;
            this.timer = 0L;
            this.switchToKill = 0;
        }

        public void updateTimer(long timeNow) {
            this.futureTime = 40L;
            this.timer += this.futureTime;
            this.lastTime = timeNow;
        }

        public StudentController.stateType getCurrentState() {
            return stateType.Explore;
        }

        public void reset() {
            this.timer = 0L;
            this.lastTime = 0L;
            this.futureTime = 0L;
        }

        public int getMovingPossibilities(int whichGator) {
            List<Node> powerPillLocations = StudentController.this.currentGameState.
                    getCurMaze().getPowerPillNodes();
            switch(whichGator) {
                case 0:
                    if (this.switchToKill >= 2) {
                        return StudentController.this.currentGameState.
                                getDefender(whichGator).getNextDir(StudentController.
                                this.currentGameState.getAttacker().getLocation()
                                , true);
                    }

                    return StudentController.this.currentGameState.getDefender(whichGator).
                            getNextDir((Node)powerPillLocations.get(1), true);
                case 1:
                    return StudentController.this.currentGameState.getDefender(whichGator).
                            getNextDir((Node)powerPillLocations.get(0), true);
                case 2:
                    return StudentController.this.currentGameState.getDefender(whichGator).
                            getNextDir((Node)powerPillLocations.get(2), true);
                case 3:
                    return StudentController.this.currentGameState.getDefender(whichGator).
                            getNextDir((Node)powerPillLocations.get(3), true);
                default:
                    return -1;
            }
        }

        public StudentController.stateType getFutureState(int ghostID) {
            if (StudentController.this.currentGameState.getDefender(ghostID).isVulnerable()) {
                return stateType.runAway;
            } else {
                if (this.switchToKill < 2) {
                    if (this.timer >= 4000L) {
                        ++this.switchToKill;
                        return stateType.Kill;
                    }
                } else if (this.switchToKill >= 2 && this.timer >= 2000L) {
                    ++this.switchToKill;
                    return stateType.Kill;
                }

                return stateType.Unchanged;
            }
        }
    }

    /* This state defines the gator behavior when it's waiting inside the jail
       at the beginning of each game round */
    private class jailedState implements StudentController.CheckState {
        private jailedState() {
        }

        public void updateTimer(long timeNow) {
        }

        public StudentController.stateType getFutureState(int whichGator) {
            return StudentController.this.currentGameState.getDefender(whichGator).
                    getLairTime() <= 0 ? stateType.Explore : stateType.Unchanged;
        }

        public int getMovingPossibilities(int whichGator) {
            return -1;
        }

        public void reset() {
        }

        public StudentController.stateType getCurrentState() {
            return stateType.Jailed;
        }
    }

	// This method will choose the stateType to execute
    private int getNextAction(int whichGator, long timeNow) {
        this.currentGatorState[whichGator].updateTimer(timeNow);
        StudentController.stateType newState = this.currentGatorState[whichGator].getFutureState(whichGator);
        if (newState != stateType.Unchanged) {
            switch(newState) {
                case Explore:
                    this.lastGatorState[whichGator] = this.currentGatorState[whichGator];
                    this.currentGatorState[whichGator] = this.exploreStates[whichGator];
                    break;
                case Kill:
                    this.lastGatorState[whichGator] = this.lastGatorState[whichGator];
                    this.currentGatorState[whichGator] = this.killStates[whichGator];
                    break;
                case runAway:
                    this.lastGatorState[whichGator] = this.currentGatorState[whichGator];
                    this.currentGatorState[whichGator] = this.runAwayStates[whichGator];
                    break;
                case Jailed:
                    this.lastGatorState[whichGator] = this.currentGatorState[whichGator];
                    this.currentGatorState[whichGator] = this.jailedStates[whichGator];
            }

            if (this.lastGatorState[whichGator].getCurrentState() != stateType.runAway) {
                this.currentGatorState[whichGator].reset();
            }
        }

        return this.currentGatorState[whichGator].getMovingPossibilities(whichGator);
    }

	static enum stateType { Kill, runAway, Explore, Jailed, Unchanged;
		private stateType(){}}


    public void ResetThisData() {
        this.lastGameState = null;
        this.currentGameState = null;

        for(int iState = 0; iState < 4; ++iState) {
            this.exploreStates[iState] = new StudentController.exploreState();
            this.killStates[iState] = new StudentController.killState();
            this.runAwayStates[iState] = new StudentController.runAwayState();
            this.jailedStates[iState] = new StudentController.jailedState();
            this.lastGatorState[iState] = null;
            this.currentGatorState[iState] = this.jailedStates[iState];
        }

    }
}