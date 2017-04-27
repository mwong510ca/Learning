package mwong.algs4.percolation;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation:  javac Percolation2objects.java
 *  Dependencies (algs4.jar): WeightedQuickUnionUF.java
 *
 *  Model a percolation system using an N-by-N grid of sites. Each site is either open
 *  or blocked.  A full site is an open site that can be connected to an open site in
 *  the top row via a chain of neighboring (left, right, up, down) open sites. We say
 *  the system percolates if there is a full site in the bottom row. In other words,
 *  a system percolates if we fill all open sites connected to the top row and that
 *  process fills some open site on the bottom row.
 *
 ****************************************************************************/

public class Percolation2objects {
    
    private int size, topSite, bottomSite;
    private boolean percolated;
    private boolean[] id;
    private WeightedQuickUnionUF ufTop, ufBottom;
    
    /**
     * Initializes an N-by-N grid, with all sites blocked.
     * 
     * @param N the number of N-by-N grids
     * @throws java.lang.IllegalArgumentException if N <= 0
     */
    public PercolationBasic(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException();
        }
        size = N;
        topSite = 0;
        bottomSite = size*size+1;
        id = new boolean[size*size+1];
        ufTop = new WeightedQuickUnionUF(size*size+1);
        ufBottom = new WeightedQuickUnionUF(size*size+2);
        percolated = false;
        
        id[0] = false;
        for (int i = 1; i <= size; i++) {
            for (int j = 1; j <= size; j++) {
                id[xyToID(i, j)] = false;
            }
        }
    }
    
    /**
     * Returns the index of the sites 1D array.
     * 
     * @param row the integer representing row ID of grid
     * @param row the integer representing column ID of grid
     * @return the index of the sites 1D array
     */
    private int xyToID(int i, int j) {
        return (i-1)*size+j;
    }
    
    /**
     * If grid is blocked, change to open status and update the status
     * with neighbor sites on 4 sides.
     * 
     * @param i the integer representing row ID of grid
     * @param j the integer representing column ID of grid
     */
    public void open(int i, int j) {
        if (i <= 0 || i > size || j <= 0 || j > size) {
            throw new IndexOutOfBoundsException();
        }
        
        int siteID = xyToID(i, j);
        if (!id[siteID]) {
            id[siteID] = true;
            // join left grid
            if (j != 1 && id[siteID-1]) {
                ufTop.union(siteID, siteID-1);
                ufBottom.union(siteID, siteID-1);
            }
            // join right grid
            if (j != size  && id[siteID + 1]) {
                ufTop.union(siteID, siteID + 1);
                ufBottom.union(siteID, siteID + 1);
            }
            // join top grid
            if (i != 1 && id[siteID-size]) {
                ufTop.union(siteID, siteID-size);
                ufBottom.union(siteID, siteID - size);
            } else if (i == 1) {
                ufTop.union(topSite, siteID);
            }
            // join bottom grid
            if (i != size && id[siteID + size]) {
                ufTop.union(siteID, siteID + size);
                ufBottom.union(siteID, siteID + size);
            } else if (i == size) {
                ufBottom.union(siteID, bottomSite);
            }
            
            // update system percolate status, if new status equal 7, percolated
            if (!percolated && ufTop.connected(topSite, siteID) 
                    && ufBottom.connected(bottomSite, siteID)) {
                percolated = true;
            }
        }
    }
    
    /**
     * Return the open status of the grid.
     * 
     * @param i the integer representing row ID of grid
     * @param j the integer representing column ID of grid
     * @return true if open status, false if blocked status.
     */
    public boolean isOpen(int i, int j) {
        if (i <= 0 || i > size || j <= 0 || j > size) {
            throw new IndexOutOfBoundsException();
        }
        return id[xyToID(i, j)];
    }
    
    /**
     * Return the full status (if connect to top) of the grid.
     * 
     * @param i the integer representing row ID of grid
     * @param j the integer representing column ID of grid
     * @return true if full status, otherwise return false.
     */
    public boolean isFull(int i, int j) {
        if (i <= 0 || i > size || j <= 0 || j > size) {
            throw new IndexOutOfBoundsException();
        }
        return ufTop.connected(topSite, xyToID(i, j));
    }
    
    /**
     * Return the system percolate status, connected from top row to bottom row.
     * 
     * @return true if percolated, otherwise return false.
     */
    public boolean percolates() {
        return percolated;
    }
}
