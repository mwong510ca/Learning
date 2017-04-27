package mwong.algs4.wordnet;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

/****************************************************************************
 *  @author   Meisze Wong
 *   www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation:  javac WordNet.java
 *  Execution:    java WordNet synsets.txt hypernyms.txt
 *  Dependencies (algs4.jar): Bag.java, Digraph.java, DirectedCycle.java
 *                            In.java, System.out.java
 *  Dependencies: SAP.java
 *
 *  An immutable data type WordNet
 *
 ****************************************************************************/

public class WordNet {
    private SAP sap;
    private String[] synset;    
    private byte[] depthMin; 
    private boolean[] fixedDepth, parallelPath;
    private int[][] path;
    private int resDistance, resAncestor;
    
    // Notes: code in positive number, bag index in negative number
    private HashMap<String, Integer> noun2referenceIdx;
    private ArrayList<Bag<Integer>> bags;
    private String lastNounA, lastNounB;
    private int lastKeyA, lastKeyB;
    
    /**
     *  Initializes Outcast and initialize the WordNet object.
     *  
     *  @param synsets the name of the synsets input file
     *  @param hypernyms the name of the hypernyms input file
     *  @throws IndexOutOfBoundsException unless synsets input file exists
     *  @throws IndexOutOfBoundsException unless hypernyms input file exists
     */
    public WordNet(String synsets, String hypernyms) {
        In inSynsets = new In(synsets);
        In inHypernyms = new In(hypernyms);
        if (!inSynsets.exists() || !inHypernyms.exists()) {
            throw new IllegalArgumentException();
        }
        if (inSynsets.isEmpty() || inHypernyms.isEmpty()) {
            throw new IllegalArgumentException();
        }

        lastNounA = "";
        lastNounB = "";
        lastKeyA = -1;
        lastKeyB = -1;
        
        String[] linesSynsets = inSynsets.readAllLines();
        String[] linesHypernyms = inHypernyms.readAllLines();
        int size = linesSynsets.length;
        
        int[] convertionTable = loadHypernyms(size, linesHypernyms);
        loadSynsets(size, linesSynsets, convertionTable);
    }     
        
    // load the hypernyms file in Digraph, throws IllegalArgunmentException
    // if WordNet is not DAG.  Reset the vertex order by depth, vertex 0 is
    // the root of DAG.  Analysis the DAG, for every vertex has single path
    // root, cache the the path.
    private int[] loadHypernyms(int size, String[] lines) {
        Digraph g = new Digraph(size);
        Digraph orgG = new Digraph(size);

        // load the hypernyms file in Digraph, if not DAG throws
        // IllegalArgunmentException
        for (String line : lines) {
            String[] cols = line.split(",");
            if (cols.length > 1) {
                int nounID = Integer.parseInt(cols[0]);
                for (int i = 1; i < cols.length; i++) {
                    orgG.addEdge(nounID, Integer.parseInt(cols[i]));
                }
            }
        }
        
        int rootCount = 0;
        int rootKey = -1;
        for (int i = 0; i < size; i++) {
            if (orgG.outdegree(i) == 0) {
                rootCount++;
                rootKey = i;
            }
        }
        
        DirectedCycle c = new DirectedCycle(orgG);
        if (c.hasCycle()) {
            throw new IllegalArgumentException();
        }
        if (rootCount != 1) {
            throw new IllegalArgumentException();
        }
        
        // make a copy of reverse Digraph, analysis the DAG,
        // reset the vertex in depth order and create the sap object
        // check the depth range to root per vertex
        byte depth = 0;
        int reorderID = 0;
        depthMin = new byte[size];
        byte[] depthMax = new byte[size];
        
        Digraph revG = orgG.reverse();
        Bag<Integer> bag = new Bag<Integer>();
        for (int i : revG.adj(rootKey)) {
            bag.add(i);
        }
        int[] convertionTable = new int[size];
        convertionTable[rootKey] = reorderID++;
        
        while (bag.size() > 0) {
            depth++;
            Bag<Integer> next = new Bag<Integer>();
            for (int i : bag) {
                for (int j : revG.adj(i)) {
                    next.add(j);
                }
                if (convertionTable[i] == 0) {
                    convertionTable[i] = reorderID++;
                }

                if (depthMin[convertionTable[i]] == 0) {
                    depthMin[convertionTable[i]] = depth;
                }
                depthMax[convertionTable[i]] = depth;                    
            }
            bag = next;
        }
        
        fixedDepth = new boolean[size];
        for (int i = 0; i < size; i++) {
            if (depthMin[i] == depthMax[i]) {
                fixedDepth[i] = true;
            }
        }
        
        for (int i = 0; i < size; i++) {
            for (int j : orgG.adj(i)) {
                g.addEdge(convertionTable[i], convertionTable[j]);
            }
        }
        sap = new SAP(g);
        buildPath(size, g);
        return convertionTable;
    }
    
    // for those vertex with fixed depth, cache the path to root
    private void buildPath(int max4fixedDepth, Digraph g) {
        // initial the path size
        path = new int[max4fixedDepth][];
        // root 0 has path to itself
        path[0] = new int[1];
        path[0][0] = 0;
        parallelPath = new boolean[max4fixedDepth];
        
        // add all vertexes with fixed depth in numeric order 
        // (means also in depth order)
        ArrayList<Integer> hasPath = new ArrayList<Integer>();
        for (int i = 1; i < max4fixedDepth; i++) {
            if (fixedDepth[i]) {
                hasPath.add(i);
            }
        }
        
        for (int code : hasPath) {
            if (g.outdegree(code) == 1) {
                // code has 1 parent, copy all plus parent
                int parent = g.adj(code).iterator().next();
                parallelPath[code] = parallelPath[parent];
                path[code] = new int[path[parent].length + 1];
                System.arraycopy(path[parent], 0, path[code], 0, path[parent].length);
                path[code][path[parent].length] = code;
            } else {
                // copy all parents per depth level until it reach the root
                ArrayList<Integer> partialPath = new ArrayList<Integer>();
                partialPath.add(0, code);
                int numParents = g.outdegree(code);
                int[][] parentPaths = new int[numParents][];
                int[] parentIdx = new int[numParents];
                int idx = 0;
                
                // copy all parent paths, add on set at a time per depth level
                // sort all parent of next depth in ascending order
                // insert into partialParh in descending order 
                TreeSet<Integer> parentList = new TreeSet<Integer>();
                for (int parent : g.adj(code)) {
                    parentList.add(parent);
                    parentPaths[idx] = path[parent];
                    parentIdx[idx] = parentPaths[idx++].length - 2;
                }
                
                for (int parent : parentList) {
                    partialPath.add(0, parent);
                }
                parallelPath[code] = true;
                int depth = depthMin[code] - 1;
                
                // all vertexes per depth level
                while (depth > 0) {
                    depth--;
                    parentList = new TreeSet<Integer>();
                    for (int parent = 0; parent < numParents; parent++) {
                        int nextParent = parentPaths[parent][parentIdx[parent]--];
                        parentList.add(nextParent);
                        
                        while (parentIdx[parent] > 0 && parentPaths[parent][parentIdx[parent]] 
                                > nextParent) {
                            nextParent = parentPaths[parent][parentIdx[parent]--];
                            parentList.add(nextParent);
                        }
                    }
                    
                    if (parentList.size() == 1) {
                        // while next parent back to single vertex
                        // copy the full path to the final destination
                        depth = 0;
                        path[code] = new int[parentIdx[0] + 2 + partialPath.size()];
                        System.arraycopy(parentPaths[0], 0, path[code], 0, parentIdx[0] + 2);
                        idx = parentIdx[0] + 2;
                        for (int j : partialPath) {
                            path[code][idx++] = j;
                        }
                    } else {
                        // add parents to the next depth level
                        for (int parent : parentList) {
                            partialPath.add(0, parent);
                        }
                    }
                }
            }
        }
    }
        
    // load the synsets file into local storage. clear the fixed path if
    // no nouns associate with it
    private void loadSynsets(int size, String[] lines, int[] convertionTable) {     
           synset = new String[size];
        int bagCount = 0;
        // noun -> bag index
        HashMap<String, Integer> noun2bagIdx = new HashMap<String, Integer>();
        // bag index -> bag of codes
        HashMap<Integer, Bag<Integer>> bagIdx2bag = new HashMap<Integer, Bag<Integer>>();
        // bag index -> set of nouns (same bay of code may shared by multiple nouns)
        HashMap<Integer, HashSet<String>> bagIdx2nouns = new HashMap<Integer, HashSet<String>>();
        
        for (String line : lines) {
            String[] cols = line.split(",");
            String[] nouns = cols[1].split(" ");
            int code = convertionTable[Integer.parseInt(cols[0])];
            synset[code] = cols[1];
            
            HashSet<String> add2bag = new HashSet<String>();
            boolean newBag = false;
            for (int i = 0; i < nouns.length; i++) {
                // if noun has other code, will add to exist storage
                if (noun2bagIdx.containsKey(nouns[i])) {
                    add2bag.add(nouns[i]);
                } else {
                    // noun (1+) -> bag index (single)
                    noun2bagIdx.put(nouns[i], bagCount);
                    // bag index (single) -> noun (1+)
                    if (bagIdx2nouns.containsKey(bagCount)) {
                        bagIdx2nouns.get(bagCount).add(nouns[i]);
                    } else {
                        // bag index (single) -> noun (first entry)
                        HashSet<String> temp = new HashSet<String>();
                        temp.add(nouns[i]);
                        bagIdx2nouns.put(bagCount, temp);
                    }
                    newBag = true;
                }
            }
            Bag<Integer> bag = new Bag<Integer>();
            // bag index -> bag (contain current code only)
            if (newBag) {
                bag.add(code);
                bagIdx2bag.put(bagCount, bag);
                bagCount++;
            }
            
            // for every noun has more than one codes, add code below 
            while (add2bag.size() > 0) {
                // get bag index of noun
                String noun = add2bag.iterator().next();
                int bagIdx = noun2bagIdx.get(noun);
                boolean merge = false;
                
                // nouns are identical, merge the current code
                if (add2bag.size() == bagIdx2nouns.get(bagIdx).size()) {
                    merge = true;
                    for (String str : bagIdx2nouns.get(bagIdx)) {
                        if (!add2bag.contains(str)) {
                            merge = false;
                            break;
                        }
                    }
                }
                if (merge) {
                    bagIdx2bag.get(bagIdx).add(code);
                   break;
                }
                
                // otherwise, split in a new bag
                // nouns are identical in both set
                HashSet<String> splitSet = new HashSet<String>();
                for (String noun2 : bagIdx2nouns.get(bagIdx)) {
                    if (add2bag.contains(noun2)) {
                        splitSet.add(noun2);
                        add2bag.remove(noun2);
                    }
                }
                for (String noun2 : splitSet) {
                    bagIdx2nouns.get(bagIdx).remove(noun2);
                }
                
                bag = new Bag<Integer>();
                for (int code2 : bagIdx2bag.get(bagIdx)) {
                    bag.add(code2);
                }
                bag.add(code);
                for (String noun2 : splitSet) {
                    noun2bagIdx.put(noun2, bagCount);
                }
                bagIdx2nouns.put(bagCount, splitSet);
                bagIdx2bag.put(bagCount++, bag);    
            }
        }
        
        // reset the bag storage and bag index, noun link to single code, 
        // otherwise, noun link to bag index and vertexes to bag
        // for those fixed depth path without any nouns link to it, 
        // remove it for paths.
        // count the number of bags has more than one code
        int numBags = 0;
        for (Bag<Integer> bag : bagIdx2bag.values()) {
            if (bag.size() > 1) {
                numBags++;
            }
        }        
        
        HashSet<Integer> removePath = new HashSet<Integer>();
        for (int i = 0; i < path.length; i++) {
            removePath.add(i);
        }
        noun2referenceIdx = new HashMap<String, Integer>();
        bags = new ArrayList<Bag<Integer>>(numBags + 1);
        for (int i = 0; i < numBags + 1; i++) {
            bags.add(i, new Bag<Integer>());
        }
        int bagIdx = 1;
        for (int i = 0; i < bagCount; i++) {
            Bag<Integer> newBag = bagIdx2bag.get(i);
            // store the code in positive number
            if (newBag.size() == 1) {
                int code = newBag.iterator().next();
                for (String noun : bagIdx2nouns.get(i)) {
                    noun2referenceIdx.put(noun, code);
                }
                // do not remove the path
                removePath.remove(code);
            } else {
                // store the bag index in negative number
                for (String noun : bagIdx2nouns.get(i)) {
                    noun2referenceIdx.put(noun, 0 - bagIdx);
                }
                for (int item : newBag) {
                    bags.get(bagIdx).add(item);
                }
                bagIdx++;
            }
        }
        
        // remove all unlinked path
        for (int i : removePath) {
            path[i] = null;
        }
    }
    
    /**
     * the set of nouns (no duplicates), returned as an Iterable.
     * 
     * @return the set of nouns (no duplicates), returned as an Iterable
     */
    public Iterable<String> nouns() {
        return noun2referenceIdx.keySet();
    }

    /**
     * is the word a WordNet noun?
     * 
     * @param word the String of given word
     * @return boolean represents the word in wordnet
     */
    public boolean isNoun(String word) {
        if (word == null) {
            throw new NullPointerException();
        }
        return noun2referenceIdx.containsKey(word);
    }

    /**
     * the distance between nounA and nounB.
     * 
     * @param nounA the String of given word
     * @param nounB the String of given word
     * @return integer value of distance between nounA and nounB
     */
    public int distance(String nounA, String nounB) {
        search(nounA, nounB);
        return resDistance;
     }
 
    /**
     * a synset that is the common ancestor of nounA and nounB in a shortest ancestral path.
     * 
     * @param nounA the String of given word
     * @param nounB the String of given word
     * @return String of a synset that is the common ancestor of nounA and nounB in 
     * a shortest ancestral path
     */
    public String sap(String nounA, String nounB) {
        search(nounA, nounB);
        return synset[resAncestor];
    }
    
    private void search(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new NullPointerException();
        }
        int keyA, keyB;
        if (nounA.equals(lastNounA)) {
            keyA = lastKeyA;
        } else {
            if (!noun2referenceIdx.containsKey(nounA)) {
                throw new IllegalArgumentException();
            }
            keyA = noun2referenceIdx.get(nounA);
        }
        if (nounB.equals(lastNounB)) {
            keyB = lastKeyB;
        } else {
            if (!noun2referenceIdx.containsKey(nounB)) {
                throw new IllegalArgumentException();
            }
            keyB = noun2referenceIdx.get(nounB);
        }
        lastNounA = nounA;
        lastNounB = nounB;
        if (lastKeyA == keyA && lastKeyB == keyB) {
            return;
        }
        
        lastKeyA = keyA;
        lastKeyB = keyB;
        if (keyA < 0 && keyB < 0) {
            resDistance = sap.length(bags.get(0 - keyA), bags.get(0 - keyB));
            resAncestor = sap.ancestor(bags.get(0 - keyA), bags.get(0 - keyB));
        } else if (keyA < 0) {
            Bag<Integer> bag = new Bag<Integer>();
            bag.add(keyB);
            resDistance = sap.length(bags.get(0 - keyA), bag);
            resAncestor = sap.ancestor(bags.get(0 - keyA), bag);
        } else if (keyB < 0) {
            Bag<Integer> bag = new Bag<Integer>();
            bag.add(keyA);
            resDistance = sap.length(bag, bags.get(0 - keyB));
            resAncestor = sap.ancestor(bag, bags.get(0 - keyB));
        } else {
            if (keyA == keyB) {
                resDistance = 0;
                resAncestor = keyA;
            } else if (fixedDepth[keyA] && fixedDepth[keyB]) {
                if (parallelPath[keyA] && !parallelPath[keyB]) {
                    searchPath(keyB, keyA);
                } else {
                    searchPath(keyA, keyB);
                }
            } else {
                resDistance = sap.length(keyA, keyB);
                resAncestor = sap.ancestor(keyA, keyB);
            }
        }
    }   
        
    private void searchPath(int keyA, int keyB) {
        int[] rowA = path[keyA];
        int[] rowB = path[keyB];
        int len = depthMin[keyA] + depthMin[keyB];
        int limit = Math.min(depthMin[keyA], depthMin[keyB]) + 1;
        int idxA = 1;        
        int matched = 0;
        
        if (!parallelPath[keyA] && !parallelPath[keyB]) {
            while (idxA < limit && rowA[idxA] == rowB[idxA]) {
                len -= 2;
                idxA++;
            }
            matched = idxA - 1;
        } else if (parallelPath[keyA] && parallelPath[keyB]) {
            int count = 1; 
            int idxB = 1;
            while (true) {               
                while (rowA[idxA] != rowB[idxB]) {
                    if (rowA[idxA] > rowB[idxB]) {
                        if (idxA + 1 < rowA.length && rowA[idxA + 1] < rowA[idxA]) {
                            idxA++;
                        } else {
                            break;
                        }
                    } else {
                        if (idxB + 1 < rowB.length && rowB[idxB + 1] < rowB[idxB]) {
                            idxB++;
                        } else {
                            break;
                        }
                    }
                }
                
                if (rowA[idxA] == rowB[idxB]) {
                    matched = idxA;
                    count++;
                    idxA++;
                    idxB++;
                    len -= 2;
                    if (count < limit) {
                        while (rowA[idxA] < rowA[idxA - 1]) {
                            idxA++;
                        }
                        while (rowB[idxB] < rowB[idxB - 1]) {
                            idxB++;
                        }
                    } else {
                        break;
                    }
                } else {
                    break;
                } 
            }           
        } else {
            if (keyA == 0) {
                resDistance = depthMin[keyB];
                resAncestor = 0;
                return;
            }
            
            int idxB = 1;
            while (true) {               
                int vertexA = rowA[idxA];
                while (rowB[idxB] > vertexA && idxB + 1 < rowB.length && rowB[idxB] > rowB[idxB + 1]) {
                    idxB++;
                }
                if (rowA[idxA] == rowB[idxB]) {
                    idxA++;
                    idxB++;
                    len -= 2;
                    if (idxA < limit) {
                        while (rowB[idxB] < rowB[idxB - 1]) {
                            idxB++;
                        }
                    } else {
                        break;
                    }
                } else {
                    break;
                } 
            } 
            matched = idxA - 1;
        }
        resDistance = len;
        resAncestor = rowA[matched];
    }  
}

