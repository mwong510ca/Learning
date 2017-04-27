package mwong.algs4.burrowswheeler;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation: javac BurrowsWheeler.java
 *  Execution:   java BurrowsWheeler - < abra.txt | java BurrowsWheeler +
 *  Dependencies (algs4.jar): BinaryStdIn.java, BinaryStdOut.java
 *  Dependencies: CircularSuffixArray.java
 *
 *  Burrows-Wheeler transform and recover the original input string. If the 
 *  jth original suffix is the ith row in the sorted order, we define next[i]
 *  to be the row in the sorted order where the (j + 1)st original suffix appears. 
 *  For example, if first is the row in which the original input string appears, 
 *  then next[first] is the row in the sorted order where the 1st original suffix 
 *  appears; next[next[first]] is the row in the sorted order where the 2nd original
 *  suffix appears; next[next[next[first]]] is the row where the 3rd original
 *  suffix appears; and so forth.
 *
 ****************************************************************************/

public class BurrowsWheeler {

    /**
     *  apply Burrows-Wheeler encoding.
     *  reading the input string and sort in circular suffix order, writing the last character of
     *  of the circular string in suffix order.
     */
    public static void encode() {
        String s = BinaryStdIn.readString();
        char[] str = s.toCharArray();
        CircularSuffixArray csa = new CircularSuffixArray(s);
        int size = s.length();
        
        int firstIdx = -1;
        for (int i = 0; i < size; i++) {
            if (csa.index(i) == 0) {
                firstIdx = i;
                BinaryStdOut.write(firstIdx);
                break;
            }
        }
        
        for (int i = 0; i < size; i++) {
            if (i == firstIdx) {}
               BinaryStdOut.write(str[size - 1]);
            } else {
               BinaryStdOut.write(str[csa.index(i) - 1]);
            }
        }
      
        BinaryStdOut.close(); 
    }

    /**
     *  apply Burrows-Wheeler decoding.
     *  reading the first suffix index and the encoded string, restore 
     *  and writing the decoded string using key index counting.
     */
    public static void decode() {
        int start = BinaryStdIn.readInt();
        String s = BinaryStdIn.readString();
        char[] str = s.toCharArray();
        int size = s.length();
        
        // need first & next
        int[] next = new int[size];
        char[] first = new char[size];        
        int[] counts = new int[256];
        
        // key index counting, 256 characters
        for (int i = 0; i < size; i++) {
            counts[str[i]]++;
        }
        
        // load to first character array,
        // and reset count to initial index of each character
        int total = 0;
        int firstIdx = 0;
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < counts[i]; j++) {
                first[firstIdx++] = (char) i;
            }
            counts[i] = total;
            total = firstIdx;
        }
        
        // load the next index array
        for (int i = 0; i < size; i++) {
            next[counts[str[i]]] = i;
            counts[str[i]]++;
        }
        
        // decode the original message
        for (int i = 0; i < size; i++) {
            BinaryStdOut.write(first[start]);
            start = next[start];
        } 
        BinaryStdOut.close();     
    }

    /**
     * if args[0] is '-', apply Burrows-Wheeler encoding.
     * if args[0] is '+', apply Burrows-Wheeler decoding.
     * 
     * @param args main function standard arguments
     */
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            encode();
        } else if (args[0].equals("+")) {
            decode();
        } else {
            throw new RuntimeException("Illegal command line argument");
        }
    }
}
