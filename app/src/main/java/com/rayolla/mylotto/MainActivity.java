package com.rayolla.mylotto;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import java.io.FileOutputStream;
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
    private static final int NUMBER_POOL_COUNT = 6;
    private static final String APP_SHARED_PREF = "AppSharedPref";
    private static final String APP_SHARED_PREF_EXCLUDE = "exclude";
    private static final String APP_SHARED_PREF_INCLUDE = "include";
    private static final String DEFAULT_FILE_PATH = "/storage/self/primary/Download/";
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

                mIncludeNumber = s.toString();
                setAppSharedPref(APP_SHARED_PREF_INCLUDE, s.toString());

                saveValue("include", s.toString());
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

                mExcludeNumber = s.toString();
                setAppSharedPref(APP_SHARED_PREF_EXCLUDE, s.toString());

                saveValue("exclude", s.toString());
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

                generateNumber();
            }
        });

        mButtonGenerate.setEnabled(false);


        mButtonWeight = (Button) findViewById(R.id.button_weight);
        mButtonWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWeightActivity();
            }
        });

        mButtonAnalysis = (Button)findViewById(R.id.button_analysis);
        mButtonAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnalysisActivity();
            }
        });

        mTV_out = (TextView) findViewById(R.id.tv_out);
        mTV_out.setText(getVersionName());

        mWeightPerNum = new int[TOTAL_NUM];
        for (int i=0; i<mWeightPerNum.length; i++) {
            mWeightPerNum[i] = 0;
        }

        mET_include.setText(getAppSharedPref("include"));
        mET_exclude.setText(getAppSharedPref("exclude"));
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

        // Reset Include, Exclude
        Log.d(TAG, "Reset Include, Exclude");
        mET_include.setText(getAppSharedPref("include"));
        mET_exclude.setText(getAppSharedPref("exclude"));
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

                    if (mLottoList.size() > 0) {
                        mButtonAnalysis.setEnabled(true);
                    }
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

    private void generateNumber() {
        int genNum = 1;

        while (genNum <= mNumToGen) {
            List<Integer> list = new ArrayList<>();
            int numIncCount = 0;
            int count = 0;

            if (mIncludeNumber.length() > 0) {
                String[] IncNumbers = mIncludeNumber.split(",");

                for (String numIncStr : IncNumbers) {
                    list.add(Integer.parseInt(numIncStr));
                    numIncCount++;
                }
            }

            count += numIncCount;
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
            } while (count < NUMBER_POOL_COUNT);

            Collections.sort(list);
            if (DEBUG) {
                Log.d(TAG, "list:  " + list.toString());
            }

            if (!checkDuplicationList(mLottoList, list)) {
                mGenList.add(list.toString()+"\n");
                genNum++;
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

    private void startWeightActivity() {
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

    private void startAnalysisActivity() {
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

    private String getListExceptElement(String list, int element) {
        String[] numbers = list.split(",");
        StringBuilder  newList = new StringBuilder();
        int index = 0;
        int index2 = 0;

        for (String number : numbers) {
            if (element != index) {
                if (index2 < 5) {
                    newList.append(number + ",");
                }
                else {
                    newList.append(number);
                }
                index2++;
            }
            index++;
        }

        return newList.toString();
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

        if (DEBUG) {
            Log.d(TAG, "gen: " + gen);
        }

        for (int i=0; i<lottoList.size(); i++) {
            String str = lottoList.get(i);
//            Log.d(TAG, String.format("#%d> %s=%s", i, str, gen));

            if (isIncludeBonus(str)) {
                String newStr = checkIncludeBonus(str);
                if (isDuplicated(newStr, gen)) {
                    Log.d(TAG, "#1 Duplicated");
                    return true;
                }

                // list of bonus number
                // for example - 10,17,22,30,35,43,44
                //   10,17,22,30,35,43(normal)
                //   17,22,30,35,43,44 #1
                //   10,22,30,35,43,44 #2
                //   10,17,22,35,43,44 #3
                //   10,17,22,30,43,44 #4
                //   10,17,22,30,35,44 #5
                for (int j=0; j<5; j++) {
                    newStr = getListExceptElement(str, j);
                    if (isDuplicated(newStr, gen)) {
                        Log.d(TAG, "#2 Duplicated");
                        return true;
                    }
                }
            }
            else {
                if (isDuplicated(str, gen)) {
                    Log.d(TAG, "#3 Duplicated");
                    return true;
                }
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

    private boolean isDuplicated(String src, String gen) {
        if (src.equals(gen)) {
            return true;
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


    private String checkIncludeBonus(String list) {
        String[] numbers = list.split(",");
        StringBuilder  newList = null;

        int count = 1;
        for (String number : numbers) {
            if (newList == null) {
                newList = new StringBuilder(number + ",");
            }
            else {
                if (count == 7) {
//                    Log.d(TAG, "Bonus number is included");
                    break;
                }

                if (count < 6) {
                    newList.append(number + ",");
                }
                else {
                    newList.append(number);
                }
            }
            count++;
        }

        return newList.toString();
    }

    private boolean checkDupExcludeNumber(int genNumber) {
        if (mExcludeNumber.length() > 0) {
            String[] numbers = mExcludeNumber.split(",");

            if (DEBUG) {
                Log.d(TAG, "Exclude numbers: " + mExcludeNumber);
            }

            for (String numStr : numbers) {
                if (genNumber == Integer.parseInt(numStr)) {
                    Log.d(TAG, genNumber + " is exclude number. Drop.");
                    return true;
                }
            }
        }
        else {
            Log.d(TAG, "Exclude number is empty");
        }

        return false;
    }

    private boolean checkIncludeNumber(int genNumber) {
        if (mIncludeNumber.length() > 0) {
            String[] IncNumbers = mIncludeNumber.split(",");

            for (String numIncStr : IncNumbers) {
                int incNumber = Integer.parseInt(numIncStr);

                if (incNumber == genNumber) {
                    Log.d(TAG, "Gen number: " + genNumber + " is included");
                    return true;
                }
            }
        }
        else {
            return true;
        }

        return false;
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

    private String getVersionName() {
        PackageManager packageManager = getPackageManager();
        String packageName = getPackageName();

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            String versionName = packageInfo.versionName;

            return "v" + versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }

    private void saveValue(String key, String value) {
        try {
            if (key.equals("include") || key.equals("exclude")) {
                Log.d(TAG, "save value: " + value);
                String filename = DEFAULT_FILE_PATH + key + ".txt";

                FileOutputStream outputStream = new FileOutputStream(filename);
                outputStream.write(value.getBytes());
                outputStream.close();
            }
            else {
                Log.d(TAG, "key must be 'include' or 'exclude'");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}