/**
 * Name: E.J. Yu
 * Date: April 29, 2019
 * Resources: None.
 * 
 * StreamlineEC.java is the extra credit part of the 3rd programming
 * assignment for the CSE 8B course at UC San Diego.
 * 
 * This file contains one class that extends the Streamline
 * class and consists of two methods, both of which are involved in 
 * the overriding of the superclass's recordAndMove() function to
 * execute tasks based on an explicit directional factor.
 * 
 * @author E.J. Yu
 */

 import java.util.*;

 /**
  * The StreamlineEC class contains two methods related to the
  * overriding of the Streamline superclass's recordAndMove()
  * method to undo certain turns when the player moves in a
  * direction that is intended to reverse the previous turn.
  * There are a few constants, and one instance variable to
  * keep track of the directions taken.
  */
 public class StreamlineEC extends Streamline {

    // Simple tracker of all directions taken.
    protected ArrayList<Integer> directions = new ArrayList<Integer>();

    // Constants to be used with the direction tracker above.
    final static int UP = 0;
    final static int RIGHT = 1;
    final static int DOWN = 2;
    final static int LEFT = 3;    


    /**
     * Generates a game with default height and width values
     * (alongside default player and goal positions) for the
     * board, plus three random obstacles. Also initializes
     * previousStates to an empty ArrayList.
     */
    public StreamlineEC() {
        super();
    }

    /**
     * Loads a game from file (as opposed to generating one). 
     * 
     * @param filename  The path to the file to load the game from
     */
    public StreamlineEC(String filename) {
        super(filename);
    }

    /**
     * Determine how to move the player (and record this movement)
     * based on a directional factor. Then move the player and
     * record the game's state information.
     * 
     * @param direction The direction that the player would like
     *                  to move towards.
     */
    @Override
    void recordAndMove(Direction direction)
    {

        // If null is passed in for direction, do nothing.
        if(direction == null) {
            return;
        }

        // If no moves have been made, perform a simple "record and move".
        if(directions.size() == 0) {

            // Moves player in the specified direction and records it.
            this.directionHandler(direction);

        }

        // If moves were made, execute tasks based on previous directions.
        else if(directions.size() > 0) {

            // Calculate the size of the directions list.
            int directionsRecorded = this.directions.size();

            // Fetch the most recent object in the directions list.
            int directionsTail = directionsRecorded - 1;

            // Get the direction of the previous move request.
            int previousDirection = this.directions.get(directionsTail);

            // Fetch the integer representing the direction passed in.
            int directionRequest = direction.getRotationCount();

            // If the player attempts to undo their last move
            // using the W/A/S/D keys, execute the undo() method.
            if(directionRequest == UP && previousDirection == DOWN ||
               directionRequest == DOWN && previousDirection == UP ||
               directionRequest == LEFT && previousDirection == RIGHT ||
               directionRequest == RIGHT && previousDirection == LEFT) {

                    super.undo();

                    // Remove the countered direction listing.
                    directions.remove(directionsTail);

            }

            // Otherwise, player is not trying to undo their last move.
            else {

                // Moves player in the specified direction and records it.
                this.directionHandler(direction);

            }

        }

    }

    /**
     * Helper method for the recordAndMove() function in
     * StreamlineEC. Saves information about the player's
     * in-game activity BEFORE the player moves, THEN moves
     * the player into the specified direction.
     * 
     * @param direction The direction that the player would
     *                  like to move towards.
     */
    private void directionHandler(Direction direction) {

        // Save a copy of currentState to previousStates.
        GameState currentStateCopy = new GameState(super.currentState);
        super.previousStates.add(currentStateCopy);

        // Record the direction that the player went in.
        this.directions.add(direction.getRotationCount());

        // Move the player in the specified direction.
        this.currentState.move(direction);

        // Fetch indicies and objects for game state comparisons.
        int recentStateIndx = this.previousStates.size() - 1;
        int recntDirectionIndx = this.directions.size() - 1;
        GameState previousState =
            new GameState(this.previousStates.get(recentStateIndx));

        // Undo the update to instance variables that just occurred if
        // no visible change to the board has been identified.
        if(previousState.equals(this.currentState)) {
            this.previousStates.remove(recentStateIndx);
            this.directions.remove(recntDirectionIndx);
        }

    }

}