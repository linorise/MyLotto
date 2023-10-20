package com.rayolla.mylotto;

import static android.content.Intent.getIntent;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AnalysisActivity extends AppCompatActivity {
    private static final String TAG = "MyLotto_AnalysisActivity";

    private int mFocus = 0;
    private String[] mGenList = null;

    AnalysisView mAnalysisView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analysis);

        String genList = getIntent().getStringExtra("gen_list");
        String winningList = getIntent().getStringExtra("winning_list");

        Log.d(TAG, "genList: " + genList);
        Log.d(TAG, "winningList: " + winningList);

        mAnalysisView = (AnalysisView) findViewById(R.id.analysisview);
        mAnalysisView.setWinningList(winningList);

        if (genList.length() > 0) {
            TextView textView = (TextView) findViewById(R.id.tv_genlist);
//            textView.setText(genList);

            String[] lines = genList.split("\n");
            mGenList = lines;
            int n = 0;
            for (String line : lines) {
                String dataStr = "";
                if (n == 0) {
                    mAnalysisView.setGeneratedList(line);
                    dataStr = "<font color='#FF0000'>" + line + "</font><br>";

                    mFocus = n;
                }
                else {
                    dataStr = line + "<br>";
                }
                textView.append(Html.fromHtml(dataStr, Html.FROM_HTML_MODE_COMPACT));
                n++;
            }
        }

        Button button_next = (Button)findViewById(R.id.button_next);
        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) findViewById(R.id.tv_genlist);
                int n = 0;

                mFocus++;
                textView.setText("");

                Log.d(TAG, "len: " + mGenList.length);
                if (mFocus >= mGenList.length) {
                    mFocus = 0;
                }

                for (String line : mGenList) {
                    String dataStr = "";
                    if (n == mFocus) {
                        mAnalysisView.setFocusedList(line);
                        dataStr = "<font color='#FF0000'>" + line + "</font><br>";
                    }
                    else {
                        dataStr = line + "<br>";
                    }
                    textView.append(Html.fromHtml(dataStr, Html.FROM_HTML_MODE_COMPACT));
                    n++;
                }

                mAnalysisView.invalidate();
            }
        });

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
}
