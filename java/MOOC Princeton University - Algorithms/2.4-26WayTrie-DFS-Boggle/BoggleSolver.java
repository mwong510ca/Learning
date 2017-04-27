package mwong.algs4.boggle;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import mwong.algs4.resources.boggle.BoggleBoard;

/****************************************************************************
 *  @author   Meisze Wong
 *  @linkedin www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation:  javac BoggleSolver.java
 *  Execution:  java BoggleSolver dictionary-algs4.txt board4x4.txt
 *  Dependencies (algs4.jar):  Bag.java, In.java, StdOut.java
 *  Dependencies:  BoggleBoard.java, BoggleBoardPlus.java, Boggle26WayTrie.java
 *
 *  The test client takes the filename of a dictionary and the filename of 
 *  a Boggle board as command-line arguments and prints out all valid words 
 *  for the given board using the given dictionary.
 *
 ****************************************************************************/

public class BoggleSolver {
    private Boggle26WayTrie trie;
    private int marker;
    
    /**
     *  Initializes the data structure using the given array of strings as the dictionary.
     *  Assume each word in the dictionary contains only the uppercase 'A' to 'Z'
     *  
     *  @param dictionary String array of dictionary
     */
    public BoggleSolver(String[] dictionary) {
        if (dictionary == null) {
            throw new IllegalArgumentException("Dictionnary is mandatory");
        }        
        trie = new Boggle26WayTrie();
        trie.loadDictionary(dictionary);
        marker = 1;
    }
    
    /**
     *  Returns the set of all valid words in the given Boggle board, as an Iterable.
     *  
     *  @param board the BoggleBoard object
     *  @return set of all valid words in the given Boggle board, as an Iterable
     */
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null) {
            throw new IllegalArgumentException("Board is mandatory");
        }
        if (board.rows() * board.cols() < 2 ) {
            return new Bag<String>();
        }
        // increment the marker for new search
        marker++;
        
        // load and convert the BoggleBoard to BoggleSolver properties 
        BoggleBoardPlus dices = new BoggleBoardPlus(board, trie.getOffset());
        // temporary set to negative negative during the search
        // restore it back when done for next search
        int[] faces = dices.getFaces();
        int[] nbrs = dices.getNbrs();
        
        Bag<String> words = new Bag<String>();
        int idxQ = trie.getIdxQ();

        // Identify each pair of the first two characters, then use depth
        // first search to find all words exists in given dictionary
        for (int i = 0; i < faces.length; i++) {
            int ch0 = faces[i];
            faces[i] = -1;
            int last = nbrs[i+1];
            for (int idx = nbrs[i]; idx < last; idx++) {
                int id = nbrs[idx];
                int ch1 = faces[id];
                int nextKey = trie.getKey(ch0, ch1);
                if (ch0 == idxQ) {
                    if (nextKey > 0 && trie.isNewString(nextKey, marker)) {
                        words.add(trie.getWord(nextKey));
                    }
                    int revKey = trie.getKey(ch1, ch0);
                    if (revKey > 0 && trie.isNewString(revKey, marker)) {
                        words.add(trie.getWord(revKey));
                    }
                }
                if (nextKey > 0) {
                    if (trie.hasNext(nextKey)) {
                        faces[id] = -1;
                        findWordsDFS(words, nextKey, faces, id, nbrs);
                        faces[id] = ch1;
                    }
                }
            } 
            faces[i] = ch0;
        }
        return words;        
    }
    
    // recursive depth first search the Boggle board to find all new words and
    // add to the words set
    private void findWordsDFS(Bag<String> words, int key, int[] faces, int id, int[] nbrs) {
        int last = nbrs[id+1];
        for (int idx = nbrs[id]; idx < last; idx++) {
            int id2 = nbrs[idx];
            int ch = faces[id2];
            if (ch >= 0) {
                int nextKey = trie.getKey(key, ch);
                if (nextKey > 0) {
                    if (trie.isNewString(nextKey, marker)) {
                        words.add(trie.getWord(nextKey));
                    }
                    if (trie.hasNext(nextKey)) {
                        faces[id2] = -1;
                        findWordsDFS(words, nextKey, faces, id2, nbrs);
                        faces[id2] = ch;
                    }
                }
            }
        }
    }

    /**
     *  Returns the number of score of the given word if it is in the dictionary, 
     *      zero otherwise.  Assume the word contains only the uppercase 'A' to 'Z'
     *      length   scores
     *      0 – 2         0
     *      3 – 4         1
     *      5             2
     *      6             3
     *      7             5
     *      8+           11  
     *  @param word the given string
     *  @return number of score of the given word if it is in the dictionary, 
     *      zero otherwise
     */
    public int scoreOf(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Word is mandatory");
        }
        if (!trie.contains(word)) {
            return 0;
        }
        switch (word.length()) {
        case 3: case 4:
            return 1;
        case 5:
            return 2;
        case 6:
            return 3;
        case 7:
            return 5;
        case 8: default:
            return 11;
        }
    }
    
    /**
     *  test client takes the filename of a dictionary and the filename of 
     *  a Boggle board as command-line arguments and prints out all valid words 
     *  for the given board using the given dictionary.
     *  
     *  @param args main function stanard input
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
