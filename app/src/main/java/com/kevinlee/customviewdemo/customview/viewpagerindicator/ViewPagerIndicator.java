package com.kevinlee.customviewdemo.customview.viewpagerindicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kevinlee.customviewdemo.R;

import java.util.List;

/**
 * ClassName:ViewPagerIndicator
 * Description: viewpager指示器
 * Author:KevinLee
 * Date:2016/11/18 0018
 * Time:下午 5:50
 * Email:KevinLeeV@163.com
 */
public class ViewPagerIndicator extends LinearLayout {

    private static final int DEFAULT_VISIBLE_COUNT = 4;// 设置默认的可见tab数量
    private static final int DEFAULT_TEXT_SIZE = 16;// 默认的字体大小
    private static final int DEFAULT_TEXT_COLOR = 0X77FFFFFF;// 默认的字体颜色
    private static final int DEFAULT_TEXT_HIGHLIGHT_COLOR = 0XFFFFFFFF;// 默认字体的高亮颜色
    private static final int DEFAULT_TAB_LINE_HEIGHT = 10;// 默认的横线下标的高度
    private static final int DEFAULT_TRIANGLE_COLOR = 0XFFFFFFFF;// 默认的小三角形的颜色
    private static final int DEFAULT_LINE_COLOR = 0XFF880000;// 默认的横线下标的颜色
    private static final float DEFAULT_PERCENT_TRIANGLE = 1 / 6F;// 小三角形的宽度相对于每个标签宽度的百分比
    private static final float DEFAULT_PERCENT_LINE = 2 / 3F;//横线下标的宽度相对于每个标签宽度的百分比

    private static final int STYLE_TRIANGLE = 0; // 小三角类型
    private static final int STYLE_LINE = 1; // 横线类型

    private int mSubScriptWidth;// 下标的宽度
    private float mSubScriptHeight;// 下标的高度
    private int mInitSubScriptX;// 下标的初始位置x坐标
    private int mSubScriptX;// 下标移动的x坐标
    private int mSubScriptColor;// 下标的颜色

    //    private int mTriangleWidth;// 小三角形的高度
//    private int mInitTriangleX;// 小三角形的初始位置的x坐标
//    private float mTriangleHeight;// 小三角形的高度
//    private int mTranslateX;// 小三角形移动的x坐标
//    private int mTriangleColor = 0XFFFFFFFF;// 小三角形的颜色
    private int mTabVisibleCount = DEFAULT_VISIBLE_COUNT;// 一行可见的tab的数量
    private int mTabWidth;// 每个可见的tab的宽度
    private int mTextSize = DEFAULT_TEXT_SIZE;
    private int mTextColor = DEFAULT_TEXT_COLOR;
    private int mTextHighLightColor = DEFAULT_TEXT_HIGHLIGHT_COLOR;
    private int triangleMaxWidth = (int) (getScreenWidth() / 3 * DEFAULT_PERCENT_TRIANGLE);// 小三角形的最大宽度

    private boolean isTriangle = true;// 判断下标是否是小三角形

    private ViewPager mViewPager;
    private OnPagerChangeListener mListener;

    private Paint mPaint;// 画笔
    private Path mPath;// 用于绘制小三角形的路径

    private int prePosition;// 上一个被选中的tab下标

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 获取自定义的属性,可见的tab数量
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        // 获取tab下标的类型
        int tabStyle = typedArray.getInt(R.styleable.ViewPagerIndicator_tab_line_style, STYLE_TRIANGLE);
        mTabVisibleCount = typedArray.getInt(R.styleable.ViewPagerIndicator_visible_tab_count, DEFAULT_VISIBLE_COUNT);
        switch (tabStyle) {
            case STYLE_TRIANGLE:
                isTriangle = true;
                break;
            case STYLE_LINE:
                isTriangle = false;
                break;
        }
        // 初始化下标的颜色
        mSubScriptColor = isTriangle ? DEFAULT_TRIANGLE_COLOR : DEFAULT_LINE_COLOR;
        mSubScriptColor = typedArray.getColor(R.styleable.ViewPagerIndicator_subscript_color, mSubScriptColor);
        typedArray.recycle();

        if (mTabVisibleCount < 1) {
            mTabVisibleCount = DEFAULT_VISIBLE_COUNT;
        }

        // 初始化画笔
        initPaint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 根据style设置下标的宽度的百分比
        float widthPercent = isTriangle ? DEFAULT_PERCENT_TRIANGLE : DEFAULT_PERCENT_LINE;
        // 根据可见的tab数量以及比例来获取下标的宽度
        mSubScriptWidth = (int) (w / mTabVisibleCount * widthPercent);
        mSubScriptWidth = isTriangle ? (Math.min(mSubScriptWidth, triangleMaxWidth)) : mSubScriptWidth;
        // 获取下标的高度
        mSubScriptHeight = isTriangle ? (float) (mSubScriptWidth / 2.0f / Math.sqrt(3)) : DEFAULT_TAB_LINE_HEIGHT;
        // 获取下标初始的x坐标
        mInitSubScriptX = w / mTabVisibleCount / 2 - mSubScriptWidth / 2;
        if (isTriangle)
            initTriangle();


//        // 根据可见的tab数量以及比例来获取小三角形的宽度
//        mTriangleWidth = (int) (w / mTabVisibleCount * DEFAULT_PERCENT_TRIANGLE);
//        mTriangleWidth = Math.min(mTriangleWidth, triangleMaxWidth);
//        // 获取小三角形的高度
//        mTriangleHeight = (float) (mTriangleWidth / 2.0f / Math.sqrt(3));
//        // 获取小三角形初始的x坐标
//        mInitTriangleX = w / mTabVisibleCount / 2 - mTriangleWidth / 2;
//        // 根据已有的数据初始化小三角形
//        initTriangle();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        canvas.save();
        // getHeight为这个ViewPagerIndicator的高度
        canvas.translate(mSubScriptX + mInitSubScriptX, getHeight());
        if (isTriangle)
            canvas.drawPath(mPath, mPaint);
        else
            canvas.drawLine(0, -mSubScriptHeight / 2, mSubScriptWidth, -mSubScriptHeight / 2, mPaint);

        canvas.restore();

        super.dispatchDraw(canvas);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(mSubScriptColor);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        if (isTriangle) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setPathEffect(new CornerPathEffect(3));// 设置path效果,圆角效果,使得小三角形不会太尖锐
        } else {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(6);
        }

    }

    /**
     * 初始化小三角形
     */
    private void initTriangle() {
        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(mSubScriptWidth, 0);
        mPath.lineTo(mSubScriptWidth / 2, -mSubScriptHeight);
        mPath.close();
    }

    /**
     * 滑动操作指示器,实现小三角形的移动
     *
     * @param position       滑动到第几个tab
     * @param positionOffset 移动的比例
     */
    public void scroll(int position, float positionOffset) {
        // getWidth()为这个ViewPagerIndicator的整个的宽度,包括隐藏部分
        mTabWidth = getWidth() / mTabVisibleCount;
        mSubScriptX = (int) (mTabWidth * (position + positionOffset));

        // 获取到子view的数量
        int childCount = getChildCount();

        if (position >= mTabVisibleCount - 2 && childCount > mTabVisibleCount && positionOffset > 0 && position < childCount - 2) {
            int x;
            if (mTabVisibleCount > 1) {
                // 当可见的tab数量大于1时
                // 当移动到可见的最后一个tab时,需要将整个ViewPagerIndicator向左移动一定的距离
                // 距离就等于隐藏的tab的总宽度+手指滑动的当前tab的距离
                x = (int) ((position - (mTabVisibleCount - 2)) * mTabWidth + mTabWidth * positionOffset);
            } else {
                // 当可见的tab数量等于1时
                x = (int) (position * mTabWidth + positionOffset * mTabWidth);
            }
            scrollTo(x, 0);
        }

        // 设置被选中的tab的文本颜色为高亮
        setTextHighLightColor(position);

        // 主线程中调用重绘方法
        invalidate();

    }

    /**
     * 设置可见的tab数量
     * 在setTabItemTitles()方法之前设置,否则无效
     *
     * @param count
     */
    public void setVisibleTabCount(int count) {
        mTabVisibleCount = count;
    }

    /**
     * 设置text的字体大小
     * 在setTabItemTitles()方法之前设置,否则无效
     *
     * @param textSize
     */
    public void setTextSize(int textSize) {
        mTextSize = textSize;
    }

    /**
     * 设置text的正常为选中颜色
     * 在setTabItemTitles()方法之前设置,否则无效
     *
     * @param textColor
     */
    public void setTextNormalColor(int textColor) {
        mTextColor = textColor;
    }

    /**
     * 为某个textView设置颜色
     *
     * @param color
     * @param pos
     */
    private void setTextColor(int color, int pos) {
        View view = getChildAt(pos);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(color);
        }
    }

    /**
     * 将选中的tab文本颜色设置为高亮,其他的设置为正常颜色
     *
     * @param pos
     */
    private void setTextHighLightColor(int pos) {
        setTextColor(mTextHighLightColor, pos);
        if (prePosition != pos) {
            setTextColor(mTextColor, prePosition);
            prePosition = pos;
        }
    }

    /**
     * 动态添加tab的文本
     *
     * @param titles 文本的数组
     */
    public void setTabItemTitles(String[] titles) {
        if (titles != null && titles.length > 0) {
            for (int i = 0; i < titles.length; i++) {
                String title = titles[i];
                addView(generateTextView(title, i));
            }
        }
    }

    /**
     * 动态添加tab的文本
     *
     * @param titles 文本的集合
     */
    public void setTabItemTitles(List<String> titles) {
        if (titles != null && titles.size() > 0) {
            for (int i = 0; i < titles.size(); i++) {
                String title = titles.get(i);
                addView(generateTextView(title, i));
            }
        }
    }

    /**
     * 根据title生成相应的textview
     *
     * @param title
     * @return
     */
    private View generateTextView(String title, final int pos) {
        TextView tv = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth() / mTabVisibleCount;
        tv.setText(title);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
        tv.setTextColor(mTextColor);
        tv.setLayoutParams(lp);
        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPager != null)
                    mViewPager.setCurrentItem(pos);
                // 设置被选中的tab的文本颜色为高亮
                setTextHighLightColor(pos);
            }
        });
        return tv;
    }

    /**
     * 获取屏幕宽度
     */
    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * 设置ViewPager
     * 将viewpager实现接口回调的过程封装到这里
     *
     * @param vp
     */
    public void setViewPager(ViewPager vp) {
        setViewPager(vp, 0);
    }

    /**
     * 设置ViewPager
     * 将viewpager实现接口回调的过程封装到这里
     * 重载方法,当用户需要指定初始化的tab位置
     *
     * @param vp
     */
    public void setViewPager(ViewPager vp, int pos) {
        mViewPager = vp;
        if (mViewPager != null)
            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    scroll(position, positionOffset);
                    // 添加判断,防止用户没有实现接口的需要
                    if (mListener != null)
                        mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }

                @Override
                public void onPageSelected(int position) {
                    // 添加判断,防止用户没有实现接口的需要
                    if (mListener != null)
                        mListener.onPageSelected(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    // 添加判断,防止用户没有实现接口的需要
                    if (mListener != null)
                        mListener.onPageScrollStateChanged(state);
                }
            });
        mViewPager.setCurrentItem(pos);
        // 设置被选中的tab的文本颜色为高亮
        setTextHighLightColor(pos);
    }

    /**
     * 自定义一个viewpager页面改变的接口供用户使用
     * 因为我们把viewpager自带的接口给封装了,这样用户没法实现他想实现的效果
     * 只有我们提供了这个接口,他才可以使用
     */
    public interface OnPagerChangeListener {

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        public void onPageSelected(int position);

        public void onPageScrollStateChanged(int state);
    }

    /**
     * 设置接口
     *
     * @param listener
     */
    public void setOnPagerChangeListener(OnPagerChangeListener listener) {
        mListener = listener;
    }
}
