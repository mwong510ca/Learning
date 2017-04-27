package mwong.algs4.wordnet;

import edu.princeton.cs.algs4.Digraph;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation:  javac SAP.java
 *  Execution:    java SAP digraph1.txt
 *  Dependencies (algs4.jar):  Digraph.java
 *
 *  An immutable data type SAP (shortest ancestral path)
 *  Notes: A given digraph maybe multiple roots and/or has cycle.
 *
 ****************************************************************************/

public class SAP {
    private static final int INFINITY = Integer.MAX_VALUE;
    private int[] adjInfo;
    private int[][] adjMulti;
    private byte[] distTo;
    private int[] visitedV, visitedW;
    private int[] reviewV, reviewW, nextV, nextW;
    private int limit, marker, lastSearchV, lastSearchW, rootKey;
    private int resLengthSingle, resAncestorSingle, resLengthCombo, resAncestorCombo;
    private String lastSearchCombo;
       
    /**
     *  Initializes SAP with the given G Digraph object.
     *  
     *  @param G the given Digraph object
     */
    public SAP(Digraph G) {
        limit = G.V();
        lastSearchV = -1;
        lastSearchW = -1;
        lastSearchCombo = "";
        distTo = new byte[limit];
        visitedV = new int[limit];
        visitedW = new int[limit];
        marker = 1;
        rootKey = 0 - limit - 1;
        
        adjInfo = new int[limit];
        int count = 1;
        int maxAdj = 1;
        for (int i = 0; i < limit; i++) {
            if (G.outdegree(i) == 1) {
                adjInfo[i] = G.adj(i).iterator().next();
            } else if (G.outdegree(i) == 0) {
                adjInfo[i] = rootKey;
            } else {
                adjInfo[i] = 0 - count;
                count++;
            }
            maxAdj = Math.max(maxAdj, G.outdegree(i)); 
        }
        maxAdj = maxAdj * Math.max(maxAdj, 5);
        reviewV = new int[maxAdj];
        reviewW = new int[maxAdj];
        nextV = new int[maxAdj];
        nextW = new int[maxAdj];
        
        adjMulti = new int[count][];
        for (int i = 0; i < limit; i++) {
            if (adjInfo[i] < 0 && adjInfo[i] > rootKey) {
                int key = 0 - adjInfo[i];
                adjMulti[key] = new int [G.outdegree(i)];
                int idx = 0;
                for (int vertex : G.adj(i)) {
                    adjMulti[key][idx++] = vertex;
                }
            }
        }
    }

    /**
     * length of shortest ancestral path between v and w; -1 if no such path.
     * 
     * @param v the integer of vertex
     * @param w the integer of vertex
     * @return length of shortest ancestral path between v and w; -1 if no such path
     */
    public int length(int v, int w) {
        search(v, w);
        return resLengthSingle;
    }
   
    /**
     * length of shortest ancestral path between any vertex in v and any vertex 
     * in w; -1 if no such path.
     * 
     * @param v the Iterable of integers of vertexes
     * @param w the Iterable of integers of vertexes
     * @return length of shortest ancestral path between any vertex in v and any vertex 
     * in w; -1 if no such path
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        search(v, w);
        return resLengthCombo;
    }

    /**
     * a common ancestor of v and w that participates in a shortest ancestral path; 
     * -1 if no such path.
     *  
     * @param v the integer of vertex
     * @param w the integer of vertex
     * @return a common ancestor of v and w that participates in a shortest ancestral path; 
     * -1 if no such path
     */
    public int ancestor(int v, int w) {
        search(v, w);
        return resAncestorSingle;
    }        

    /**
     * a common ancestor of v and w that participates in a shortest ancestral path; 
     * -1 if no such path.
     * 
     * @param v the Iterable of integers of vertexes
     * @param w the Iterable of integers of vertexes
     * @return a common ancestor of v and w that participates in a shortest ancestral path; 
     * -1 if no such path
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        search(v, w);
        return resAncestorCombo;
    }
    
    private void search(int v, int w) {
        if (v < 0 || v >= limit) {
            throw new IndexOutOfBoundsException();
        }
        if (w < 0 || w >= limit) {
            throw new IndexOutOfBoundsException();
        }
        if (lastSearchV == v && lastSearchW == w) {
            return;    
        }
        lastSearchV = v;
        lastSearchW = w;
        marker += 2;        
        int countV = 0;
        int countW = 0;
        
        if (v == w) {
            resLengthSingle = 0;
            resAncestorSingle = v;
            return;
        }  
        visitedV[v] = marker;
        distTo[v] = 0;
        reviewV[countV++] = v;
        visitedW[w] = marker;
        distTo[w] = 0;
        reviewW[countW++] = w;
        
        searchAll(countV, countW, false);
    }

    private void search(Iterable<Integer> vMulti, Iterable<Integer> wMulti) {
        for (int i : vMulti) {
            if (i < 0 || i >= limit) {
                throw new IndexOutOfBoundsException();
            }
        }
        for (int i : wMulti) {
            if (i < 0 || i >= limit) {
                throw new IndexOutOfBoundsException();
            }
        }
        String str = vMulti.toString() + "_" + wMulti.toString();
        if (lastSearchCombo.equals(str)) {
            return;    
        }
        lastSearchCombo = str;
        marker += 2;        
        int countV = 0;
        int countW = 0;
             
        for (int key : vMulti) {
            visitedV[key] = marker;
            distTo[key] = 0;
            reviewV[countV++] = key;
        }
        for (int key : wMulti) {
            if (visitedV[key] == marker) {
                resLengthCombo = 0;
                resAncestorCombo = key;
                return;
            }
            visitedW[key] = marker;
            distTo[key] = 0;
            reviewW[countW++] = key;
        }
        searchAll(countV, countW, true);
    }
  
    private void searchAll(int countV, int countW, boolean isMulti) {
        if (isMulti) {
            resLengthCombo = -1;
            resAncestorCombo = -1;
        } else {
            resLengthSingle = -1;
            resAncestorSingle = -1;
        }
        int min = INFINITY;
        int ancestor = -1;
        byte dist = 1;
        int sum;
        
        while (dist < min) {
            int countNext = 0;
            for (int i = 0; i < countV; i++) {
                int vertex = adjInfo[reviewV[i]];
                if (vertex > -1) {
                    if (visitedV[vertex] != marker) {
                        visitedV[vertex] = marker;
                        if (visitedW[vertex] == marker) {
                            sum = dist + distTo[vertex];
                            if (sum < min) {
                                min = sum;
                                ancestor = vertex;
                            }
                        }
                        distTo[vertex] = dist;
                        nextV[countNext++] = vertex;
                    }
                } else if (vertex > rootKey) {
                    for (int vertexMulti : adjMulti[0 - vertex]) {
                        if (visitedV[vertexMulti] != marker) {
                            visitedV[vertexMulti] = marker;
                            if (visitedW[vertexMulti] == marker) {
                                sum = dist + distTo[vertexMulti];
                                if (sum < min) {
                                    min = sum;
                                    ancestor = vertexMulti;
                                }
                            }
                            distTo[vertexMulti] = dist;
                            nextV[countNext++] = vertexMulti;
                        }
                    }
                }
            }
            
            countV = countNext;
            if (countV > 0) {
                countNext = 0;
                int[] temp = reviewV;
                reviewV = nextV;
                nextV = temp;
            }
            
            for (int i = 0; i < countW; i++) {
                int vertex = adjInfo[reviewW[i]];
                if (vertex > -1) {
                    if (visitedW[vertex] != marker) {
                        visitedW[vertex] = marker;
                        if (visitedV[vertex] == marker) {
                            sum = dist + distTo[vertex];
                            if (sum < min) {
                                min = sum;
                                ancestor = vertex;
                            }
                        }
                        distTo[vertex] = dist;
                        nextW[countNext++] = vertex;
                    }
                } else if (vertex > rootKey) {
                    for (int vertexMulti : adjMulti[0 - vertex]) {
                        if (visitedW[vertexMulti] != marker) {
                            visitedW[vertexMulti] = marker;
                            if (visitedV[vertexMulti] == marker) {
                                sum = dist + distTo[vertexMulti];
                                if (sum < min) {
                                    min = sum;
                                    ancestor = vertexMulti;
                                }
                            }
                            distTo[vertexMulti] = dist;
                            nextW[countNext++] = vertexMulti;
                        }
                    }
                }
            }
             
            countW = countNext;
            if (countW > 0) { 
                int[] temp = reviewW;
                reviewW = nextW;
                nextW = temp;
            }
            if (countV == 0 && countW == 0) {
                break;
            }
            // Notes: rotate 2 array storage is faster than reset/re-initiate the array.
            dist++;
        }
         
        if (min < INFINITY) {
            if (isMulti) {
                resLengthCombo = min;
                resAncestorCombo = ancestor;
            } else {
                resLengthSingle = min;
                resAncestorSingle = ancestor;
            }
        }
    }
}
