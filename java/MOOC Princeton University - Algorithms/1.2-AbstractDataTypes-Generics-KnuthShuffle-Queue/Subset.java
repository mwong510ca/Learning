package mwong.algs4.queue;

import edu.princeton.cs.algs4.StdIn;
import java.util.Random;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation:  javac Subset.java
 *  Execution:    echo A B C D E F G H I | java Subset 3 
 *  Dependencies (algs4.jar): StdIn.java
 *  Dependencies: RandomizedQueue.java
 *
 *  Subset client that takes a command-line integer k; reads in a sequence of 
 *  N strings from standard input; and prints out exactly k of them, uniformly 
 *  at random. Each item from the sequence can be printed out at most once. 
 *  Assume that 0 ≤ k ≤ N, where N is the number of string on standard input.
 *
 ****************************************************************************/

public class Subset {
    
    /**
     *  Subset client that takes a command-line integer k; reads in a sequence of
     *  N strings from standard input; and prints out exactly k of them, uniformly
     *  at random. Each item from the sequence can be printed out at most once.
     *  Assume that 0 ≤ k ≤ N, where N is the number of string on standard input.
     *  
     *  @param args main function standard arguments
     */
    public static void main(String[] args) {
        if (args[0] == null) {
            throw new IndexOutOfBoundsException();
        }
        int sizeK = Integer.parseInt(args[0]);
        if (sizeK < 0) {
            throw new IllegalArgumentException();
        }

        RandomizedQueue<String> q = new RandomizedQueue<String>();
        int inputCount = 0;
        Random random = new Random();
        
        while (!StdIn.isEmpty() && inputCount < sizeK) {
            q.enqueue(StdIn.readString());
            inputCount++;
        }
        
        while (!StdIn.isEmpty()) {
            if (random.nextInt(inputCount + 1) > inputCount - sizeK) {
                q.dequeue();
                q.enqueue(StdIn.readString());
            } else {
                StdIn.readString();
            }
            inputCount++;
        }
        
        while (!q.isEmpty()) {
            System.out.println(q.dequeue());
        }
    }
}
