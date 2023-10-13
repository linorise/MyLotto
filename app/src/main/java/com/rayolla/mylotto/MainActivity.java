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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import org.apache.poi.hssf.usermodel.HSSFSheet;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Workbook;

//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;

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

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyLotto_MainActivity";
    private static final int PICK_FILE_REQUEST_CODE = 100;

    private ArrayList<String> mLottoList = null;
    private List<Integer> mGenList = null;

    private Button button_generate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText et_include = (EditText) findViewById(R.id.et_include);
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
        et_include.setFilters(filters);
        et_include.addTextChangedListener(new TextWatcher() {
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

        EditText et_exclude = (EditText)findViewById(R.id.et_exclude);
        et_exclude.setFilters(filters);
        et_exclude.addTextChangedListener(new TextWatcher() {
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

        button_generate = (Button) findViewById(R.id.button_generate);
        button_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = 0;
//                List<Integer> list = new ArrayList<>();

                do {
                    int num = getRand();
                    mGenList.add(num);
                    count++;
                } while (count < 6);

                Collections.sort(mGenList);
                Log.d(TAG, "mGenList:  " + mGenList.toString());

                checkDuplication();
            }
        });

        button_generate.setEnabled(false);

        Button button_select = (Button) findViewById(R.id.button_select);
        button_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        mLottoList = new ArrayList<String>();
        mGenList = new ArrayList<>();

//        runOnUiThread(new Runnable() {
//            public void run() {
//                Toast.makeText(getApplicationContext(), "Select file", Toast.LENGTH_LONG).show();
//
//                try {
//                    Thread.sleep(2000);
//                }
//                catch (Exception e) {e.printStackTrace();}
//
//                showFileChooser();
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
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
                }
                catch (IOException e) { e.printStackTrace(); }
            }

            button_generate.setEnabled(true);
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("*/*");
        intent.setType("text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select the winning list file"), PICK_FILE_REQUEST_CODE);
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

    private boolean checkDuplication() {
        String gen = "";
        for (int i=0; i<mGenList.size(); i++) {

            if (i != mGenList.size() - 1) {
                gen += mGenList.get(i) + ",";
            }
            else {
                gen += mGenList.get(i);
            }
        }

        // FOR TEST
//        gen = "10,23,29,33,37,40";

        Log.d(TAG, "gen: " + gen);

        for (int i=0; i<mLottoList.size(); i++) {
            String str = mLottoList.get(i);
            Log.d(TAG, String.format("#%d> %s=%s", i, str, gen));

            if (str.equals(gen)) {
                Log.d(TAG, "Duplication found");
                return true;
            }
        }

        return false;
    }
}