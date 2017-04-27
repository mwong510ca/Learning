package mwong.algs4.percolation;

import java.util.Random;
import java.util.Scanner;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation:  javac PercolationStats.java
 *  Execution:    java PercolationStats
 *  Dependencies: Percolation.java
 *
 *  This program prompt to enter two integers:
 *    - Reads the grid size N of the percolation system.
 *      Creates an N-by-N grid of sites (initially all blocked)
 *    - Reads the T times of repeating this computation experiment 
 *
 *  By repeating this computation experiment T times and averaging the results, we 
 *  obtain a more accurate estimate of the percolation threshold. Let x-t be the 
 *  fraction of open sites in computational experiment t. The sample mean μ provides 
 *  an estimate of the percolation threshold; the sample standard deviation σ measures 
 *  the sharpness of the threshold.
 *
 ****************************************************************************/

public class PercolationStats {
    private static Scanner scanner;
    private double mean, stddev, confintv;
    
    /**
     * Initializes PercolationStats structure, compute and store the mean,
     * the 95% confidence interval, and standard deviation of the number of call
     * of Percolation on N-by-N grid.
     * @param N the integer representing the size of N-by-N grid
     * @param T the integer perform T independent computational experiments
     */
    public PercolationStats(int N, int T) {
        Percolation sites;
        double[] counts;
        
        if ((N <= 0) || (T <= 0)) {
            throw new IllegalArgumentException();
        }
        counts = new double[T];
        
        Random generator = new Random();
        int count;
        
        for (int i = T - 1; i > -1; i--) {
            count = 0;
            sites = new Percolation(N);
            
            while (!sites.percolates()) {
                int j = generator.nextInt(N)+1;
                int k = generator.nextInt(N)+1;
                if (!sites.isOpen(j, k)) {
                    sites.open(j, k);
                    count++;
                }
            }
            counts[i] = count;
        }
        
        double sum = 0;
        int size = N * N;
        for (int i = T - 1; i > -1; i--) {
            sum += counts[i]/size;
        }
        mean = sum/T;
        
        sum = 0;
        for (int i = T - 1; i > -1; i--) {
            sum += (counts[i]/(size) - mean)*(counts[i]/(size) - mean);
        }
        stddev = Math.sqrt(sum/(T - 1));
        confintv = 1.96*stddev/(Math.sqrt(T));
    }
    
    /**
     * Returns the mean of percolation threshold.
     * 
     * @return the mean of percolation threshold
     */
    public double mean() {
        return mean;
    }
    
    /**
     * Returns the standard deviation of percolation threshold.
     * 
     * @return the standard deviation of percolation threshold
     */
    public double stddev() {
        return stddev;
    }
    
    /**
     * Returns the 95% confidence interval of percolation threshold.
     * 
     * @return the 95% confidence interval of percolation threshold
     */
    private double confintv() {
        return confintv;
    }
    
    /**
     * Returns the lower bound of the 95% confidence interval of percolation threshold.
     * 
     * @return the lower bound of the 95% confidence interval of percolation threshold
     */
    public double confidenceLo() {
        return (mean() - confintv());
    }
    
    /**
     * Returns the upper bound of the 95% confidence interval of percolation threshold.
     * 
     * @return the upper bound of the 95% confidence interval of percolation threshold
     */
    public double confidenceHi() {
        return (mean() + confintv());
    }
    
    /**
     * test client prompt to enter 2 numbers N and T, performs T independent
     * computational experiments (discussed above) on an N-by-N grid, and prints out 
     * the mean, standard deviation, and the 95% confidence interval for the percolation 
     * threshold. Use standard random from our standard libraries to generate
     * random numbers; use standard statistics to compute the sample mean and standard
     * deviation.
     * 
     * @param args main function standard arguments
     */
    public static void main(String[] args) {
        scanner = new Scanner(System.in, "UTF-8");
        System.out.println("Enter an integer for the size of N x N grids:");
        int N = scanner.nextInt();
        System.out.println("Enter an integer for the number of times:");
        int T = scanner.nextInt();
        PercolationStats p = new PercolationStats(N, T);
        System.out.println("mean                    = " + p.mean());
        System.out.println("stddev                  = " + p.stddev());
        System.out.println("95% confidence interval = "
                           + p.confidenceLo() + ", " + p.confidenceHi());
    }
}

