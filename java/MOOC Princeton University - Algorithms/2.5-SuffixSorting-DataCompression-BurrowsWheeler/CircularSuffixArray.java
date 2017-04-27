package mwong.algs4.burrowswheeler;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation: javac CircularSuffixArray.java
 *
 *  A data type for BurrowsWheeler that sorted an array of the N circular
 *  suffixes of a string of length N
 *
 ****************************************************************************/

public class CircularSuffixArray {
    private static final int CUTOFF = 12;
    private int minRange, maxRange;
    private int length;
    private int[] suffixIndex;
    
    /**
     * Initializes a input string s and sort the circular suffixes index.
     * 
     * @param s original string
     */
    public CircularSuffixArray(String s) {
        length = s.length();
        suffixIndex = new int[length];
        
        if (length < 2) {
            return;
        }
        // initial an doubleStr that repeat s once, the original index of s
        // and set minimum and maximum range for MSD radix sort, then sort the string
        char[] doubleStr = (s+s).toCharArray();
        boolean[] r256 = new boolean[256];
        minRange = 255;
        maxRange = 0;
        int count = 0;
        for (int i = 0; i < length; i++) {
            suffixIndex[i] = i;
            minRange = Math.min(minRange, doubleStr[i]);
            maxRange = Math.max(maxRange, doubleStr[i]);
            
            if (!r256[doubleStr[i]]) {
                r256[doubleStr[i]] = true;
                count++;
            }
        }
        if (count == 1) {
            return;
        }
        stringMSDradixsort(0, length-1, 0, doubleStr);
    }
    
    // recursive call to sort the string using MSD radix sort
    private void stringMSDradixsort(int first, int last, int level, char[] doubleStr) {
        // if subset size less then CUTOFF, use insertion sort for better performance
        if (last <= first + CUTOFF) {
            insertion(first, last, level, doubleStr);
            return;
        }
        
        // compute frequency counts
        int[] count = new int[maxRange + 3];
        int[] aux = new int[last - first + 1];
        for (int i = first; i <= last; i++) {
            int c = doubleStr[suffixIndex[i] + level];
            count[c+2]++;
        }
        
        // transform counts to indicies
        for (int r = minRange; r < maxRange + 1; r++) {
            count[r+1] += count[r];
        }
        
        // distribute
        for (int i = first; i <= last; i++) {
            int c = doubleStr[suffixIndex[i] + level];
            aux[count[c + 1]++] = suffixIndex[i];
        }
        
        // copy back
        for (int i = first; i <= last; i++) {
            suffixIndex[i] = aux[i - first];
        }
        
        // recursively sort for each character (excludes sentinel -1)
        for (int r = minRange; r <= maxRange; r++) {
            if (count[r] + 1 < count[r+1]) {
                stringMSDradixsort(first + count[r], first + count[r+1] - 1, level+1, doubleStr);
            }
        }
    }
    
    // insertion sort the suffix index from first to last subset position starts at
    // the given level
    private void insertion(int first, int last, int level, char[] doubleStr) {
        for (int i = first + 1; i <= last; i++) {
            for (int j = i; j > first; j--) {
                if (less(j, j - 1, level, doubleStr)) {
                    swap(j, j - 1);
                } else {
                    break;
                }
            }
        }
    }
    
    // compare the order of suffix index at pos1 and pos2 starts at given level
    private boolean less(int pos1, int pos2, int level, char[] doubleStr) {
        int ch1 = suffixIndex[pos1] + level;
        int ch2 = suffixIndex[pos2] + level;
        int d = level;
        while (d < length) {
            if (doubleStr[ch1] < doubleStr[ch2]) {
                return true;
            }
            if (doubleStr[ch1++] > doubleStr[ch2++]) {
                return false;
            }
            d++;
        }
        return false;
    }
    
    // exchange the value of idx1 and idx2 in index array
    private void swap(int idx1, int idx2) {
        int temp = suffixIndex[idx1];
        suffixIndex[idx1] = suffixIndex[idx2];
        suffixIndex[idx2] = temp;
    }
    
    /**
     *  Returns the length of string.
     *  
     *  @return number of the length of string
     */
    public int length() {
        return length;
    }
    
    /**
     *  Returns the index of ith sorted suffix.
     *  
     *  @param i the index of character in original string 
     *  @return index of ith sorted suffix
     */
    public int index(int i) {
        if (length == 0 || i >= length) {
            throw new IndexOutOfBoundsException();
        }
        return suffixIndex[i];
    }
}
