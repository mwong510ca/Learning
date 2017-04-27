package mwong.algs4.solver8puzzle;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation: java Board.java
 *
 *  A data type for 8Puzzle boards (for any 2 ≤ N < 128)
 *
 ****************************************************************************/

public class Board {
    private byte N;
    private int[] tiles;
    private byte zeroX, zeroY;
    private int hamming, manhattan;
    private boolean isGoal;
    
    /**
     * Initializes a N-by-N board, where blocks[i][j] = block in row i, column j.
     * 
     * @param blocks 2 dimension array of tiles
     */
    public Board(int[][] blocks) {
        N = (byte) blocks.length;
        tiles = new int[N * N];
        
        for (int row = 0; row < N; row++) {
            System.arraycopy(blocks[row], 0, tiles, row * N, N);
        }
        setPriorities();
    }
    
    // use by twin()
    // Initializes a board, tiles list in a 1d array
    private Board(int[] blocks, byte N) {
        this.N = N;
        tiles = new int[blocks.length];
        System.arraycopy(blocks, 0, tiles, 0, blocks.length);
        setPriorities();
    }
    
    // use by neighbors()
    // Initializes a board, tiles list in a 1d array with properties
    private Board(int[] blocks, byte N, int h, int m, int x, int y) {
        this.N = N;
        tiles = new int[blocks.length];
        System.arraycopy(blocks, 0, tiles, 0, blocks.length);
        zeroX = (byte) x;
        zeroY = (byte) y;
        hamming = h;
        manhattan  = m;
        if (hamming == 0) {
            isGoal = true;
        } else {
            isGoal = false;
        }
    }
    
    // loop over the board and cache hamming, manhattan and isGoal properties
    private void setPriorities() {
        hamming = 0;
        manhattan = 0;
        int pos = 1;
        int value;
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                value = tiles[xyTo1D(row, col)];
                if (value != 0) {
                    manhattan += Math.abs((value-1) % N - col);
                    manhattan += Math.abs((((value-1) - (value-1) % N)/N) - row);
                    if (value != pos) {
                        hamming++;
                    }
                } else {
                    zeroX = (byte) col;
                    zeroY = (byte) row;
                }
                pos++;
            }
        }
        isGoal = true;
        if (zeroX != N-1 && zeroY != N-1) {
            isGoal = false;
        }
        if (isGoal) {
            for (int i = 0; i < N * N - 1 && isGoal; i++) {
                if (tiles[i] != i + 1) {
                    isGoal = false;
                }
            }
        }
    }
    
    // convert row and column position to tile index
    private int xyTo1D(int row, int col) {
        return row * N + col;
    }
    
    /**
     * Returns the number of board dimension N.
     * 
     * @return number of board dimension N
     */
    public int dimension() {
        return N;
    }
    
    /**
     * Returns the number of blocks out of place.
     * 
     * @return number of blocks out of place
     */
    public int hamming() {
        return hamming;
    }
    
    /**
     * Returns the sum of Manhattan distances between blocks and goal.
     * 
     * @return sum of Manhattan distances between blocks and goal
     */
    public int manhattan() {
        return manhattan;
    }
    
    /**
     * Returns the boolean represent this board is the goal board.
     * 
     * @return boolean represent this board is the goal board
     */
    public boolean isGoal() {
        return isGoal;
    }
    
    /**
     * Returns a board obtained by exchanging two adjacent blocks in the same row.
     * 
     * @return board obtained by exchanging two adjacent blocks in the same row
     */
    public Board twin() {
        int temp, row, pos1 = 0, pos2 = 0;
        Board twin;
        boolean generateTwin = true;
        
        // determine the first block to switch/exchange
        // if twin board is the goal board, make it become the goal board
        if (zeroY == N-1 && zeroX == N-1 && hamming == 2 && manhattan == 2) {
            if (tiles[xyTo1D((N-2), (N-2))] != (N-1)*(N-2)-1
                && tiles[xyTo1D((N-2), (N-1))] != (N-1)*(N-2)) {
                pos1 = xyTo1D((N-2), (N-2));
                generateTwin = false;
            } else if (tiles[xyTo1D((N-1), (N-3))] != N*N-2
                     && tiles[xyTo1D((N-1), (N-2))] != N*N-1) {
                pos1 = xyTo1D((N-1), (N-3));
                generateTwin = false;
            }
        }
        // otherwise, exchange two blocks above or below the zero block
        if (generateTwin) {
            if (zeroY == N-1) {
                row = (byte) (N - 2);
            } else {
                row = (byte) (zeroY + 1);
            }
            pos1 = xyTo1D(row, 0);
        }
        
        // after determine the first block to switch, exchange the block on right
        pos2 = pos1 + 1;
        temp = tiles[pos1];
        tiles[pos1] = tiles[pos2];
        tiles[pos2] = temp;
        twin = new Board(tiles, N);
        tiles[pos2] = tiles[pos1];
        tiles[pos1] = temp;
        return twin;
    }
    
    /**
     * Returns the boolean represent this board equal y.
     * 
     * @param y the other board
     * @return boolean represent this board equal y
     */
    public boolean equals(Object y) {
        if (y == null) {
            return false;
        }
        if (this.getClass() != y.getClass()) {
            return false;
        }
        Board that = (Board) y;
        if (this.N != that.N) {
            return false;
        }
        if (this.hamming == that.hamming && this.manhattan == that.manhattan) {
            for (int i = 0; i < N*N; i++) {
                if (this.tiles[i] != that.tiles[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    /**
     * Returns an independent iterator over all neighboring boards.
     * 
     * @return independent iterator over all neighboring boards
     */
    public Iterable<Board> neighbors() {
        Deque<Board> allNeighbors = new Deque<Board>();
        // move right
        if (zeroX != N-1) {
            allNeighbors.addFirst(createNeighbor(true, 1));
        }
        // move down
        if (zeroY != N-1) {
            allNeighbors.addFirst(createNeighbor(false, 1));
        }
        // move left
        if (zeroX != 0) {
            allNeighbors.addFirst(createNeighbor(true, -1));
        }
        // move up
        if (zeroY != 0) {
            allNeighbors.addFirst(createNeighbor(false, -1));
        }
        return allNeighbors;
    }
    
    // create a neighbor board with preset all properties
    private Board createNeighbor(boolean horizontal, int diff)  {
        // get the position of the neighbor tile
        int pos0 = xyTo1D(zeroY, zeroX);
        int pos = pos0;
        if (horizontal) {
            pos = pos0 + diff;
        } else {
            pos = pos0 + N * diff;
        }
        int value = tiles[pos];
        int valuePos = value - 1;
        
        // update hamming value
        int h = hamming;
        if (valuePos == pos0) {
            h -= 1;
        } else if (valuePos == pos) {
            h += 1;
        }
        
        // update manhattan value
        int m = manhattan;
        
        if (horizontal) {    // same row
            int key = valuePos % N;
            if ((diff < 0 && key < zeroX) || (diff > 0 && key > zeroX)) {
                m++;
            } else {
                m--;
            } 
        } else {    // same col
            int key = valuePos / N;
            if ((diff < 0 && key < zeroY) || (diff > 0 && key > zeroY)) {
                m++;
            } else {
                m--;
            }
        }
        
        // exchange the neighbor block with zero block, create the new board
        // restore it back in original position
        tiles[pos0] = value;
        tiles[pos] = 0;
        Board neighbor;
        if (horizontal) {
            neighbor = new Board(tiles, N, h, m, zeroX + diff, zeroY);
        } else {
            neighbor = new Board(tiles, N, h, m, zeroX, zeroY + diff);
        }
        tiles[pos0] = 0;
        tiles[pos] = value;
        return neighbor;
    }
    
    /**
     * Returns a string representation of the board, N rows with N numbers.
     * 
     * @return a string representation of the board, N rows with N numbers
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(N + "\n");
        for (int i = 0; i < N * N; i++) {
            s.append(String.format("%2d ", tiles[i]));
            if (i % N == (N - 1)) {
                s.append("\n");
            }
        }
        return s.toString();
    }
}
