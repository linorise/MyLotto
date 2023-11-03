package com.rayolla.mylotto;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import org.apache.poi.hssf.usermodel.HSSFSheet;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Workbook;

//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyLotto_MainActivity";
    private static final int PICK_WIN_FILE_RETURN_CODE = 100;
    private static final int PICK_INCLUDE_FILE_RETURN_CODE = 101;
    private static final int PICK_EXCLUDE_FILE_RETURN_CODE = 102;
    private static final int TOTAL_NUM = 45;
    private static final int DEFAULT_GEN_NUM = 9;
    private static final boolean DEBUG = false;

    private static int mUseNumOfWinning = 0;

    private ArrayList<String> mLottoList = null;    // from 1 to last winning
    private ArrayList<String> mGenList = null;
    private ArrayList<String> mGenListWithWeight = null;

    private String mIncludeNumber = "";
    private String mExcludeNumber = "";

    private EditText mET_include;
    private EditText mET_exclude;

    private Button mButtonGenerate;
    private Button mButtonAnalysis;
    private Button mButtonWeight;
    private TextView mTV_out;

    private int mNumToGen = 0;
    private int[] mWeightPerNum = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUseNumOfWinning = GiftFromGodInfo.USE_NUM_OF_WINNING;

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
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button button_select_include = (Button) findViewById(R.id.button_select_include);
        button_select_include.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser(PICK_INCLUDE_FILE_RETURN_CODE);
            }
        });

        mET_exclude = (EditText)findViewById(R.id.et_exclude);
        mET_exclude.setFilters(filters);
        mET_exclude.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: " + s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button button_select_exclude = (Button) findViewById(R.id.button_select_exclude);
        button_select_exclude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser(PICK_EXCLUDE_FILE_RETURN_CODE);
            }
        });

        Button button_select_win = (Button) findViewById(R.id.button_select_win);
        button_select_win.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser(PICK_WIN_FILE_RETURN_CODE);

                mButtonWeight.setEnabled(true);
            }
        });

        mLottoList = new ArrayList<String>();
        mGenList = new ArrayList<String>();
        mGenListWithWeight = new ArrayList<String>();

        Spinner spinner = findViewById(R.id.spinner);
        String[] data = new String[100];
        for (int i=0; i<data.length; i++) {
            data[i] = Integer.toString(i+1);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(DEFAULT_GEN_NUM);    // default

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = data[position];
                Log.d(TAG, "selectedItem: " + selectedItem);

                mNumToGen = Integer.parseInt(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mButtonGenerate = (Button) findViewById(R.id.button_generate);
        mButtonGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTV_out.setText("");
                mGenList.clear();

                for (int i=0; i<mNumToGen; i++) {
                    List<Integer> list = new ArrayList<>();
                    int count = 0;

                    do {
                        int num = getRand();

                        if (checkDupExcludeNumber(num)) {
                            continue;
                        }

                        if (isDuplicated(list, num)) {
                            continue;
                        }

                        list.add(num);
                        count++;
                    } while (count < 6);

                    Collections.sort(list);
                    if (DEBUG) {
                        Log.d(TAG, "list:  " + list.toString());
                    }

                    if (!checkDuplicationList(mLottoList, list)) {
                        mGenList.add(list.toString()+"\n");
                    }

                    list.clear();
                }

                String genList = "";
                for (int i=0; i<mNumToGen; i++) {
                    String data = "";
                    data = mGenList.get(i).replace("[", "");

                    data = data.replace("]", "");
                    data = data.replace(" ", "");

                    genList += data;
                }

                genList = GiftFromGodInfo.sortGenList(mNumToGen, genList, true);
                mTV_out.append(genList);
                GiftFromGodInfo.setCurGenList(genList);

                mButtonAnalysis.setEnabled(true);
            }
        });

        mButtonGenerate.setEnabled(false);


        mButtonWeight = (Button) findViewById(R.id.button_weight);
        mButtonWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String winning_list_50 = "";

                for (int i=0; i<mUseNumOfWinning; i++) {
                    winning_list_50 += mLottoList.get(i) + "\n";
                }

                Intent i = new Intent(Intent.ACTION_MAIN);
                i.setClassName("com.rayolla.mylotto", "com.rayolla.mylotto.WeightActivity");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                i.putExtra("gen_list", mTV_out.getText().toString());
                i.putExtra("winning_list", winning_list_50);
                getApplicationContext().startActivity(i);
            }
        });

        mButtonAnalysis = (Button)findViewById(R.id.button_analysis);
        mButtonAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "list: " + mTV_out.getText().toString());
                String winning_list_50 = "";

                for (int i=0; i<mUseNumOfWinning; i++) {
                    winning_list_50 += mLottoList.get(i) + "\n";
                }

                Intent i = new Intent(Intent.ACTION_MAIN);
                i.setClassName("com.rayolla.mylotto", "com.rayolla.mylotto.AnalysisActivity");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                i.putExtra("gen_list", mTV_out.getText().toString());
                i.putExtra("winning_list", winning_list_50);
                getApplicationContext().startActivity(i);
            }
        });

        mTV_out = (TextView) findViewById(R.id.tv_out);

        mWeightPerNum = new int[TOTAL_NUM];
        for (int i=0; i<mWeightPerNum.length; i++) {
            mWeightPerNum[i] = 0;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d(TAG, "onRestart");
        String modifiedGenList = GiftFromGodInfo.getCurGenList();
        if (DEBUG) {
            Log.d(TAG, "modifiedGenList: " + modifiedGenList);
        }
        mTV_out.setText(modifiedGenList);
        GiftFromGodInfo.setCurGenList(modifiedGenList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_WIN_FILE_RETURN_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedFileUri = data.getData();
                Log.d(TAG, "selectedFileUri: " + selectedFileUri);

                if (mLottoList != null) {
                    mLottoList.clear();
                }

                try {
                    InputStream inputStream = getInputStreamFromUri(selectedFileUri);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
//                        Log.d(TAG, "line: " + line);
                        mLottoList.add(line);
                    }

                    String winning_list_50 = "";
                    for (int i=0; i<mUseNumOfWinning; i++) {
                        String orgStr = mLottoList.get(i);

                        if (isIncludeBonus(orgStr)) {
                            String pattern = "\\d+$";

                            String removedStr = orgStr.replaceAll(pattern, "");
                            orgStr = removedStr.substring(0, removedStr.length() - 1);
                        }
                        winning_list_50 += orgStr + "\n";
                    }
                    GiftFromGodInfo.calculateWeight(winning_list_50);
                    GiftFromGodInfo.printWeightStatistics();
                }
                catch (IOException e) { e.printStackTrace(); }
            }

            mButtonGenerate.setEnabled(true);
        }
        else if (requestCode == PICK_INCLUDE_FILE_RETURN_CODE && resultCode == RESULT_OK) {
            Uri selectedFileUri = data.getData();
            Log.d(TAG, "selectedFileUri: " + selectedFileUri);

            // Clear
            mIncludeNumber = "";

            try {
                InputStream inputStream = getInputStreamFromUri(selectedFileUri);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
//                        Log.d(TAG, "line: " + line);
                    mIncludeNumber = line;
                }

                mET_include.setText(mIncludeNumber);
            }
            catch (IOException e) { e.printStackTrace(); }
        }
        else if (requestCode == PICK_EXCLUDE_FILE_RETURN_CODE && resultCode == RESULT_OK) {
            Uri selectedFileUri = data.getData();
            Log.d(TAG, "selectedFileUri: " + selectedFileUri);

            // Clear
            mExcludeNumber = "";

            try {
                InputStream inputStream = getInputStreamFromUri(selectedFileUri);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
//                        Log.d(TAG, "line: " + line);
                    mExcludeNumber = line;
                }

                mET_exclude.setText(mExcludeNumber);
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    }

    private void showFileChooser(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("*/*");
        intent.setType("text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select the winning list file"), requestCode);
        } catch (android.content.ActivityNotFoundException ex) {
        }
    }

    private InputStream getInputStreamFromUri(Uri uri) {
        try {
            return getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int getRand() {
        Random random = new Random();
        int num;

        do {
            num = random.nextInt(46);
        } while (num == 0);

        return num;
    }

    private boolean checkDuplicationList(ArrayList<String> lottoList, List<Integer> genList) {
        String gen = "";
        for (int i=0; i<genList.size(); i++) {

            if (i != genList.size() - 1) {
                gen += genList.get(i) + ",";
            }
            else {
                gen += genList.get(i);
            }
        }

        // FOR TEST
//        gen = "10,23,29,33,37,40";

        if (DEBUG) {
            Log.d(TAG, "gen: " + gen);
        }

        for (int i=0; i<lottoList.size(); i++) {
            String str = lottoList.get(i);
//            Log.d(TAG, String.format("#%d> %s=%s", i, str, gen));

            if (str.equals(gen)) {
                Log.d(TAG, "Duplication found");
                return true;
            }
        }

        return false;
    }

    private boolean isDuplicated(List<Integer> list, int num) {
        for (int n=0; n<list.size(); n++) {
            if (list.get(n) == num) {
                return true;
            }
        }

        return false;
    }

    private boolean isIncludeBonus(String list) {
        String[] numbers = list.split(",");

        int count = 0;
        for (String number : numbers) {
            count++;
        }

//        Log.d(TAG, "count: " + count);
        if (count == 7) {
            return true;
        }

        return false;
    }

    private boolean checkDupExcludeNumber(int number) {
        String[] numbers = mExcludeNumber.split(",");

        if (DEBUG) {
            Log.d(TAG, "Exclude numbers: " + mExcludeNumber);
        }

        for (String numStr : numbers) {
            if (number == Integer.parseInt(numStr)) {
                Log.d(TAG, number + " is exclude number. Drop.");
                return true;
            }
        }

        return false;
    }
}