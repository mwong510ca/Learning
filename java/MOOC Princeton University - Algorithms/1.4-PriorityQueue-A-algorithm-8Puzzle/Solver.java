package mwong.algs4.solver8puzzle;

import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.Scanner;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation: java Solver.java
 *  Execution: java Solver 
 *  Dependencies (algs4.jar) : MinPQ.java
 *  Dependencies : Board.java
 *
 *  Solver should work correctly for arbitrary N-by-N boards (for any 2 ≤ N < 128),
 *  even if it is too slow to solve some of them in a reasonable amount of time.
 *
 ****************************************************************************/

public class Solver {
    private static Scanner scanner;    
    private int steps;
    private Deque<Board> solutionPath;
    private int cutoffLimit = 2500000;  // cutoff limit only good for 15 puzzle

    /**
     * Initializes Solver with the N-by-N Board and find a solution using the A* algorithm.
     * 
     * @param initial the Board object
     */
    public Solver(Board initial) {
        // initialize MinPQ object for Best First Search
        MinPQ<Node> pq1 = new MinPQ<Node>();
        solutionPath = new Deque<Board>();
        steps = -1;
        
        // check the initial board is goal or unsolvable.
        if (initial.isGoal()) {
            steps = 0;
            solutionPath.addLast(initial);
            return;
        }
        if (initial.twin().isGoal()) {
            steps = -1;
            return;
        }
        
        // create the neighbors from the initial board, push in priority queue
        Node node0 = new Node(initial, 0, null);
        for (Board move: initial.neighbors()) {
            if (move.isGoal()) {
                steps = 1;
                solutionPath.addLast(move);
                solutionPath.addLast(initial);
                return;
            }
            
            pq1.insert(new Node(move, 1, node0));
        }
        
        //  loop until the puzzle is solved or priority queue is empty
        while (!pq1.isEmpty()) {
            if (pq1.size() > cutoffLimit) {
                //System.out.println(cutoffLimit);
                //cutoffLimit += 100000;
                steps = -2;
                solutionPath = null;
                return;
            }
            
            // pull the next best move Board from the priority queue
            // skip it if move back to the same position
            Node curr = pq1.delMin();
            Node b = curr;
            boolean bypass = false;
            while (b.prev != null) {
                b = b.prev;
                if (b.board.equals(curr.board)) {
                    bypass = true;
                    break;
                }
            }
            if (bypass) {
                continue;
            }
            
            // create next moves except move it back to the previous state
            for (Board move: curr.board.neighbors()) {
                // if move is goal board, return store the solution and exit the loop
                if (move.isGoal()) {
                    steps = curr.steps + 1;
                    solutionPath.addLast(move);
                    Node temp = curr;
                    solutionPath.addLast(temp.board);
                    while (temp.prev != null) {
                        temp = temp.prev;
                        solutionPath.addLast(temp.board);
                    }
                    return;
                }
                
                // if move is not the previous state, push in priority queue
                // only if hamming distance is 2 and manhattan is 2, check if twin board
                // is the goal board to determine unsolvable and exit the loop
                if (!curr.prev.board.equals(move)) {
                    if (move.hamming() == 2 && move.manhattan() == 2) {
                        if (move.twin().isGoal()) {
                            steps = -1;
                            solutionPath = null;
                            return;
                        }
                    }
                    pq1.insert(new Node(move, curr.steps+1, curr));
                }
            }
        }
    }
    
    /**
     * Returns the boolean represents the initial board is solvable.
     * 
     * @return boolean represents the initial board is solvable
     */
    public boolean isSolvable() {
        return (steps >= 0);
    }
    
    /**
     * Returns the minimum number of moves to solve initial board; -1 if no solution.
     * 
     * @return minimum number of moves to solve initial board; -1 if no solution
     */
    public int moves() { 
        return steps; 
    }
    
    /**
     * Returns an independent iterator over solution of Boards.
     * 
     * @return independent iterator over solution of Boards
     */
    public Iterable<Board> solution() {
        if (isSolvable()) {
            return solutionPath;
        } else {
            return null;
        }
    }
    
    // data type Node class implements Comparable
    // use Manhattan priority as heuristic value
    private class Node implements Comparable<Node> {
        private final Board board;
        private final int steps, h;
        private final Node prev;
        
        private Node(Board board, int steps, Node prev) {
            this.board = board;
            this.steps = steps;
            h = steps + board.manhattan();
            this.prev = prev;
        }
        
        @Override public int compareTo(Node that) {
            if (this.h < that.h) {
                return -1;
            }
            if (this.h > that.h) {
                return 1;
            }
            if (this.steps > that.steps) {
                return -1;
            }
            if (this.steps < that.steps) {
                return 1;
            }
            return 0;
        }
    }
    
    private void solutionSummary() { 
        if (isSolvable()) {
            System.out.println("Minimum number of moves = " + steps);
        } else if (steps == -2) {
            System.out.println("Search suspend, priority queue too large.");
        } else {
            System.out.println("No solution possible");
        }
    }      

    private void solutionDetail() { 
        if (isSolvable()) {
            for (Board move : solutionPath) {
                System.out.println(move);
            }
        }
    }
    
    /**
     *  test client to read a puzzle from a file (specified as a command-line argument)
     *  and print the solution to standard output.
     *  
     *  @param args main function standard arguments
     */
    public static void main(String[] args) { // solve a slider puzzle (given below)  
        System.out.println("solver 8puzzle - algs4 Princetion University");
        int value;
        byte N;
        scanner = new Scanner(System.in, "UTF-8");
        do {
            do {
                System.out.println("Enter the size N (2 - 127) of N x N puzzle, or 0 to exit:");
                while (!scanner.hasNextInt()) {
                    scanner.next();
                }
                value = scanner.nextInt();     
            } while ((value < 2 || value > 127) && value != 0);
            
            if (value == 0) {
                System.out.println("Goodbye!");
                return;
            }
            N = (byte) value;    
            
            int szPuzzle = N * N;
            System.out.println("Enter " + szPuzzle + " number from 0 to " + (szPuzzle - 1) + " :"); 
            int[][] blocks = new int[N][N];            
            boolean[] used = new boolean[szPuzzle];
            int count = 0;
            int row = 0; 
            int col = 0;
            while (count < szPuzzle) {
                while (!scanner.hasNextInt()) {
                    scanner.next();
                }
                value = scanner.nextInt();
                if (value < 0 || value >= szPuzzle) {
                    System.out.println("Invalid number, try again.");
                } else if (used[value]) {
                    System.out.println(value + " already entered, try again.");
                } else {
                    blocks[row][col++] = value;
                    used[value] = true;
                    if (col == N) {
                        row++;
                        col = 0;
                    }
                    count++;
                }
            }
            
            Board initial = new Board(blocks);
            Stopwatch stopwatch = new Stopwatch();
            Solver solver = new Solver(initial);
            System.out.println("Total Time " + stopwatch.elapsedTime());
            solver.solutionSummary();
            if (solver.moves() > 0) {
                solver.solutionDetail();
            } else {
                System.out.println();
            }       
        } while (true);
    }
}





