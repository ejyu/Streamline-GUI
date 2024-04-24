import javafx.scene.shape.Rectangle;

/**
 * This class is a wrapper for the Rectangle class. It creates equilateral 
 * Rectangles with curved corners. 
 */
public class RoundedSquare extends Rectangle
{
    static final double DEFAULT_ARC_FRACTION = 0.325;
    static final int HALF_DIVISOR = 2;
    
    double arcFraction = DEFAULT_ARC_FRACTION;
    double centerX, centerY; //The superclass Rectangle uses upperleft corner

    /**************************************************************************
                                    CONSTRUCTORS
    **************************************************************************/

    /**
     * Creates a default rounded square at (0,0) with size 0. 
     */
    public RoundedSquare()
    {
        this(0, 0, 0);
    }
    
    /**
     * Creates a default rounded square at (0,0) with input size. 
     * 
     * @param size the side length
     */
    public RoundedSquare(double size)
    {
        this(0, 0, size);
    }

    /**
     * Creates a rounded square at (x,y) with input size. 
     * 
     * @param centerX the center x-coordinate
     * @param centerY the center y-coordinate
     * @param size    the side length
     */
    public RoundedSquare (double centerX, double centerY, double size)
    {
        this.centerX = centerX;
        this.centerY = centerY;
        setSize(size);
    }

    /**************************************************************************
                                        Getters
    **************************************************************************/
    
    /**
     * Getter for the center x-coordinate of this RoundedSquare.
     * 
     * @return the center x-coordinate
     */
    public double getCenterX()
    {
        return centerX;
    }

    /**
     * Getter for the center y-coordinate of this RoundedSquare.
     * 
     * @return the center y-coordinate
     */
    public double getCenterY()
    {
        return centerY;
    }

    /**
     * Getter for the side length of this RoundedSquare.
     * 
     * @return the side length
     */
    public double getSize()
    {
        return getWidth();
    }
    
    /**
     * Getter for the arc fraction (amount of curve of the corners) of this 
     * RoundedSquare.
     * 
     * @return the arc fraction (of the sidelength)
     */
    public double getArcFraction()
    {
        return arcFraction;
    }

    /**************************************************************************
                                        Setters
    **************************************************************************/

    /**
     * Setter for the center x-coordinate of this RoundedSquare.
     * 
     * @param centerX the new center x-coordinate
     */
    public void setCenterX(double centerX)
    {
        this.centerX = centerX;
        this.setX(centerX - getSize()/HALF_DIVISOR);
    }

    /**
     * Setter for the center y-coordinate of this RoundedSquare.
     * 
     * @param centerY the new center y-coordinate
     */
    public void setCenterY(double centerY) {
        this.centerY = centerY;
        this.setY(centerY - getSize()/HALF_DIVISOR);
    }

    /**
     * Setter for the side length of this RoundedSquare (maintains same center).
     * 
     * @param size the new side length
     */
    public void setSize(double size)
    {
        setWidth(size);
        setHeight(size);
        setArcWidth(size*arcFraction);
        setArcHeight(size*arcFraction);
        setX(centerX - size/HALF_DIVISOR);
        setY(centerY - size/HALF_DIVISOR);
    }

    /**
     * Setter for the arc fracton (amount of curve of the corners) of this 
     * RoundedSquare.
     * 
     * @param arcFraction the new arc fraction (of the sidelength)
     */
    void setArcFraction(double arcFraction)
    {
        this.arcFraction = arcFraction;
        setArcWidth(getSize()*arcFraction);
        setArcHeight(getSize()*arcFraction);
    }
}
