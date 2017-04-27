package mwong.algs4.collinear;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation:  javac PointReference.java
 *  Dependencies: Point.java
 *  
 *  An immutable data type ReferencePoint that represents a point, the order 
 *  and a reference slope in the plane
 *
 ******************************************************************************/

public class ReferencePoint {
    private Point p;
    private int pos;
    private double slope;

    /**
     * Initializes a new ReferencePoint.
     *
     * @param p the Point object
     * @param pos the integer of the order or position of the point
     */
    public ReferencePoint(Point p, int pos) {
        this.p = p;
        this.pos = pos;
    }
    
    /**
     * Returns the Point object.
     * 
     * @return the Point object.
     */
    public Point getPoint() {
        return p;
    }
    
    /**
     * Returns an integer representation of the order or position of the Point.
     * 
     * @return an integer representation of the order or position of the Point.
     */
    public int getPos() {
        return pos;
    }
    
    /**
     * Calculate the reference slope of Point "that".
     * 
     * @param that the other point
     */
    public void setSlope(ReferencePoint that) {
        slope = p.slopeTo(that.p);
    }
    
    /**
     * Returns a double representation of the reference slope of the Point.
     * 
     * @return a double representation of the reference slope of the Point.
     */
    public double getSlope() {
        return slope;
    }
}
