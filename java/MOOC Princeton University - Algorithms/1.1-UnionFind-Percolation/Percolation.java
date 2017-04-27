package mwong.algs4.percolation;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation:  javac Percolation.java
 *  Dependencies (algs4.jar): WeightedQuickUnionUF.java
 *
 *     Model a percolation system using an N-by-N grid of sites. Each site is either open 
 *  or blocked.  A full site is an open site that can be connected to an open site in 
 *  the top row via a chain of neighboring (left, right, up, down) open sites. We say 
 *  the system percolates if there is a full site in the bottom row. In other words, 
 *  a system percolates if we fill all open sites connected to the top row and that 
 *  process fills some open site on the bottom row.
 *
 ****************************************************************************/

public class Percolation {
    private byte statusClose = 0;       // 0000
    private byte statusOpen = 1;        // 0001
    private byte statusBottom = 2;      // 0010
    private byte statusTop = 4;         // 0100
    private byte statusPercolate = 7;   // 0111
    
    private int size;
    private byte[] sites;
    private boolean isPercolated;
    private WeightedQuickUnionUF uf;
    
    /**
     * Initializes an N-by-N grid, with all sites blocked.
     * 
     * @param N the number of N-by-N grids
     * @throws java.lang.IllegalArgumentException if N <= 0
     */
    public Percolation(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException();
        }
        size = N;
        sites = new byte[size*size+1];
        uf = new WeightedQuickUnionUF(size*size+1);
        isPercolated = false;
    }
    
    /**
     * Returns the index of the sites 1D array.
     * 
     * @param row the integer representing row ID of grid
     * @param row the integer representing column ID of grid
     * @return the index of the sites 1D array.
     */
    private int yxTo1D(int row, int col) {
        return (row - 1)*size + col;
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
        
        int siteID = yxTo1D(i, j);
        int key, key2;
        byte value;
        key = siteID;
        
        // if site is blocked, change open and update status
        // Notes: Always connect with the top key instead of neighbor of the grid
        if (sites[siteID] == statusClose) {
            sites[siteID] = statusOpen;
            // join upper site and update join status
            // if new site connect to top, update status
            if (i != 1 && sites[siteID-size] > 0) {
                key = uf.find(siteID-size);
                uf.union(key, siteID);
            } else if (i == 1) {
                sites[siteID] |= statusTop;
            }
            // join left site and update join status
            if (j != 1 && sites[siteID-1] > 0) {
                key2 = uf.find(siteID-1);
                uf.union(key2, siteID);
                value = (byte) (sites[key] | sites[key2]);
                key = uf.find(key2);
                sites[key] = value;
            }
            // join right site and update join status
            if (j != size  && sites[siteID+1] > 0) {
                key2 = uf.find(siteID+1);
                uf.union(key2, siteID);
                value = (byte) (sites[key] | sites[key2]);
                key = uf.find(key2);
                sites[key] = value;
            }
            // join bottom site and update join status
            // if new site connect to bottom, update status
            if (i != size && sites[siteID+size] > 0) {
                key2 = uf.find(siteID+size);
                uf.union(key2, siteID);
                value = (byte) (sites[key] | sites[key2]);
                key = uf.find(key2);
                sites[key] = value;
            } else if (i == size) {
                sites[siteID] |= statusBottom;
                sites[key] = (byte) (sites[key] | sites[siteID]);
            }
            
            // update system percolate status, if new status equal 7, precolated
            if (!isPercolated && sites[key] == statusPercolate) {
                isPercolated = true;
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
        return sites[yxTo1D(i, j)] > 0;
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
        if (isOpen(i, j)) {
            return (sites[uf.find(yxTo1D(i, j))] >> 2) > 0;
        } else {
            return false;
        }
    }
    
    /**
     * Return the system percolate status, connect from top row to bottom row.
     * 
     * @return true if percolated, otherwise return false.
     */
    public boolean percolates() {
        return isPercolated;
    }
}
