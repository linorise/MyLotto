package com.rayolla.mylotto;

import android.util.Log;

// This class includes the weight info about the only 6 numbers
public class WeightInfo {
    private static final String TAG = "MyLotto_WeightInfo";
    private static final int BUFFER_SIZE = 6;
    private static int[] mNumberList = null;
    private static int[] mNumberListWeight = null;

    private static int[] mTotalWeightTable = null;

    public static void init(String list) {
        if (mNumberList == null) {
            mNumberList = new int[BUFFER_SIZE];
        }

        if (mNumberListWeight == null) {
            mNumberListWeight = new int[BUFFER_SIZE];
        }

        int n = 0;
        String[] numbers = list.split(",");
        for (String number : numbers) {
            int num = Integer.parseInt(number);

            Log.d(TAG, "num: " + num);
            mNumberList[n++] = num;
        }
    }

    public static int getNumberWeight(int element) {
        try {
            int weight = mNumberListWeight[element];
            Log.d(TAG, String.format("%d weight: %d", element, weight));
            return weight;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static int getNumber(int element) {
        try {
            int num = mNumberList[element];
            Log.d(TAG, String.format("num(%d): %d", element, num));
            return num;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // Set weight value to focused(or selected) number list
    public static void setTotalWeightTable(int[] totWeight) {
        mTotalWeightTable = totWeight;
        Log.d(TAG, "Set total weight table");

        for (int i=0; i<mNumberList.length; i++) {
            int weight = mTotalWeightTable[mNumberList[i]-1];

            Log.d(TAG, "Weight: " + weight);
            mNumberListWeight[i] = weight;
        }
    }

    // Get weight from the total number table
    public static int getWeightFromTotalTable(int element) {
        int weight = 0;

        try {
            weight = mTotalWeightTable[element];
        }
        catch (NullPointerException e) {e.printStackTrace();}

        return weight;
    }

    // Print the weight of total table
    public static void printTotalWeightTable() {
        Log.d(TAG, "Print total weight table");
        for (int i = 0; i<mTotalWeightTable.length; i++) {

            try {
                Log.d(TAG, String.format("#%d: %d", i, mTotalWeightTable[i]));

            }
            catch (NullPointerException e) {e.printStackTrace();}
        }
    }

    public static int getListWeightSum() {
        int sum = 0;

        for (int i=0; i < mNumberListWeight.length; i++) {
            sum += mNumberListWeight[i];
        }

        return sum;
    }
}
