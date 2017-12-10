package ufl.cs1.controllers;
import game.controllers.DefenderController;
import game.models.Game;
import game.models.Node;
import java.util.List;

/* DISCLAIMER: The team is implementing the existing classic AI base code
and building from there. We did not create the base code for the defender controller.
We tweaked the behavior so that the gators actually have objectives
 */
public final class StudentController implements DefenderController {
    Game lastGameState, currentGameState;

    // This is how many gators are supposed to exist in the game
    private final int Gator1 = 0;    // RED GHOST -> slave
    private final int Gator2 = 1;    // PINK GHOST -> Colin's
    private final int Gator3 = 2;    // YELLOW GHOST -> Juan's
    private final int Gator4 = 3;    // TEAL GHOST -> Stalim's

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

    // Default constructor
    public StudentController() {
        // This initializes the state of all gators at the
        // beginning of the game to be in jail.
        this.currentGatorState = new State[]
                {this.jailedStates[0], this.jailedStates[1], this.jailedStates[3], this.
                        jailedStates[2]};
    }

    /* This initializes an array of gator states to store information and properly
    sort between setting a new state and passing the current state to last state used.
    This part is helpful for using the replay mode.*/
    private State[] lastGatorState =
            new State[] {null, null, null, null};
    private State[] currentGatorState;

    // init(), shutdown(), update() are mandatory from the DefenderController interface
    public void init(Game game) { }
    public void shutdown(Game game) { }
    public int[] update(Game game,long timeDue) {

        // Initializes the actions array with -1 for all gators since this meets
        // the criteria of the jailed state
        int[] actions = new int[]{-1, -1, -1, -1};
        this.currentGameState = game;

        // If this is the first round ever, it will store the current game as last game
        if (this.lastGameState == null) {
            this.lastGameState = this.currentGameState;
        }

        // This loop updates each gator's actions
        for(int i = 0; i < 4; ++i) {
            actions[i] = this.getNextAction(i, timeDue);
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
            else if (whichGator == 0) {
                return stateType.Kill;
            }
            for (int i = 0; i < powerPillLocations.size()-1; i++) {
                if ((StudentController.this.currentGameState.getAttacker().
                        getLocation().getPathDistance(powerPillLocations.get(i)) < 25)) {
                    Node pill = powerPillLocations.get(i);

                /* Checks if ghost is near pacman while pacman is near a pill and
                if the gator is within a good distance to kill pacman before it reaches the pill*/

                    if ((StudentController.this.currentGameState.
                            getDefender(whichGator).getLocation().
                            getPathDistance(powerPillLocations.get(i)) < StudentController.
                            this.currentGameState.getAttacker().getNextDir(pill,true)) &&
                            StudentController.this.currentGameState.
                                    getDefender(whichGator).getLocation().
                                    getPathDistance(wherePacmanIs) < 10) {
                        boolean isPillThere = StudentController.this.currentGameState.checkPowerPill(powerPillLocations.get(i));
                        if (isPillThere) {
                            return stateType.runAway;
                        } else {
                            return stateType.Unchanged;
                        }
                    } else {
                        return stateType.runAway;
                    }
                }
            }
            return stateType.Unchanged;
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
                case 0: // This ghost will be the most aggressive
                    /* This case will return the location of attacker as a target for the
                     defender */
                    return StudentController.this.currentGameState.getDefender(whichGator)
                            .getNextDir(StudentController.this.currentGameState.getAttacker()
                                    .getLocation(), true);
                case 1: // This is Colin's ghost
                    List<Node> PowerPillLocations = StudentController.this.currentGameState.getCurMaze().getPowerPillNodes();   //Makes a list of power pills

                    Node attackerLocation = StudentController.this.currentGameState.getAttacker().getLocation();       //Attacker's location

                    int defenderX = StudentController.this.currentGameState.getDefender(whichGator).getLocation().getX();   //Defender's x and y
                    int defenderY = StudentController.this.currentGameState.getDefender(whichGator).getLocation().getY();

                    int attackerX = StudentController.this.currentGameState.getAttacker().getLocation().getX(); //Attacker's x and y
                    int attackerY = StudentController.this.currentGameState.getAttacker().getLocation().getY();

                    int pillCount = 0;
                    Node lastPill = null;

                    for(int ii = 0;ii < PowerPillLocations.size();ii++){    //Counts the number of power pills left
                        boolean isPillThere = StudentController.this.currentGameState.checkPowerPill(PowerPillLocations.get(ii));
                        if(isPillThere){
                            lastPill = PowerPillLocations.get(ii);
                            pillCount++;
                        }
                    }

                    if(pillCount == 1){     //If 1 power pill left, targets that pill and guards it
                        //System.out.println("1 left!");
                        return StudentController.this.currentGameState.getDefender(whichGator).getNextDir(lastPill, true);
                    }


                    for(int ii = 0;ii < PowerPillLocations.size();ii++) {   //If pacman is by a power pill, avoids her
                        boolean isPillThere = StudentController.this.currentGameState.checkPowerPill(PowerPillLocations.get(ii));
                        if (isPillThere) {
                            int pillX = PowerPillLocations.get(ii).getX();
                            int pillY = PowerPillLocations.get(ii).getY();
                            if (java.lang.Math.abs(attackerX - pillX) < 50 && java.lang.Math.abs(attackerY - pillY) < 50) {
                                //System.out.println("She's close!");
                                return StudentController.this.currentGameState.getDefender(whichGator)
                                        .getNextDir(StudentController.this.currentGameState.getAttacker()
                                                .getLocation(), false);
                            }
                        }
                    }

                    if(defenderX == attackerX || attackerY == defenderY){   //Immediately targets pacman if close
                        //System.out.println("Got 'em!");
                        return StudentController.this.currentGameState.getDefender(whichGator).getNextDir(attackerLocation, true);
                    }

                    int direction = StudentController.this.currentGameState.getAttacker().getDirection();
                    Node ahead = StudentController.this.currentGameState.getAttacker().getLocation().getNeighbor(direction);

                    if(ahead == null){      //Targets 1 node ahead of pacman
                        ahead = StudentController.this.currentGameState.getAttacker().getLocation().getNeighbor(2);
                        if(ahead == null){
                            ahead = StudentController.this.currentGameState.getAttacker().getLocation().getNeighbor(1);
                            if(ahead == null){
                                ahead = StudentController.this.currentGameState.getAttacker().getLocation().getNeighbor(3);
                            }
                        }
                    }

                    return StudentController.this.currentGameState.getDefender(whichGator)
                            .getNextDir(ahead, true);
                case 3:

                    return StudentController.this.currentGameState.getDefender(whichGator)
                            .getNextDir(StudentController.this.currentGameState.getAttacker()
                                    .getLocation(), true);
                case 2:
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
            // This is what gators will do in runAway State
            Node wherePacmanIs = StudentController.this.currentGameState.
                    getAttacker().getLocation();
            if (whichGator == 2 || whichGator == 3 || whichGator == 1) {
                if (StudentController.this.currentGameState.
                        getDefender(whichGator).isVulnerable()) {
                    return StudentController.this.currentGameState.
                            getDefender(whichGator).
                            getNextDir(wherePacmanIs, false);
                } else {
                    return StudentController.this.currentGameState.
                            getDefender(whichGator).
                            getNextDir(wherePacmanIs, false);
                }
            }
            else if (whichGator == 0) {
                    return StudentController.this.currentGameState.
                            getDefender(whichGator).
                            getNextDir(wherePacmanIs, true);
            }
            else {
                return -1;
            }
        }

        public void reset() {
        }
    }

    // This state defines when the gator is exploring the maze
    private class exploreState implements State {
        private long lastTime, futureTime, timer;

        private exploreState() {
            this.futureTime = 0L;
            this.timer = 0L;
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
                    return StudentController.this.currentGameState.getDefender(whichGator)
                            .getNextDir(StudentController.this.currentGameState.getAttacker()
                                    .getLocation(), true);
                case 1:
                    return StudentController.this.currentGameState.getDefender(whichGator)
                            .getNextDir(StudentController.this.currentGameState.getAttacker()
                                    .getLocation(), true);
                case 2:
                    return StudentController.this.currentGameState.getDefender(whichGator)
                            .getNextDir(StudentController.this.currentGameState.getAttacker()
                                    .getLocation(), true);
                case 3:
                    return StudentController.this.currentGameState.getDefender(whichGator)
                            .getNextDir(StudentController.this.currentGameState.getAttacker()
                                    .getLocation(), true);
                default:
                    return -1;
            }
        }

        public StudentController.stateType getFutureState(int whichGator) {
            if (StudentController.this.currentGameState.getDefender(whichGator).isVulnerable()) {
                return stateType.runAway;
            } else {
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