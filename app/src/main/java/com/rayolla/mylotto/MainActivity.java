package com.rayolla.mylotto;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyLotto_MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Button button = (Button) findViewById(R.id.button_generate);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        readExcel();
    }

    public void readExcel() {
        try {
            InputStream is = getBaseContext().getResources().getAssets().open("test.xls");
            //엑셀파일
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
                                Log.d("Main",  col + "번째: "  + contents);
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