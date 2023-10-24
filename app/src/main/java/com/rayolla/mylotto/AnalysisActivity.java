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
    private String mPickedList = "";

    AnalysisView mAnalysisView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analysis);

        String genList = getIntent().getStringExtra("gen_list");
        String winningList = getIntent().getStringExtra("winning_list");

        Log.d(TAG, "genList: " + genList);
//        Log.d(TAG, "winningList: " + winningList);

//        GiftFromGodInfo.calculateWeight(winningList);
//        GiftFromGodInfo.printWeightStatistics();

        mAnalysisView = (AnalysisView) findViewById(R.id.analysisview);
        mAnalysisView.setWinningList(winningList);

        if (genList.length() > 0) {
            TextView textView = (TextView) findViewById(R.id.tv_genlist);

            String[] lines = genList.split("\n");
            mGenList = lines;
            int n = 0;
            for (String line : lines) {
                String dataStr = "";
                if (n == 0) {
                    mAnalysisView.setGeneratedList(line);
                    dataStr = "<font color='#FF0000'>" + line + "</font><br>";

                    mFocus = n;
                    mAnalysisView.setFocusedList(line);
                    setWeightInfo(line);
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
                TextView tv_genlist = (TextView) findViewById(R.id.tv_genlist);
                int n = 0;

                mFocus++;
                tv_genlist.setText("");

                Log.d(TAG, "len: " + mGenList.length);
                if (mFocus >= mGenList.length) {
                    mFocus = 0;
                }

                for (String line : mGenList) {
                    String dataStr = "";
                    if (n == mFocus) {
                        mAnalysisView.setFocusedList(line);
                        dataStr = "<font color='#FF0000'>" + line + "</font><br>";

                        // set weight
                        setWeightInfo(line);
                    }
                    else {
                        dataStr = line + "<br>";
                    }
                    tv_genlist.append(Html.fromHtml(dataStr, Html.FROM_HTML_MODE_COMPACT));
                    n++;
                }

                mAnalysisView.invalidate();
            }
        });

        Button button_pick = (Button) findViewById(R.id.button_pick);
        button_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPickedList += mAnalysisView.getFocusedList() + "\n";

                Log.d(TAG, "Your picked list: " + mPickedList);
            }
        });

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void setWeightInfo(String list) {
        Log.d(TAG, "Set weight info");

        WeightInfo.init(list);
        WeightInfo.setTotalWeightTable(GiftFromGodInfo.getTotalWeightTable());

        TextView tv_total_score = (TextView) findViewById(R.id.tv_total_score);
        String info = "Total: " + WeightInfo.getListWeightSum() + "\n";
        String[] infos = new String[6];
        int n = 0;

        String focused = mAnalysisView.getFocusedList();
        String[] numbers = focused.split(",");
        for (String number : numbers) {
            try {
                int num = Integer.parseInt(number);
                int weight = WeightInfo.getWeightFromTotalTable(num-1);
                infos[n++] = num + ": " + weight + "\n";
            } catch (NumberFormatException e) {
                Log.w(TAG, "It's not number !");
            }
        }

        String total = "";
        for (int i = 0; i<infos.length; i++) {
            total += infos[i];
        }
        tv_total_score.setText(info + total);
    }
}
