import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.HashMap;

public class WordNet {
    private SAP sap;
    private String[] synset;    
    private HashMap<String, Bag<Integer>> noun2id;
    private String lastSearch;
    private int resDistance;
    private int resSAP;
    
    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        In inSynsets = new In(synsets);
        In inHypernyms = new In(hypernyms);
        if (!inSynsets.exists() || !inHypernyms.exists()) {
			throw new IllegalArgumentException();
		}
        if (inSynsets.isEmpty() || inHypernyms.isEmpty()) {
			throw new IllegalArgumentException();
		}

        noun2id = new HashMap<String, Bag<Integer>>();       
        lastSearch = "";
        String[] lines = inSynsets.readAllLines();
        int size = lines.length;
        synset = new String[size];    
        Digraph g = new Digraph(size);

        for (String line : lines) {
            String[] cols = line.split(",");
            String[] nouns = cols[1].split(" ");
            int id = Integer.parseInt(cols[0]);
            Bag<Integer> sIDs;

            synset[id] = cols[1];
            
            for (int i = 0; i < nouns.length; i++) {
                if (!noun2id.containsKey(nouns[i])) {
                    sIDs = new Bag<Integer>();
                    sIDs.add(id);
                    noun2id.put(nouns[i], sIDs);
                } else {
                    sIDs = noun2id.get(nouns[i]);
                    sIDs.add(id);
                }                
            }            
        }
        
        lines = inHypernyms.readAllLines();
        for (String line : lines) {
            String[] cols = line.split(",");
            if (cols.length > 1) {
                int nounID = Integer.parseInt(cols[0]);
                for (int i = 1; i < cols.length; i++) {
                    g.addEdge(nounID, Integer.parseInt(cols[i]));
                }
            }
        }
        
        int rootCount = 0;
        for (int i = 0; i < size; i++) {
            if (g.outdegree(i) == 0) {
				rootCount++;
			}
        }
        
        DirectedCycle c = new DirectedCycle(g);
        if (c.hasCycle()) {
			throw new IllegalArgumentException();
		}
        if (rootCount != 1) {
			throw new IllegalArgumentException();
		}
        
        sap = new SAP(g);
    }
    
    // the set of nouns (no duplicates), returned as an Iterable
    public Iterable<String> nouns() {
        return noun2id.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
			throw new NullPointerException();
		}
        return (noun2id.get(word) != null);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        search(nounA, nounB);
        return resDistance;
     }
 
    // a synset (second field of synsets.txt) that is the common ancestor of 
    // nounA and nounB in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        search(nounA, nounB);
        return synset[resSAP];
    }

    private void search(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
			throw new NullPointerException();
		}
        
        String str = nounA + "_" + nounB;
        if (lastSearch.equals(str)) {
			return;
		}    
        
        Bag<Integer> keysA, keysB;
        keysA = noun2id.get(nounA);
        if (keysA == null) {
			throw new IllegalArgumentException();
		}  
        keysB = noun2id.get(nounB);
        if (keysB == null) {
			throw new IllegalArgumentException();
		}  
        
        lastSearch = str;

        if (keysA.size() == 1 && keysB.size() == 1) {
            resDistance = sap.length(keysA.iterator().next(), keysB.iterator().next());
            resSAP = sap.ancestor(keysA.iterator().next(), keysB.iterator().next());
        } else {
            resDistance = sap.length(keysA, keysB);
            resSAP = sap.ancestor(keysA, keysB);
        }
    }   
    
    // for unit testing of this class
    public static void main(String[] args) {
    }
}
