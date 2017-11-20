package com.striketec.mobile.util.sensormanager.mmaGlove;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.FontManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton utility class to read and get values from forceTable.csv file.
 *
 * @author Created by #1053
 */
public class ForceTableTemplate {

    private static final String TAG = "ForceTableTemplate";
    private static final boolean DEBUG = false;

    private static ForceTableTemplate forceTableTemplate;

    private int[][] forceTable;

    private int rowStep;
    private int colStep;

    private ForceTableTemplate() {
        super();

        // read the force table csv values & store it in forceTable array
        readForceTableCSV();

        // calculate the row step from the obtained values
        calculateRowStep();
        // calculate the column step from the obtained values
        calculateColumnStep();
    }

    /**
     * @return create & or return the already existing instance of this class
     */
    public static synchronized ForceTableTemplate getInstance() {
        if (forceTableTemplate == null) {
            forceTableTemplate = new ForceTableTemplate();
        }
        return forceTableTemplate;
    }

    /**
     * Actually performs the read operation & stores the values in an array
     *
     * @throws IOException
     */
    private final void readForceTableCSV() {
        try {

            // Read from the /assets directory
            InputStream inputStream = FontManager.assetManager.open(AppConst.FORCE_TABLE_TEMPLATE_FILEPATH);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // ArrayList of array of int's to store rows of integers read from file
            List<int[]> tmpList = new ArrayList<int[]>();

            String line = null;
            Log.d(TAG, "Start Reading file: " + AppConst.FORCE_TABLE_TEMPLATE_FILEPATH);
            while ((line = bufferedReader.readLine()) != null) {    // perform line read & check if its not null
                tmpList.add(splitStringIntoArray(line));
            }
            Log.d(TAG, "-Reading file complete-");

            // initialize the forceTable with the tmpList size
            forceTable = new int[tmpList.size()][];

            // store the ArrayList in forceTable array
            toForceTableArray(tmpList);

        } catch (FileNotFoundException fnfe) {
            Log.d(TAG, '"' + AppConst.FORCE_TABLE_TEMPLATE_FILEPATH + "\" File Not Found Exception:-");
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            Log.d(TAG, "I/O Exception:-");
            ioe.printStackTrace();
        } catch (Exception e) {
            Log.d(TAG, "Some Other Exception:-");
            e.printStackTrace();
        }

        // print the array to logger
        if (DEBUG) printArray();
    }

    /**
     * Splits the String line into a String array. Convert & store the elements into integer array.
     *
     * @param line single string line containing csv integers
     * @return int[] array of integers from String line
     */
    private int[] splitStringIntoArray(String line) {
        String[] arr = line.split(",");
        int[] temp = new int[arr.length];
        String t;
        for (int i = 0; i < arr.length; i++) {
            t = (null != arr[i]) ? arr[i].trim() : "0";
            // if there is no data or null value read from file, in that case set it to zero
            temp[i] = Integer.parseInt((t.length() == 0) ? "0" : t);
        }
        return temp;
    }

    /**
     * Store the elements of List to forceTable array
     *
     * @param tmp List<int[]> (ArrayList of int[]) read from the csv file.
     */
    private void toForceTableArray(List<int[]> tmp) {
        for (int i = 0; i < tmp.size(); i++) {
            forceTable[i] = tmp.get(i);
        }
    }

    /**
     * Calculate row step by subtracting value of 2nd element of row from the 1st element.
     */
    private void calculateRowStep() {
        rowStep = getValueFromForceTable(2, 0) - getRowMinValue();
    }

    /**
     * Calculate column step by subtracting value of 2nd element of column from the 1st element.
     */
    private void calculateColumnStep() {
        colStep = getValueFromForceTable(0, 2) - getColumnMinValue();
    }

    /**
     * Reads and returns the value from the array table.
     * If the specified row/column no. exceeds that in the table, then it will consider the maximum row/column respectively.
     *
     * @param rowIdx Row no. of the table
     * @param colIdx Column no. of the table
     * @return the value that lies on the respective row & column.
     */
    private int getValueFromForceTable(int rowIdx, int colIdx) {
        int rowLen = forceTable.length;
        // calculate this separately as its also being used for calculating colIdx
        rowIdx = (rowIdx >= rowLen) ? rowLen - 1 : ((rowIdx < 0) ? 0 : rowIdx);
        int colLen = forceTable[rowIdx].length;
        return forceTable[rowIdx][(colIdx >= colLen) ? colLen - 1 : ((colIdx < 0) ? 0 : colIdx)];
    }

    /**
     * Prints the forceTable array to the standard logger.
     */
    private void printArray() {
        Log.d(TAG, "-Printing Values read from force table csv:-");
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < forceTable.length; i++) {
            str.append('\n');
            for (int j = 0; j < forceTable[i].length; j++) {
                str.append(forceTable[i][j]).append('\t');
            }
        }
        Log.d(TAG, str.toString());
    }

    /**
     * Get the difference between two values of a row
     *
     * @return the difference between two values of a row
     */
    public int getRowStep() {
        return rowStep;
    }

    /**
     * Get the difference between two values of a columns
     *
     * @return the difference between two values of a columns
     */
    public int getColumnStep() {
        return colStep;
    }

    /**
     * Get the minimum value of the first row
     *
     * @return the minimum value of the first row
     */
    public int getRowMinValue() {
        return getValueFromForceTable(1, 0);
    }

    /**
     * Get the minimum value of the first column
     *
     * @return the minimum value of the first column
     */
    public int getColumnMinValue() {
        return getValueFromForceTable(0, 1);
    }

    /**
     * Returns the value that lies on the respective row & column. If the specified row/column no. exceeds that in the table, then it will consider the maximum row/column respectively.
     *
     * @param rowIdx row index from which to get the value
     * @param colIdx column index from which to get the value
     * @return Returns the value that lies on the respective row & column.
     */
    public int getForceValue(int rowIdx, int colIdx) {
        return getValueFromForceTable(rowIdx, colIdx);
    }
}
