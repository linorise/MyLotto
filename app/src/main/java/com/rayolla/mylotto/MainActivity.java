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

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyLotto_MainActivity";
    private static final int PICK_FILE_REQUEST_CODE = 100;

    private ArrayList<String> mLottoList = null;
    private ArrayList<String> mGenList = null;

    private Button mB_generate;
    private TextView mTV_out;

    private int mNumToGen = 0;

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

        Button button_select = (Button) findViewById(R.id.button_select);
        button_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        mLottoList = new ArrayList<String>();
        mGenList = new ArrayList<String>();

        Spinner spinner = findViewById(R.id.spinner);
//        String[] data = {"항목 1", "항목 2", "항목 3", "항목 4"};
        String[] data = new String[100];
        for (int i=0; i<data.length; i++) {
            data[i] = Integer.toString(i+1);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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

        mB_generate = (Button) findViewById(R.id.button_generate);
        mB_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTV_out.setText("");
                mGenList.clear();

                for (int i=0; i<mNumToGen; i++) {
                    List<Integer> list = new ArrayList<>();
                    int count = 0;

                    do {
                        int num = getRand();

                        if (isDuplicated(list, num)) {
                            continue;
                        }

                        list.add(num);
                        count++;
                    } while (count < 6);

                    Collections.sort(list);
                    Log.d(TAG, "list:  " + list.toString());

                    if (!checkDuplicationList(mLottoList, list)) {
                        mGenList.add(list.toString()+"\n");
                    }

                    list.clear();
                }

                for (int i=0; i<mNumToGen; i++) {
                    String data = mGenList.get(i).replace("[", "");

                    data = data.replace("]", "");
                    data = data.replace(" ", "");
                    mTV_out.append(data);
                }
            }
        });

        mB_generate.setEnabled(false);

        Button button_analysis = (Button)findViewById(R.id.button_analysis);
        button_analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "list: " + mTV_out.getText().toString());
                String winning_list_50 = "";

                for (int i=0; i<50; i++) {
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

            mB_generate.setEnabled(true);
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

        Log.d(TAG, "gen: " + gen);

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
}