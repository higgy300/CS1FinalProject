package ufl.cs1.controllers;
import game.controllers.DefenderController;

import game.models.Game;

import game.models.Node;

import java.util.List;
public final class StudentController implements DefenderController {
    Game lastGameState, currentGameState;

    // This is how many gators are supposed to exist in the game
    private final int Gator1 = 0;    // RED GHOST
    private final int Gator2 = 1;    // PINK GHOST DUH
    private final int Gator3 = 2;    // YELLOW GHOST
    private final int Gator4 = 3;    // TEAL GHOST

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

    // Constructor
    public StudentController() {
        // This initializes the state of all gators at the
        // beginning of the game to be in jail.
        this.currentGatorState = new State[]
                {this.jailedStates[0], this.jailedStates[1], this.jailedStates[3], this.
                        jailedStates[2]};
    }

    /* This initializes an array of gator states to store information and properly
    sort between setting a new state and passing the current state to last state used */
    private State[] lastGatorState =
            new State[] {null, null, null, null};
    private State[] currentGatorState;

    // init(), shutdown(), update() are mandatory from the DefenderController interface
    public void init(Game game) { }
    public void shutdown(Game game) { }
    public int[] update(Game game,long timeDue) {
        int[] actions = new int[]{-1, -1, -1, -1};
        this.currentGameState = game;
        if (this.lastGameState == null) {
            this.lastGameState = this.currentGameState;
        }

        // This loop updates each gator's actions

        for(int iGator = 0; iGator < 4; ++iGator) {

            actions[iGator] = this.getNextAction(iGator, timeDue);

        }



        this.lastGameState = this.currentGameState;

        return actions;

    }

    // Created this interface to check current and future state
    private interface State {
        void updateTimer(long newTime);
        StudentController.stateType getCurrentState();
        StudentController.stateType getFutureState(int nxtState);
        int getMovingPossibilities(int dir);
        void reset();
    }

    // This state defines when the gator is chasing the attacker
    private class killState implements State {
        private int switchToExplore;
        private long timer, futureTime, previousTime;

        // default constructor initializes everything to ZERO
        private killState() {
            this.switchToExplore = 0;
            this.timer = 0L;
            this.futureTime = 0L;
            this.previousTime = 0L;
        }

        // This will reset all attributes of this class
        public void reset() {
            this.switchToExplore = 0;
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
            // Createa a location where pacman is
            Node wherePacmanIs = StudentController.this.currentGameState.
                    getAttacker().getLocation();
            // Create a list that contains the locations of all power pills
            List<Node> powerPillLocations = StudentController.this.
                    currentGameState.getCurMaze().getPowerPillNodes();

            // If there is no rush of time
            if (this.switchToExplore < 3 && this.timer >= 20000L) {
                ++this.switchToExplore;
                return stateType.Explore;
            }
            // Checks if pacman is really close to a power pill
            else if ((StudentController.this.currentGameState.getAttacker().
                    getLocation().getPathDistance(powerPillLocations.get(0)) < 25)) {
                Node pill = powerPillLocations.get(0);
                /* Checks if ghost is near pacman while pacman is near a pill and
                if the gator is within a good distance to kill pacman before it reaches the pill*/
                if ((StudentController.this.currentGameState.
                        getDefender(whichGator).getLocation().
                        getPathDistance(powerPillLocations.get(0)) < StudentController.
                        this.currentGameState.getAttacker().getNextDir(pill,true)) &&
                        StudentController.this.currentGameState.
                                getDefender(whichGator).getLocation().
                                getPathDistance(wherePacmanIs) < 10) {
                    return stateType.Unchanged;
                } else {
                    return stateType.runAway;
                }
            }
            else if ((StudentController.this.currentGameState.getAttacker().
                    getLocation().getPathDistance(powerPillLocations.get(0)) < 25)) {
                Node pill = powerPillLocations.get(0);
                /* Checks if ghost is near pacman while pacman is near a pill and
                if the gator is within a good distance to kill pacman before it reaches the pill*/
                if ((StudentController.this.currentGameState.
                        getDefender(whichGator).getLocation().
                        getPathDistance(powerPillLocations.get(0)) < StudentController.
                        this.currentGameState.getAttacker().getNextDir(pill,true)) &&
                        StudentController.this.currentGameState.
                                getDefender(whichGator).getLocation().
                                getPathDistance(wherePacmanIs) < 10) {
                    return stateType.Unchanged;
                } else {
                    return stateType.runAway;
                }
            }
            else if ((StudentController.this.currentGameState.getAttacker().
                    getLocation().getPathDistance(powerPillLocations.get(0)) < 25)) {
                Node pill = powerPillLocations.get(0);
                /* Checks if ghost is near pacman while pacman is near a pill and
                if the gator is within a good distance to kill pacman before it reaches the pill*/
                if ((StudentController.this.currentGameState.
                        getDefender(whichGator).getLocation().
                        getPathDistance(powerPillLocations.get(0)) < StudentController.
                        this.currentGameState.getAttacker().getNextDir(pill,true)) &&
                        StudentController.this.currentGameState.
                                getDefender(whichGator).getLocation().
                                getPathDistance(wherePacmanIs) < 10) {
                    return stateType.Unchanged;
                } else {
                    return stateType.runAway;
                }
            }
            else if ((StudentController.this.currentGameState.getAttacker().
                    getLocation().getPathDistance(powerPillLocations.get(0)) < 25)) {
                Node pill = powerPillLocations.get(0);
                /* Checks if ghost is near pacman while pacman is near a pill and
                if the gator is within a good distance to kill pacman before it reaches the pill*/
                if ((StudentController.this.currentGameState.
                        getDefender(whichGator).getLocation().
                        getPathDistance(powerPillLocations.get(0)) < StudentController.
                        this.currentGameState.getAttacker().getNextDir(pill,true)) &&
                        StudentController.this.currentGameState.
                                getDefender(whichGator).getLocation().
                                getPathDistance(wherePacmanIs) < 10) {
                    return stateType.Unchanged;
                } else {
                    return stateType.runAway;
                }
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

        /* This method will retrieve what are the possible movements for the defender based
         off which gator is the one retrieving */
        public int getMovingPossibilities(int whichGator) {
            /* Notice there are no breaks between the switch so they get all get carried out
            depending on the gator */
            switch(whichGator) {
                case 0: // This is Stalim's ghost
                    /* This case will return the location of attacker as a target for the
                     defender */
                    return StudentController.this.currentGameState.getDefender(whichGator)
                            .getNextDir(StudentController.this.currentGameState.getAttacker()
                                    .getLocation(), true);
                case 1: // This is Colin's ghost
                    return StudentController.this.currentGameState.getDefender(whichGator)
                            .getNextDir(StudentController.this.currentGameState.getAttacker()
                                    .getLocation(), true);
                case 3: // This is Juan's ghost
                    /*
                    int nodeCount = 4;
                    if (whichGator == 3) {
                        nodeCount = 2;
                    }
                    int wherePacmanIsHeading = StudentController.this.
                            currentGameState.getAttacker().getDirection();
                    Node target = StudentController.this.
                            currentGameState.getAttacker().getLocation();
                    for(int i = 0; i < nodeCount; ++i) {
                        if (target != null) {
                            target = target.getNeighbor(wherePacmanIsHeading);
                        }
                    }
                    if (wherePacmanIsHeading == 0) {
                        for(int i = 0; i < nodeCount; ++i) {
                            if (target != null) {
                                target = target.getNeighbor(3);
                            }
                        }
                    } */
                    return StudentController.this.currentGameState.getDefender(whichGator)
                            .getNextDir(StudentController.this.currentGameState.getAttacker()
                                    .getLocation(), true);
                case 2: // This is Juan's second ghost
                    /*Node wherePacmanIs = StudentController.this.currentGameState.
                            getAttacker().getLocation();
                    List<Node> powerPillLocations = StudentController.this.
                            currentGameState.getCurMaze().getPowerPillNodes();
                    Node exploreTarget = (Node)powerPillLocations.get(2);

                    if (StudentController.this.currentGameState.
                            getDefender(whichGator).getLocation().
                            getPathDistance(wherePacmanIs) > 20) { // This was 40
                        return StudentController.this.currentGameState.
                                getDefender(whichGator).getNextDir(StudentController.
                                this.currentGameState.getAttacker().
                                getLocation(), true);
                    } */

                    return StudentController.this.currentGameState.getDefender(whichGator).
                            getNextDir(StudentController.
                                    this.currentGameState.getAttacker().
                                    getLocation(), true); // This had explore Target
                default:

                    return StudentController.this.currentGameState.getDefender(whichGator)
                            .getNextDir(StudentController.this.currentGameState.getAttacker()
                                    .getLocation(), true);
            }
        }
    }

    // This state defines when the gator is running away from the attacker
    private class runAwayState implements State {
        private runAwayState() {
        }

        public void updateTimer(long timeNow) {
        }

        public StudentController.stateType getFutureState(int whichGator) {
            /* If the lair time for the gator has time it will change the sate to
            jailed state and send the gator to jail */
            if (StudentController.this.currentGameState.
                    getDefender(whichGator).getLairTime() > 0) {
                return stateType.Jailed;
            } else {
                // If gator is in vulnerable state, stay in runAway state
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
            } /*else if (whichGator == 3) {
                return StudentController.this.currentGameState.getDefender(whichGator).
                        getNextDir((Node)powerPillLocations.get(3), true);
            } else {
                return whichGator == 2 ? StudentController.this.currentGameState.
                        getDefender(whichGator).getNextDir((Node)powerPillLocations.
                        get(2), true) : -1;
            } */
            // This is what Juan's gators will do in runAway State
            else if (whichGator == 2 || whichGator == 3) {
                if (StudentController.this.currentGameState.
                        getDefender(whichGator).isVulnerable()) {
                    Node wherePacmanIs = StudentController.this.currentGameState.
                            getAttacker().getLocation();
                    return StudentController.this.currentGameState.
                            getDefender(whichGator).
                            getNextDir(wherePacmanIs, false);
                } else {
                    return StudentController.this.currentGameState.getDefender(whichGator).
                            getNextDir((Node)powerPillLocations.get(3), true);
                }
            } else {
                return -1;
            }
        }

        public void reset() {
        }
    }

    // This state defines when the gator is exploring the maze
    private class exploreState implements State {

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

                    if (this.switchToKill >= 1) { // This was 2

                        return StudentController.this.currentGameState.

                                getDefender(whichGator).getNextDir(StudentController.

                                        this.currentGameState.getAttacker().getLocation()

                                , true);

                    }



                    /* This return statement will make them target the powerPill

                    location area 1 while in explore mode */

                    //return StudentController.this.currentGameState.getDefender(whichGator).

                    //       getNextDir((Node)powerPillLocations.get(1), true);

                    return StudentController.this.currentGameState.getDefender(whichGator)

                            .getNextDir(StudentController.this.currentGameState.getAttacker()

                                    .getLocation(), true);

                case 1:

                    /*return StudentController.this.currentGameState.getDefender(whichGator).

                            getNextDir((Node)powerPillLocations.get(0), true); */

                    return StudentController.this.currentGameState.getDefender(whichGator)

                            .getNextDir(StudentController.this.currentGameState.getAttacker()

                                    .getLocation(), true);

                case 2:

                    /*return StudentController.this.currentGameState.getDefender(whichGator).

                            getNextDir((Node)powerPillLocations.get(2), true);*/

                    return StudentController.this.currentGameState.getDefender(whichGator)

                            .getNextDir(StudentController.this.currentGameState.getAttacker()

                                    .getLocation(), true);

                case 3:

                    /*return StudentController.this.currentGameState.getDefender(whichGator).

                            getNextDir((Node)powerPillLocations.get(3), true);*/

                    return StudentController.this.currentGameState.getDefender(whichGator)

                            .getNextDir(StudentController.this.currentGameState.getAttacker()

                                    .getLocation(), true);

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
       at the beginning of each game round ***THIS STATE IS COMPLETE***/
    private class jailedState implements State {
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
        // This updates the timer on X gator so it can update the actions
        this.currentGatorState[whichGator].updateTimer(timeNow);

        /* This will create a temporary state object to swap the current state as
         last and store a new state in the current state */
        StudentController.stateType temporaryState = this.currentGatorState[whichGator].
                getFutureState(whichGator);

        // This is where gator state swapping occurs
        if (temporaryState != stateType.Unchanged) {
            switch(temporaryState) {
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

    enum stateType { Kill, runAway, Explore, Jailed, Unchanged;

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