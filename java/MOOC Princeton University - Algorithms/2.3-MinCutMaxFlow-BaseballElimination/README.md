Coursera  - Algorithms Part 2 Princeton University by Kevin Wayne, Robert Sedgewick  
Programming assignment : BaseballElimination - reduction, max flow, min cut   

http://www.cs.princeton.edu/courses/archive/spring04/cos226/assignments/baseball.html  

In the baseball elimination problem, there is a division consisting of N teams.  A team is mathematically eliminated if it cannot possibly finish the season in (or tied for) first place.  Assume no games end in a tie and that there are no rainouts.  
Input format - Number of teams follow by the team name, the number of wins, the number of losses, the number of remaining games, and the number of remaining games against each team in the divsion per line.  
<pre> Example:  teams4.txt
    4
    Atlanta       83 71  8  0 1 6 1
    Philadelphia  80 79  3  1 0 0 2
    New_York      78 78  6  6 0 0 0
    Montreal      77 82  3  1 2 0 0
</pre>

---

FordFulkersonModified.java (optional) - modified FordFulkerson.java (algs4.jar) for performance tuning  
    Change the logical oreder, and use 2 array storage instead of queue data structure.

BaseballElimination.java - An immutable data type BaseballElimination that represents a sports division and determines which teams are mathematically eliminated  
* Before apply min cut max flow network, eliminated all team by the score board.  (all version)
* Reduce the flow network size by filter the teams with protential to be eliminated by only.  (Final version only)
* For an eliminated teams, all other teams with the same estimated best score will be eliminated as well.  (Final version only)  

<pre>
    Timing BaseballElimination
     N   constructor isEliminated() + certificateOfElimination()
    60       0.01       0.00    (Final version)
    60       1.87       0.00    (First version with FordFulkersonModified.java)
    60       8.10       0.00    (First version)
    
    Computing memory of BaseballElimination - First version with FordFulkersonModified.java
                                              or Final version
    vertices     = 0.50 N^2 + -6.27 N + 20.62         (R^2 = 1.000)
    edges        = 1.50 N^2 + -21.35 N + 74.75        (R^2 = 1.000)
    memory of G  = 175.56 N^2 + -2466.67 N + 8592.00  (R^2 = 1.000)
    memory       = 2.83 N^2 + 187.38 N + 342.57       (R^2 = 1.000)
    
    Computing memory of BaseballElimination - First version
    vertices     = 0.50 N^2 + -0.55 N + 2.37         (R^2 = 1.000)
    edges        = 1.50 N^2 + -3.64 N + 3.12         (R^2 = 1.000)
    memory of G  = 176.29 N^2 + -400.41 N + 476.00   (R^2 = 1.000)
    memory       = 2.83 N^2 + 187.38 N + 342.57      (R^2 = 1.000)
</pre>
