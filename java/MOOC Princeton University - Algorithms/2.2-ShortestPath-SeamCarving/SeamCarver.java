package mwong.algs4.seamcarver;

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.Arrays;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation: javac SeamCarver.java
 *  Dependencies (algs4.jar): BinaryStdIn.java, BinaryStdOut.java
 *
 *  A mutable data type SeamCarver
 *
 ****************************************************************************/

public class SeamCarver {
    private static final int BORDER = 1000, MULTIPER = 1000000;
    private int width, height;
    private int[][] picRGB; 
    private int[][] picEnergy;
    private int[] lastSeamH, lastSeamV;
    private boolean isTranspose, recallSeamH, recallSeamV;
    
    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture)                
    {
        /*
         Notes:  set and get functions in Picture.java
         public Color get(int col, int row)
         public void set(int col, int row, Color color)
         All function parameters in this project use (int col, int row)
         instead of (int row, int col) is other projects.
         Be careful to reverse x, y indexes when call these functions.
         
         For this program, 2d array is faster than 1d array.
         */
        Picture pic = new Picture(picture);
        width = pic.width();
        height = pic.height();
        picRGB = new int[height][width];
        picEnergy = new int[height][width];
        isTranspose = false;
        
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                picRGB[row][col] = picture.get(col, row).getRGB();
            }
        }
       
        int lastRow = height - 1;
        int lastCol = width - 1;
        if (height > 1) {
            for (int row = 1; row < lastRow; row++) {
                 for (int col = 1; col < lastCol; col++) {
                    picEnergy[row][col] = (int) (getEnergy(row, col) * MULTIPER);
                }
            }
        }
    }
    
    private void transpose() {
        int[][] rgbTrans = new int[width][height];
        int[][] energyTrans = new int[width][height];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                rgbTrans[col][row] = picRGB[row][col];
                energyTrans[col][row] = picEnergy[row][col];
            }
        }        
        
        picRGB = rgbTrans;
        picEnergy = energyTrans;
        isTranspose = !isTranspose;
        int temp = width;
        width = height;
        height = temp;
    }
    
    // current picture
    public Picture picture() {
        Picture resumePic;
        if (isTranspose) {
            resumePic = new Picture(height, width);
            for (int row = 0; row < width(); row++) {
                for (int col = 0; col < height(); col++) {
                    resumePic.set(row, col, new Color(picRGB[row][col]));
                }
            }
        } else {         
            resumePic = new Picture(width, height);
            for (int row = 0; row < height(); row++) {
                for (int col = 0; col < width(); col++) {
                    resumePic.set(col, row, new Color(picRGB[row][col]));
                }
            }
        }
        return resumePic;
    }
        
    // width of current picture
    public int width() {
        if (isTranspose) {
            return height;
        }
        return width;
    }
    
    // height of current picture
    public int height() {
        if (isTranspose) {
            return width;
        }
        return height;
    }
    
    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (isTranspose) {
            if (x < 0 || y < 0 || x > height - 1 || y > width - 1) {
                throw new IndexOutOfBoundsException();
            }
            if (x == 0 || x == height - 1 || y == 0 || y == width - 1) {
                return BORDER;
            }
            return getEnergy(x, y);
        }
        
        if (x < 0 || y < 0 || x > width - 1 || y > height - 1) {
            throw new IndexOutOfBoundsException();
        }
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
            return BORDER;
        }
        return getEnergy(y, x);
    }
    
    private double getEnergy(int row, int col) {
        return Math.sqrt(calculateDelta(picRGB[row-1][col], picRGB[row+1][col])
                + calculateDelta(picRGB[row][col-1], picRGB[row][col+1]));
    }
    
    private int calculateDelta(int rgb1, int rgb2) {
        int ired, igreen, iblue;
        ired = ((rgb1 & 0xff0000) >> 16) - ((rgb2 & 0xff0000) >> 16);
        igreen = ((rgb1 & 0xff00) >> 8) - ((rgb2 & 0xff00) >> 8);
        iblue = (rgb1 & 0xff) - (rgb2 & 0xff);
        return ired * ired + igreen * igreen + iblue * iblue;
    }
    
    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (width < 0 || height < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (!recallSeamH) {
            if (!isTranspose) {
                transpose();
            }
            lastSeamH = getSeam();
            recallSeamH = true;
        }
        return lastSeamH.clone();
    }
    
    // sequence of indices for vertical seam
    public int[] findVerticalSeam()  {
        if (width < 0 || height < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (!recallSeamV) {
            if (isTranspose) {
                transpose();
            }
            lastSeamV = getSeam();
            recallSeamV = true;
        }
        return lastSeamV.clone();
    }
    
    private int[] getSeam() {
        int[] seam = new int[height];
        if (width < 3 || height < 3) {
            Arrays.fill(seam, width - 1);
            return seam;
        } 
        
        if (height == 3) {
            seam[1] = 1;
            int min = picEnergy[1][1];
            for (int col = 2; col < width - 1; col++) {
                if (picEnergy[1][col] < min) {
                    min = picEnergy[1][col];
                    seam[1] = col;
                }
            }
            seam[0] = seam[1];
            seam[2] = seam[1];
            return seam;
        } 
        
        if (width == 3) {
            Arrays.fill(seam, 1);
            return seam;
        } 

        long[] distToParent = new long[width];
        long[] distToChild = new long[width];
        byte[][] paths = new byte[height - 3][width - 1];    
        
        int lastRow = height - 2;
        int lastCol = width - 2;
        int row = 1;
        int pathRow = -1;
        for (int col = 1; col <= lastCol; col++) {
            distToChild[col] = picEnergy[row][col];
        }
        
        while (row < lastRow) {
            long[] swap = distToParent;
            distToParent = distToChild;
            distToChild = swap;
            pathRow++;
            row++;
            
            int col = 1;
            long nbrVal = distToParent[col];
            long minVal = nbrVal;
            byte path = 0;
            
            while (col < lastCol) {
                long nbrValRight = distToParent[col + 1];
                if (nbrValRight < minVal) {
                    minVal = nbrValRight;
                    path = 1;
                }
                distToChild[col] = minVal + picEnergy[row][col];
                paths[pathRow][col++] = path;
                
                if (nbrValRight < nbrVal) {
                    minVal = nbrValRight;
                    nbrVal = nbrValRight;
                    path = 0;
                } else {           
                    minVal = nbrVal;
                    nbrVal = nbrValRight;
                    path = -1;
                }
            }            
            distToChild[lastCol] = minVal + picEnergy[row][lastCol];
            paths[pathRow][lastCol] = path;                         
        }
        
        int seamCol = 1;
        long minVal = distToChild[1];
        for (int col = 2; col <= lastCol; col++) {
            if (distToChild[col] < minVal) {
                minVal = distToChild[col];
                seamCol = col;
            }
        }
        
        seam[row + 1] = seamCol;
        seam[row--] = seamCol;
        while (row > 0) {
            seamCol += paths[pathRow--][seamCol]; 
            seam[row--] = seamCol;
        }
        seam[0] = seam[1];
        return seam;
    }
    
    // remove horizontal seam from picture
    public void removeHorizontalSeam(int[] seam) {
        if (!isTranspose) {
            transpose();
        }
        removeSeam(seam);
    }
    
    // remove vertical seam from picture
    public void removeVerticalSeam(int[] seam) {
        if (isTranspose) {
            transpose();
        }
        removeSeam(seam);
    }

    private void validateSeam(int[] seam) {
        if (seam.length != height) {
            throw new IllegalArgumentException();
        }
        for (int col : seam) {
            if (col < 0 || col >= width) {
                throw new IllegalArgumentException();
            }
        }
        for (int i = 1; i < height; i++) {
            int diff = seam[i - 1] - seam[i];
            if (diff < -1 || diff > 1) {
                throw new IllegalArgumentException();
            }
        }
    }
    
    private void removeSeam(int[] seam) {
        validateSeam(seam);
        
        width--;
        for (int row = 0; row < height; row++) {
            if (seam[row] == width) {
                continue;
            }
            System.arraycopy(picRGB[row], seam[row] + 1, picRGB[row], seam[row], width - seam[row]);
        }
        
        for (int row = 1; row < height - 1; row++) {
            if (seam[row] == width) {
                continue;
            }
            
            int col = seam[row];
            if (col > 1) {
                picEnergy[row][col - 1] = (int) (getEnergy(row, col - 1) * MULTIPER);
            }
            if (col < width - 1) {
                if (col > 0) {
                    picEnergy[row][col] = (int) (getEnergy(row, col) * MULTIPER);
                }
                int copyLen = width - col - 1;
                if (copyLen > 0) {
                    System.arraycopy(picEnergy[row], col + 2, picEnergy[row], col + 1, copyLen);
                }
            }
        }
        recallSeamH = false;
        recallSeamV = false;         
    }
} 
