import java.io.*;
import java.util.*;

/**
 * A manager class that runs the Streamline game. It parses command line
 * arguments, initializes the game accordingly, then plays it level by level.
 */
public class GameManager{

    static final String USAGE = 
        "Usage: \n" + 
        "> java GameManager                    - "
        + "to start a game with default size 6*5 and random obstacles \n" + 
        "> java GameManager <filename>         - "
        + "to start a game by reading game state from the specified file \n" +
        "> java GameManager <directory>        - "
        + "to start a game by reading all game states from files in \n" +
        "                                        "
        + "the specified directory and playing them in order\n" +
        "> java GameManager <dir/file> --solve - "
        + "to load levels from file / director then auto play them\n";

    static final String SOLVE_TOKEN = "--solve";

    // how long to pause between each move in auto solve, ms
    static final int PAUSE_DURATION = 500;

    /**
     * Main method. Parses command line arg, load games, run games.
     * 
     * @param args an array of command line args in Strings
     */
    public static void main(String[] args) {

        if (args.length != 0 && args.length != 1 && args.length != 2) {
            System.out.print(USAGE);
            return;
        }

        if (args.length == 0) {
            System.out.println("Starting a default-sized random game..");
            Streamline game = new Streamline();
            game.play();   
            return;
        }

        // at this point args.length == 1
        
        File file = new File(args[0]);
        if (!file.exists()) {
            System.out.printf("File %s does not exist. Exiting..", args[0]);
            return;
        }

        // if is not a directory, read from the file and start the game
        if (!file.isDirectory()) {
            System.out.printf("Loading single game from file %s..\n", args[0]);
            Streamline game = new Streamline(args[0]);
            if (args.length == 2 && SOLVE_TOKEN.equals(args[1]))
                solve(game);
            else
                game.play();   
            return;
        }

        // file is a directory, walk the directory and load from all files
        File[] subfiles = file.listFiles();
        for (int i=0; i<subfiles.length; i++) {
            File subfile = subfiles[i];
            
            // in case there's a directory in there, skip
            if (subfile.isDirectory()) continue;

            // assume all files are properly formatted games, 
            // create new game for each file, play it, move on to the next one
            System.out.printf("Loading game %d/%d from file %s..\n",
                i+1, subfiles.length, subfile.toString());
            Streamline game = new Streamline(subfile.toString());

            if (args.length == 2 && SOLVE_TOKEN.equals(args[1]))
                solve(game);
            else
                game.play();            
        }       
        
    }

    /**
     * Look for a solution, then autoplay the game.
     * 
     * @param game the game we want to try to solve
     */
    static void solve(Streamline game) {
        System.out.println("Looking for solution for the game...");

        List<Direction> solution = findSolution(game.currentState);

        if (solution == null) {
            System.out.println("No solution found.");
            return;
        }

        System.out.println("Solution found, auto-playing...");
        System.out.print(game.currentState);

        for (Direction dir_to_move : solution) {
            try {
                Thread.sleep(PAUSE_DURATION);
            } catch (Exception e) {}
            System.out.printf("Moving %s\n", dir_to_move);
            game.recordAndMove(dir_to_move);
            System.out.print(game.currentState);
        }
        System.out.println("Level passed!");
    }

    /**
     * Recursively perform a DFS to look for a list of direction
     * that we can move towards to result in a level being passed.
     * 
     * @param start_state  the start state of game we want to solve
     * @return             if solution is found, a list of movement required
     *                     to go from start_state to the solved state
     *                     if solution not found, null
     */
    static List<Direction> findSolution(GameState start_state) {

        final Direction[] all_dirs = {
            Direction.UP, 
            Direction.RIGHT, 
            Direction.DOWN, 
            Direction.LEFT
        };

        // base case, the gamestate is already solved, no movement necessary
        // return an empty list
        if (start_state.levelPassed)
            return new ArrayList<Direction>();

        // for each direction, make a copy of the game state and move it
        // 
        // if after move, the state stays the same, then move was meaningless
        // skip 
        // else we changed to a new state, recursively find the solution 
        // starting from the changed state 
        for (int i=0; i<all_dirs.length; i++){
            Direction dir_to_move = all_dirs[i];

            GameState copy = new GameState(start_state);
            copy.move(dir_to_move);

            // if move is meaningless, skip this direction
            if (copy.equals(start_state)) continue;

            // recursively find the solution 
            List<Direction> solution = findSolution(copy);

            // if it returns null, there's no solution in this path
            if (solution == null) continue;

            // there is a solution, append dir_to_move to the front
            // then return up
            solution.add(0, dir_to_move);
            return solution;
        }

        // if we get here and still haven't returned
        // all four direction either cannot move
        // or they do not have solution, the game is unsolvable starting
        // at this start_state, there is no solution
        return null;
    }
}