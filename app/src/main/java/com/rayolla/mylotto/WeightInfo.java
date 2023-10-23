package com.rayolla.mylotto;

import android.util.Log;

public class WeightInfo {
    private static final String TAG = "MyLotto_WeightInfo";
    private static final int BUFFER_SIZE = 6;
    private static int[] mNumberList = null;
    private static int[] mNumberListWeight = null;

    private static int[] mTotalWeightTable = null;

    public static void init(String list) {
        mNumberList = new int[BUFFER_SIZE];
        mNumberListWeight = new int[BUFFER_SIZE];

        int n = 0;
        String[] numbers = list.split(",");
        for (String number : numbers) {
            int num = Integer.parseInt(number);

            Log.d(TAG, "num: " + num);
            mNumberList[n++] = num;
        }
    }

    public static int getWeight(int element) {
        try {
            int weight = mNumberListWeight[element];
            Log.d(TAG, String.format("weight(%d): %d", element, weight));
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

    public static void setTotalWeightTable(int[] totWeight) {
        mTotalWeightTable = totWeight;
        Log.d(TAG, "Set total weight table");

        for (int i=0; i<mNumberList.length; i++) {
            int weight = mTotalWeightTable[mNumberList[i]];

            Log.d(TAG, "Weight: " + weight);
            mNumberListWeight[i] = weight;
        }
    }

    public static int getWeightSum() {
        int sum = 0;

        for (int i=0; i < mNumberListWeight.length; i++) {
            sum += mNumberListWeight[i];
        }

        return sum;
    }
}
