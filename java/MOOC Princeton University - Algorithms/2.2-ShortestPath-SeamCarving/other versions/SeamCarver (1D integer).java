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

public class SeamCarver1Dint {
    private static final double BORDER = 1000;
    private static final int MULTIPER = 1000000;
    private static final int BORDER_WEIGHT = (int) (BORDER * MULTIPER);
    private int width, height, picWidth;
    private int[] picRGB, lastSeamH, lastSeamV;
    private int[] picEnergy;
    private boolean isTranspose, recallSeamH, recallSeamV;
    
    // Notes: functions in Picture.java use x(col), y(row) coordinates (int x, int y)
    //        All my private functions use standard order (int row, int col) 
    
    /**
     * Initializes a seam carver object based on the given picture.
     * 
     * @param picture Picture object
     */
    public SeamCarver1Dint(Picture picture) {
        // store the RGB pixel and energy value in local storage
        Picture pic = new Picture(picture);
        width = pic.width();
        height = pic.height();
        picWidth = width;
        int size = width * height;
        picRGB = new int[size];
        picEnergy = new int[size];
        isTranspose = false;
        
        int idx = 0;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                picRGB[idx++] = picture.get(col, row).getRGB();
            }
        }
        
        idx = width + 1;
        for (int row = 1; row < height - 1; row++) {
            for (int col = 1; col < width - 1; col++) {
                picEnergy[idx] = (int) (calculateEnergy(idx++) * MULTIPER);
            }
            idx += 2;
        }
    }
    
    // transpose the RGB pixel and energy with resize the storage
    private void transpose() {
        int size = width * height;
        int[] rgbTrans = new int[size];
        int[] energyTrans = new int[size];
        
        for (int row = 0; row < height; row++) {
            int idxTrans = row;
            rgbTrans[idxTrans] = picRGB[xyTo1d(row, 0)];
            if (width == 1) {
                continue;
            }
            for (int col = 1; col < width - 1; col++) {
                idxTrans += height;
                int idx = xyTo1d(row, col);
                rgbTrans[idxTrans] = picRGB[idx];
                energyTrans[idxTrans] = picEnergy[idx];
            }
            idxTrans += height;
            rgbTrans[idxTrans] = picRGB[xyTo1d(row, width - 1)];
        }
        picRGB = rgbTrans;
        picEnergy = energyTrans;
        isTranspose = !isTranspose;
        int temp = width;
        width = height;
        height = temp;
        picWidth = width;
        rgbTrans = null;
        energyTrans = null;
    }
    
    /**
     *  Returns the current picture.
     *  
     *  @return Picture object of the current picture
     */
    public Picture picture() {
        // create a new Picture and copy every RGB pixel, then return
        Picture resumePic;
        if (isTranspose) {
            resumePic = new Picture(height, width);
            for (int row = 0; row < width(); row++) {
                for (int col = 0; col < height(); col++) {
                    resumePic.set(row, col, new Color(picRGB[xyTo1d(row, col)]));
                }
            }
        } else {         
            resumePic = new Picture(width, height);
            for (int row = 0; row < height(); row++) {
                for (int col = 0; col < width(); col++) {
                    resumePic.set(col, row, new Color(picRGB[xyTo1d(row, col)]));
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
            return calculateEnergy(xyTo1d(x, y));
        }
        if (x < 0 || y < 0 || x > width - 1 || y > height - 1) {
            throw new IndexOutOfBoundsException();
        }
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
            return BORDER;
        }       
        return calculateEnergy(xyTo1d(y, x));
    }
    
    // convert row, column to actual position in RGB pixel or energy storage
    private int xyTo1d(int row, int col) {
        return row * picWidth + col;
    }
    
    // return the energy value of the given RGB pixel position
    private double calculateEnergy(int idx) {
        return Math.sqrt(calculateDelta(picRGB[idx-picWidth], picRGB[idx+picWidth])
        + calculateDelta(picRGB[idx-1], picRGB[idx+1]));
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
        // transpose the original position and find the vertical seam 
        // as the horizontal seam of the original picture
        if (!recallSeamH) {
            if (!isTranspose) {
                transpose();
            }
            lastSeamH = getVerticalSeam();
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
        // restore the storage in original position and find the vertical seam
        if (!recallSeamV) {
            if (isTranspose) {
                transpose();
            }
            lastSeamV = getVerticalSeam();
            recallSeamV = true;
        }
        return lastSeamV.clone();
    }
    
    // get the vertical seam from the local storage with current height and width
    private int[] getVerticalSeam() {
        int[] seam = new int[height];
        // get the seam width <= 3 or height <= 3
        if (width < 3 || height < 3) {
            Arrays.fill(seam, 0);
        } else if (height == 3) {
            seam[1] = 1;
            int picIdx = picWidth + 1;
            long val = picEnergy[picIdx++];
            for (int col = 2; col < width - 1; col++) {
                long temp = picEnergy[picIdx++]; 
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
            // calculate the top-down, left to right weight table, and find 
            // vertical seam
            // initial and calculate the weight table from row 2 to height - 2
            int lastCol = width - 1;
            long[] weight = new long[(height - 3) * (width - 1) + 1];
            long max = BORDER_WEIGHT;
            int idx = 0;
            weight[idx++] = max;
            for (int col = picWidth + 1; col < picWidth + lastCol; col++) {
                weight[idx++] = picEnergy[col]; 
            }
            
            max += BORDER_WEIGHT;
            for (int row = 2; row < height - 2; row++) {
                weight[idx++] = max;
                int wtIdx = idx - width + 1;
                int picIdx = xyTo1d(row, 1);
                weight[idx++] = picEnergy[picIdx++] + Math.min(weight[wtIdx++], weight[wtIdx]);
                for (int col = 2; col < lastCol; col++) {
                    weight[idx++] = picEnergy[picIdx++] + Math.min(weight[wtIdx - 1],
                            Math.min(weight[wtIdx++], weight[wtIdx]));
                }
                max += BORDER_WEIGHT;
            }
            weight[idx] = max;
        
            // calculate the weight for row height - 1, and find the miniimum value of the row
            int row = height - 2;
            int wtIdx = idx - width + 2;
            seam[row] = 1;
            int picIdx = xyTo1d(row, 1);
            long val = picEnergy[picIdx++] + Math.min(weight[wtIdx++], weight[wtIdx]); 
            seam[row] = 1;
            for (int col = 2; col < lastCol; col++) {
                long temp = picEnergy[picIdx++] + Math.min(weight[wtIdx - 1],
                        Math.min(weight[wtIdx++], weight[wtIdx]));
                if (temp < val) {
                    val = temp;
                    seam[row] = col;
                }
            }
            
            // walk through the weight table backward to find the seam
            seam[row+1] = seam[row];
            int wtBase = wtIdx;
            while (row > 1) {
                int currCol = seam[row--];
                wtBase -= width - 1;
                int wtPos = wtBase + currCol;
                val = weight[wtPos];
                seam[row] = currCol;
                if (weight[wtPos - 1] < val) {
                    val = weight[wtPos - 1];
                    seam[row]--;
                }
                if (weight[wtPos + 1] < val) {
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
        // transpose the original position and eliminate the vertical seam 
        // as the horizontal seam of the original picture
        if (!isTranspose) {
            transpose();
        }
        if (seam.length != height) {
            throw new IllegalArgumentException();
        }
        eliminateVerticalSeam(seam);
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
        // restore the storage in original position and eliminate the vertical seam
        if (isTranspose) {
            transpose();
        }
        if (seam.length != height) {
            throw new IllegalArgumentException();
        }
        eliminateVerticalSeam(seam);
    }
    
    // validate the given seam is an in range connected seam
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
    
    // eliminate the given seam, by shift the RGB pixel and energy value
    // in storage, update the energy value if needed, 
    // without resize the storage until next transpose() called
    private void eliminateVerticalSeam(int[] seam) {
        validateSeam(seam);
        width--;        
        for (int row = 0; row < height; row++) {
            int rowidx = row * picWidth;
            System.arraycopy(picRGB, rowidx + seam[row]+1, picRGB, 
                    rowidx + seam[row], width-seam[row]);
        }
        
        for (int row = 1; row < height - 1; row++) {
            int col = seam[row];
            if (col > 1) {
                picEnergy[row*picWidth + col-1] = (int) (calculateEnergy(xyTo1d(row, col - 1)) * MULTIPER);
            }
            if (col < width - 1) {
                picEnergy[row* picWidth + col] = (int) (calculateEnergy(xyTo1d(row, col)) * MULTIPER);
                int copyLen = width - seam[row] - 1;
                if (copyLen > 0) {
                    System.arraycopy(picEnergy, xyTo1d(row, seam[row] + 2), 
                            picEnergy, xyTo1d(row, seam[row] + 1), copyLen);
                }
            }
        }
        recallSeamH = false;
        recallSeamV = false;     
    }
}
