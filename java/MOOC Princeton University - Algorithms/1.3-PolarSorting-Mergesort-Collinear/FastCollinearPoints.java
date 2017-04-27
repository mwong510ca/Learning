package mwong.algs4.collinear;

import edu.princeton.cs.algs4.StdDraw;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

import mwong.algs4.resources.collinear.LineSegment;
 
/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation:  javac FastCollinearPoints.java
 *  Execution:    java FastCollinearPoints input80.txt
 *  Dependencies  (algs4.jar): StdDraw.java
 *  Dependencies: Point.java, ReferencePoint.java, LineSegment.java(resource)
 *
 *  This program enter a file name at the command line.
 *    - Reads the an input file in the following format: An integer N, followed
 *    by N pairs of integers (x, y), each between 0 and 32,767.
 *
 *  Draw all points and lines with 4 points or more and print all line segments.
 *
 ****************************************************************************/

public class FastCollinearPoints {
    private static Scanner scanner;
    private static String charsetName = "ISO-8859-1";
    private static Locale usLocale = new Locale("en", "US");
    private int numLines, N;
    private LineSegment[] allSegments;
    
    /**
     * Initializes FastCollinearPoints class, take a list of Points and
     * find all 4+ points line segment.
     * @param points the array of Points.
     */
    public FastCollinearPoints(Point[] points) {
        validatePoints(points);
        N = points.length;
        numLines = 0;
        allSegments = new LineSegment[0];
        if (N < 4) {
            return;
        }
        findAllLines(points);
    }
    
    /**
     * Returns the number of line segments.
     * 
     * @return number of line segments
     */
    public int numberOfSegments() {
        return numLines;
    }
    
    /**
     * Returns the array of LineSegments.
     * 
     * @return array of LineSegments
     */
    public LineSegment[] segments() {
        return allSegments.clone();
    }
    
    // validate all points, throw IllegalArgumentException if duplicate points
    private void validatePoints(Point[] points) {
        Point[] p = points.clone();
        Arrays.sort(p);
        for (int i = 0; i < points.length - 1; i++) {
            if (p[i].compareTo(p[i + 1]) == 0) {
                throw new IllegalArgumentException();
            }
        }
    }
    
    // loop over all points and search for 4+ points line segment;
    // increment line segment counter and store the line segment.
    private void findAllLines(Point[] points) {
        numLines = 0;
        allSegments = new LineSegment[0];
        ArrayList<LineSegment> collectLines = new ArrayList<LineSegment>();
        
        // make a copy of point array and sort in position order
        Point[] copy = points.clone();
        Arrays.sort(copy);
        ReferencePoint[] org = new ReferencePoint[N];
        for (int i = 0; i < N; i++) {
            org[i] = new ReferencePoint(copy[i], i);
        }
        
        // initialize a boolean bypass tracking array and bypassIdx for position 0 to
        // position (N - 2) points
        // bypassIdx[0] = 0, bypassIdx[1] = 1, bypassIdx[2] = 3, bypassIdx[3] = 6
        // bypassIdx[4] = 10, ... etc
        boolean[] bypass = new boolean[(N-1)*(N-2)/2];
        int[] bypassIdx = new int[N-1];
        int idx = 0;
        for (int i = 0, j = 1; j < bypassIdx.length; i++, j++) {
            idx += i;
            bypassIdx[j] = idx;
        }
        
        // starts from N - 1 Point as the pivot Point, create a ReferencePoint array from 0 
        // to N - 2 points and set reference slope with the pivot point
        int i = N - 1;
        ReferencePoint pivot = org[i];
        idx = i; 
        ReferencePoint[] p = new ReferencePoint[idx];
        System.arraycopy(org, 0, p, 0, idx);        
        for (int j = 0; j < i; j++) {
            p[j].setSlope(pivot);
        }    
        // sort the reference points in slope order
        mergesort(p);
        
        // loop over all points and find lines
        do {            
            // find all lines, print and draw
            int j = idx - 1;
            while (j > 1) {
                double slope = p[j].getSlope();
                int lnP1 = j;
                
                while (j > 0 && p[j-1].getSlope() == slope) {
                    --j;
                }

                if (lnP1 - j > 1) {
                    for (int l = j; l < lnP1; l++) {
                        int pos1 = p[l].getPos();
                        for (int k = l+1; k < lnP1+1; k++) {
                            // all points along the exists line, update bypass status is true
                            bypass[bypassIdx[p[k].getPos()] + pos1] = true;
                        }                         
                    }
                    numLines++;
                    collectLines.add(new LineSegment(pivot.getPoint(), p[j].getPoint()));
                }
                j--;
            }
            
            // move the pivot point to the next Point in reverse order, reset and load the point
            // in ReferencePoint array and set the slope.  All point alone with exists line will
            // be skipped based on the bypass status.  If less then minimum 4 points to from a line
            // segment, move to next pivot point.
            ReferencePoint[] temp;
            do {
                --i;
                pivot = org[i];
                idx = 0; 
                int bpi = i*(i-1)/2;
            
                // load all non duplicate reference points with slope
                temp = new ReferencePoint[i];
                for (int k = 0; k < i; k++) {
                    if (!bypass[bpi++]) {
                        org[k].setSlope(pivot);
                        temp[idx++] = org[k];
                    }
                }     
                // if less then 3 points in array, move to next pivot point
                if (idx > 2) {
                    break;
                }
            } while (i > 2);

            p = new ReferencePoint[idx];
            System.arraycopy(temp, 0, p, 0, idx);
            mergesort(p);
        } while (i > 2);
        
        allSegments = new LineSegment[numLines];
        collectLines.toArray(allSegments);
    }
    
    // rearranges the array in ascending order, using the natural order.
    private static void mergesort(ReferencePoint[] a) {
        ReferencePoint[] aux = new ReferencePoint[a.length];
        sort(a, aux, 0, a.length-1);
    }

    // mergesort a[lo..hi] using auxiliary array aux[lo..hi]
    private static void sort(ReferencePoint[] a, ReferencePoint[] aux, int lo, int hi) {
        if (hi <= lo) {
            return;
        }
        int mid = lo + (hi - lo) / 2;
        sort(a, aux, lo, mid);
        sort(a, aux, mid + 1, hi);
        merge(a, aux, lo, mid, hi);
    }

    // stably merge a[lo .. mid] with a[mid+1 .. hi] using aux[lo .. hi]
    private static void merge(ReferencePoint[] a, ReferencePoint[] aux, int lo, int mid, int hi) {
         // copy to aux[]
        for (int k = lo; k <= hi; k++) {
            aux[k] = a[k]; 
        }

        // merge back to a[]
        int i = lo, j = mid+1;
        for (int k = lo; k <= hi; k++) {
            if      (i > mid) {
                a[k] = aux[j++];   // this copying is unnecessary
            } else if (j > hi) {
                a[k] = aux[i++];
            } else if (less(aux[j], aux[i])) {
                a[k] = aux[j++];
            } else {
                a[k] = aux[i++];
            }
        }
    }
    
    // is slope of referencePoint v < slope of referencePoint w ?
    private static boolean less(ReferencePoint v, ReferencePoint w) {
        if (v.getSlope() < w.getSlope()) {
            return true;
        }
        if (v.getSlope() == w.getSlope() && v.getPos() < w.getPos()) {
            return true;
        }
        return false;
    }
    
    /**
     * Read the points from an input file in the following format: An integer N, 
     * followed by N pairs of integers (x, y), each between 0 and 32,767.
     * 
     * @param args main function standard arguments
     */
    public static void main(String[] args) {
        // read the N points from a file
        try {    
            File file = new File(args[0]);
            if (file.exists()) {
                scanner = new Scanner(file, charsetName);
                scanner.useLocale(usLocale);
            }
        } catch (IOException ioe) {
            System.err.println("Invalid file.");
        }
        
        int N = scanner.nextInt();
        
        Point[] points = new Point[N];
        for (int i = 0; i < N; i++) {
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.show(0);
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            System.out.println(segment);
            segment.draw();
        }
    }
}
