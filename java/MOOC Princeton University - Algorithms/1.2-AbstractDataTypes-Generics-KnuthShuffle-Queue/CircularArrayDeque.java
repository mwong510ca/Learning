package mwong.algs4.queue;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation:  javac CircularArrayDeque.java
 *  Execution:    java CircularArrayDeque
 *
 *  A generic data type for a dequeue implement elementary data structures 
 *  using re-sizable circular array, generic and iterators.
 *  Dequeue - A double-ended queue or deque (pronounced "deck") is a generalization 
 *  of a stack and a queue that supports adding and removing items from either
 *  the front or the back of the data structure.
 *
 ****************************************************************************/

public class CircularArrayDeque<Item> implements Iterable<Item> {
    private static Scanner scanner;
    private Item[] array;
    private int size;
    private int firstIdx, lastIdx;

    /**
     *  Initializes an initial array of size 8 for dequeue.
     */
    public CircularArrayDeque() {
        array = (Item[]) new Object[8];
        firstIdx = -1;
        lastIdx = -1;
        size = 0;
    }   

    /**
     * Return the empty status of the dequeue.
     * 
     * @return true if dequeue is empty, false otherwise.
     */
    public boolean isEmpty() {
        return size == 0;
    }   
    
    /**
     * Return the number of items in this  dequeue.
     * @return an integer, the number of items in this  dequeue.
     */
    public int size() {
        return size;
    }   
  
    /**
     * resize the circular array when full or over 75% empty, copy the items to the new array
     * 
     * @param an integer, the maximum capacity of the circular array.
     */
    private void resize(int capacity) {
        Item[] temp = (Item[]) new Object[capacity];
        int end = lastIdx;
        int newIdx;
        
        if (lastIdx < firstIdx || firstIdx == array.length - 1) {
            end = array.length - 1;
        }
        
        System.arraycopy(array, firstIdx, temp, 0, end - firstIdx + 1);
        newIdx = end - firstIdx;

        if (lastIdx < firstIdx) {
            System.arraycopy(array, 0, temp, end - firstIdx + 1, lastIdx + 1);
            newIdx += lastIdx + 1;
        }
        
        firstIdx = 0;
        lastIdx = newIdx;
        array = temp;
    }

    /**
     * Adds the item at the front of the dequeue.
     * 
     * @param item the item to add
     */
    public void addFirst(Item item) {
        if (item == null) {
            throw new NullPointerException();
        } 
        // double the circular array size when full
        if (size == array.length) {
            resize(2*array.length);
        }
        int id;
        if (size == 0) {
            id = 0;
            lastIdx = id;
        } else if (firstIdx > 0) {
            id = firstIdx - 1;
        } else if (firstIdx == 0) {
            id = array.length-1;
        } else {
            throw new IndexOutOfBoundsException("addFirst failed");
        }
        array[id] = item;
        firstIdx = id; 
        size++;
    }   

    /**
     * Adds the item at the end of the dequeue.
     * 
     * @param item the item to add
     */
    public void addLast(Item item) {
        if (item == null) {
            throw new NullPointerException();
        } 
        // double the circular array size when full
        if (size == array.length) {
            resize(2*array.length);
        }
        int id;
        if (size == 0) {
            id = 0;
            firstIdx = id;
        } else if (lastIdx == array.length - 1) {
            id = 0;
        } else if (lastIdx >= 0 && lastIdx < array.length - 1) {
            id = lastIdx + 1;
        } else {
            throw new IndexOutOfBoundsException("addLast failed");
        }
        array[id] = item;
        lastIdx = id;
        size++;
    }    
    
    /**
     * Removes and returns the first item on this dequeue.
     * @return the first item on this dequeue.
     * @throws java.util.NoSuchElementException if this queue is empty
     */
    public Item removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        Item item;
        if (size == 0) {
            throw new NoSuchElementException("removeFirst empty");
        }
        item = array[firstIdx];
        array[firstIdx] = null;
        size--;
        if (size == 0) {
            firstIdx = -1;
            lastIdx = -1;
        } else if (firstIdx == array.length-1) {
            firstIdx = 0;
        } else {
            ++firstIdx;
        }

        // shrink the circular array in half when over 75% empty
        if (size > 2 && size == array.length/4) {
            resize(array.length/2);
        }
        return item;
    }         
    
    /**
     * Removes and returns the last item on this deque.
     * @return the last item on this deque.
     * @throws java.util.NoSuchElementException if this queue is empty
     */
    public Item removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        Item item;
        if (size == 0) {
            throw new NoSuchElementException("removeFirst empty");
        }
        
        item = array[lastIdx];
        array[lastIdx] = null;
        size--;
        if (size == 0) {
            firstIdx = -1;
            lastIdx = -1;
        } else if (lastIdx == 0) {
            lastIdx = array.length-1;
        } else {
            --lastIdx;
        }
        // shrink the circular array in half when over 75% empty
        if (size > 2 && size == array.length/4) {
            resize(array.length/2);
        }
        return item;
    }          
    
    /**
     * Returns an iterator that iterates over the items in this queue in FIFO order.
     * @return an iterator that iterates over the items in this queue in FIFO order
     */
    public Iterator<Item> iterator() {
        return new DequeArrayIterator();
    }   

    // an iterator, doesn't implement remove() since it's optional
    private class DequeArrayIterator implements Iterator<Item> {
        private int count = size;
        private int len = array.length;
        private int idx = firstIdx;
        
        public boolean hasNext() { 
            return count > 0; 
        }
        
        public void remove() { 
            throw new UnsupportedOperationException(); 
        }
        
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Item temp;
            if (idx == len) {
                idx = 0;
            }
            temp = array[idx++];
            count--;
            return temp;
        }
    }
    
    /**
     * Unit tests the <tt>Deque</tt> data type.
     * 
     * @param args main function standard argument
     */
    public static void main(String[] args) {
        scanner = new Scanner(System.in, "UTF-8");
        int idx = 1;
        Deque<Integer> q = new Deque<Integer>();
        while (true) {
            System.out.println("Enter 0 - exit, 1 - addFirst, 2 - addLast, 3 - removeFirst,"
                    + " 4 - removeLast:");
            int option = scanner.nextInt();
            if (option == 0) {
                break;
            } else if (option == 1) {
                q.addFirst(idx++);
            } else if (option == 2) {
                q.addLast(idx++);
            } else if (option == 3) {
                if (q.isEmpty()) {
                    System.out.println("Empty queue.");
                } else {
                    q.removeFirst();
                }
            } else if (option == 4) {
                if (q.isEmpty()) {
                    System.out.println("Empty queue.");
                } else {
                    q.removeLast();
                }
            } else {
                System.out.println("Invalid input");
            }
            
            if (!q.isEmpty()) {
                Iterator<Integer> itr = q.iterator();
                while (itr.hasNext()) {
                    System.out.print(itr.next() + " ");
                }
                System.out.println();
            }
            if (option == 0) {
                System.exit(0);
            }
        }
    }
}

