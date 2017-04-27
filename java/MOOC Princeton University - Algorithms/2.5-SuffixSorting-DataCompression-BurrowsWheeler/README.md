Coursera  - Algorithms Part 2 Princeton University by Kevin Wayne, Robert Sedgewick  
Programming assignment : BurrowsWheeler - suffix sorting, arrays, data compression  

http://www.cs.princeton.edu/courses/archive/spring04/cos226/assignments/burrows.html

Implement Burrows-Wheeler and move-to-front components efficiently.   
Burrows-Wheeler transform. Given a typical English text file, transform it into a text file in which sequences of the same character occur near each other many times.  
Move-to-front encoding. Given a text file in which sequences of the same character occur near each other many times, convert it into a text file in which certain characters appear more frequently than others.  

---

MoveToFront.java
* Encode: Initialize a 8-bit array for encoding, read a ASCII character, lookup and write it's position, use arraycopy to shift one position and move the character to the front.
* Decode: Initialize a 8-bit array for decoding, read a 8-bit position, write the ASCII at this position, use arraycopy to shift one position and move the character to the front.

CircularSuffixArray.java
* Method 1 - 3 way radix quicksort
    Sort the suffix index with 3 way radix quicksort, if size less then 12 use insertion sort.
* Method 2 - MSD Radix sort 
    Sort the suffix index with MSD radix sort, if size less then 12 use insertion sort.  Review and determin the minimum and maximum ASCII in the original input string, reduce the range and process time during the sorting.

<pre>
    Timing CircularSuffixArray
    *--------------------------------------------------------------------
    Tests 1-13: Timing constructor with random ASCII strings of length N
                              N        student   reference     ratio
    ---------------------------------------------------------------------
    3 way radix quicksort  4096000       0.85       0.81       1.05
    MSD radix sort         4096000       0.33       1.04       0.32
    </pre>
    
BurrowsWheeler.java
* Encode: Reading the input string, use CircularSuffixArray to sort the suffix index order, writing the encoded string.   
* Decode: Reading the frist suffix index and encoded string, use key index counting to restore the original string, writing the decoded string.      

