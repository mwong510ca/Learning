Coursera  - Algorithms Part 2 Princeton University by Kevin Wayne, Robert Sedgewick  
Programming assignment : 2.1  WordNet - direct graph, BFS  

WordNet is a semantic lexicon for the English language that is used extensively by computational linguists and cognitive scientists. WordNet groups words into sets of synonyms called synsets and describes semantic relationships between them. One such relationship is the is-a relationship, which connects a hyponym (more specific synset) to a hypernym (more general synset).  
The wordnet digraph is a rooted Directed Acyclic Graph(DAG): it is acyclic and has one vertex—the root—that is an ancestor of every other vertex.  

---

SAP.java: An immutable data type SAP (shortest ancestral path)  
Notes: Unlike WordNet digraph is a rooted DAG, SAP takes any digraph include multiple roots and has cycle.  
* It take an Digraph input and store all components in local storage.  For each pair of vertex or vertex set, use BFS to find the length and common ancestor.  
* Cached the latest search results in history in 2 sets instead of 1 set.  It boost the preformace about 8 % and archive bonus point 3 for less than 0.5x reference.
    * If compare 2 vertices, store integer vertex v, vertex w and it's results.
    * If compare 2 sets of vertices, store String value of set v + "_" + set w and it's results.

WordNet.java: A WordNet data type takes synsets.txt and hypernums.txt.  If hypernums.txt is the rooted DAG, convert into Digraph to generate SAP object.
* It load hypernums.txt, reorder the vertex from root and increment by depth, and convert into WordNet Digraph to generate SAP object.  
* Load the synsets.txt into storage, for all nouns that only has one code, and these codes has fixed depth path to root include parallel path, cached the path.  
* When search the distance and synsets for any pair of these nouns, use the cached paths to improve performance.  Otherwise call SAP functions as the program design.  
* After this analysis and tuning, it improve the performance about 20% and achieve the bonus point.  

Outcast.java: Outcast detection to identify an outcast.  
* Compute the sum of the distances between each noun and every other and return a noun is maximum sum of the distances. 
* Calculate the distance of each pair of the nouns once, increment the sum of these nouns.  Find the maximum sum of these nouns to determine the outcast.
    
