package mwong.algs4.boggle;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation: javac Boggle26WayTrie.java
 *
 *  A data type to load the dictionary into R26 way Trie store in two
 *  dimension arrays, with specific feather for BoggleSolver
 *
 ****************************************************************************/

public class Boggle26WayTrie {
    private final int offset = 'A';
    private final int idxQ = 'Q' - offset;
    private int[][] trieR26;
    private String[] words;
    private int[] visited;
    
    /**
     *  default constructor.
     */
    public Boggle26WayTrie() { };
    
    /**
     *  Initialize the storage size and load the dictionary in trie object, skip the word 
     *  if invalid or duplicated word.
     *  
     *  @param dict string array of the dictionary
     */
    protected void loadDictionary(String[] dict) {
        int estCapacity = dict.length * 4;
        int[][] tempTrie = new int[estCapacity][];
        String[] tempWords = new String[estCapacity];
        
        int count = 26;
        int ctInsert = 0;
        int[] pendingDepth = new int[dict.length];
        int[] pendingIdx = new int[dict.length];
        String[] pendingKey = new String[dict.length];
        
        // initial the temporary trie index for the first 2 characters only
        for (String key : dict) {
            if (key.isEmpty() || key.length() < 3) {
                continue;
            }
            
            int d = 0;
            int idx = key.charAt(d++) - offset;
            if (idx == idxQ) {
                if (key.charAt(d++) != 'U') {
                    continue;
                }
            }
            
            int chIdx = key.charAt(d++) - offset;
            if (chIdx == idxQ) {
                if (d >= key.length() || key.charAt(d++) != 'U') {
                    continue;
                }
            }
            
            if (tempTrie[idx] == null) {
                tempTrie[idx] = new int[26];
                tempTrie[idx][chIdx] = count;
                idx = count++;
            } else if (tempTrie[idx][chIdx] == 0) {
                tempTrie[idx][chIdx] = count;
                idx = count++;
            } else {
                idx = tempTrie[idx][chIdx];
            }
            
            if (d == key.length()) {
                tempWords[idx] = key;
            } else {
                pendingDepth[ctInsert] = d;
                pendingIdx[ctInsert] = idx;
                pendingKey[ctInsert++] = key;
            }
        }
        
        // load all words in temporary trie, expend the depth on at a time
        while (ctInsert > 0) {
            int[] nextDepth = new int[ctInsert];
            int[] nextIdx = new int[ctInsert];
            String[] nextKey = new String[ctInsert];
            int ct = 0;

            for (int i = 0; i < ctInsert; i++) {
                int d = pendingDepth[i];
                int idx = pendingIdx[i];
                String key = pendingKey[i];
                
                int ch = key.charAt(d++) - offset;
                if (ch == idxQ) {
                    if (d >= key.length() || key.charAt(d++) != 'U') {
                        continue;
                    }
                }
                
                if (tempTrie[idx] == null) {
                    tempTrie[idx] = new int[26];
                    tempTrie[idx][ch] = count;
                    idx = count++;
                } else if (tempTrie[idx][ch] == 0) {
                    tempTrie[idx][ch] = count;
                    idx = count++;
                } else {
                    idx = tempTrie[idx][ch];
                }
                
                if (d == key.length()) {
                    tempWords[idx] = key;
                } else {
                    nextDepth[ct] = d;
                    nextIdx[ct] = idx;
                    nextKey[ct++] = key;
                }
            }
            
            pendingDepth = nextDepth;
            pendingIdx = nextIdx;
            pendingKey = nextKey;
            ctInsert = ct;
        }
        
        trieR26 = new int[count][];
        System.arraycopy(tempTrie, 0, trieR26, 0, count);
        words = new String[count];
        System.arraycopy(tempWords, 0, words, 0, count);
        visited = new int[count];
    }
    
    /**
     *  Returns the number of offset value of character.
     *  
     *  @return number of offset value of character
     */
    protected int getOffset() {
        return offset;
    }
 
    /**
     *  Returns the number of the index of character 'Q'.
     *  
     *  @return number of the index of character 'Q'
     */
    protected int getIdxQ() {
        return idxQ;
    }
 
    /**
     *  Returns the boolean represent the trie contains the given string key.
     *  
     *  @param key the given string
     *  @return boolean represent the trie contains the given string key
     */
    protected boolean contains(String key) {
        if (key.isEmpty() || key.length() < 3) {
            return false;
        } 
        
        int d = 1;
        int idx = key.charAt(0) - offset;
        if (idx == idxQ) {
            if (key.charAt(1) == 'U') {
                d = 2;
            } else {
                return false;
            }
        }
        
        while (true) {
            int chIdx = key.charAt(d++) - offset;
            if (chIdx == idxQ) {
                if (d < key.length() && key.charAt(d) == 'U') {
                    d++;
                } else {
                    return false;
                }
            }
            
            if (trieR26[idx] == null || trieR26[idx][chIdx] == 0) {
                return false;
            }
            idx = trieR26[idx][chIdx];
            if (d == key.length()) {
                if (words[idx] == null) {
                    return false;
                }
                return true;
            }
        }
    }
    
    /**
     *  Returns the number of next key in trie, 0 if not exists.
     *  
     *  @param key the number of current key 
     *  @param chIdx the next character index
     *  @return number of next key in trie, 0 if not exists
     */
    protected int getKey(int key, int chIdx) {
        if (trieR26[key] == null) {
            return 0;
        }
        return trieR26[key][chIdx];
    }
    
    /**
     *  Returns the boolean represent the key has next character.
     *  
     *  @param key the number of current key 
     *  @return boolean represent the key has next character
     */
    protected boolean hasNext(int key) {
        return !(trieR26[key] == null);
    }
    
    /**
     *  Returns the boolean represent the key is not a visited word.
     *  
     *  @param key the number of current key 
     *  @param marker the number of current marker 
     *  @return boolean represent the key is not a visited word
     */
    protected boolean isNewString(int key, int marker) {
        if (words[key] == null) {
            return false;
        }
        if (visited[key] == marker) {
            return false;
        }
        visited[key] = marker;
        return true;
    }
    
    /**
     *  Returns the string of the given key.
     *  
     *  @param key the number of current key 
     *  @return string of the given key
     */
    protected String getWord(int key) {
        return words[key];
    }
}
