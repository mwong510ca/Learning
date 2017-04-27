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
 *  Compilation:  javac BruteCollinearPoints.java
 *  Execution:    java BruteCollinearPoints input80.txt
 *  Dependencies (algs4.jar): In.java, StdDraw.java
 *  Dependencies: Point.java, LineSegment.java(resource)
 *
 *  This program enter a file name at the command line.
 *    - Reads the an input file in the following format: An integer N, followed
 *    by N pairs of integers (x, y), each between 0 and 32,767.
 *
 *  Draw all points, find all line segments with 4 points, draw and print these lines.
 *
 ****************************************************************************/


public class BruteCollinearPoints {
    private static Scanner scanner;
    private static String charsetName = "ISO-8859-1";
    private static Locale usLocale = new Locale("en", "US");
    private int numLines, N;
    private LineSegment[] allSegments;
    
    /**
     * Initializes BruteCollinearPoints class, take a list of Points and
     * find all 4 points line segment.
     * @param points the array of Points.
     */
    public BruteCollinearPoints(Point[] points) {
        validatePoints(points);
        N = points.length;
        numLines = 0;
        allSegments = new LineSegment[0];
        findAllLines(points);
    }
    
    /**
     * Returns the number of line segments.
     * 
     * @return the number of line segments
     */
    public int numberOfSegments() {
        return numLines;
    }
    
    /**
     * Returns the array of LineSegments.
     * 
     * @return the array of LineSegments
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
    
    // loop over all points and search for 4 points line segment;
    // increment line segment counter and store the line segment.
    private void findAllLines(Point[] points) {
        numLines = 0;
        allSegments = new LineSegment[0];
        ArrayList<LineSegment> collectLines = new ArrayList<LineSegment>();
        Point[] p = points.clone();
        // sort the points in position order
        Arrays.sort(p);
        
        // loop over the points in position order and find the 4 points line segment.
        for (int i = 0; i < N - 3; i++) {
            for (int j = i + 1; j < N - 2; j++) {
                double slope = p[i].slopeTo(p[j]);
                for (int k = j + 1; k < N - 1; k++) {
                    if (p[i].slopeTo(p[k]) != slope) {
                        continue;
                    }
                    for (int l = k + 1; l < N; l++) {
                        if (p[i].slopeTo(p[l]) != slope) {
                            continue;
                        }
                        // if found, update the counter and store the line segment.
                        numLines++;
                        collectLines.add(new LineSegment(p[i], p[l]));
                    }
                }
            }
        }
        allSegments = new LineSegment[numLines];
        collectLines.toArray(allSegments);
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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            System.out.println(segment);
            segment.draw();
        }
    }
}
