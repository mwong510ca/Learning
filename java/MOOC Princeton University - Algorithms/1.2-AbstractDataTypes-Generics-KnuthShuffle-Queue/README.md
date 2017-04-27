Coursera  - Algorithms Part 1 Princeton University by Kevin Wayne, Robert Sedgewick
Programming assignment : Randomized Queues and Deques - abstract data types, generics, shuffle.

http://www.cs.princeton.edu/courses/archive/fall05/cos226/assignments/queues.html  

---

Deque.java:  A double-ended queue is a generalization of a stack and a queue that supports adding and removing items from either the front or the back of the data structure.  It can be implemented by double linked list or array.  
* Method 1: Double linked list  
    2 node pointers: "head" pointed to the first node, and "tail" node pointed to the last node.  Each node in the deque pointed to the next node and last node.  
* Method 2: Array  
    To reuse the storage, make is as circular array.  Double the array size when full and reduce half when over 75% empty.   
    
RandomizedQueue.java:  A randomized queue is similar to a stack or queue, except that the item removed is chosen uniformly at random from items in the data structure.  
* Use array, add an item in insertion order.  Return an item in random order.  Move the last item to the empty storage after removal.  For iterator, make a copy of the array and return each next() call in random order.  

Subset.java:  Write a client program Subset.java that takes a command-line integer k; reads in a sequence of N strings from standard input using StdIn.readString(); and prints out exactly k of them, uniformly at random. Each item from the sequence can be printed out at most once.  
* Method 1:  Insert all strings in RandomizedQueue then return k string in random order.  
* Method 2:  Apply Knuth shuffle, keep RandomizedQueue size k. -- Achieve the bonus point.  
    Insert the first k strings in RandomizedQueue.  Pick a random number x from 1 to the nth string, if x fall on the last kth location, replace the nth string in RandomizedQueue.  When done, return all strings in RandomizedQueue.  

<pre>
Computing memory of Subset
*-----------------------------------------------------------
Test 3 (bonus): Check that maximum size of any or Deque or RandomizedQueue object
created is <= k
* filename = tale.txt, N = 138653, k = 5
* filename = tale.txt, N = 138653, k = 50
* filename = tale.txt, N = 138653, k = 500
* filename = tale.txt, N = 138653, k = 5000
* filename = tale.txt, N = 138653, k = 50000
Total: 3/2 tests passed!

Timing Deque
*-----------------------------------------------------------
Test 1a-1g:  N random calls to addFirst(), addLast(), removeFirst(), and removeLast().
                    N  seconds
------------------------------
=> passed     2048000     0.22

Test 2a-2g:  Create deque of N objects, then iterate over the N objects by calling next() 
             and hasNext().
                    N  seconds
------------------------------
=> passed     2048000     0.06

Test 3a-3g:  Create deque of N objects, then interleave N calls each to removeFirst()/removeLast()
             and addFirst()/addLast().
                    N  seconds
----------------------------------
=> passed       32769     0.01

Timing RandomizedQueue
*-----------------------------------------------------------
Test 1a-1g:  N random calls to enqueue(), sample(), dequeue(), isEmpty(), and size().
                    N  seconds
----------------------------------
=> passed     2048000     0.31

Test 2a-2g:  Create randomized queue of N objects, then iterate over the N objects by calling 
             next() and hasNext().
                    N  seconds
----------------------------------
=> passed     2048000     0.24

Test 3a-3g:  Create randomized queue of N objects, then interleave N calls each to dequeue() 
             and enqueue().
                    N  seconds
----------------------------------
=> passed       32769     0.00
</pre>
