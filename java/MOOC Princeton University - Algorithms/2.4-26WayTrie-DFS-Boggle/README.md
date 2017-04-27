Coursera  - Algorithms Part 2 Princeton University by Kevin Wayne, Robert Sedgewick  
Programming assignment : Boggle - Trie, DFS  

Boggle is a word game involves a board made up of 16 cubic dice, where each die has a letter printed on each of its sides. At the beginning of the game, the 16 dice are shaken and randomly distributed into a 4-by-4 tray, with only the top sides of the dice visible. The players compete to accumulate points by building valid words out of the dice according to the following rules:
* A valid word must be composed by following a sequence of adjacent dice—two dice are adjacent if they are horizontal, vertical, or diagonal neighbors.  
* A valid word can use each die at most once.
* A valid word must contain at least 3 letters.
* A valid word must be in the dictionary (which typically does not contain proper nouns).
* The Qu special case. In the English language, the letter Q is almost always followed by the letter U. When scoring, Qu counts as two letters; for example, the word QuEUE scores as a 5-letter word.

---

BoggleBoardPlus.java - A data type for boggleSovler  
    Load the BoggleBoard, convert the face character into char index in dictionary trie and a set of it's neighbor dices' positions.'

Boggle26WayTrie.java - A data type of dictionary trie for boggle Solver  
    Load the dictionary in R26 way trie and store in 2 dimension arrays, also store the original word and status record check for BoggleSolver words lookup.

BoggleSolver.java - Boggle solver that finds all valid words in a given Boggle board, using a given dictionary.  
    Load the dictionary in R26 way trie.  Analysis the boggle board.  Walk through the board with depth first search, if there is a word in dictionary, store the word in the words set.  Revisited word will not add the words set.  

* Ternary search tries (TST) and Radix (Patricia) trie use less memeory, but it increase the depth of trie which take more time to search down the nodes.  
* 26-way tire reduce the depth of tree and improve search performance, but it take more memory since each leaf may carry 0-25 unuse empty nodes.  It's good for a small range of R.  It R is a large number, TST would be better.
* Combine the benefit of both Radix trie and 26-way trie, I implemented a [26-way radix trie].  From the autograder of boggle programming assignment, it takes 50% less performance and keep the same preformance.  

<pre>
    Computing memory of BoggleSolver     26-way trie   vs   (NEW) 26-way radix trie with reorder
    =================================================================================
    Test 1: memory with dictionary-algs4.txt (must be <= 2x reference solution). 
    memory of student   BoggleSolver   = 2423928 bytes      907416 bytes
    student / reference                = 0.47               0.18

    Test 2: memory with dictionary-shakespeare.txt (must be <= 2x reference solution).
    memory of student   BoggleSolver   = 8069192 bytes      3362016 bytes
    student / reference                = 0.46               0.19

    Test 3: memory with dictionary-yawl.txt (must be <= 2x reference solution).
    memory of student   BoggleSolver   = 85540064 bytes     41157576 bytes
    student / reference                = 0.48               0.23

    Timing BoggleSolver                  26-way trie   vs  (NEW) 26-way radix trie with reorder
    =================================================================================
    Test 2: timing getAllValidWords() for 5.0 seconds using dictionary-yawl.txt
    (must be <= 2x reference solution)
    reference solution calls per second: 9428.12            9688.37
    student solution calls per second:   20030.11           20418.32
    reference / student ratio:           0.47               0.47
</pre>

Given resources:  
BoggleBoard.java - A data type of boggle board  
BoggleGame.java - An interactive application of boggle board game.  

[26-way radix trie]: https://github.com/mwong510ca/java/tree/master/Algorithm%20-%2026-way%20Radix%20Trie%20-%20Boggle%20
