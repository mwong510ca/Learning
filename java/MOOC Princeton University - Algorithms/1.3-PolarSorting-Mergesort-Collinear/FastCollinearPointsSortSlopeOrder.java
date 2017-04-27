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
 *  Compilation:  javac FastCollinearPointsSortSlopeOrder.java
 *  Execution:    java FastCollinearPointsSortSlopeOrder input80.txt
 *  Dependencies  (algs4.jar): StdDraw.java
 *  Dependencies: Point.java, LineSegment.java(resource)
 *
 *  This program enter a file name at the command line.
 *    - Reads the an input file in the following format: An integer N, followed
 *    by N pairs of integers (x, y), each between 0 and 32,767.
 *
 *  Draw all points and lines with 4 points or more and print all line segments.
 *
 ****************************************************************************/

public class FastCollinearPointsSortSlopeOrder {
    private static Scanner scanner;
    private static String charsetName = "ISO-8859-1";
    private static Locale usLocale = new Locale("en", "US");
    private int numLines, N;
    private LineSegment[] allSegments;

    /**
     * Initializes FastCollinearPoints class, take a list of Points and
     * find all 4+ points line segment.
     * 
     * @param points the array of Points.
     */
    public FastCollinearPointsSortSlopeOrder(Point[] points) {
        validatePoints(points);
        N = points.length;
        numLines = 0;
        allSegments = new LineSegment[0];
        findAllLines(points);
    }
    
    private void findAllLines(Point[] points) {
        numLines = 0;
        allSegments = new LineSegment[0];
        ArrayList<LineSegment> collectLines = new ArrayList<LineSegment>();
        Point[] org = points.clone();
        Arrays.sort(org);
        
        Point[] p, linesHead;
        linesHead = new Point[0];
        
        for (int i = 0; i < N - 3; i++) {
            int count, p2i, lnP1, lnP2;
            double slope, p2slope;
            boolean newLine;
            
            p2i = 0;
            p2slope = 0.0;
            boolean duplicateLine = false;
            
            if (linesHead.length > 0) {
                Arrays.sort(linesHead, org[i].slopeOrder());
                p2slope = org[i].slopeTo(linesHead[0]);
                p2i++; 
            }     
            
            p = new Point[N - i - 1];
            System.arraycopy(org, i + 1, p, 0, org.length - i -1);
            Arrays.sort(p, org[i].slopeOrder());

            int j = 0;
            while (j < N - i - 1) { 
                
                newLine = true;
                slope = org[i].slopeTo(p[j]);                    
                count = 1;
                lnP1 = j;
                lnP2 = j;
                 
                if (linesHead.length > 0) {
                    while (p2i < linesHead.length && p2slope < slope) {
                        p2slope = org[i].slopeTo(linesHead[p2i++]);
                    }                            
                    if (p2slope == slope) {
                        newLine = false;
                    }
                }   
                 
                while (j < p.length - 1 && org[i].slopeTo(p[j+1]) == slope) {
                    lnP2 = ++j;
                }
                
                count = lnP2 - lnP1 + 1;
                if (count >= 3 && newLine) {
                    numLines++;
                    collectLines.add(new LineSegment(org[i], p[j]));
                    if (count > 3) {
                        duplicateLine = true;
                    }
                }  
                j++;
            }
            
            if (duplicateLine) {
                Point[] temp = new Point[linesHead.length + 1];
                System.arraycopy(linesHead, 0, temp, 0, linesHead.length);
                linesHead = temp;
                linesHead[linesHead.length - 1] = org[i];
            }           
        }
        
        allSegments = new LineSegment[numLines];
        collectLines.toArray(allSegments);
    }

    private void validatePoints(Point[] points) {
        Point[] p = points.clone();
        Arrays.sort(p);
        for (int i = 0; i < points.length - 1; i++) {
            if (p[i].compareTo(p[i + 1]) == 0) {
                throw new IllegalArgumentException();
            }
        }
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
