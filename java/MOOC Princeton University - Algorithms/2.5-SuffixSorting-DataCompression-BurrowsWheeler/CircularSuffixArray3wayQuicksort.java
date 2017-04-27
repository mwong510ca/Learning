package mwong.algs4.burrowswheeler;

/****************************************************************************
 *  @author   Meisze Wong
 *  @linkedin www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation: javac CircularSuffixArray.java
 *
 *  A data type for BurrowsWheeler that sorted an array of the N circular
 *  suffixes of a string of length N
 *
 ****************************************************************************/

public class CircularSuffixArray3wayQuicksort {
    private static final int CUTOFF = 12;
    private int length;
    private int [] suffixIndex;
    
    /**
     * Initializes a input string s and sort the circular suffixes index.
     *
     * @param s original string
     */
    public CircularSuffixArray3wayQuicksort(String s) {
        length = s.length();
        suffixIndex = new int[length];
        
        if (length < 2) {
            return;
        }
        // initial an doubleStr that repeat s once and the original index of s
        // then sor tthe string using 3 way quicksort
        char [] doubleStr = (s+s).toCharArray();
        int count = 0;
        for (int i = 0; i < length; i++) {
            suffixIndex[i] = i;
        }
        if (count == 1) {
            return;
        }
        string3wayRadixQuicksort(0, length-1, 0, doubleStr);
    }
    
    // recursive call to sort the string using 3-way radix quicksort
    private void string3wayRadixQuicksort(int first, int last, int level, char [] doubleStr) {
        // if subset size less then CUTOFF, use insertion sort for better performance
        if (last < first + CUTOFF) {
            insertion(first, last, level, doubleStr);
            return;
        }
        
        int pivot = doubleStr[suffixIndex[first] + level];
        int lt = first, gt = last;
        int j = first + 1;
        int max = last;
        while (j <= max) {
            int ch = doubleStr[suffixIndex[j] + level];
            if (ch < pivot) {
                swap(lt++, j++);
            } else if (ch > pivot) {
                swap(j, gt--);
                max--;
            } else {
                j++;
            }
        }
        
        // if not reach the end of string, sort the next level
        if (level + 1 < length) {
            string3wayRadixQuicksort(lt, gt, level+1, doubleStr);
            if (first + 1 < lt) {
                string3wayRadixQuicksort(first, lt-1, level, doubleStr);
            }
            if (last - 1 > gt) {
                string3wayRadixQuicksort(gt+1, last, level, doubleStr);
            }
        }
    }
    
    // insertion sort the suffix index from first to last subset position starts at
    // the given level
    private void insertion(int first, int last, int level, char [] doubleStr) {
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
    private boolean less(int pos1, int pos2, int level, char [] doubleStr) {
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
