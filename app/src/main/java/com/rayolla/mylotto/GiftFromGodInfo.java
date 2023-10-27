package com.rayolla.mylotto;

import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class GiftFromGodInfo {
    private static final String TAG = "MyLotto_GiftFromGodInfo";
    private static final int TOTAL_NUM = 45;    // the number of each number for lotto
    public static final int USE_NUM_OF_WINNING = 100;
    private static int[] mTotalWeightPerNum = null;

    private static int mTotalWinningNum = 0;
    private static String mCurGenList = "";

    // String list. Separator: \n
    public static void calculateWeight(String totList) {
        int winningNum = 0;

        Log.d(TAG, "Calculate weight");

        if (totList == null) {
            Log.d(TAG, "totList is null");
            return;
        }

        if (mTotalWeightPerNum == null) {
            mTotalWeightPerNum = new int[TOTAL_NUM];
        }

        for (int i = 0; i < mTotalWeightPerNum.length; i++) {
            mTotalWeightPerNum[i] = 0;
        }

        String [] lists = totList.split("\n");
        for (String list : lists) {
            winningNum++;
        }

        mTotalWinningNum = winningNum;
        Log.d(TAG, "winningNum: " + winningNum);

        for (String list : lists) {
            String[] numbers = list.split(",");
            for (String number : numbers) {
                try {
                    int num = Integer.parseInt(number);
                    if ((num - 1) >= 0 && (num - 1) < TOTAL_NUM) {
                        mTotalWeightPerNum[num - 1] += 1;
                    } else {
                        Log.d(TAG, "Buffer overflow! num:" + num);
                    }
                } catch (NumberFormatException e) {
                    Log.w(TAG, "It's not number !");
                }
            }
        }
    }

    public static void printWeightStatistics() {
        Log.d(TAG, "Print total weight");
        for (int i=0; i<mTotalWeightPerNum.length; i++) {
            Log.d(TAG, (i+1) + ": " + mTotalWeightPerNum[i]);
        }
    }

    public static void printWeightStatistics(TextView tv1, TextView tv2) {
        tv1.setText("");
        tv2.setText("");

        try {
            for (int i = 0; i < mTotalWeightPerNum.length; i++) {
//                Log.d(TAG, (i + 1) + ": " + mTotalWeightPerNum[i]);

                String str = (i + 1) + ": " + mTotalWeightPerNum[i] + "\n";
                if (i < 25) {
                    tv1.append(str);
                } else {
                    tv2.append(str);
                }
            }
        }
        catch (NullPointerException e) {
            Log.w(TAG, "Null check !");
            e.printStackTrace();
        }
    }

    public static int[] getTotalWeightTable() {
        if (mTotalWeightPerNum == null) {
            Log.w(TAG, "Weight per number is null !");
        }

        return mTotalWeightPerNum;
    }

    public static int getWeightFromTable(int num) {
        try {
            Log.d(TAG, String.format("Get %d's weight: %d", num, mTotalWeightPerNum[num - 1]));
            return mTotalWeightPerNum[num - 1];
        }
        catch (NullPointerException e) { e.printStackTrace(); }

        return -1;
    }

    public static String sortGenList(int genNum, String genList, boolean descending) {
        String[] list = genList.split("\n");
        String[] tmp = new String[genNum];    // include weight at the last
        String tmp2 = "";
        int n = 0;

        Log.d(TAG, "Sort generated list");
        Log.d(TAG, "org genList: " + genList);

        for (String str : list) {
            int totWeight = 0;

            String[] eles = str.split(",");

            for (String ele : eles) {
                int num = Integer.parseInt(ele);
                if (num > 0) {
                    totWeight += mTotalWeightPerNum[num - 1];
                }
                else {
                    Log.w(TAG, "num is 0 !");
                }
            }

            Log.d(TAG, str + "(weight): " + totWeight);
            tmp2 +=  str + "," + totWeight + "\n";
        }

        tmp = tmp2.split("\n");

        Comparator<String> customComparator = new Comparator<String>() {
            @Override
            public int compare(String str1, String str2) {
                String[] parts1 = str1.split(",");
                String[] parts2 = str2.split(",");
                int lastNumber1 = Integer.parseInt(parts1[parts1.length - 1]);
                int lastNumber2 = Integer.parseInt(parts2[parts2.length - 1]);

                if (descending) {
                    // descending order
                    return Integer.compare(lastNumber2, lastNumber1);
                }
                else {
                    // ascending order
                    return Integer.compare(lastNumber1, lastNumber2);
                }
            }
        };

        Arrays.sort(tmp, customComparator);
        /*
        for (int i=0; i<tmp.length; i++) {
            Log.d(TAG, "tmp: " + tmp[i]);
        }
        */

        String newList = "";
        for (int i=0; i< tmp.length; i++) {
            String pattern = "\\d+$";   // Regular expression to find the number at the end
            String removedString1 = tmp[i].replaceAll(pattern, "");
            String removedString2 = removedString1.substring(0, removedString1.length() - 1);   // last ',' remove
            newList += removedString2 + "\n";
        }

        Log.d(TAG, "new genList: " + newList);

        return newList;
    }

    public static void setCurGenList(String genList) {
        Log.d(TAG, "Set generated list");
        mCurGenList = genList;
    }

    public static String getCurGenList() {
        Log.d(TAG, "Get current generated list");
        return mCurGenList;
    }
}
