package com.rayolla.mylotto;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyLotto_MainActivity";
    private static final int PICK_FILE_REQUEST_CODE = 100;

    private ArrayList<String> mLottoList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button_generate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        mLottoList = new ArrayList<String>();

        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), "Select file", Toast.LENGTH_LONG).show();

                try {
                    Thread.sleep(2000);
                }
                catch (Exception e) {e.printStackTrace();}

                showFileChooser();
            }
        });
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
                        Log.d(TAG, "line: " + line);
                        mLottoList.add(line);
                    }
                }
                catch (IOException e) { e.printStackTrace(); }
            }
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

    public void readExcel() {
        try {
            InputStream is = getApplicationContext().getResources().getAssets().open("test.xls");
            Log.d(TAG, "is: " + is);

//            HSSFWorkbook workbook = new HSSFWorkbook(is);
//            HSSFSheet sheet = workbook.getSheetAt(0);
//            for (Row row : sheet) {
//                for (Cell cell : row) {
//                    String cellValue = cell.toString();
//                    Log.d(TAG, "cellValue: " + cellValue);
//                }
//            }
//            workbook.close();

//            //엑셀파일
            Workbook wb = Workbook.getWorkbook(is);
            //엑셀 파일이 있다면
            if(wb != null){

                Sheet sheet = wb.getSheet(0);//시트 블러오기

                if(sheet != null){

                    int colTotal = sheet.getColumns(); //전체 컬럼
                    int rowIndexStart = 1; //row 인덱스 시작
                    int rowTotal = sheet.getColumn(colTotal-1).length;

                    StringBuilder sb;
                    for(int row = rowIndexStart; row < rowTotal; row++){

                        sb = new StringBuilder();

                        //col: 컬럼순서, contents: 데이터값
                        for(int col = 0; col < colTotal; col++){
                            String contents = sheet.getCell(col, row).getContents();

                            if(row > 0){
                                Log.d(TAG,  col + "번째: "  + contents);
                            }

                        }
                    }
                }
            }
        } catch (IOException | BiffException e) {
            e.printStackTrace();
        }
    }
}