package com.rayolla.mylotto;

import static android.content.Intent.getIntent;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AnalysisActivity extends AppCompatActivity {
    private static final String TAG = "MyLotto_AnalysisActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analysis);

        String genList = getIntent().getStringExtra("gen_list");
        String winningList = getIntent().getStringExtra("winning_list");

        Log.d(TAG, "genList: " + genList);
        Log.d(TAG, "winningList: " + winningList);

        if (genList.length() > 0) {
            TextView textView = (TextView) findViewById(R.id.tv_genlist);
            Log.d(TAG, "textView: " + textView);
            textView.setText(genList);
        }

        AnalysisView analysisView = (AnalysisView) findViewById(R.id.analysisview);
        analysisView.setWinningList(winningList);

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
}
