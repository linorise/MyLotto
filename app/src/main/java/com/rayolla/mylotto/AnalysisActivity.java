package com.rayolla.mylotto;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AnalysisActivity extends AppCompatActivity {
    private static final String TAG = "MyLotto_AnalysisActivity";
    private static final String APP_SHARED_PREF = "AppSharedPref";
    private static final String APP_SHARED_PREF_EXCLUDE = "exclude";
    private static final String APP_SHARED_PREF_INCLUDE = "include";

    private int mFocus = 0;
    private String[] mGenList = null;

    private AnalysisView mAnalysisView = null;
    private TextView mTV_genlist;

    private EditText mET_include;
    private EditText mET_exclude;
    private String mGenListStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analysis);

        mGenListStr = getIntent().getStringExtra("gen_list");
        String winningList = getIntent().getStringExtra("winning_list");

        Log.d(TAG, "mGenListStr: " + mGenListStr);
//        Log.d(TAG, "winningList: " + winningList);

//        GiftFromGodInfo.calculateWeight(winningList);
//        GiftFromGodInfo.printWeightStatistics();

        mTV_genlist = (TextView) findViewById(R.id.tv_genlist);
        mAnalysisView = (AnalysisView) findViewById(R.id.analysisview);
        mAnalysisView.setWinningList(winningList);

        setTextGenList(mGenListStr);

        Button button_sort = (Button)findViewById(R.id.button_sort);
        button_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Sort generate lists by the weight");

                String tmp = "";
                String[] lines = mGenListStr.split("\n");
                int len = lines.length;

                for (int i=0; i<len; i++) {
                    tmp += lines[len - 1 - i] + "\n";
                }

                mGenListStr = tmp;
                setTextGenList(mGenListStr);
            }
        });

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

        Button button_prev = (Button) findViewById(R.id.button_prev);
        button_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv_genlist = (TextView) findViewById(R.id.tv_genlist);
                int n = 0;

                mFocus--;
                tv_genlist.setText("");

                Log.d(TAG, "len: " + mGenList.length);
                if (mFocus < 0) {
                    mFocus = mGenList.length - 1;
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

        Button button_delete = (Button) findViewById(R.id.button_delete);
        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deleteList = mAnalysisView.getFocusedList();
                String tmpList = "";
                String[] tmpGenList = mGenList;
                Log.d(TAG, "You delete: " + deleteList);

                int count = 0;
                for (int i=0; i<tmpGenList.length; i++) {
                    if (deleteList.equals(tmpGenList[i])) {
                        tmpGenList[i] = "";
                    }
                    else {
                        count++;
                    }
                }

                mGenList = null;
                mGenList = new String[count];
                count = 0;
                mGenListStr = "";
                for (int i=0; i<tmpGenList.length; i++) {
                    if (tmpGenList[i].length() > 0) {
                        mGenList[count++] = tmpGenList[i];
                        tmpList += tmpGenList[i] + "\n";
                        mGenListStr += tmpGenList[i] + "\n";
                    }
                }

                for (int i=0; i<mGenList.length; i++) {
                    Log.d(TAG, "new Gen List: " + mGenList[i]);
                }

                redrawGenList();
                mAnalysisView.invalidate();

                GiftFromGodInfo.setCurGenList(tmpList);
            }
        });

        mET_include = (EditText) findViewById(R.id.et_include);
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                String filteredText = source.subSequence(start, end).toString();

                String regex = "[0-9,]+";
                if (filteredText.matches(regex)) {
                    return null;
                } else {
                    return "";
                }
            }
        };
        mET_include.setFilters(filters);
        mET_include.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: " + s);
                setAppSharedPref(APP_SHARED_PREF_INCLUDE, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mET_exclude = (EditText) findViewById(R.id.et_exclude);
        mET_exclude.setFilters(filters);
        mET_exclude.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: " + s);

                setAppSharedPref(APP_SHARED_PREF_EXCLUDE, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mET_include.setText(getAppSharedPref("include"));
        mET_exclude.setText(getAppSharedPref("exclude"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    private void redrawGenList() {
        mTV_genlist.setText("");

        if (mGenList.length < (mFocus + 1)) {
            Log.d(TAG, "Reset focus");
            mFocus = 0;
        }

        for (int i=0; i<mGenList.length; i++) {
            String dataStr = "";
            String line = mGenList[i];

            if (i == mFocus) {
                mAnalysisView.setGeneratedList(line);
                dataStr = "<font color='#FF0000'>" + line + "</font><br>";

                mAnalysisView.setFocusedList(line);
                setWeightInfo(line);
            }
            else {
                dataStr = line + "<br>";
            }
            mTV_genlist.append(Html.fromHtml(dataStr, Html.FROM_HTML_MODE_COMPACT));
        }
    }

    private String getAppSharedPref(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(APP_SHARED_PREF, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, "");

        Log.d(TAG, "get key: " + key + " value: " + value);

        return value;
    }

    private void setAppSharedPref(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences(APP_SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Log.d(TAG, "set key: " + key + " value: " + value);
        editor.putString(key, value);
        editor.apply();
    }

    private void setTextGenList(String genList) {
        if (genList.length() > 0) {
            mTV_genlist.setText("");

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
                mTV_genlist.append(Html.fromHtml(dataStr, Html.FROM_HTML_MODE_COMPACT));
                n++;
            }
        }
    }
}
