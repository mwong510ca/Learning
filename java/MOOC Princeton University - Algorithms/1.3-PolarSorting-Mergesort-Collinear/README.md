Coursera  - Algorithms Part 1 Princeton University by Kevin Wayne, Robert Sedgewick  
Programming assignment : Collinear Points - polar sorting  

http://introcs.cs.princeton.edu/java/assignments/collinear.html  

---

Given a set of Euclidean points, determine any groups of 4 or more that are collinear.  

Point.java:  Represents a point in the plane, implements Comparable.  
* compareTo() compare the position of 2 points.  
* slopeTo() return the slope of 2 points.  SLOPE_ORDER compare the slope of 2 points.  

ReferencePoint.java:  ReferencePoint class represents a point with properties.  
* p the point object  
* pos the original order/position on the plane  
* slope the slope with a specific reference point.  

BruteCollinearPoints.java:  Given a set of Euclidean points, determine any groups of 4 that are collinear.  
    Use Arrays.sort() to sort all points in position order.  Loop over all points, compare the slopeTo() and find all group of 4 are collinear.

FastCollinearPoints.java:  Given a set of Euclidean points, determine any groups of 4+ or more that are collinear.  
* Method 1:  sort the point in slopeOrder  
    Sort all points in position order.  Loop over all points as pivot point, sort all other points in slopeOrder.  Check for all line, if pivot point is the first point of the line, it is collinear.  Otherwise, duplicate line was found previously.  
* Method 2:  Calculate the slope of each pair of points only once, cache these slope for sort by mergesort.  Use a bypass table to marked all the point for each line.  Eliminated these points for ongoing searching.  
    First, sort all point in position order, store these point in ReferencePoint with position order.  For each pivot point x from N - 1 to 3, calculated the reference from 0 to x - 1.  Use mergesort to sort these point by the reference slope.   If 3+ points has the same slope, it is collinear.  Also marked these points in bypass table, these points will be eliminated in ongoing search.  

<pre>
Timing FastCollinearPoints
*-----------------------------------------------------------
Test 1a-1g: Find collinear points among N random distinct points
slopeTo()
                  N    time     slopeTo()   compare()  + 2*compare()        compareTo()
-----------------------------------------------------------------------------------------------
slopeOrder  -  2048   0.82     4190170    17852193       39894556                42113       
cache slope -  2048   0.28     2096124           0        2096124                41995        

Test 2a-2g: Find collinear points among the N points on an N-by-1 grid
slopeTo()
                  N    time     slopeTo()   compare()  + 2*compare()        compareTo()
-----------------------------------------------------------------------------------------------
slopeOrder  -  4096   0.19     8390649     8382464       25155577                92179        
cache slope -  4096   0.10        4095           0           4095                92183         

Test 3a-3g: Find collinear points among the N points on an N/4-by-4 grid
slopeTo()
                  N    time     slopeTo()   compare()  + 2*compare()        compareTo()
-----------------------------------------------------------------------------------------------
slopeOrder  -  4096   0.48    13287069    14227017       41741103                92141      
cache slope -  4096   0.43     5246970           0        5246970                92219          

Test 4a-4g: Find collinear points among the N points on an N/8-by-8 grid
slopeTo()
                  N    time     slopeTo()   compare()  + 2*compare()        compareTo()
-----------------------------------------------------------------------------------------------
slopeOrder  -  4096   0.85    16836407    28747659       74331725                92315 
cache slope -  4096   0.48     5489126           0        5489126                92255   

--------------------------------------------------------------------------------
</pre>
Given resources:  
* LineSegment.java
