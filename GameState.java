/**
 * Name: E.J. Yu
 * Date: April 18, 2019
 * Resources: Assignment documentation, Oracle's Java documentation,
 *            and self-authored Piazza posts.
 * 
 * The GameState.java file contains one class consisting of two constructors
 * and multiple methods which serve to define and manipulate the information
 * held on the board, board contents, player / goal location, and all
 * related. The GameState class will define the game's grid and "piece"
 * positions; likewise, Streamline objects (representing functions of the
 * Streamline game itself) will define how the user [player] may 
 * manipulate these GameState objects.
 * 
 * @author E.J. Yu
 */

import java.util.*;

/**
 * The GameState class defines: (1) the grid on which the user plays the
 * game, and (2) the positions of the "pieces" on the board. Variables are
 * available at the top of the class to define the board's "pieces" and
 * keep information behind the scenes about the board, the player, the
 * goal, and the state of level completion.
 */
public class GameState {

    // Used to populate char[][] board below and to display the
    // current state of play.
    final static char PLAYER_CHAR = '@';
    final static char GOAL_CHAR = 'G';
    final static char SPACE_CHAR = ' ';
    final static char TRAIL_CHAR = '+';
    final static char OBSTACLE_CHAR = 'O';
    final static char NEWLINE_CHAR = '\n';
    final static char HORIZONTAL_BORDER_CHAR = '-';
    final static char SIDE_BORDER_CHAR = '|';

    // This represents a 2D map of the board.
    char[][] board;

    // Location of the player.
    int playerRow;
    int playerCol;

    // Location of the goal.
    int goalRow;
    int goalCol;

    // True means the player completed this level.
    boolean levelPassed;

    /**
     * A detailed constructor that initializes the variables in a
     * GameState object.
     * 
     * @param height      Rows in a GameState object's board.
     * @param width       Columns in a GameState object's board.
     * @param playerRow   Vertical location of the player.
     * @param playerCol   Horizontal location of the player.
     * @param goalRow     Vertical location of the goal.
     * @param goalCol     Horizontal location of the goal.
     */
    public GameState(int height, int width, int playerRow, int playerCol, 
            int goalRow, int goalCol) {

        // Initialize variables indicating player position.
        this.playerRow = playerRow;
        this.playerCol = playerCol;

        // Initialize variables indicating goal position.
        this.goalRow = goalRow;
        this.goalCol = goalCol;

        // Initialize board w/ specified height and width lengths.
        this.board = new char[height][width];

        // Completely fill the board with SPACE_CHARs.
        for(int i = 0; i < this.board.length; i++) {
            for(int j = 0; j < this.board[0].length; j++) {
                this.board[i][j] = SPACE_CHAR;
            }
        }

        // Place the GOAL_CHAR on the board.
        this.board[this.goalRow][this.goalCol] = GOAL_CHAR;

        // Place the PLAYER_CHAR on the board.
        this.board[this.playerRow][this.playerCol] = PLAYER_CHAR;

        // Begin initializing the level competion flag.
        if(this.playerRow == this.goalRow &&
           this.playerCol == this.goalCol) {
                // If the player is on the goal, then level is completed.
                this.levelPassed = true;
        }
        else {
            // If the player isn't on the goal, level isn't completed.
            this.levelPassed = false;
        }

    }

    /**
     * A copy constructor that initializes instance variables
     * based on that of the ones stored in the GameState object
     * passed in.
     * 
     * @param other The GameState object in question.
     */
    public GameState(GameState other) {

        // Initialize variables indicating player position.
        this.playerRow = other.playerRow;
        this.playerCol = other.playerCol;

        // Initialize variables indicating goal position.
        this.goalRow = other.goalRow;
        this.goalCol = other.goalCol;

        // Initialize board w/ specified height and width lengths.
        this.board = new char[other.board.length][other.board[0].length];

        // Completely fill the board with other board's chars.
        for(int i = 0; i < this.board.length; i++) {
            for(int j = 0; j < this.board[0].length; j++) {
                this.board[i][j] = other.board[i][j];
            }
        }

        // Initialize the level competion flag.
        this.levelPassed = other.levelPassed;

    }

    /**
     * Add a specified number of random obstacles onto the game board.
     * 
     * @param count The number of random obstacles to be added.
     */
    void addRandomObstacles(int count) {

        // If count is less than 1, return immediately.
        if(count < 1) {
            return;
        }

        // If count is > board's area, return immediately.
        if(count > (this.board.length * this.board[0].length)) {
            return;
        }

        // Helps track the number of SPACE_CHARs on the board.
        int spacesAvailable = 0;

        // Figure out how many available spaces there are on the board.
        for(int i = 0; i < this.board.length; i++) {
            for(int j = 0; j < this.board[0].length; j++) {
                if(this.board[i][j] == SPACE_CHAR) {
                    // Increment counter if space character is found.
                    spacesAvailable++;
                }
            }
        }

        // If count is a larger number than there are empty spaces...
        if(count > spacesAvailable) {
            // ...return immediately.
            return;
        }

        // Create new random obstacle counter to help keep track.
        int randomObstaclesAdded = 0;

        // Create place random obstacles until counter equals argument.
        while(randomObstaclesAdded != count) {

            // Create new Random object to help generate random numbers.
            Random random = new Random();

            // Random board space coordinates will be stored here.
            int randomRow = random.nextInt(this.board.length);
            int randomCol = random.nextInt(this.board[0].length);

            // Check if coordinates generated are occupied.
            if(this.board[randomRow][randomCol] == SPACE_CHAR) {

                    // If location in board is vacant, place obstacle.
                    this.board[randomRow][randomCol] = OBSTACLE_CHAR;

                    // Increment counter.
                    randomObstaclesAdded++;

            }

        }

    }

    /**
     * Rotate the GameState's board counter-clockwise ONCE.
     */
    void rotateCounterClockwise() {

        // Create a rotated board to temporarily work with.
        char[][] rotatedBoard = new char[this.board[0].length]
                                        [this.board.length];

        // Copy original board's values into the new rotated board.
        for(int i = 0; i < rotatedBoard.length; i++) {
            for(int j = 0; j < rotatedBoard[0].length; j++) {
                rotatedBoard[i][j] =
                    this.board[j][this.board[0].length - 1 - i];
            }
        }

        // Update the original board.
        this.board = rotatedBoard;

        // Preserve old player coordinates.
        int playerRowOld = this.playerRow;
        int playerColOld = this.playerCol;

        // Preserve old goal coordinates.
        int goalRowOld = this.goalRow;
        int goalColOld = this.goalCol;

        // Update player values according to rotated board.
        this.playerRow = this.board.length - 1 - playerColOld;
        this.playerCol = playerRowOld;

        // Update goal values according to rotated board.
        this.goalRow = this.board.length - 1 - goalColOld;
        this.goalCol = goalRowOld;

    }

    /**
     * Move the player's current position up until it is stopped by an
     * obstacle. If the player tries to go up and has hit an edge, the
     * player will "snake" around the board to get to the opposite end
     * of the column that it's in.
     * 
     * @return Nothing. Only used to exit program.
     */
    void moveUp() {

        // Begin the process of moving the player upwards.
        for(int i = this.playerRow; i >= 0; i--) {

            // Checker for handling level pre-completion.
            if(this.levelPassed == true ||
               this.levelPassed == true &&
               this.playerRow == this.goalRow &&
               this.playerCol == this.goalCol ||
               this.playerRow == this.goalRow &&
               this.playerCol == this.goalCol) {
                    return;
            }

            // If the player hit the topmost edge, snake around the board.
            if(i - 1 == -1) {

                // Fetch character at the last row of the same column.
                char isUsed = this.board[this.board.length - 1]
                                        [this.playerCol];

                // Ensure that the last row of the same column is vacant.
                if(isUsed == SPACE_CHAR) {

                    // Move player to the other side of the column.
                    this.playerRow = this.board.length - 1;

                    // Update the board to reflect changes.
                    this.board[this.playerRow][this.playerCol] = PLAYER_CHAR;
                    this.board[i][this.playerCol] = TRAIL_CHAR;

                    // Ensure that loop will continue to run as expected.
                    i = this.playerRow + 1;

                }

                // If that character is the goal, prepare for victory.
                if(isUsed == GOAL_CHAR) {

                    // Move player to the other side of the column.
                    this.playerRow = this.goalRow;
                    this.playerCol = this.goalCol;

                    // Update the board to reflect changes.
                    this.board[this.playerRow][this.playerCol] = PLAYER_CHAR;
                    this.board[i][this.playerCol] = TRAIL_CHAR;

                    // Ensure that loop will continue to run as expected.
                    i = this.playerRow + 1;

                    // Indicate that the goal has been reached.
                    this.levelPassed = true;

                }
                
            }

            // If the player is NOT against the topmost edge, then
            // check if the space immediately above is vacant.
            else if(this.board[i - 1][this.playerCol] == SPACE_CHAR) {

                // Update the player location if player can move.
                this.playerRow = i - 1;

                // Update the board to reflect changes.
                this.board[this.playerRow][this.playerCol] = PLAYER_CHAR;
                this.board[i][this.playerCol] = TRAIL_CHAR;

            }

            // If the space immediately above is occupied by an obstacle
            // or trail, then return immediately.
            else if(this.board[i - 1][this.playerCol] == OBSTACLE_CHAR ||
                    this.board[i - 1][this.playerCol] == TRAIL_CHAR) {
                        return;
            }

            // Check if the space above is occupied by the GOAL.
            else if(this.board[i - 1][this.playerCol] == GOAL_CHAR) {

                // Update the player location if player can move.
                this.playerRow = i - 1;

                // Update the board to reflect changes.
                this.board[this.playerRow][this.playerCol] = PLAYER_CHAR;
                this.board[i][this.playerCol] = TRAIL_CHAR;

                // Indicate that the level has been completed.
                this.levelPassed = true;

            }

        }
    
    }

    /**
     * Moves player in a specified direction. How this works:
     * (1) Rotate board by some amount to move player in a direction.
     * (2) Move up until the player hits some non-space object.
     * (3) Rotate back to the original board orientation.
     * 
     * @param direction The direction that the player wants to move in.
     */
    void move(Direction direction) {

        // Use switch statement to determine what to do according to
        // the argument passed into this method.
        switch(direction) {

            // UP: Simply move upwards.
            case UP:
                moveUp();
                break;

            // RIGHT: Rotate CC, move upwards, re-orient board.
            case RIGHT:
                rotateCounterClockwise();
                moveUp();
                rotateCounterClockwise();
                rotateCounterClockwise();
                rotateCounterClockwise();
                break;

            // DOWN: Rotate CC twice, move upwards, re-orient board.
            case DOWN:
                rotateCounterClockwise();
                rotateCounterClockwise();
                moveUp();
                rotateCounterClockwise();
                rotateCounterClockwise();
                break;

            // LEFT: Rotate CC three times, move upwards, re-orient board.
            case LEFT: 
                rotateCounterClockwise();
                rotateCounterClockwise();
                rotateCounterClockwise();
                moveUp();
                rotateCounterClockwise();
                break;

        }

    }

    /**
     * Override the toString() method for the GameState class.
     * Returns a String representation of the calling GameState
     * object.
     * 
     * @return GameState object's String representation.
     */
    @Override
    public String toString() {

        // Initialize object for storing the upper / lower borders.
        StringBuilder horizontalBorder = new StringBuilder();

        // Loop to create a horizontal border of proper length.
        for(int i = 0; i < (2 * this.board[0].length + 3); i++){
            horizontalBorder.append(HORIZONTAL_BORDER_CHAR);
        }

        // Initialize object holding GameState's String representation.
        StringBuilder gameBoardStringified = new StringBuilder();

        // Ensure that the first line is the horizonal border.
        gameBoardStringified.append(horizontalBorder);
        gameBoardStringified.append(NEWLINE_CHAR);

        // Begin creating GameState's String representation.
        for(int i = 0; i < board.length; i++) {

            // Places '|' at the BEGINNING of every BOARD row.
            gameBoardStringified.append(SIDE_BORDER_CHAR);
            gameBoardStringified.append(SPACE_CHAR);

            // Place the board and space chars in the appropriate spots.
            for(int j = 0; j < board[0].length; j++) {
                gameBoardStringified.append(this.board[i][j]);
                gameBoardStringified.append(SPACE_CHAR);
            }
            
            // Places '|' at the END of every BOARD row.
            gameBoardStringified.append(SIDE_BORDER_CHAR);
            gameBoardStringified.append(NEWLINE_CHAR);

        }

        // Ensure that the last line is the horizontal border.
        gameBoardStringified.append(horizontalBorder);
        gameBoardStringified.append(NEWLINE_CHAR);

        // Finally return GameState's String representation.
        return gameBoardStringified.toString();

    }

    /**
     * Override the equals() method for the GameState class.
     * Compares two GameState objects and returns true if they're
     * the same. And, as expected, returns false if otherwise.
     * 
     * @param other The object in question to be compared to
     *              the calling object.
     * @return      True if both objects are precisely the same.
     *              False if both objects are not the same.
     */
    @Override
    public boolean equals(Object other) {

        // If argument passed in is null, return false immediately.
        if(other == null) {
            return false;
        }

        // If the parameter ISN'T a GameState object, return false.
        if(!(other instanceof GameState)) {
            return false;
        }

        // Check if both objects have differing values for levelPassed.
        if(this.levelPassed != ((GameState)other).levelPassed) {
            return false;
        }

        // Check if both objects have differing playerRow values.
        if(this.playerRow != ((GameState)other).playerRow) {
            return false;
        }

        // Check if both objects have differing playerCol values.
        if(this.playerCol != ((GameState)other).playerCol) {
            return false;
        }

        // Check if both objects have differing values for goalRow.
        if(this.goalRow != ((GameState)other).goalRow) {
            return false;
        }

        // Check if both objects have differing values for goalCol.
        if(this.goalCol != ((GameState)other).goalCol) {
            return false;
        }

        // Check if both objects have differing GameState board heights.
        if(this.board.length != ((GameState)other).board.length) {
            return false;
        }

        // Check if both objects have differing GameState board widths.
        if(this.board[0].length != ((GameState)other).board[0].length) {
            return false;
        }

        // Check if the board contents have differing chars.
        for(int i = 0; i < this.board.length; i++) {
            for(int j = 0; j < this.board[0].length; j++) {

                // Characters at one particular spot on both boards.
                char thisBoard = this.board[i][j];
                char thatBoard = ((GameState)other).board[i][j];

                // Where the character checker itself occurs.
                if(thisBoard != thatBoard) {
                    return false;
                }

            }
        }

        // Return true if both objects are equal.
        return true;

    }

}
