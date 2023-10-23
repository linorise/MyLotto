package com.rayolla.mylotto;

import android.util.Log;
import android.widget.TextView;

public class GiftFromGodInfo {
    private static final String TAG = "MyLotto_GiftFromGodInfo";
    private static final int TOTAL_NUM = 45;
    private static int[] mWeightPerNum = null;

    private static int mTotalWinningNum = 0;

    public static void calculateWeightStatistics(String totList) {
        int winningNum = 0;

        Log.d(TAG, "Calculate weight");

        if (totList == null) {
            Log.d(TAG, "totList is null");
            return;
        }

        if (mWeightPerNum == null) {
            mWeightPerNum = new int[TOTAL_NUM];
        }

        for (int i = 0; i < mWeightPerNum.length; i++) {
            mWeightPerNum[i] = 0;
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
                        mWeightPerNum[num - 1] += 1;
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
        for (int i=0; i<mWeightPerNum.length; i++) {
            Log.d(TAG, (i+1) + ": " + mWeightPerNum[i]);
        }
    }

    public static void printWeightStatistics(TextView tv1, TextView tv2) {
        tv1.setText("");
        tv2.setText("");

        try {
            for (int i = 0; i < mWeightPerNum.length; i++) {
//                Log.d(TAG, (i + 1) + ": " + mWeightPerNum[i]);

                String str = (i + 1) + ": " + mWeightPerNum[i] + "\n";
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
        if (mWeightPerNum == null) {
            Log.w(TAG, "Weight per number is null !");
        }

        return mWeightPerNum;
    }
}
