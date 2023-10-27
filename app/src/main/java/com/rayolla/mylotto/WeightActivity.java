package com.rayolla.mylotto;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WeightActivity extends AppCompatActivity {
    private static final String TAG = "MyLotto_WeightActivity";

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
            String[] lists = mGenList.split("\n");
            int n = 1;

            for (String list : lists) {
                String data = n + ": " + list + "\n";
                mTV_gen_weight.append(data);
                n++;
            }
//            mTV_gen_weight.setText(mGenList);
        }
        else {
            mTV_gen_weight.setText("No Generated");
        }

        mTV_weight1 = (TextView) findViewById(R.id.tv_weight1);
        mTV_weight2 = (TextView) findViewById(R.id.tv_weight2);

//        GiftFromGodInfo.calculateWeight(mWinningList);
        GiftFromGodInfo.printWeightStatistics(mTV_weight1, mTV_weight2);
    }
}
