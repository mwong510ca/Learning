package mwong.algs4.wordnet;

import edu.princeton.cs.algs4.In;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation:  javac Outcast.java
 *  Execution:  java Outcast synsets.txt hypernyms.txt outcast5.txt outcast8.txt outcast11.txt
 *  Dependencies (algs4.jar):  In.java, StdOut.java
 *  Dependencies:  WordNet.java
 *
 *  Outcast detection. Given a list of wordnet nouns (A1, A2, ..., An) to identify an outcast.
 *  compute the sum of the distances between each noun and every other and return a noun
 *  is maximum sum of the distances.
 *
 ****************************************************************************/

public class Outcast {
    private WordNet wordnet;
    
    /**
     * Initializes Outcast with the given WordNet object.
     * 
     * @param wordnet the given WordNet object
     */
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    /**
     * Returns the string of an outcast.
     * 
     * @param nouns the given array of WordNet nouns
     * @return string of an outcast
     */
    public String outcast(String[] nouns) {
        int length = nouns.length;
        int[] sum = new int[length];
        
        // count the sum for each pair of nouns
        for (int nounID = length - 1; nounID >= 0; nounID--) {
            for (int otherID = 0; otherID < nounID; otherID++) {
                int path = wordnet.distance(nouns[nounID], nouns[otherID]);
                sum[nounID] += path;
                sum[otherID] += path;
            }
        } 
        
        // find max value of the sum and return outcast 
        int max = sum[0];
        String outcast = nouns[0];
        for (int nounID = length - 1; nounID > 0; nounID--) {
            if (sum[nounID] > max) {
                max = sum[nounID];
                outcast = nouns[nounID];
            }
        }
        return outcast;
    }

    /**
     * test client takes from the command line the name of a synset file, 
     * the name of a hypernym file, followed by the names of outcast files, 
     * and prints out an outcast in each file.
     * 
     * @param args synset filename, hypernym filename
     */
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            System.out.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
