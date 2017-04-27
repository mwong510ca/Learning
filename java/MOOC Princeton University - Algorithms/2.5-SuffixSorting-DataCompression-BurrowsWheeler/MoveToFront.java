package mwong.algs4.burrowswheeler;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation: javac MoveToFront.java
 *  Execution: java MoveToFront - < abra.txt | java HexDump 16
 *             java MoveToFront - < abra.txt | java MoveToFront +
 *  Dependencies (algs4.jar): BinaryStdIn.java, BinaryStdOut.java
 *
 *  Move-to-front encoding - Maintain an ordered sequence of the 256 extended 
 *  ASCII characters.  Initialize the sequence by making the ith character in 
 *  the sequence equal to the ith extended ASCII character.  Read in each 8-bit 
 *  character c from standard input one at a time, output the 8-bit index in 
 *  the sequence where c appears, and move c to the front.
 *  Move-to-front decoding - Initialize an ordered sequence of 256 characters, 
 *  where extended ASCII character i appears ith in the sequence. Read in each 
 *  8-bit character i as integer from standard input one at a time, write the 
 *  ith character in the sequence, and move that character to the front.  Check 
 *  that the decoder recovers any encoded message.
 *
 ****************************************************************************/

public class MoveToFront {
    /**
     *  apply move-to-front encoding.
     *  reading an ASCII character, writing the 8-bit position index, move
     *  the character to the front.
     */
    public static void encode() {
        // Initialize an integer array of 8-bit character
        int[] chOrder = new int[256]; 
        for (int i = 0; i < 256; i++) {
            chOrder[i] = i;
        }
        
        while (!BinaryStdIn.isEmpty()) {
            // read a character and search it's position
            int ascii = BinaryStdIn.readChar();
            int pos = 0;
            while (chOrder[pos] != ascii) { 
                pos++;
            }
            
            // write the position as 8-bit character and move it to the front
            BinaryStdOut.write((char) pos);
            if (pos == 0) {
                continue;
            }
            System.arraycopy(chOrder, 0, chOrder, 1, pos);
            chOrder[0] = ascii;
        }
        BinaryStdOut.close();
    }
    
    /**
     *  apply move-to-front decoding.
     *  reading an ASCII characters the position, writing the 8-bit character
     *  at the position move the character to the front.
     */
    public static void decode() {
        // Initialize an integer array of 8-bit character
        int[] chOrder = new int[256];
        for (int i = 0; i < 256; i++) {
            chOrder[i] = i;
        }
        char ch0 = (char) chOrder[0];
        int pos;
        
        while (!BinaryStdIn.isEmpty()) {
            // read 8-bit position, write the 8-bit character at this position, move
            // it to the front
            pos = BinaryStdIn.readChar();
            if (pos > 0) {
                ch0 = (char) chOrder[pos];
                System.arraycopy(chOrder, 0, chOrder, 1, pos);
                chOrder[0] = ch0;
            }
            BinaryStdOut.write(ch0);
        }
        BinaryStdOut.close(); 
    }

    /**
     * if args[0] is '-', apply move-to-front encoding.
     * if args[0] is '+', apply move-to-front decoding.
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
