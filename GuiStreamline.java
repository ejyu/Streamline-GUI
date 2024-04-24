/**
 * Name: E.J. Yu
 * Date: May 18, 2019
 * Resources: Assignment documentation,
 *            Oracle's Java documentation,
 *            and self-authored Piazza posts.
 * 
 * The GuiStreamline.java file contains one class extending
 * Application which consists of one nested class, multiple
 * constants, multiple instance variables to aid in the
 * rendering of the Streamline game's designated window, as
 * well as multiple methods to aid in the building and
 * manipulation of the Streamline game's entire front-end.
 * 
 * This file is used to give the Streamline game's back-end
 * a graphical representation of said game in order to provide
 * the game with a much more enjoyable and attractive overall
 * experience.
 * 
 * @author E.J. Yu
 */

import javafx.scene.*;
import javafx.scene.shape.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.*;
import javafx.util.Duration;

/**
 * The GuiStreamline class renders a graphical representation of
 * the Streamline game's backend. Displays the board with a
 * proper array of shapes, and handles supported keystrokes
 * that are pressed when playing a level. Contains multiple
 * instance variables to aid in the rendering of the Streamline
 * game's designated window.
 */
public class GuiStreamline extends Application {

    // Scene dimensions.
    static final double MAX_SCENE_WIDTH = 600;
    static final double MAX_SCENE_HEIGHT = 600;

    // Aesthetic attributes.
    static final double PREFERRED_SQUARE_SIZE = 100;
    static final double MIDDLE_OFFSET = 0.5;
    static final double DOUBLE_MULTIPLIER = 2;

    // Animation durations in milliseconds.
    static final double SCALE_TIME = 175;
    static final double FADE_TIME = 250;

    // Titles and taglines.
    static final String TITLE = "CSE 8b Streamline GUI";
    static final String USAGE = 
        "Usage: \n" + 
        "> java GuiStreamline               - to start a game with default" +
            " size 6*5 and random obstacles\n" + 
        "> java GuiStreamline <filename>    - to start a game by reading g" +
            "ame state from the specified file\n" +
        "> java GuiStreamline <directory>   - to start a game by reading a" +
            "ll game states from files in\n" +
        "                                     the specified directory and " +
            "playing them in order\n";

    // Colors of grid pieces.
    static final Color TRAIL_COLOR = Color.web("0f5991");
    static final Color GOAL_COLOR = Color.web("8cd1b4");
    static final Color OBSTACLE_COLOR = Color.web("756f6f");

    // Trail radius will be set to this
    // fraction of the size of a board square.
    static final double TRAIL_RADIUS_FRACTION = 0.1;

    // Squares will be resized to this
    // fraction of the size of a board square.
    static final double SQUARE_FRACTION = 0.8;
    
    // The primary stage / scene.
    Stage mainStage;
    Scene mainScene;

    // For obstacles and trails.
    Group levelGroup;

    // Parent group for everything else.
    Group rootGroup;
    
    // GUI representation of the player.
    Player playerRect;

    // GUI representation of the goal.
    RoundedSquare goalRect;

    // Same dimensions as the game board.
    Shape[][] grid;
    
    // The current level.
    Streamline game;

    // Future levels.
    ArrayList<Streamline> nextGames;

    // Handles keyboard input.
    MyKeyHandler myKeyHandler;

    /** 
     * Coverts the given board column and row into scene coordinates.
     * Gives the center of the corresponding tile.
     * 
     * @param boardCol a board column to be converted to a scene x
     * @param boardRow a board row to be converted to a scene y
     * @return scene coordinates as length 2 array where index 0 is x
     */
    public double[] boardIdxToScenePos(int boardCol, int boardRow) {

        // Convert the board's column value to the scene's X-coordinate.
        double sceneX = ((boardCol + MIDDLE_OFFSET) * 
            (mainScene.getWidth() - 1)) / getBoardWidth();

        // Convert the board's row value to the scene's Y-coordinate.
        double sceneY = ((boardRow + MIDDLE_OFFSET) * 
            (mainScene.getHeight() - 1)) / getBoardHeight();

        // Return the board's positions converted to scene coordinates.
        return new double[]{sceneX, sceneY};
        
    }

    /**
     * Getter for the width of the current level's board.
     * 
     * @return Current level's board width.
     */
    public int getBoardWidth() {
        return this.game.currentState.board[0].length;
    }

    /**
     * Getter for the height of the current level's board.
     * 
     * @return Current level's board height.
     */
    public int getBoardHeight() {
        return this.game.currentState.board.length;
    }

    /**
     * Finds a size for a single square of the board that
     * will fit nicely in the current scene size.
     * 
     * @return The smallest side length of some calculated shape.
     */
    public double getSquareSize() {

        // Get the dimensions of the scene.
        double sceneWidth = this.mainScene.getWidth();
        double sceneHeight = this.mainScene.getHeight();

        // Get the dimensions of the current level's board.
        double boardWidth = (double)(this.getBoardWidth());
        double boardHeight = (double)(this.getBoardHeight());

        // Calculate the exact fitting side length for the shape.
        double shapeWidth = sceneWidth / boardWidth;
        double shapeHeight = sceneHeight / boardHeight;

        // Return this shape's smallest side length.
        if(shapeWidth < shapeHeight){
            return shapeWidth;
        }
        else {
            return shapeHeight;
        }

    }
    
    /**
     * Destroy and recreate grid and all trail and obstacle shapes.
     * Assumes the dimensions of the board may have changed.
     */
    public void resetGrid() {

        // Clear levelGroup before rebuilding the grid.
        this.levelGroup.getChildren().clear();

        // Rebuild the grid to an explicit height and width.
        this.grid = new Shape[this.getBoardHeight()]
                             [this.getBoardWidth()];

        // Calculate the sizes of all levelGroup shapes.
        double squareSize = getSquareSize() * SQUARE_FRACTION;
        double circleSize = getSquareSize() * TRAIL_RADIUS_FRACTION;

        // Iterate through the board to place the "board pieces" down.
        for(int i = 0; i < this.getBoardHeight(); i++) {
            for(int j = 0; j < this.getBoardWidth(); j++) {

                // Convert this board position to scene coordinates.
                double[] scenePos = this.boardIdxToScenePos(j, i);

                // Add a solid, colored Circle representing
                // some trail character to levelGroup.
                if(this.game.currentState.board[i][j] ==
                    GameState.TRAIL_CHAR) {
                        this.grid[i][j] = new Circle(
                            scenePos[0],
                            scenePos[1],
                            circleSize
                        );
                        this.grid[i][j].setFill(TRAIL_COLOR);
                        this.levelGroup.getChildren().add(grid[i][j]);
                }

                // Add a solid, colored RoundedSquare representing
                // some obstacle character to levelGroup.
                else if(this.game.currentState.board[i][j] ==
                    GameState.OBSTACLE_CHAR) {
                        this.grid[i][j] = new RoundedSquare(
                            scenePos[0],
                            scenePos[1],
                            squareSize
                        );
                        this.grid[i][j].setFill(OBSTACLE_COLOR);
                        this.levelGroup.getChildren().add(grid[i][j]);
                }

                // Add a transparent Circle representing
                // some space character (or any position
                // on the board not occupied by a temporary
                // ojbect) to levelGroup.
                else {
                        this.grid[i][j] = new Circle(
                            scenePos[0],
                            scenePos[1],
                            circleSize
                        );
                        this.grid[i][j].setFill(Color.TRANSPARENT);
                        this.levelGroup.getChildren().add(grid[i][j]);
                }

            }
        }

    }

    /**
     * Sets the fill color of all trail Circles, making
     * them visible or not depending on if that board
     * position equals TRAIL_CHAR.
     */
    public void updateTrailColors() {

        // Iterate through the current level's board.
        for(int i = 0; i < this.getBoardHeight(); i++) {
            for(int j = 0; j < this.getBoardWidth(); j++) {

                // Update Circle's fill to transparent
                // if a space is identified.
                if(this.game.currentState.board[i][j] ==
                    GameState.SPACE_CHAR) {
                        this.grid[i][j].setFill(Color.TRANSPARENT);
                }

                // Update Circle's fill to TRAIL_COLOR
                // if a trail is identified.
                if(this.game.currentState.board[i][j] ==
                    GameState.TRAIL_CHAR) {
                        this.grid[i][j].setFill(TRAIL_COLOR);
                }

            }
        }

    }
    
    /**
     * Called by the event handler to handle player movement.
     * 
     * @param fromCol Initial column value.
     * @param fromRow Initial row value.
     * @param toCol Target column value.
     * @param toRow Target row value.
     */
    public void onPlayerMoved(int fromCol, int fromRow,
                              int toCol, int toRow) {
        
        // Immediately return if the level has been passed.
        if(this.game.currentState.levelPassed) {
            this.onLevelLoaded();
            this.onLevelFinished();
            return;
        }

        // Immediately return if the position is the same.
        if(fromCol == toCol && fromRow == toRow) {
            return;
        }

        // Update the player's position.
        double[] playerPos = this.boardIdxToScenePos(toCol,toRow);
        this.playerRect.setCenterX(playerPos[0]);
        this.playerRect.setCenterY(playerPos[1]);

        // Update the trail's colors.
        this.updateTrailColors();

    }
    
    /**
     * Helper method to help handle key presses.
     * 
     * @param keyCode The keystroke in question.
     */
    void handleKeyCode(KeyCode keyCode) {

        // Preserve the player's old location.
        int playerRowOld = this.game.currentState.playerRow;
        int playerColOld = this.game.currentState.playerCol;

        // Execute certain tasks for specific keystrokes.
        switch (keyCode) {

            // NOTE:
            // W / A / S / D keys can move in a direction.
            // Non-numeric keypad arrow keys can move in a direction.
            // Numeric keypad arrow keys can also move in a direction.

            case W:
                // Move up.
                this.game.recordAndMove(Direction.UP);
                break;
            case S:
                // Move down.
                this.game.recordAndMove(Direction.DOWN);
                break;
            case A:
                // Move left.
                this.game.recordAndMove(Direction.LEFT);
                break;
            case D:
                // Move right.
                this.game.recordAndMove(Direction.RIGHT);
                break;

            case UP:
                // Move up.
                this.game.recordAndMove(Direction.UP);
                break;
            case DOWN:
                // Move down.
                this.game.recordAndMove(Direction.DOWN);
                break;
            case LEFT:
                // Move left.
                this.game.recordAndMove(Direction.LEFT);
                break;
            case RIGHT:
                // Move right.
                this.game.recordAndMove(Direction.RIGHT);
                break;

            case KP_UP:
                // Move up.
                this.game.recordAndMove(Direction.UP);
                break;
            case KP_DOWN:
                // Move down.
                this.game.recordAndMove(Direction.DOWN);
                break;
            case KP_LEFT:
                // Move left.
                this.game.recordAndMove(Direction.LEFT);
                break;
            case KP_RIGHT:
                // Move right.
                this.game.recordAndMove(Direction.RIGHT);
                break;

            case U:
                // Undo move.
                this.game.undo();
                break;
            case O:
                // Save user's data.
                this.game.saveToFile();
                break;
            case Q:
                // Terminate the game.
                System.exit(0);

            default:
                System.out.println("Possible commands:\n w - up\n " + 
                    "a - left\n s - down\n d - right\n u - undo\n " + 
                    "q - quit level");
                break;
        }

        // Call onPlayerMoved() to update the GUI to reflect the player's 
        // movement (if any).
        this.onPlayerMoved(
            playerColOld,
            playerRowOld,
            this.game.currentState.playerCol,
            this.game.currentState.playerRow
        );

    }

    /**
     * This nested private class handles keyboard input
     * and calls handleKeyCode().
     */
    private class MyKeyHandler implements EventHandler<KeyEvent> {
        
        /**
         * Handles what to do in the even that a
         * key has been pressed.
         */
        public void handle(KeyEvent e) {

            // Determine what to do based on the
            // user's keystroke.
            handleKeyCode(e.getCode());

        }

    }

    /**
     * Rebuilds the graphical representation of some level
     * once said level has been loaded.
     */
    public void onLevelLoaded() {

        // Empty and rebuild the grid.
        this.resetGrid();

        // Calculate the sizes of all squares.
        double squareSize = this.getSquareSize() * SQUARE_FRACTION;

        // Update the player's position.
        double[] playerPos = boardIdxToScenePos(
            this.game.currentState.playerCol,
            this.game.currentState.playerRow
        );
        this.playerRect.setSize(squareSize);
        this.playerRect.setCenterX(playerPos[0]);
        this.playerRect.setCenterY(playerPos[1]);

        // Update the goal's position.
        double[] goalPos = boardIdxToScenePos(
            this.game.currentState.goalCol,
            this.game.currentState.goalRow
        );
        this.goalRect.setSize(squareSize);
        this.goalRect.setCenterX(goalPos[0]);
        this.goalRect.setCenterY(goalPos[1]);

    }

    /** 
     * Called when the player reaches the goal. Shows the winning animation
     * and loads the next level if there is one.
     */
    public void onLevelFinished() {

        // Clone the goal rectangle and scale it up
        // until it covers the screen.

        // Clone the goal rectangle.
        Rectangle animatedGoal = new Rectangle(
            goalRect.getX(),
            goalRect.getY(),
            goalRect.getWidth(),
            goalRect.getHeight()
        );
        animatedGoal.setFill(goalRect.getFill());

        // Scope for children.
        {
            // Add the clone to the scene.
            List<Node> children = rootGroup.getChildren();
            children.add(children.indexOf(goalRect), animatedGoal);
        }

        // Create the scale animation.
        ScaleTransition st = new ScaleTransition(
            Duration.millis(SCALE_TIME), animatedGoal
        );
        st.setInterpolator(Interpolator.EASE_IN);
        
        // Scale enough to eventually cover the entire scene.
        st.setByX(DOUBLE_MULTIPLIER * 
            this.mainScene.getWidth() / animatedGoal.getWidth());
        st.setByY(DOUBLE_MULTIPLIER * 
            this.mainScene.getHeight() / animatedGoal.getHeight());

        // This will be called after the scale animation finishes.
        // If there is no next level, quit. Otherwise switch to it and
        // fade out the animated cloned goal to reveal the new level.
        st.setOnFinished(e1 -> {

            // Check if there is no next game.
            if(this.nextGames.isEmpty()) {

                // Quit if there is no next game.
                System.exit(0);

            }

            // Fetch the next level.
            this.game = nextGames.get(0);

            // Update the list of queued games.
            this.nextGames.remove(0);

            // Remove the animated goal.
            this.rootGroup.getChildren().remove(animatedGoal);

            // Update UI to the next level, but it won't be visible yet
            // because it's covered by the animated cloned goal.
            this.onLevelLoaded();

            Rectangle fadeRect = new Rectangle(0, 0, 
                this.mainScene.getWidth(), mainScene.getHeight());
            fadeRect.setFill(goalRect.getFill());
            
            // Scope for children.
            {
                // Add the fading rectangle to the scene.
                List<Node> children = rootGroup.getChildren();
                children.add(children.indexOf(goalRect), fadeRect);
            }

            FadeTransition ft = new FadeTransition(
                Duration.millis(FADE_TIME), fadeRect
            );
            ft.setFromValue(1);
            ft.setToValue(0);
            
            // Remove the cloned goal after it's finished fading out.
            ft.setOnFinished(e2 -> {
                this.rootGroup.getChildren().remove(fadeRect);
            });
            
            // Start the fade-out now.
            ft.play();
        });
        
        // Start the scale animation.
        st.play();

    }

    /** 
     * Performs file IO to populate game and nextGames using filenames from
     * command line arguments.
     */
    public void loadLevels() {

        game = null;
        nextGames = new ArrayList<Streamline>();
        
        List<String> args = getParameters().getRaw();
        if (args.size() == 0) {
            System.out.println("Starting a default-sized random game...");
            game = new Streamline();
            return;

        }

        // at this point args.length == 1
        
        File file = new File(args.get(0));
        if (!file.exists()) {
            System.out.printf("File %s does not exist. Exiting...", 
                args.get(0));
            return;
        }

        // if is not a directory, read from the file and start the game
        if (!file.isDirectory()) {
            System.out.printf("Loading single game from file %s...\n", 
                args.get(0));
            game = new Streamline(args.get(0));
            return;
        }

        // file is a directory, walk the directory and load from all files
        File[] subfiles = file.listFiles();
        Arrays.sort(subfiles);
        for (int i=0; i<subfiles.length; i++) {

            File subfile = subfiles[i];
            
            // in case there's a directory in there, skip
            if (subfile.isDirectory()) continue;

            // assume all files are properly formatted games, 
            // create a new game for each file, and add it to nextGames
            System.out.printf("Loading game %d/%d from file %s...\n",
                i+1, subfiles.length, subfile.toString());
            nextGames.add(new Streamline(subfile.toString()));

        }

        // Switch to the first level
        game = nextGames.get(0);
        nextGames.remove(0);

    }

    /**
     * The main entry point for all JavaFX Applications
     * Initializes instance variables, creates the scene,
     * and sets up the UI.
     * 
     * @param  primaryStage The window for this application.
     * @throws Exception    The error in question.
     */
    public void start(Stage primaryStage) throws Exception {

        // Populate game and nextGames.
        loadLevels();

        // Initialize the scene and our groups.
        rootGroup = new Group();
        mainScene = new Scene(
            rootGroup,
            MAX_SCENE_WIDTH,
            MAX_SCENE_HEIGHT, 
            Color.GAINSBORO
        );
        levelGroup = new Group();
        rootGroup.getChildren().add(levelGroup);

        // Initialize goalRect.
        this.goalRect = new RoundedSquare();
        this.goalRect.setFill(GOAL_COLOR);

        // Initialize playerRect.
        this.playerRect = new Player();

        // Add goal / player shapes to rootGroup.
        this.rootGroup.getChildren().add(this.goalRect);
        this.rootGroup.getChildren().add(this.playerRect);

        // Set up loaded levels.
        this.onLevelLoaded();

        // Set up keyboard input handling.
        this.myKeyHandler = new MyKeyHandler();
        this.mainScene.setOnKeyPressed(this.myKeyHandler);
        
        // Make the scene visible.
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    /**
     * Execution begins here, but at this point we don't
     * have a UI yet. The only thing to do is call launch()
     * which will eventually result in start() above being
     * called.
     * 
     * @param args The arguments from the command line.
     *             Should be one argument at most which
     *             would be the level/directory to load.
     */
    public static void main(String[] args) {
        
        if(args.length != 0 && args.length != 1) {
            System.out.print(USAGE);
            return;
        }

        launch(args);

    }

}