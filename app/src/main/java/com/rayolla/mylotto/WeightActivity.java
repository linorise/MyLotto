package com.rayolla.mylotto;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WeightActivity extends AppCompatActivity {
    private static final String TAG = "MyLotto_WeightActivity";

    private static final int USE_NUM_OF_WINNING = 50;
    private static final int TOTAL_NUM = 45;

    private String mGenList = "";
    private String mWinningList = "";

    private int[] mWeightPerNum = null;

    private TextView mTV_gen_weight;
    private TextView mTV_weight1;
    private TextView mTV_weight2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weight);

        mGenList = getIntent().getStringExtra("gen_list");
        mWinningList = getIntent().getStringExtra("winning_list");

        mWeightPerNum = new int[TOTAL_NUM];
        for (int i=0; i<mWeightPerNum.length; i++) {
            mWeightPerNum[i] = 0;
        }

        mTV_gen_weight = (TextView) findViewById(R.id.tv_gen_weight);
        if (mGenList.length() > 0) {
            mTV_gen_weight.setText(mGenList);
        }
        else {
            mTV_gen_weight.setText("No Generated");
        }

        mTV_weight1 = (TextView) findViewById(R.id.tv_weight1);
        mTV_weight2 = (TextView) findViewById(R.id.tv_weight2);

//        calculateWeightStatistics();
        GiftFromGodInfo.calculateWeightStatistics(mWinningList);
        GiftFromGodInfo.printWeightStatistics(mTV_weight1, mTV_weight2);
//        printWeightStatistics();
    }

//    private void calculateWeightStatistics() {
//        Log.d(TAG, "mWinningList: " + mWinningList);
//        String[] lists = mWinningList.split("\n");
//
//        for (String list : lists) {
//            String[] numbers = list.split(",");
//            for (String number : numbers) {
//                try {
//                    int num = Integer.parseInt(number);
//                    if ((num - 1) >= 0 && (num - 1) < TOTAL_NUM) {
//                        mWeightPerNum[num - 1] += 1;
//                    }
//                    else {
//                        Log.d(TAG, "Buffer overflow! num:" + num);
//                    }
//                } catch (NumberFormatException e) {
//                    Log.w(TAG, "It's not number !");
//                }
//            }
//        }
//    }
//
//    private void printWeightStatistics() {
//        mTV_weight1.setText("");
//        mTV_weight2.setText("");
//
//        for (int i=0; i<mWeightPerNum.length; i++) {
//            Log.d(TAG, (i+1) + ": " + mWeightPerNum[i]);
//
//            String str = (i+1) + ": " + mWeightPerNum[i] + "\n";
//            if (i < 25) {
//                mTV_weight1.append(str);
//            }
//            else {
//                mTV_weight2.append(str);
//            }
//        }
//    }
}
