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

public class SeamCarver2Ddouble {
    private static final double BORDER = 1000.0;
    private int width, height;
    private int[][] picRGB; 
    private double[][] picEnergy;
    private int[] lastSeamH, lastSeamV;
    private boolean isTranspose, recallSeamH, recallSeamV;
    
    // Notes: functions in Picture.java use x(col), y(row) coordinates (int x, int y)
    //        All my private functions use standard order (int row, int col) 
    
    /**
     * Initializes a seam carver object based on the given picture.
     * 
     * @param picture Picture object
     */
    public SeamCarver2Ddouble(Picture picture) {
        Picture pic = new Picture(picture);
        width = pic.width();
        height = pic.height();
        picRGB = new int[height][width];
        picEnergy = new double[height][width];
        isTranspose = false;
        
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                picRGB[row][col] = picture.get(col, row).getRGB();
            }
        }
        
        for (int row = 1; row < height - 1; row++) {
            for (int col = 1; col < width - 1; col++) {
                picEnergy[row][col] = calculateEnergy(col, row);
            }
        }
    }
    
    // transpose the RGB pixel and energy with resize the storage
    private void transpose() {
        int[][] rgbTrans = new int[width][height];
        double[][] energyTrans = new double[width][height];

        int lastRow = height - 1;
        for (int col = 0; col < width; col++) {
            rgbTrans[col][0] = picRGB[0][col];
            rgbTrans[col][lastRow] = picRGB[lastRow][col];
        }
        for (int row = 1; row < lastRow; row++) {
            rgbTrans[0][row] = picRGB[row][0];
            for (int col = 1; col < width - 1; col++) {
                rgbTrans[col][row] = picRGB[row][col];
                energyTrans[col][row] = picEnergy[row][col];
            }
            rgbTrans[width-1][row] = picRGB[row][width-1];
        }
        
        picRGB = rgbTrans;
        picEnergy = energyTrans;
        isTranspose = !isTranspose;
        int temp = width;
        width = height;
        height = temp;
    }
    
    /**
     *  Returns the current picture.
     *  
     *  @return Picture object of the current picture
     */
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
        
    /**
     *  Returns the width of current picture.
     *  
     *  @return number of the width of current picture
     */
    public int width() {
        if (isTranspose) {
            return height;
        }
        return width;
    }
    
    /**
     *  Returns the height of current picture.
     *  
     *  @return number of the height of current picture
     */
    public int height() {
        if (isTranspose) {
            return width;
        }
        return height;
    }
    
    /**
     *  Returns the energy of pixel at column x and row y.
     *  
     *  @param x the column index of the picture
     *  @param y the row index of the picture
     *  @return double value of the energy of pixel at column x and row y
     *  @throws IndexOutOfBoundsException unless 0 <= x < width
     *  @throws IndexOutOfBoundsException unless 0 <= y < height 
     */
    public double energy(int x, int y) {
        if (isTranspose) {
            if (x < 0 || y < 0 || x > height - 1 || y > width - 1) {
                throw new IndexOutOfBoundsException();
            }
            if (x == 0 || x == height - 1 || y == 0 || y == width - 1) {
                return BORDER;
            }
            return picEnergy[x][y];
        }
        
        if (x < 0 || y < 0 || x > width - 1 || y > height - 1) {
            throw new IndexOutOfBoundsException();
        }
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
            return BORDER;
        }
        return picEnergy[y][x];
    }
    
    // return the energy value of the given RGB pixel position
    private double calculateEnergy(int col, int row) {
        if (row == 0 || row == height - 1 || col == 0 || col == width - 1) {
            return BORDER;
        }
        return Math.sqrt(calculateDelta(picRGB[row-1][col], picRGB[row+1][col])
                + calculateDelta(picRGB[row][col-1], picRGB[row][col+1]));
    }
    
    // return the delta value between two given RGB pixel
    private int calculateDelta(int rgb1, int rgb2) {
        int ired, igreen, iblue;
        ired = ((rgb1 & 0xff0000) >> 16) - ((rgb2 & 0xff0000) >> 16);
        igreen = ((rgb1 & 0xff00) >> 8) - ((rgb2 & 0xff00) >> 8);
        iblue = (rgb1 & 0xff) - (rgb2 & 0xff);
        return ired * ired + igreen * igreen + iblue * iblue;
    }
    
    /**
     *  Returns the sequence of indices for horizontal seam of current picture.
     *  
     *  @return the integer array of sequence of indices for horizontal seam
     */
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
    
    /**
     *  Returns the sequence of indices for vertical seam of current picture.
     *  
     *  @return the integer array of sequence of indices for vertical seam
     */
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
    
    // get the vertical seam from the local storage with current height and width
    private int[] getSeam() {
        int[] seam = new int[height];
        double val;
        if (width < 3 || height < 3) {
            Arrays.fill(seam, 0);
        } else if (height == 3) {
            seam[1] = 1;
            val = picEnergy[1][1];
            for (int col = 2; col < width - 1; col++) {
                double temp = picEnergy[1][col]; 
                if (temp < val) {
                    val = temp;
                    seam[1] = col;
                }
            }
            seam[0] = seam[1];
            seam[2] = seam[1];
        } else if (width == 3) {
            Arrays.fill(seam, 1);
        } else {
            int lastCol = width - 1;
            double[][] weight = new double[height][width];
            double max = BORDER;
            weight[1][0] = max;
            for (int col = 1; col < lastCol; col++) {
                weight[1][col] = picEnergy[1][col]; 
            }
            weight[1][lastCol] = max;
            
            max += BORDER;
            for (int row = 2; row < height - 2; row++) {
                int lastRow = row - 1;
                weight[row][0] = max;
                weight[row][1] = picEnergy[row][1] + Math.min(weight[lastRow][1], weight[lastRow][2]);
                for (int col = 2; col < lastCol; col++) {
                    weight[row][col] = picEnergy[row][col] + Math.min(weight[lastRow][col],
                            Math.min(weight[lastRow][col-1], weight[lastRow][col+1]));
                }
                weight[row][lastCol] = max;
                max += BORDER;
            }
            
            int row = height - 2;
            int lastRow = row - 1;
            seam[row] = 1;
            val = picEnergy[row][1] + Math.min(weight[lastRow][1], weight[lastRow][2]);          
            for (int col = 1; col < lastCol; col++) {
                double temp = picEnergy[row][col] + Math.min(weight[lastRow][col],
                        Math.min(weight[lastRow][col-1], weight[lastRow][col+1]));
                if (temp < val) {
                    val = temp;
                    seam[row] = col;
                }
            }
            seam[row+1] = seam[row];
            
            while (row > 0) {
                lastRow = row - 1;
                int currCol = seam[row--];
                val = weight[row][currCol];
                seam[row] = currCol;
                double temp = weight[row][currCol-1]; 
                if (temp < val) {
                    val = temp;
                    seam[row]--;
                }
                if (weight[row][currCol+1] < val) {
                    seam[row] = currCol + 1;
                }
            }
            seam[0] = seam[1];
        }
        return seam;
    }
    
    /**
     *  Remove horizontal seam from the current picture.
     *  
     *  @param seam the integer array of give seam
     *  @throws IllegalArgumentException if the length of
     *  seam is not equal the width of current picture 
     *  @throws IllegalArgumentException if the seam is out of range
     *  @throws IllegalArgumentException unless is a connected seam
     */
    public void removeHorizontalSeam(int[] seam) {
        if (!isTranspose) {
            transpose();
        }
        if (seam.length != height) {
            throw new IllegalArgumentException();
        }
        validateSeam(seam);
        removeSeam(seam);
    }
    
    /**
     *  Remove vertical seam from the current picture.
     *  
     *  @param seam the integer array of give seam
     *  @throws IllegalArgumentException if the length of
     *  seam is not equal the height of current picture 
     *  @throws IllegalArgumentException if the seam is out of range
     *  @throws IllegalArgumentException unless is a connected seam
     */
    public void removeVerticalSeam(int[] seam) {
        if (isTranspose) {
            transpose();
        }
        if (seam.length != height) {
            throw new IllegalArgumentException();
        }
        validateSeam(seam);
        removeSeam(seam);
    }

    // validate the given seam is an in range connected seam
    private void validateSeam(int[] seam) {
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
    
    // eliminate the given seam, by shift the RGB pixel and energy value
    // in storage, update the energy value if needed, 
    // without resize the storage until next transpose() called
    private void removeSeam(int[] seam) {
        width--;
        for (int row = 0; row < height; row++)  {
            System.arraycopy(picRGB[row], seam[row]+1, picRGB[row], seam[row], width-seam[row]);
        }
        
        for (int row = 1; row < height - 1; row++) {
            int col = seam[row];
            if (col > 1) {
                picEnergy[row][col-1] = calculateEnergy(col-1, row);
            }
            if (col < width - 1) {
                picEnergy[row][col] = calculateEnergy(col, row);
                int copyLen = width - seam[row] - 1;
                if (copyLen > 0) {
                    System.arraycopy(picEnergy[row], seam[row]+2, picEnergy[row], seam[row]+1, copyLen);
                }
            }
        }
        recallSeamH = false;
        recallSeamV = false;         
    }
}
