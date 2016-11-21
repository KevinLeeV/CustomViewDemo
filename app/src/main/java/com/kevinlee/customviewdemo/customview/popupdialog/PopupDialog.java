package com.kevinlee.customviewdemo.customview.popupdialog;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * ClassName:PopupDialog
 * Description:泡泡对话框
 * Author:KevinLee
 * Date:2016/11/1 0001
 * Time:下午 1:56
 * Email:KevinLeeV@163.com
 */
public class PopupDialog extends View {

    // 对话框中的文本
    private String mTextValue = new String();

    // 对话框中文本的颜色
    private int mTextColor;

    // 对话框中文本的大小
    private int mTextSize;

    //对话框颜色
    private int mDialogColor;

    // 文本的大小范围
    private Rect mBounds;

    // 对话框显示的最大的宽度
    private int maxWidth = 500;

    //对话框显示的最小宽度
    private int minWidth = 200;

    //对话框显示的最小高度
    private int minHeight = 100;

    //画笔
    private Paint mPaint;

    // 内边距
    private int padding = 20;

    // 圆角矩形的弧度半径
    private int radius = 10;

    // 小三角的高度
    private int triangleHeight = 20;

    // 文本显示的行间距
    private int lineSpace = 5;

    // 是否显示
    protected boolean isShow = false;

    // 文本的长度是否大于最大宽度
    private boolean isTooLong = false;

    // 显示的位置的x坐标
    private float x;

    //显示的位置的y坐标
    private float y;

    // 小三角的x坐标
    private float triangleX;

    // 小三角的y坐标
    protected float triangleY;

    // 屏幕宽度
    private int screenWidth;

    // 屏幕高度
    private int screenHeight;

    // 小三角形箭头是否在下方
    protected boolean isTriangleBottom = true;

    public PopupDialog(Context context) {
        this(context, null);
    }

    public PopupDialog(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopupDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getScreenSize();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取宽度的测量模式与尺寸
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        // 获取高度的测量模式与尺寸
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;//宽度
        int height;//高度
        mBounds = new Rect();
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            // 如果文本内容不为空
            if (!TextUtils.isEmpty(mTextValue)) {
                // 获取到文本的大小范围
                mPaint.getTextBounds(mTextValue, 0, mTextValue.length(), mBounds);
                // 如果文本的大小范围超过最大宽度，则需要将文本换行显示，则宽度为最大宽度
                // 如果文本的大小范围未超过最大宽度，则宽度为文本宽度加内边距
                if (mBounds.width() > maxWidth) {
                    isTooLong = true;
                    width = maxWidth;
                } else
                    width = mBounds.width() + padding * 2;
            } else {
                // 如果文本为空，宽度为最小宽度
                width = minWidth;
            }
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            if (!TextUtils.isEmpty(mTextValue)) {
                // 获取到文本的大小范围
                mPaint.getTextBounds(mTextValue, 0, mTextValue.length(), mBounds);
                // 如果文本的大小范围超过最大宽度，则需要将文本换行显示，高度为文本换行后的高度加内边距，再加小三角的高度与几行文本的行间距
                // 如果文本的大小范围未超过最大宽度，则高度为文本高度加内边距，再加小三角的高度与几行文本的行间距
                if (mBounds.width() > maxWidth) {
                    isTooLong = true;
                    int count = mBounds.width() / maxWidth;
                    int value = mBounds.width() % maxWidth;
                    if (value > 0)
                        ++count;
                    height = mBounds.height() * count + padding * 2 + triangleHeight + lineSpace * (count - 1);
                } else
                    height = mBounds.height() + padding * 2 + triangleHeight;
            } else {
                // 如果文本为空，高度为最小高度，再加小三角的高度与几行文本的行间距
                height = minHeight + triangleHeight;
            }
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isShow) {
            drawDialog(canvas);
            drawText(canvas);
            showInLoc(x, y);
        }
        super.onDraw(canvas);
    }

    /**
     * 绘制文本
     *
     * @param canvas 画布
     */
    private void drawText(Canvas canvas) {

        /**
         * 绘制文本时区分小三角形箭头在上和在下的情况
         */
        int paddingTop;
        if (isTriangleBottom)
            paddingTop = padding;
        else
            paddingTop = padding + triangleHeight;

        // 设置画笔颜色为文本颜色，绘制文本
        mPaint.setColor(mTextColor);
        /**
         * 如果文本的长度没有超过最大宽度，则直接画出
         */
        if (!isTooLong) {
            canvas.drawText(mTextValue, padding, mBounds.height() + paddingTop, mPaint);
        } else {
            /**
             * 文本长度如果超过最大宽度，则需要将文本分段画出
             */
            // 获取到对话框一行能显示的最大字符数
            Rect bounds = new Rect();
            int len = 0;// 记录最大字符数
            for (int i = 0; i < mTextValue.length(); i++) {
                mPaint.getTextBounds(mTextValue, 0, mTextValue.length() - i, bounds);
                if (bounds.width() < maxWidth - 2 * padding) {
                    len = mTextValue.length() - i;
                    break;
                }
            }
            int index = 0; // 记录字符串截取的位置
            int lines = 0; // 记录截取后一共多少行字符串
            while (index < mTextValue.length() - len) {
                String str = mTextValue.substring(index, index + len);
                index += len;
                lines++;
                canvas.drawText(str, padding, mBounds.height() * lines + paddingTop + lineSpace * (lines - 1), mPaint);
            }
            // 判断字符串截取的位置是否小于字符串的长度，如果小于，再截取最后一段字符串
            if (index < mTextValue.length()) {
                String str = mTextValue.substring(index, mTextValue.length());
                lines++;
                canvas.drawText(str, padding, mBounds.height() * lines + paddingTop + lineSpace * (lines - 1), mPaint);
            }

        }
    }

    /**
     * 绘制对话框
     *
     * @param canvas 画布
     */
    private void drawDialog(Canvas canvas) {
        // 设置画笔颜色为对话框颜色，画对话框
        mPaint.setColor(mDialogColor);
        Path path = new Path();
        /**
         * 小三角形在下的对话框
         */
        if (isTriangleBottom) {
            // 画圆角矩形，顶着坐标原点开始画
            path.addRoundRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - triangleHeight, radius, radius, Path.Direction.CW);
            /**
             * 画三角形箭头
             * 判断对话框是否超出屏幕范围，如果超出，则小三角的位置随手指点击位置移动
             * 1.对话框超出左边屏幕范围，则小三角的x坐标即手指点击的x坐标
             * 2.对话框超出右边屏幕范围，则小三角的x坐标即手指点击的x坐标减去对话框最左边的x坐标
             * y坐标不变，为整个canvas区域的高度
             */
            path.moveTo(triangleX, triangleY);
            path.lineTo(triangleX + 20, triangleY - 20);
            path.lineTo(triangleX - 20, triangleY - 20);
            path.close();
            canvas.drawPath(path, mPaint);
        } else {
            /**
             * 三角形箭头在上的对话框
             */
            // 画圆角矩形，顶着坐标原点开始画
            path.addRoundRect(0, triangleHeight, getMeasuredWidth(), getMeasuredHeight(), radius, radius, Path.Direction.CW);
            /**
             * 画三角形箭头
             * 当小三角形箭头在上时
             * y坐标不变，为0
             */
            path.moveTo(triangleX, triangleY);
            path.lineTo(triangleX + 20, triangleY + 20);
            path.lineTo(triangleX - 20, triangleY + 20);
            path.close();
            canvas.drawPath(path, mPaint);
        }
    }

    // 设置对话框中的文本
    public void setmTextValue(String mTextValue) {
        this.mTextValue = mTextValue;
    }

    // 设置对话框的颜色
    public void setmDialogColor(int mDialogColor) {
        this.mDialogColor = mDialogColor;
    }

    // 设置对话框中文本的颜色
    public void setmTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    // 设置对话框中文本的大小

    public void setmTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
    }

    // 显示view
    public void show() {
        isShow = true;
        initDialog();
    }

    /**
     * 设置对话框显示的位置
     */
    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
        postInvalidate();
    }

    /**
     * 在这个位置显示
     */
    private void showInLoc(float x, float y) {
        x = calculateX(x);
        y = calculateY(y);
        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(getLayoutParams());
        marginLayoutParams.setMargins((int) x, (int) y, marginLayoutParams.width, marginLayoutParams.height);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginLayoutParams);
        setLayoutParams(layoutParams);
    }

    /**
     * 计算最终y坐标
     *
     * @param y
     * @return
     */
    protected float calculateY(float y) {
        y -= getMeasuredHeight();
        if (y < 10) {
            y = 10;
            isTriangleBottom = false;
            triangleY = 0;
        } else if (y > screenHeight) {
            y = screenHeight - 10;
            isTriangleBottom = true;
            triangleY = getMeasuredHeight();
        } else {
            isTriangleBottom = true;
            triangleY = getMeasuredHeight();
        }

        return y;
    }

    /**
     * 计算x最终坐标
     *
     * @param x
     */
    protected float calculateX(float x) {
        // 将位置设置为小三角形的坐标
        x -= getMeasuredWidth() / 2;
        /**
         * 判断对话框是否超出屏幕范围，如果超出则将对话框显示在屏幕边缘
         */
        if (x < 0) {
            x = 10;
            // 屏幕超出左边范围后，再计算手指点击的x坐标是否小于40，若小于40，则小三角的x坐标等于40
            triangleX = this.x > 40 ? this.x : 40;
        } else if (x > screenWidth - getMeasuredWidth()) {
            x = screenWidth - getMeasuredWidth() - 10;
            // 屏幕超出右边范围后，再计算手指点击的x坐标距离右侧屏幕是否小于40，若小于40，则小三角的x坐标等于getMeasuredWidth() - 40
            triangleX = this.x - x > getMeasuredWidth() - 40 ? getMeasuredWidth() - 40 : this.x - x;
        } else
            triangleX = getMeasuredWidth() / 2;
        return x;
    }

    /**
     * 初始化对话框
     */
    public void initDialog() {
        mPaint = new Paint();
        // 设置默认文本大小为16sp
        if (mTextSize == 0)
            mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics());
        // 设置默认文本颜色为白色
        if (mTextColor == 0)
            mTextColor = Color.WHITE;
        // 设置默认对话框颜色为蓝色
        if (mDialogColor == 0)
            mDialogColor = Color.parseColor("#2C97DE");
        // 设置画笔的文本大小
        mPaint.setTextSize(mTextSize);
        mPaint.setAntiAlias(true);// 消除锯齿
        mPaint.setDither(true);//设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
    }

    /**
     * 获取屏幕的宽高
     */
    private void getScreenSize() {
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
    }
}
