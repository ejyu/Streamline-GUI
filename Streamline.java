/**
 * Name: E.J. Yu
 * Date: April 23, 2019
 * Resources: Assignment documentation, Oracle's Java documentation,
 *            and self-authored Piazza posts.
 * 
 * The Streamline.java file contains one class consisting of two
 * constructors and multiple methods that have been implemented
 * to define and administer how the user interacts with the game.
 * The Streamline class uses GameState objects, which define
 * the game's grid and "piece" positions; likewise, Streamline
 * objects will define how the user [player] may control those
 * pieces.
 * 
 * @author E.J. Yu
 */

import java.util.*;
import java.io.*;

/**
 * The Streamline class will define how the player controls the
 * pieces of the Streamline game and how the game is updated when
 * the player makes a move. There are only a couple of instance
 * variables here, where both of which serve to hold the Streamline
 * game's internal information: board data, player data, goal data,
 * and related data. The rest, as observed, topping the class are
 * constants.
 */
public class Streamline {
    
    // GameState objects explicitly involved with Streamline.
    GameState currentState;
    List<GameState> previousStates;

    // Default GameState board lengths.
    final static int DEFAULT_HEIGHT = 6;
    final static int DEFAULT_WIDTH = 5;

    // Default number of random obstacles to be generated.
    final static int DEFAULT_OBSTACLE_COUNT = 3;

    // The name of the save data file.
    final static String OUTFILE_NAME = "saved_streamline_game";

    // Strings representing specific user input keystrokes.
    final static String LOWERCASE_W = "w";
    final static String LOWERCASE_A = "a";
    final static String LOWERCASE_S = "s";
    final static String LOWERCASE_D = "d";
    final static String LOWERCASE_U = "u";
    final static String LOWERCASE_O = "o";
    final static String LOWERCASE_Q = "q";

    // Constants used for File I/O tasks in Streamline.
    final static String GREATER_THAN = ">";
    final static String SPACE = " ";
    final static char NEWLINE_CHAR = '\n';

    // A message to indicate that the level has been completed.
    final static String WIN_MESSAGE = "Level passed!";

    // A message to indicate that the game has been saved.
    final static String SAVED_MESSAGE = "Saved current state to: ";

    /**
     * Generates a game with default height and width values
     * (alongside default player and goal positions) for the
     * board, plus three random obstacles. Also initializes
     * previousStates to an empty ArrayList.
     */
    public Streamline() {

        // Initialize board using default values.
        this.currentState = new GameState(DEFAULT_HEIGHT, DEFAULT_WIDTH,
                                          DEFAULT_HEIGHT - 1, 0,
                                          0, DEFAULT_WIDTH - 1);

        // Add three random obstacles to the current GameState board.
        this.currentState.addRandomObstacles(DEFAULT_OBSTACLE_COUNT);

        // Initialize the list of previous states, currently empty.
        this.previousStates = new ArrayList<GameState>();

    }

    /**
     * Loads a game from file (as opposed to generating one). 
     * 
     * @param filename  The path to the file to load the game from
     */
    public Streamline(String filename) {

        try {

            // Try loading the Streamline game from a file.
            loadFromFile(filename);

        } catch (IOException e) {

            // If an exception is found, print it to the console.
            e.printStackTrace();

        }

        // Initialize previousStates to an empty ArrayList.
        this.previousStates = new ArrayList<GameState>();
    }

    /**
     * Takes in a parameter "filename", reads a file's contents
     * (presumably the player's save data file), and initializes
     * the appropriate instance variables.
     * 
     * Note: Edge cases do not need to be considered in this
     * method implementation since it is noted on the assignment
     * documentation that a well-formatted file will always be used.
     * 
     * @param filename The name of the player's save data file.
     */
    protected void loadFromFile(String filename) throws IOException {

        // Create new File object in accordance to the parameter.
        File saveData = new File(filename);

        // Create a Scanner object to read in File object.
        Scanner fileReader = new Scanner(saveData);

        // Fetch information about the board, player, and goal.
        int boardHeight = Integer.parseInt(fileReader.next());
        int boardWidth = Integer.parseInt(fileReader.next());
        int playerRowImport = Integer.parseInt(fileReader.next());
        int playerColImport = Integer.parseInt(fileReader.next());
        int goalRowImport = Integer.parseInt(fileReader.next());
        int goalColImport = Integer.parseInt(fileReader.next());

        // Restore the board based on parameter's contents.
        this.currentState = new GameState(boardHeight,
                                          boardWidth,
                                          playerRowImport,
                                          playerColImport,
                                          goalRowImport,
                                          goalColImport);

        // Restore the player to its found location.
        this.currentState.playerRow = playerRowImport;
        this.currentState.playerCol = playerColImport;

        // Restore the goal to its found location.
        this.currentState.goalRow = goalRowImport;
        this.currentState.goalCol = goalColImport;

        // Fetch the height of currentState's restored board.
        int thisHeight = this.currentState.board.length;

        // Fetch the width of currentState's restored board.
        int thisWidth = this.currentState.board[0].length;

        // Begin restoring the board based on parameter's contents.
        while(fileReader.hasNextLine()) {

            // Initialize a new String for storing a line fetched
            // from the scanner.
            String line = new String(fileReader.nextLine());

            // Iterate through the board to restore the board's
            // contents in one specified row.
            for(int i = 0; i < thisHeight; i++) {

                // Fetch a new line from the scanner for every row
                // in board that needs its contents to be restored.
                line = new String(fileReader.nextLine());

                // Restore the specified row in currentState's board.
                for(int j = 0; j < thisWidth; j++) {
                    this.currentState.board[i][j] = line.charAt(j);
                }

            }

        }

        // Ensure that the scanner closes once all tasks have concluded.
        fileReader.close();

    }
    
    /**
     * Save the current state of the Streamline game before
     * the player moves, then move the player in the specified
     * direction.
     * 
     * @param direction The direction that the player would
     *                  like to move towards.
     */
    void recordAndMove(Direction direction) {

        // If null is passed in for direction, do nothing.
        if(direction == null) {
            return;
        }

        // Save a copy of currentState to previousStates.
        GameState currentStateCopy = new GameState(this.currentState);
        this.previousStates.add(currentStateCopy);

        // Move the player in the specified direction.
        this.currentState.move(direction);

        // Fetch indicies and objects for game state comparisons.
        int recentStateIndx = this.previousStates.size() - 1;
        GameState previousState =
            new GameState(this.previousStates.get(recentStateIndx));

        // Undo the update to previousStates that just occurred if
        // no visible change to the board has been identified.
        if(previousState.equals(this.currentState)) {
            this.previousStates.remove(recentStateIndx);
        }

    }

    /**
     * Allows the player to under their last turn.
     */
    void undo() {

        // If previousStates is empty, do nothing.
        if(this.previousStates.size() < 1) {
            return;
        }

        // Fetch the index of the object at the end of previousStates.
        int lastStateIndex = this.previousStates.size() - 1;

        // Fetch the GameState object at lastStateIndex.
        GameState recentState = this.previousStates.get(lastStateIndex);

        // Update currentState to reverse the player's move.
        this.currentState = new GameState(recentState);

        // Remove the restored GameState object from previousStates.
        this.previousStates.remove(lastStateIndex);

    }

    /**
     * Handles the interactive part of the Streamline game.
     * Prints the state of the game to the console and asks for
     * the player's input once a turn has concluded.
     */
    void play() {

        // Set up a Scanner object for reading incoming keystrokes. 
        Scanner inputScanner = new Scanner(System.in);

        // Execute user-specified tasks on incomplete levels.
        while(this.currentState.levelPassed == false){

            // Print out the current game's board.
            System.out.print(this.currentState.toString()); 

            // Indicate that some user input may now be taken.
            System.out.print(GREATER_THAN + SPACE);

            // Store the user's keystroke(s) as a String.
            String userInput = inputScanner.nextLine();

            // Determine what to execute based on the user input.
            switch (userInput) {

                // For keystroke "w", move up and record movement.
                case LOWERCASE_W:
                    recordAndMove(Direction.UP);
                    break;

                // For keystroke "a", move left and record movement.
                case LOWERCASE_A:
                    recordAndMove(Direction.LEFT);
                    break;

                // For keystroke "s", move down and record movement.
                case LOWERCASE_S:
                    recordAndMove(Direction.DOWN);
                    break;

                // For keystroke "d", move right and record movement.
                case LOWERCASE_D:
                    recordAndMove(Direction.RIGHT);
                    break;

                // For keystroke "u", undo the player's last move.
                case LOWERCASE_U:
                    undo();
                    break;

                // For keystroke "o", save the game.
                case LOWERCASE_O:
                    saveToFile();
                    break;

                // For keystroke "q", safely exit the game.
                case LOWERCASE_Q:
                    return;

                // On any other input, ask user for another input.
                default:
                    break;

            }

        }

        // Indicate that the level has been passed, and return.
        if(this.currentState.levelPassed == true) {
            System.out.print(this.currentState.toString()); 
            System.out.println(WIN_MESSAGE);
            return;
        }

    }

    /**
     * Writes the state of the Streamline game to a file with
     * explicit formatting.
     */
    void saveToFile() {

        try {

            // Create a new File object to write data to.
            File saveData = new File(OUTFILE_NAME);

            // Writing object enables the program to write output.
            PrintWriter saveDataWriter = new PrintWriter(saveData);

            // Fetch the side lengths of currentState's board.
            int boardHeight = this.currentState.board.length;   
            int boardWidth = this.currentState.board[0].length;         

            // Write board / player / goal data to the writing object.
            saveDataWriter.print(boardHeight +
                                 SPACE + 
                                 boardWidth +
                                 NEWLINE_CHAR +
                                 this.currentState.playerRow +
                                 SPACE +
                                 this.currentState.playerCol +
                                 NEWLINE_CHAR +
                                 this.currentState.goalRow +
                                 SPACE +
                                 this.currentState.goalCol +
                                 NEWLINE_CHAR);

            // Write data the board data to the writing object.
            for(int i = 0; i < boardHeight; i++) {
                for(int j = 0; j < boardWidth; j++) {
                    saveDataWriter.print(this.currentState.board[i][j]);
                }
                saveDataWriter.print(NEWLINE_CHAR);
            }

            // Flush the stream to ensure the data is written.
            saveDataWriter.flush();

            // Close the writer once all tasks have concluded.
            saveDataWriter.close();

            // Inform the user that their game has been saved.
            System.out.println(SAVED_MESSAGE + OUTFILE_NAME);

            
        // If an exception occurs, print to console.
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
