package com.kevinlee.customviewdemo.customview.progressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

import com.kevinlee.customviewdemo.R;

/**
 * RoundProgressBar
 * 圆环形进度条
 */
public class RoundProgressBar extends HorizontalProgressBar {

    private static final int DEFAULT_RADIUS = 30; // 默认半径为30dp

    private int mRadius = dp2px(DEFAULT_RADIUS);// 圆环形进度条的半径

    private int mRingMaxHeight;// 圆环的最大的宽度

    private Paint mPaint;// 画笔

    private RectF reachProgressRect;// 已完成进度条的范围

    private int mStartX;// 开始绘制的x坐标

    private int mStartY;// 开始绘制的y坐标

    private Rect mBound;// 文本的范围

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取属性值
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundProgressBar, defStyleAttr, 0);
        mRadius = (int) typedArray.getDimension(R.styleable.RoundProgressBar_progress_radius, mRadius);
        typedArray.recycle();

        //初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        mBound = new Rect();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        mRingMaxHeight = Math.max(mReachHeight, mUnReachHeight);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = getPaddingLeft() + getPaddingRight() + (mRingMaxHeight + mRadius) * 2;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = getPaddingTop() + getPaddingBottom() + (mRingMaxHeight + mRadius) * 2;
        }
        int mRealWidth = Math.min(width, height);// 通过比对获得最小值作为宽高
        setMeasuredDimension(mRealWidth, mRealWidth);

        mRadius = (getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - mRingMaxHeight) / 2;

        mStartX = getMeasuredWidth() / 2 - mRadius;
        mStartY = getMeasuredWidth() / 2 - mRadius;

        int mEndX = getMeasuredWidth() / 2 + mRadius;
        int mEndY = getMeasuredWidth() / 2 + mRadius;

        reachProgressRect = new RectF(mStartX, mStartY, mEndX, mEndY);

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {

        /**
         * 绘制未完成进度条
         */
        mPaint.setColor(mUnReachColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mUnReachHeight);
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, mRadius, mPaint);

        /**
         * 绘制已完成进度条
         */
        mPaint.setColor(mReachColor);
        mPaint.setStrokeWidth(mReachHeight);
        int sweepAngle = (int) (getProgress() * 1.0f / getMax() * 360);
        canvas.drawArc(reachProgressRect, -90, sweepAngle, false, mPaint);

        /**
         * 绘制文本
         */
        String progress = getProgress() + "%";
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);
        mPaint.getTextBounds(progress, 0, progress.length(), mBound);
        canvas.drawText(progress, (getMeasuredWidth() - mBound.width())/2, (getMeasuredHeight() + mBound.height()) / 2, mPaint);

    }
}
