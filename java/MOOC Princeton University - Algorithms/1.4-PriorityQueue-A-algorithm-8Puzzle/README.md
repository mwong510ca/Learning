Coursera  - Algorithms Part 1 Princeton University by Kevin Wayne, Robert Sedgewick
Programming assignment : 8 Puzzle - priority queue, A* algorithm

http://introcs.cs.princeton.edu/java/assignments/8puzzle.html

---

It is played on any N-by-N slide puzzle for N < 128.  For a 3-by-3 grid with 8 square blocks labeled 1 through 8 and a blank square. Use fewest moves to rearrange the blocks back in order.  You are permitted to slide blocks horizontally or vertically into the blank square. 

Board.java: The data type for 8Puzzle, for N < 128. 
It cache hamming priority, manhattan priority, generate twin board and neighbor boards.

Solver.java - Solve the N-by-N slider puzzle with minimum moves, for N < 128.
Use priority queue and A* algorithm to solve the N-by-by slide puzzle.  
To determine the puzzle is unsolvable, it is not suppose to use th equation by the assignment.  
* Instead of insert a pair of initial board and it's twin board in the priority queue, until it reach the goal state.  Trace it back to the first board wheather it is the initial board or the twin board. 
* I take the property of the twin board of the goal state, both the hamming priority and manhattan priority must be 2.  I only insert initial board in the priority queue.  Until it reach both the hamming priority and manhattan priority are 2, I check if it's twin board is the goal state to determine unsolvable.
    * Notes: I modified my twin() will return the goal state if it's the twin board of the goal state.
    <pre>
    8 puzzle:           15 puzzle                       24 puzzle                       
    1 2 3      1 2 3     1  2  3  4       1  2  3  4     1  2  3  4  5       1  2  3  4  5
    4 6 5  OR  4 5 6     5  6  7  8  OR   5  6  7  8     6  7  8  9 10  OR   6  7  8  9 10
    7 8 0      8 7 0     9 10 12 11       9 10 11 12    11 12 13 14 15      11 12 13 14 15
                            13 14 15  0      13 15 14  0    16 17 18 20 19      16 17 18 19 20
                                                            21 22 23 24  0      21 22 24 23  0

    </pre> 
    
Notes:  
* A* algorithm take a lot of memory, it may solve a 15 puzzle up to 50 moves.  
For a better solution, view my project [Heuristic Search - Additive Pattern Database - 15Puzzle].  
It has Manhattan Distance with Linear Conflict, Walking Distance, statically partitioned additive pattern database 5-5-5, 6-6-6 and 7-8, with additional solver enhancments.
[Heuristic Search - Additive Pattern Database - 15Puzzle]: https://github.com/mwong510ca/java/tree/master/Heuristic%20Search%20-%20Additive%20Pattern%20Database%20-%2015Puzzle
