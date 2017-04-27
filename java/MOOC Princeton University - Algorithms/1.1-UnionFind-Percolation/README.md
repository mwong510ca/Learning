Coursera  - Algorithms Part 1 Princeton University by Kevin Wayne, Robert Sedgewick
Programming assignment : Percolation - union-find, Monte Carlo simulation

http://introcs.cs.princeton.edu/java/assignments/percolation.html

Given an N x N board, open one site at a time, until the top row connect to the bottom row.  Also avoid backwash.

---

Percolation.java: Track OPEN, FULL and PERCOLATED status of each site.  There are 2 ways to implement percolation.java.  

* Method 1 - use 2 union-find objects.  
    * First union-find object - check the PERCOLATED status.  Add 2 sites, "top" site connected to all sites on the first row, and "bottom" site connected to all sites on the last row.  When "top" and "bottom" is connected, it is precolated.  It will create backwash issue, some sites on the last row may not connected to "top".  
    * Second union-find object - handle the backwash issue, check the open status only.  Only add "top" site connected to all sites on first row to check the FULL status.  
    Notes: 2 objects almost perform the same process, double the time and memory to handled the backwash issue.

* Method 2 - use 1 union-find object to rechive the goal. -- Achieve the bonus point.    
    * Use 1 union-find object when 2 sites are connected.  Use an byte array to keep track the status of each site and connection.  (0001 for OPEN site, 0010 for connected to top, 0100 for connected to bottom)  Always join with the root, if 0111 exists after the new connection, it is percolated.

<pre>
    Estimated memory: 
    use 2 union-find objects -  17.00 N^2 + 0.00 N + 296.00 
    use 1 union-find objects -  9.00 N^2 + 0.00 N + 176.00  

    Timing for N = 1024:
                               seconds   union()    2 * connected() + find()    constructor
    use 2 union-find objects -  0.38     1456647            3108016                  2         
    use 2 union-find objects -  0.18      727731            1588209                  1  
</pre>
PercolationStats.java: Monte Carlo Simluation
Performs T independent computational experiments on an N-by-N grid, and prints out the mean, 
standard deviation, and the 95% confidence interval for the percolation threshold. 
<pre>
    Sample Output:
    Enter an integer for the size of N x N grids:
    100 
    Enter an integer for the number of times:
    20
    mean                    = 0.593755
    stddev                  = 0.018603097506654783
    95% confidence interval = 0.5856018330390804, 0.6019081669609196
</pre>

Given resource for testing:  
* InteractivePercolationVisualizer.java
* PercolationVisulizer.java

