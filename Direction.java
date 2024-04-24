/**
 * An enumerator defining the 4 possible move directions in a
 * Streamline game.
 */
public enum Direction
{
    UP(0), 
    RIGHT(1), 
    DOWN(2), 
    LEFT(3);

    private int rotationCount;

    /**
     * Constructor for the enum
     * 
     * @param rotationCount see getRotationCount()
     */
    Direction(int rotationCount)
    {
        this.rotationCount = rotationCount;
    }

    /**
     * Returns rotationCount, the number of counterclockwise rotations 
     * needed to rotate the game state before moving up (e.g. to 
     * move right, rotate once then move up). 
     * 
     * @return counterclockwise rotation count
     */
    public int getRotationCount()
    {
        return this.rotationCount;
    }
}