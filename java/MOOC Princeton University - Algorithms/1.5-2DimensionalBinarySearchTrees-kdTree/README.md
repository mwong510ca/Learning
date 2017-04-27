Coursera  - Algorithms Part 1 Princeton University by Kevin Wayne, Robert Sedgewick  
Programming assignment : kd Tree - Multidimensional binary search trees (BST)
    
---

Write a data type to represent a set of points in the unit square (all points have x- and y-coordinates between 0 and 1) using a 2d-tree to support efficient range search (find all of the points contained in a query rectangle) and nearest neighbor search (find a closest point to a query point). 2d-trees have numerous applications, ranging from classifying astronomical objects to computer animation to speeding up neural networks to mining data to image retrieval.

PointSET.java -  
    Write a mutable data type PointSET.java that represents a set of points in the unit square. Implement the following API by using a red-black BST (using either SET from algs4.jar or java.util.TreeSet).

kdtree.java -  
    Write a mutable data type KdTree.java that uses a 2d-tree to implement the same API (but replace PointSET with KdTree). A 2d-tree is a generalization of a BST to two-dimensional keys. The idea is to build a BST with points in the nodes, using the x- and y-coordinates of the points as keys in strictly alternating sequence.

Given resources for testing:
* KdTreeGenerator.java - creates N random points in the unit square and print to standard output, you may save these for future testing.
* KdTreeVisualizer.java - computes and draws the 2d-tree that results from the sequence of points clicked by the user in the standard drawing window.
* RangeSearchVisualizer.java - reads a sequence of points from a file (specified as a command-line argument) and inserts those points into a 2d-tree. Then, it performs range searches on the axis-aligned rectangles dragged by the user in the standard drawing window.
* NearestNeighborVisualizer.java - reads a sequence of points from a file (specified as a command-line argument) and inserts those points into a 2d-tree. Then, it performs nearest neighbor queries on the point corresponding to the location of the mouse in the standard drawing window.
