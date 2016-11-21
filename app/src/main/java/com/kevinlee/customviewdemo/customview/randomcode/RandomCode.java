package com.kevinlee.customviewdemo.customview.randomcode;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.kevinlee.customviewdemo.R;

/**
 * ClassName:RandomCode
 * Description: 随机验证码
 * Author:KevinLee
 * Date:2016/10/31 0031
 * Time:下午 2:33
 * Email:KevinLeeV@163.com
 */
public class RandomCode extends View {

    // 自定义View的内容
    private String mTextValue;
    // 自定义View的颜色
    private int mTextColor;
    // 自定义View的尺寸
    private int mTextSize;

    /**
     * 绘制时控制文本绘制的范围
     */
    private Rect mBound;
    private Paint mPaint;

    // 旋转角度
    private int angle;

    // 是否显示view
    private boolean isShow = false;

    // 验证码类型
    private CodeType codeType;

    // 所有的字母与数字的字符串
    private static final String ORIGINALSTR = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM0123456789";

    // 回调接口
    private OnGetTextListener mListener;

    /**
     * 因为系统默认调用带两个参数的构造方法，
     * 所以将一个参数和两个参数的构造方法使用this关键字，
     * 这样就会调用带有三个参数的构造方法，
     * 我们在带有三个参数的构造方法中定义View的属性
     */

    public RandomCode(Context context) {
        this(context, null);
    }

    public RandomCode(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RandomCode(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /**
         * 获得我们所定义的自定义属性
         */
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RandomCode, defStyleAttr, 0);
        int indexCount = typedArray.getIndexCount();
        /**
         * 遍历所有自定义属性，并获取相对应的值
         */
        for (int i = 0; i < indexCount; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.RandomCode_textColor:
                    //获取attr的值，并设置默认颜色为黑色
                    mTextColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.RandomCode_textSize:
                    // 将获取到的attr的值转为px，并设置默认尺寸为16sp
                    mTextSize = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 30, getResources().getDisplayMetrics()));
                    break;
            }
        }
        // 释放
        typedArray.recycle();

        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                refresh();
            }

        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;

        /**
         * EXACTLY：一般是设置了明确的值或者是MATCH_PARENT
         * AT_MOST：表示子布局限制在一个最大值内，一般为WARP_CONTENT
         * UNSPECIFIED：表示子布局想要多大就多大，很少使用
         */
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            if (!TextUtils.isEmpty(mTextValue)) {
                float textWidth = mBound.width();
                int desired = (int) (getPaddingLeft() + textWidth + getPaddingRight());
                width = desired;
            } else {
                width = 212;
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            if (!TextUtils.isEmpty(mTextValue)) {
                float textHeight = mBound.height();
                int desired = (int) (getPaddingTop() + textHeight + getPaddingBottom());
                height = desired;
            } else {
                height = 98;
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isShow) {
            // 设置画笔的颜色为黄色,然后绘制成底色
            mPaint.setColor(Color.GRAY);
            canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);

            // 设置画笔绘制的颜色为字的颜色，然后绘制字
            mPaint.setColor(mTextColor);
            mPaint.setTextAlign(Paint.Align.CENTER);

            // 获取到颜色合集
            int[] colors = colorArray();

            drawText(canvas, mTextValue, (getHeight() + mBound.height()) / 2, colors, mPaint);
            drawLine(canvas, colors, mPaint);
            drawCircle(canvas, colors, mPaint, 2);
        }
    }

    /**
     * 画数字或英文等字符串
     * 先将一串字符串取出每个字符，将整个画布的宽度分为8个部分，4个字符分别在1/8处、3/8处、5/8处、7/8处
     * 然后获得一个随机旋转角度，旋转画布
     * 在画布上画出字符，
     * 最后一步，一定要有，将画布按照负角度再旋转回来，要不然，字符串会跑偏
     *
     * @param canvas 画布
     * @param text   字符串
     * @param y      y坐标（baseline坐标）
     * @param colors 颜色合集，作为随机取色用
     * @param paint  画笔
     */
    private void drawText(Canvas canvas, String text, float y, int[] colors, Paint paint) {
        for (int i = 0; i < text.length(); i++) {
            if (mTextColor == 0) {
                //设置画笔颜色
                paint.setColor(randomColor(colors));
            }
            String data = String.valueOf(text.charAt(i));
            float x = getWidth() / 8 * (2 * i + 1);
            angle = randomAngle();
            if (angle != 0) {
                canvas.rotate(angle, x, y);
            }
            canvas.drawText(data, x, y, paint);
            canvas.rotate(-angle, x, y);
        }
    }

    /**
     * 画直线噪点
     *
     * @param canvas 画布
     * @param paint  画笔
     * @param colors 颜色合集，作为随机取色用
     */
    private void drawLine(Canvas canvas, int[] colors, Paint paint) {
        // 设置画笔空心宽度
        paint.setStrokeWidth(2);
        // 随机取3-4条直线
        int count = (int) Math.random() * 2 + 3;
        // View的区域范围
        int width = getWidth();
        int height = getHeight();
        /**
         * 随机获取到直线开始的坐标和直线结束的坐标
         */
        for (int i = 0; i < count; i++) {
            //设置画笔颜色
            paint.setColor(randomColor(colors));
            int startX = (int) (Math.random() * width);
            int startY = (int) (Math.random() * height);
            int endX = (int) (Math.random() * width);
            int endY = (int) (Math.random() * height);
            canvas.drawLine(startX, startY, endX, endY, paint);
        }
    }

    /**
     * 画圆形噪点
     *
     * @param canvas 画布
     * @param paint  画笔
     * @param radius 半径
     * @param colors 颜色合集，作为随机取色用
     */
    private void drawCircle(Canvas canvas, int[] colors, Paint paint, float radius) {
        // 画20个圆形噪点
        for (int i = 0; i < 20; i++) {
            //设置画笔颜色
            paint.setColor(randomColor(colors));
            // 圆形坐标
            int x = (int) (Math.random() * getWidth());
            int y = (int) (Math.random() * getHeight());
            canvas.drawCircle(x, y, radius, paint);
        }
    }

    /**
     * 颜色合集
     */
    private int[] colorArray() {
        return new int[]{Color.BLACK, Color.BLUE, Color.CYAN, Color.DKGRAY, Color.GREEN, Color.LTGRAY, Color.MAGENTA, Color.RED, Color.WHITE, Color.YELLOW};
    }

    /**
     * 随机取色
     *
     * @return
     */
    private int randomColor(int[] colors) {
        return colors[(int) (Math.random() * colors.length)];
    }

    /**
     * 随机角度(-40度~+40度)
     */
    private int randomAngle() {
        return (int) (4 - Math.random() * 8) * 10;
    }

    /**
     * 随机数字与英文字母
     *
     * @return
     */
    private String randomText() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            if (codeType == CodeType.ALL)
                sb.append(ORIGINALSTR.charAt((int) (Math.random() * ORIGINALSTR.length()))); //所有
            else if (codeType == CodeType.LETTER)
                sb.append(ORIGINALSTR.charAt((int) (Math.random() * (ORIGINALSTR.length() - 10)))); // 大小写字母
            else if (codeType == CodeType.LOWERCASE)
                sb.append(ORIGINALSTR.charAt((int) (Math.random() * (ORIGINALSTR.length() - 36)))); // 小写字母
            else if (codeType == CodeType.NUM)
                sb.append((int) (Math.random() * 10));// 全数字
            else if (codeType == CodeType.UPPERCASE)
                sb.append(ORIGINALSTR.charAt((int) (Math.random() * (ORIGINALSTR.length() - 35) + 26))); // 大写字母
            else
                sb.append(ORIGINALSTR.charAt((int) (Math.random() * ORIGINALSTR.length()))); // 当没指定类型时，默认为所有
        }
        mListener.getText(sb.toString());
        return sb.toString();
    }

    // 设置随机码颜色
    public void setmTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }
    // 设置随机码大小
    public void setmTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
    }

    /**
     * 设置验证码类型
     *
     * @param type
     */
    public void setCodeType(CodeType type) {
        this.codeType = type;

    }

    /**
     * 设置接口回调
     */
    public void setOnGetTextListener(OnGetTextListener listener) {
        this.mListener = listener;
    }

    /**
     * 显示View
     */
    public void show() {
        this.isShow = true;
        initCode();
    }

    /**
     * 初始化验证码
     */
    private void initCode() {
        mTextValue = randomText();

        mPaint = new Paint();
        mPaint.setAntiAlias(true); // 消除锯齿
        mPaint.setDither(true);// 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        /**
         * 当xml文件中没有对textSize、textColor属性做设置，则在下面做默认值设置
         */
        if (mTextSize == 0) {
            // 设置默认尺寸为30sp
            mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 30, getResources().getDisplayMetrics());
        }
        // 设置画笔绘制的字体大小
        mPaint.setTextSize(mTextSize);
        // 设置文本的大小范围
        mBound = new Rect();
        // 获取内容的范围,并设置到mBound中
        mPaint.getTextBounds(mTextValue, 0, mTextValue.length(), mBound);
    }

    /**
     * 刷新验证码的方法
     */
    public void refresh() {
        mTextValue = randomText();
        postInvalidate();
    }
}
