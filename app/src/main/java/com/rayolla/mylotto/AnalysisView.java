package com.rayolla.mylotto;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.StringTokenizer;

public class AnalysisView extends View {
    private static final String TAG = "MyLotto_AnalysisView";
    private static final int DEFAULT_LINE_INTERVAL = 30;
    private static final int START_X = 30;
    private static final int START_X0 = 10;
    private static final int STOP_X0 = 10;
    private static final int START_Y = 30;
    private static final int TOTAL_NUM = 45;
    private int mWidth_X;
    private int mHeight_Y;
    private int mXNum = 0;   // number to draw from X to Y
    private int mYNum = 0;   // number to draw from Y to X

    private int mMaxY = 0;

    private String mWinningList = "";
    private String mGenList = "";
    private String mFocusedList = "";

    private Canvas mCanvas;

    private int[] mWeightPerNum = null;

    // FOR TEST
//    private int mXNum = 2;  // number to draw from X to Y
//    private int mYNum = 2;  // number to draw from Y to X

    public AnalysisView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Log.d(TAG, "AnalysisView constructor");
        mXNum = GiftFromGodInfo.USE_NUM_OF_WINNING + 1;
        mYNum = TOTAL_NUM + 1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d(TAG, "onDraw");

        mCanvas = canvas;

        Log.d(TAG, "width(X): " + canvas.getWidth());
        Log.d(TAG, "height(Y): " + canvas.getHeight());
        mWidth_X = canvas.getWidth();
        mHeight_Y = canvas.getHeight();

        Log.d(TAG, "MaximumBitmapHeight: " + canvas.getMaximumBitmapHeight());
        Log.d(TAG, "MaximumBitmapWidth: " + canvas.getMaximumBitmapWidth());

        drawX(canvas);
        drawY(canvas);

        drawWinningList();
        drawFocusedList();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        Log.d(TAG, "Detached from window");

        mWeightPerNum = null;
    }

    private void drawX(Canvas canvas) {
        Paint paint = new Paint();
        PathEffect pathEffect = new DashPathEffect(new float[]{10, 5}, 0);

        int startX = START_X0;
        int startY = START_Y;
        int stopX = STOP_X0;
        int stopY = START_Y + (mYNum-1)*DEFAULT_LINE_INTERVAL;

//        Log.d(TAG, "drawX:");
//        Log.d(TAG, "startX   startY   stopX   stopY");
        for (int i=0; i<mXNum; i++) {
            boolean dot = false;
            boolean thick = false;

            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);
            paint.setPathEffect(null);

            if (i != 0) {
                if (i%10 != 0 && i%5 == 0) {
//                    Log.d(TAG, "i: " + i);
                    dot = true;
                }

                if (i%10 == 0) {
                    thick = true;
                }
            }

            if (dot) {
                paint.setStrokeWidth(3);
                paint.setPathEffect(pathEffect);
            }

            if (thick) {
                paint.setStrokeWidth(4);
                paint.setColor(Color.RED);
            }

//            Log.d(TAG, String.format("%d   %d   %d   %d", startX + (i*DEFAULT_LINE_INTERVAL), startY, stopX + (i*DEFAULT_LINE_INTERVAL), stopY));
            canvas.drawLine(startX + (i*DEFAULT_LINE_INTERVAL), startY, stopX + (i*DEFAULT_LINE_INTERVAL), stopY, paint);
        }
    }

    private void drawY(Canvas canvas) {
        Paint paint = new Paint();
        PathEffect pathEffect = new DashPathEffect(new float[]{10, 5}, 0);

        int startX = START_X0;
        int startY = START_Y;
        int stopX = STOP_X0 + (mXNum - 1)*DEFAULT_LINE_INTERVAL;
        int stopY = START_Y;
        int i;
        int count = 0;

//        Log.d(TAG, "drawY:");
//        Log.d(TAG, "startX   startY   stopX   stopY");
        for (i=mYNum-1; i>=0; i--) {
            boolean dot = false;
            boolean thick = false;

            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);
            paint.setPathEffect(null);

            if (count != 0) {
                if (count%10 != 0 && count%5 == 0) {
                    dot = true;
                }

                if (count%10 == 0) {
                    thick = true;
                }
            }
            else {
                mMaxY  = startY + (i*DEFAULT_LINE_INTERVAL);
            }

            if (count != 0 && count%10 == 0) {
                thick = true;
            }

            if (dot) {
                paint.setStrokeWidth(3);
                paint.setPathEffect(pathEffect);
            }

            if (thick) {
                paint.setStrokeWidth(4);
                paint.setColor(Color.BLUE);
            }

//            Log.d(TAG, String.format("%d   %d   %d   %d", startX, startY + (i*DEFAULT_LINE_INTERVAL), stopX, stopY + (i*DEFAULT_LINE_INTERVAL)));
            canvas.drawLine(startX, startY + (i*DEFAULT_LINE_INTERVAL), stopX, stopY + (i*DEFAULT_LINE_INTERVAL), paint);

            count++;
        }
    }

    private void drawPoint(Canvas canvas, int x, int y) {
        Paint paint = new Paint();
        paint.setStrokeWidth(18);
        canvas.drawPoint(x, y, paint);
    }

    private void drawPoint(Canvas canvas, int color, String list, int startPos) {
        Paint paint = new Paint();
        paint.setStrokeWidth(18);
        paint.setColor(color);

        String tok="";
        StringTokenizer st = new StringTokenizer(list, ",");
        int n = 0;
        int[] data = {0,0,0,0,0,0};

//        Log.d(TAG, "draw list: " + list);

        while (st.hasMoreTokens()) {
            tok = st.nextToken();
//            Log.d(TAG, "tok: " + tok);

            if (tok != null) {
                // bonus could be included
                if (n < 6) {
                    data[n] = Integer.parseInt(tok);
                    n++;
                }
            }
        }

        for (int i=0; i<data.length; i++) {
            canvas.drawPoint(startPos, mMaxY - (DEFAULT_LINE_INTERVAL * data[i]), paint);
        }
    }

    public void setWinningList(String list) {
        mWinningList = list;
    }

    public void setGeneratedList(String list) {
        mGenList = list;
    }

    public void setFocusedList(String focused) {
        mFocusedList = focused;
    }

    private void drawWinningList() {
        if (mWinningList.length() == 0) {
            Log.d(TAG, "Winning list is empty!");
            return;
        }

        Log.d(TAG, "Draw winning list");

        String[] lines = mWinningList.split("\n");
        int n = 2;  // second row. first is for generated number.
        for (String line : lines) {
            drawPoint(mCanvas, Color.BLACK, line, (n * START_X) - 20);
            n++;
        }
    }

    private void drawFocusedList() {
        if (mFocusedList.length() == 0) {
            if (mGenList.length() > 0) {
                String[] lines = mGenList.split("\n");
                for (String line : lines) {
                    mFocusedList = line;
                    break;
                }
            }
            else {
                Log.d(TAG, "Focused list is empty!");
                return;
            }
        }

        Log.d(TAG, "Draw focused list. " + mFocusedList);

        drawPoint(mCanvas, Color.RED, mFocusedList, START_X0);
    }

    public String getFocusedList() {
        return mFocusedList;
    }
}
