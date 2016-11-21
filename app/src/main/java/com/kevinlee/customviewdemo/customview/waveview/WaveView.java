package com.kevinlee.customviewdemo.customview.waveview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.kevinlee.customviewdemo.R;

/**
 * ClassName:WaveView
 * Description:波浪形的View
 * Author:KevinLee
 * Date:2016/11/2 0002
 * Time:下午 1:43
 * Email:KevinLeeV@163.com
 */
public class WaveView extends View {

    // view的背景颜色
    private int background;

    // view的默认宽度
    private int defaultWidth = 300;

    // view的默认高度
    private int defaultHeight = 300;

    // 画笔
    private Paint mPaint;

    // 三角形的高度
    private int triangleHeight = 10;

    //半圆形的半径
    private int radius = 10;

    // 三角形的数量
    private int cnt = 20;

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WaveView, defStyleAttr, 0);
        int indexCount = typedArray.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.WaveView_backgroundColor:
                    background = typedArray.getColor(attr, Color.BLUE);
                    break;
            }
        }
        typedArray.recycle();
        initWaveView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取宽度的测量模式和测量值
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        //获取高度的测量模式和测量值
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width; //实际宽度
        int height; // 实际高度
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = defaultWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = defaultHeight;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制矩形
        canvas.drawRect(new RectF(triangleHeight, 0, getMeasuredWidth() - radius, getMeasuredHeight()), mPaint);
        int len = getMeasuredHeight() / cnt;// 每个三角形底边长度
        Path path = new Path();
        path.moveTo(triangleHeight, 0);
        // 画左边的三角形
        for (int i = 0; i < 2 * cnt; i++) {
            int x = i % 2 == 0 ? 0 : triangleHeight;
            path.lineTo(x, len / 2.0f * (i + 1));
        }
        path.close();
        canvas.drawPath(path, mPaint);
        // 画右边的半圆形
        //path.moveTo(getMeasuredWidth() - triangleHeight, 0);
        int count = getMeasuredHeight() / (2 * radius);// 圆形的数量
        for (int j = 1; j <= count; j++) {
            float y = radius * (j * 2.0f - 1);
            path.addCircle(getMeasuredWidth() - radius, y, radius, Path.Direction.CW);
            canvas.drawPath(path, mPaint);
        }
        path.close();
    }

    /**
     * 初始化View
     */
    private void initWaveView() {
        // 设置默认颜色
        if (background == 0)
            background = Color.BLUE;
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setColor(background);
    }

}
