Coursera  - Algorithms Part 2 Princeton University by Kevin Wayne, Robert Sedgewick  
Programming assignment : 2.1  SeamCarving - shortest path  

Seam-carving is a content-aware image resizing technique where the image is reduced in size by one pixel of height (or width) at a time. A vertical seam in an image is a path of pixels connected from the top to the bottom with one pixel in each row. (A horizontal seam is a path of pixels connected from the left to the right with one pixel in each column.)  Unlike standard content-agnostic resizing techniques, the most interesting features of the image are preserved.  

---

SeamCarver.java - a mutable data type
Read each Color pixel (x, y) and store the RGB value as integer.  Pre-calculate and store the energy value in double value.   Build the EdgeWeightedDigraph and use AcyclicSP to find the shortest path.  Transpose the RGB table and energy table when flipping between horizonal seam and vertical seam, reuse vertical seam functions only.  When picture() called, create a new Picture object and resume the current picture from pixel storage.  Update the RGB value and energy along the removed seam only, do not resize the storages.  Resize the pixel and energy only when transpose() function is called.  
Enhancement - find the shortest path
* distTo full table
    Read the energy table, from left to right, top to bottom, and build the full distTo table.  Find the minimum value of the bottom row.  Back trace and find the shortest path.  Store the energy value in integer with 6 decimal space with multipler, and calculate distTo in long value.  Improve 90% from EdgeWeightedDigraph version.
* 2 rows of distTo instead of full table
    Keep the parent row and current row of distTo only.  A full byte table to keep track the path changes, -1 for left shift, 0 for no change, and 1 for right shift.  Sames as above, use integer energy value and long distTo value.  Improve 50% from distTo full table version.

<pre>
    Memory Comparison:  W, H = 200  reference (bytes) = 158408
                                    4.00 N^2 + -16.06 N + 1602.00   (R^2 = 1.000)
    ===============================================================================
    2 dimensional arrays: 
    saved energy in integer value         331224     8.00 N^2 + 56.79 N + -35.73
                    double value          488040    12.00 N^2 + 40.40 N + 10.14
    1 dimensional array:
    saved energy in integer value         316968     8.00 N^2 + -16.00 N + 168.00
                    double value          474576    12.00 N^2 + -28.00 N + 176.00

    Timing Comparison:  Finding and removing 50 seams for a 500-by-500 image
    	                     h-seams/v-seams:      50/0      0/50      25/25
    ===============================================================================
    EdgeWeightedDigraph and AcyclicSP:
    energy stored in double value,  2d arrays:     1.55      1.53      1.53

    distTo full table 
    energy stored in double value,  1d array:      0.18      0.14      0.14
                                    2d arrays:     0.17      0.12      0.13
    energy stored in integer value, 1d array:      0.12      0.09      0.09
                                    2d arrays:     0.11      0.07      0.08
    
    2 rows of distTo instead of full table
    energy stored in integer value, 2d arrays:     0.04      0.04      0.04                
</pre>		

Given resources for testing:
* PrintEnergy.java
* PrintSeams.java
* SCUtility.java
* ShowEnergy.java
* ShowSeams.java
* ResizeDemo.java
