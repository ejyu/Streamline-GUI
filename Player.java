/**
 * Name: E.J. Yu
 * Date: May 18, 2019
 * Resources: Assignment documentation and
 *            Oracle's Java documentation.
 * 
 * The Player.java file contains one class extending the
 * RoundedSquare class that consists of a few constants,
 * one no-argument constructor, and one method for setting
 * very specific attributes for the Player object's shape.
 * These will be used to identify the Player on the game's
 * grid. Sets colors, stroke types, and shape sizes, but
 * not much more than that.
 * 
 * @author E.J. Yu
 */

import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

/**
 * The Player class sets the appropriate attributes needed
 * to display the player's block onto the Streamline game's
 * grid. Sets colors, stroke types, and shape sizes.
 */
public class Player extends RoundedSquare {

    // Fractional multiplier of stroke width.
    final static double STROKE_FRACTION = 0.1;

    // Player object's colors.
    private static final String PLAYER_FILL = "0f5991";
    private static final String PLAYER_STROKE = "329ebf";

    /**
     * Default constructor. Sets a fill color, sets a
     * stroke color, and sets the stroke type to centered.
     */
    public Player() {

        // Set Player fill color.
        setFill(Color.web(PLAYER_FILL));

        // Set Player stroke color.
        setStroke(Color.web(PLAYER_STROKE));

        // Set stroke type to centered.
        setStrokeType(StrokeType.CENTERED);

    }
    
    /**
     * Sets some defined size to a target attribute in
     * this Player object.
     * 
     * @param size The RoundedSquare size value in question.
     */
    @Override
    public void setSize(double size) {

        // Calculate the RoundedSquare object's stroke width.
        double strokeSize = size * STROKE_FRACTION;

        // Set the size of this RoundedSquare object.
        super.setSize(size);
        super.setStrokeWidth(strokeSize);

    }
}