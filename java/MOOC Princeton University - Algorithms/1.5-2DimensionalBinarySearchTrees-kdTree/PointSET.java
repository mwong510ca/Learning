package mwong.algs4.kdtree;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.TreeSet;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation: java PointSET.java
 *  Dependencies (algs4.jar): Point2D.java, RectHV.java
 *
 *  A mutable data type PointSET.java that represents a set of points in the unit square. 
 *  Implement the following API by using a red-black BST (using java.util.TreeSet).
 *
 ****************************************************************************/

public class PointSET {
    private TreeSet<Point2D> pSet; 
       
    /**
     * Initializes an empty set of points.
     */
    public PointSET() {
        pSet = new TreeSet<Point2D>();
    }
   
    /**
     * Returns the boolean represent the set is empty.
     * 
     * @return boolean represent the set is empty
     */
    public boolean isEmpty() {
        return (pSet.isEmpty());
    }
   
    /**
     * Returns the number of points in the set.
     * 
     * @return number of points in the set
     */
    public int size() {
        return pSet.size();
    }
   
    /**
     * Add the point p to the set (if it is not already in the set).
     * 
     * @param p the point
     */
    public void insert(Point2D p) {
        pSet.add(p);
    }
   
    /**
     *  Returns the boolean represent the set contain the point p.
     *  
     *  @param p the other point
     *  @return boolean represent the set contain the point p
     */
    public boolean contains(Point2D p) {
        return pSet.contains(p);
    }
   
    /**
     *  Draw all of the points to standard draw.
     */
    public void draw() {
        for (Point2D p : pSet) {
            p.draw();
        }
    }
    
    /**
     * Returns an independent iterator of all points in the set that are inside the rectangle.
     * 
     * @param rect the other RectHV object represent the rectangle
     * @return independent iterator of all points in the set that are inside the rectangle
     */
    public Iterable<Point2D> range(RectHV rect) {
        double xmin, xmax, ymin, ymax;
        xmin = rect.xmin();
        xmax = rect.xmax();
        ymin = rect.ymin();
        ymax = rect.ymax();
        
        TreeSet<Point2D> rSet = new TreeSet<Point2D>();
        
        for (Point2D p : pSet) {
            if (p.y() < ymin) {
                continue;
            } else if (p.y() >= ymin && p.y() <= ymax) {
                if (p.x() >= xmin && p.x() <= xmax) {
                    rSet.add(p);
                }
            } else {
                break;
            }
        }
        return rSet;
    }
   
    /**
     * Returns a nearest neighbor in the set to p; null if set is empty.
     * 
     * @param p the other point
     * @return a nearest neighbor in the set to p; null if set is empty
     */
    public Point2D nearest(Point2D p) {
        if (pSet.isEmpty()) {
            return null;
        }
        if (pSet.size() == 1) {
            return pSet.first();
        }
        Point2D np = null;
        double minDist = Double.POSITIVE_INFINITY;
        for (Point2D p2 : pSet) {
            if (p2.distanceSquaredTo(p) < minDist) {
                np = p2;
                minDist = np.distanceSquaredTo(p);
            }
        }
        return np;
    }
}
