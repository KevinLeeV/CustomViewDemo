package com.kevinlee.customviewdemo.customview.progressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;

import com.kevinlee.customviewdemo.R;

/**
 * 水平的进度条
 */
public class HorizontalProgressBar extends ProgressBar {

    private static final int DEFAULT_TEXT_COLOR = 0XFFFF0000;// 默认文本颜色
    private static final int DEFAULT_TEXT_SIZE = 10; // 默认文本大小为10sp
    private static final int DEFAULT_REACH_COLOR = 0XFF00FF00;// 默认已完成进度条的颜色
    private static final int DEFAULT_REACH_HEIGHT = 2;// 默认已完成进度条的高度为2dp
    private static final int DEFAULT_UNREACH_COLOR = 0XFF0000FF;// 默认未完成进度条的颜色
    private static final int DEFAULT_UNREACH_HEIGHT = 2;// 默认未完成进度条的高度为2dp
    private static final int DEFAULT_TEXT_OFFSET = 5;// 默认文本左右的间距为5dp

    protected int mTextColor = DEFAULT_TEXT_COLOR;// 进度文本颜色
    protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);// 进度文本尺寸大小
    protected int mReachColor = DEFAULT_REACH_COLOR;// 已完成进度条的颜色
    protected int mReachHeight = dp2px(DEFAULT_REACH_HEIGHT);// 已完成进度条的高度
    protected int mUnReachColor = DEFAULT_UNREACH_COLOR;// 未完成进度条的颜色
    protected int mUnReachHeight = dp2px(DEFAULT_UNREACH_HEIGHT);// 未完成进度条的高度
    protected int mTextOffset = dp2px(DEFAULT_TEXT_OFFSET);// 进度文本左右两边的间距

    private int mProgressWidth;// 进度条的宽度

    private Paint mPaint;// 画笔

    public HorizontalProgressBar(Context context) {
        this(context, null);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttributeValue(attrs, defStyleAttr);
    }

    /**
     * 获取属性值
     */
    private void getAttributeValue(AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.HorizontalProgressBar, defStyleAttr, 0);
        mTextColor = typedArray.getColor(R.styleable.HorizontalProgressBar_progress_text_color, mTextColor);
        mReachColor = typedArray.getColor(R.styleable.HorizontalProgressBar_progress_reach_color, mReachColor);
        mUnReachColor = typedArray.getColor(R.styleable.HorizontalProgressBar_progress_unreach_color, mUnReachColor);
        mTextSize = (int) typedArray.getDimension(R.styleable.HorizontalProgressBar_progress_text_size, mTextSize);
        mReachHeight = (int) typedArray.getDimension(R.styleable.HorizontalProgressBar_progress_reach_height, mReachHeight);
        mUnReachHeight = (int) typedArray.getDimension(R.styleable.HorizontalProgressBar_progress_unreach_height, mUnReachHeight);
        mTextOffset = (int) typedArray.getDimension(R.styleable.HorizontalProgressBar_progress_text_offset, mTextOffset);
        typedArray.recycle();

        /**
         * 必须对画笔设置文本的大小，否则下面测量值无法测量
         */
        mPaint = new Paint();
        mPaint.setTextSize(mTextSize);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 默认用户对progress的width不设置wrap_content属性
         */
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int height = getProgressHeight(heightMeasureSpec);
        setMeasuredDimension(widthSize, height);
        // 获取进度条的宽度
        mProgressWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    /**
     * 获取进度条的高度
     *
     * @param heightMeasureSpec
     * @return
     */
    private int getProgressHeight(int heightMeasureSpec) {
        int height;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        /**
         * 如果用户设置精确值
         */
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            // 获取文本的高度
            int textHeight = (int) (mPaint.descent() + mPaint.ascent());
            // 获取文本高度、已完成进度条的高度、未完成进度条的高度中的最大值
            height = Math.max(Math.max(mReachHeight, mUnReachHeight), Math.abs(textHeight));
        }
        return height + getPaddingBottom() + getPaddingTop();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        // save一下，但是不知道干嘛用的
        canvas.save();
        // 将画布坐标移动到垂直居中的位置
        canvas.translate(getPaddingLeft(), getMeasuredHeight() / 2);

        /**
         * 画已完成的进度条
         * 1.必须先获取文本长度，判断已完成进度条应该画多长
         */
        // 进度条文本
        String text = getProgress() + "%";
        // 获取文本长度
        int textWidth = getTextWidth(mPaint, text);
        // 获取到画多长
        int radios = (int) ((getProgress() * 1.0f / getMax()) * mProgressWidth);
        // 定义已完成进度条的最大宽度
        int reachMaxWidth = mProgressWidth - textWidth - mTextOffset * 2;
        // 得到已完成进度条的实际宽度
        int reachWidth = radios >= reachMaxWidth ? reachMaxWidth : radios;

        // 设置画笔颜色为已完成进度条的颜色
        mPaint.setColor(mReachColor);
        // 设置画笔的宽度
        mPaint.setStrokeWidth(mReachHeight);
        canvas.drawLine(0, 0, reachWidth, 0, mPaint);

        /**
         * 画文本
         */
        // 设置画笔颜色
        mPaint.setColor(mTextColor);
        // 设置画笔宽度
        mPaint.setStrokeWidth(mTextSize);
        int y = (int) (Math.abs(mPaint.descent() + mPaint.ascent()) / 2);
        canvas.drawText(text, reachWidth + mTextOffset, y, mPaint);

        /**
         * 画未完成的进度条
         */
        if (reachWidth < reachMaxWidth) {
            // 设置画笔颜色
            mPaint.setColor(mUnReachColor);
            // 设置画笔的宽度
            mPaint.setStrokeWidth(mUnReachHeight);
            canvas.drawLine(reachWidth + textWidth + mTextOffset * 2, 0, mProgressWidth, 0, mPaint);
        }

        // 不知道干嘛用的
        canvas.restore();
    }

    /**
     * 获取到文本的宽度
     *
     * @param paint
     * @param str
     * @return
     */
    public static int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

    /**
     * dp转px
     *
     * @param dpValue dp的值
     * @return
     */
    protected int dp2px(int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param spValue sp的值
     * @return
     */
    private int sp2px(int spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, getResources().getDisplayMetrics());
    }

}
