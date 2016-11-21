package com.kevinlee.customviewdemo.customview.progressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.kevinlee.customviewdemo.R;

/**
 * ClassName:CircleProgressBar
 * Description: 类似于新浪微博中图片加载的圆形进度条
 * Author:KevinLee
 * Date:2016/11/18 0018
 * Time:上午 10:27
 * Email:KevinLeeV@163.com
 */
public class CircleProgressBar extends ProgressBar {

    private static final int DEFAULT_PROGRESS_COLOR = 0XBBFFFFFF;// 默认颜色
    private static final int DEFAULT_RING_HEIGHT = 1;// 默认外圆环高度
    private static final int DEFAULT_RADIUS = 20;// 默认半径
    private static final int DEFAULT_RING_OFFSET = 3;// 默认间距

    private int mProgressColor = DEFAULT_PROGRESS_COLOR;// 进度条的颜色
    private int mRingHeight = dp2px(DEFAULT_RING_HEIGHT);// 进度条外圆环的高度
    private int mRadius = dp2px(DEFAULT_RADIUS); // 进度条的半径
    private int mRingOffset = dp2px(DEFAULT_RING_OFFSET);// 进度条中外圆环与内圆的间距

    private Paint mPaint;// 画笔

    private RectF circleRect;// 内圆形的矩阵范围

    private int ringRadius;// 外圆环的半径

    private int ringX;// 外圆环在X轴上的偏移量
    private int ringY;// 外圆环在Y轴上的偏移量

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttributesValue(attrs, defStyleAttr);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int realWidth = getRealWidth(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(realWidth, realWidth);

        // 再根据真实的view的宽度，重置一下半径
        mRadius = (getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - (mRingHeight + mRingOffset) * 2) / 2;
        // 初始化外圆环半径
        ringRadius = (int) (mRingHeight / 2.0f + mRingOffset + mRadius);
        ringX = getPaddingLeft() + mRingHeight / 2;
        ringY = getPaddingTop() + mRingHeight / 2;

        // 初始化内圆形开始的x坐标，y坐标也相同
        int circleStartX = getMeasuredWidth() / 2 - mRadius;
        // 初始化内圆形结束的x坐标，y坐标也相同
        int circleEndX = getMeasuredWidth() / 2 + mRadius;

        // 初始化内圆形的范围
        circleRect = new RectF(circleStartX, circleStartX, circleEndX, circleEndX);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        drawProgressBar(canvas);
    }

    /**
     * 绘制进度条
     * @param canvas
     */
    private void drawProgressBar(Canvas canvas) {
        canvas.save();
        // 设置画笔颜色
        mPaint.setColor(mProgressColor);
        mPaint.setStyle(Paint.Style.STROKE);
        /**
         * 绘制外圆环
         */
        // 设置画笔的模式为空心模式，因为要画外圆环
        mPaint.setStyle(Paint.Style.STROKE);
        // 设置画笔的宽度
        mPaint.setStrokeWidth(mRingHeight);
        canvas.drawCircle(ringRadius + ringX, ringRadius + ringY, ringRadius, mPaint);

        /**
         * 绘制内圆形
         */
        mPaint.setStyle(Paint.Style.FILL);
        // 设置结束的角度
        float sweepAngle = getProgress() * 1.0f / getMax() * 360;
        // 绘制内圆形，默认从3点钟位置为零度角，我们为了从12点钟开始，所以开始角度设为-90度
        canvas.drawArc(circleRect, -90, sweepAngle, true, mPaint);

        canvas.restore();
    }

    /**
     * 获取属性值
     *
     * @param attrs
     */
    private void getAttributesValue(AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, defStyleAttr, 0);
        mProgressColor = typedArray.getColor(R.styleable.CircleProgressBar_progress_color, mProgressColor);
        mRingHeight = (int) typedArray.getDimension(R.styleable.CircleProgressBar_progress_ring_height, mRingHeight);
        mRadius = (int) typedArray.getDimension(R.styleable.CircleProgressBar_progress_radius, mRadius);
        mRingOffset = (int) typedArray.getDimension(R.styleable.CircleProgressBar_progress_ring_offset, mRingOffset);
        typedArray.recycle();
        // 初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }

    /**
     * 获取view的真实宽度
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     * @return
     */
    private int getRealWidth(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;// 整个view的宽度
        int height;// 整个view的高度
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = (mRingHeight + mRingOffset + mRadius) * 2 + getPaddingLeft() + getPaddingRight();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = (mRingHeight + mRingOffset + mRadius) * 2 + getPaddingTop() + getPaddingBottom();
        }

        // 比对高度和宽度，获得最小值作为整个view的宽高
        return Math.min(width, height);
    }

    /**
     * dp转px
     */
    private int dp2px(int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     */
    private int sp2px(int spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, getResources().getDisplayMetrics());
    }
}
