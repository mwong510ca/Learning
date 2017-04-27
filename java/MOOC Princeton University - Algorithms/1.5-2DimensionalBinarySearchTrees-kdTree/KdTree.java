package mwong.algs4.kdtree;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation: java KdTree.java
 *  Dependencies (algs4.jar): Point2D.java, StdDraw.java, Bag.java, RetHV.java
 *
 *  A mutable data type KdTree.java that uses a 2d-tree to implement the same API 
 *  (but replace PointSET with KdTree). 
 *  A 2d-tree is a generalization of a BST to two-dimensional keys. The idea is 
 *  to build a BST with points in the nodes, using the x- and y-coordinates of 
 *  the points as keys in strictly alternating sequence.
 *
 ****************************************************************************/

public class KdTree {
    private static final double XMIN = 0;
    private static final double XMAX = 1;
    private static final double YMIN = 0;
    private static final double YMAX = 1;
    private static final double DISTMAX = XMAX * XMAX + YMAX * YMAX;
    
    private int N;
    private Node root;
    private double minDist;
    private Point2D nearestPoint;
    
    //  data tyoe Node class for KdTree.
    //  it has the point, x and y coordinates, the pointer to left/bottom subtree,
    //  and the pointer to right/top subtree
    private class Node {
        private Point2D p;
        private double x, y;
        private Node lb, rt;        // left/bottom subtree and right/top subtree
    }
    
    /**
     * Initializes an empty set of points.
     */
    public KdTree() {
        N = 0;
        root = null;
    }
   
    /**
     * Returns the boolean represent the set is empty.
     * 
     * @return boolean represent the set is empty
     */
    public boolean isEmpty() {
        return (N == 0);
    }
   
    /**
     * Returns the number of points in the set.
     * 
     * @return number of points in the set
     */    
    public int size() {
        return N;
    }
   
    /**
     * Add the point p to the set (if it is not already in the set).
     * 
     * @param p the point
     */
    public void insert(Point2D p) {
        Node next = new Node();
        next.p = p;
        next.x = p.x();
        next.y = p.y();
        
        // insert first node of the tree
        if (root == null) {    
            root = next;
            N++;
            return;
        }
        
        // continue search down the kdtree until it reach an empty node for new point
        Node node = root;
        boolean yaxis = true;
        while (true) {
            // search the left/bottom subtree
            if ((yaxis && next.x < node.x) || (!yaxis && next.y < node.y)) {
                if (node.lb == null) {
                    node.lb = next;
                    N++;        
                    return;
                } else {
                    node = node.lb;
                }
            } else if (next.x == node.x && next.y == node.y) {
                return;  // stop the loop if new point exists in the kdtree
            } else {
                if (node.rt == null) {
                    node.rt = next;
                    N++;        
                    return;
                } else {
                    node = node.rt;
                }
            }
            
            // flip the axis for the next search
            yaxis = !yaxis;
        } 
    }
    
    /**
     *  Returns the boolean represent the set contain the point p.
     *  
     *  @param p the other point
     *  @return boolean represent the set contain the point p
     */
    public boolean contains(Point2D p) {
        if (N == 0) {
            return false;
        }
        Node node = root;
        double px = p.x();
        double py = p.y();
        while (true) {
            // first search along the y-axis
            if (px < node.x) {
                if (node.lb == null) {
                    break;
                }
                node = node.lb;
            } else if (py == node.y && px == node.x) {
                return true;
            } else {
                if (node.rt == null) {
                    break;
                }
                node = node.rt;
            }
            
            // second round along the x-axis
            if (py < node.y) {
                if (node.lb == null) {
                    break;
                }
                node = node.lb;
            } else if (px == node.x && py == node.y) {
                return true;
            } else {
                if (node.rt == null) {
                    break;
                }
                node = node.rt;
            }
        } 
        return false;
    }
            
    /**
     *  Draw the boundary of kdtree and all of the points to standard draw.
     */
    public void draw() {
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.line(XMIN, YMIN, XMIN, YMAX);
        StdDraw.line(XMAX, YMIN, XMAX, YMAX);
        StdDraw.line(XMIN, YMIN, XMAX, YMIN);
        StdDraw.line(XMIN, YMAX, XMAX, YMAX);
        StdDraw.show(0);
        draw(root, XMIN, XMAX, YMIN, YMAX, true);
    }

    // recursive call to draw all points in the kdtree
    private void draw(Node node, double xmin, double xmax, double ymin, double ymax, 
        boolean yaxis) {
        if (node == null) {
            return;
        }
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setPenRadius(0.01);
        
        if (yaxis) {
            double px = node.x;
            StdDraw.point(px, node.y);
            StdDraw.setPenColor(Color.RED);
            StdDraw.setPenRadius();
            
            StdDraw.line(px, ymin, px, ymax);
            draw(node.lb, xmin, px, ymin, ymax, false);
            draw(node.rt, px, xmax, ymin, ymax, false);
        } else {
            double py = node.y;
            StdDraw.point(node.x, py);
            StdDraw.setPenColor(Color.BLUE);
            StdDraw.setPenRadius();
            
            StdDraw.line(xmin, py, xmax, py);
            draw(node.lb, xmin, xmax, ymin, py, true);
            draw(node.rt, xmin, xmax, py, ymax, true);
        }
    }

    /**
     * Returns an independent iterator of all points in the set that are inside the rectangle.
     * 
     * @param rect the other RectHV object represent the rectangle
     * @return independent iterator of all points in the set that are inside the rectangle
     */
    public Iterable<Point2D> range(RectHV rect) {
        Bag<Point2D> pointsBag = new Bag<Point2D>();
        if (N == 0) {
            return pointsBag;
        }
        inRange(rect, root, pointsBag, true);
        return pointsBag;
    }
   
    // recursive call to add all points in the set that are inside the rectangle
    private void inRange(RectHV rect, Node node, Bag<Point2D> pointsBag, boolean yaxis) {
        if (node == null) {
            return;
        }
        if (yaxis) {
            if (node.x >= rect.xmin() && node.x <= rect.xmax()) {
                if (rect.contains(node.p)) {
                    pointsBag.add(node.p);
                }
                inRange(rect, node.lb, pointsBag, false);
                inRange(rect, node.rt, pointsBag, false);
            } else if (node.x < rect.xmin()) {
                inRange(rect, node.rt, pointsBag, false);
            } else {
                inRange(rect, node.lb, pointsBag, false);
            }
        } else {
            if (node.y >= rect.ymin() && node.y <= rect.ymax()) {
                if (rect.contains(node.p)) {
                    pointsBag.add(node.p);
                }
                inRange(rect, node.lb, pointsBag, true);
                inRange(rect, node.rt, pointsBag, true);
            } else if (node.y < rect.ymin()) {
                inRange(rect, node.rt, pointsBag, true);
            } else {
                inRange(rect, node.lb, pointsBag, true);
            }
        }
    }

    /**
     * Returns a nearest neighbor in the set to p; null if set is empty.
     * 
     * @param p the other point
     * @return a nearest neighbor in the set to p; null if set is empty
     */
    public Point2D nearest(Point2D p) {
        if (N == 0) {
            return null;
        }
        
        minDist = DISTMAX;
        nearestPoint = root.p;
        searchNearest(root, p, true, true);
        return nearestPoint;
    }
    
    // recursive call update the nearest point and the distance between point p
    private void searchNearest(Node node, Point2D p, boolean yaxis, boolean inRange) {
        if (node == null) {
            return;
        }

        double px = p.x();
        double py = p.y();
        double dx = px - node.x;
        double dy = py - node.y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < minDist) {
            minDist = dist;
            nearestPoint = node.p;
        }
        
        if (inRange) {
            if ((yaxis && px < node.x) || (!yaxis && py < node.y)) {
                searchNearest(node.lb, p, !yaxis, true);
                if ((yaxis && px + minDist >= node.x) || (!yaxis && py + minDist >= node.y)) {
                    searchNearest(node.rt, p, !yaxis, false);
                }
            } else {
                searchNearest(node.rt, p, !yaxis, true);
                if ((yaxis && px - minDist < node.x) || (!yaxis && py - minDist < node.y)) {
                    searchNearest(node.lb, p, !yaxis, false);
                }
            }
        } else {
            if ((yaxis && px - minDist < node.x) || (!yaxis && py - minDist < node.y)) {
                searchNearest(node.lb, p, !yaxis, false);
            }
            if ((yaxis && px + minDist >= node.x) || (!yaxis && py + minDist >= node.y)) {
                searchNearest(node.rt, p, !yaxis, false);
            }
        }
    }
}
