package com.kevinlee.customviewdemo.customview.drawermenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.kevinlee.customviewdemo.R;

/**
 * ClassName:DrawerMenu
 * Description: 抽屉菜单栏
 * Author:KevinLee
 * Date:2016/11/2 0002
 * Time:下午 5:40
 * Email:KevinLeeV@163.com
 */
public class DrawerMenu extends ViewGroup implements View.OnClickListener {

    private static final int POS_LEFT_TOP = 0; // 左上角
    private static final int POS_LEFT_BOTTOM = 1; // 左下角
    private static final int POS_RIGHT_TOP = 2; // 右上角
    private static final int POS_RIGHT_BOTTOM = 3; // 右下角

    private Position position = Position.LEFT_TOP;// 设置默认位置为左上角

    private OnMenuItemClickListener mListener;

    private int padding; // 设置边距

    private View mButton;// 主button

    private Status mCurrentStatus = Status.CLOSE; // 默认状态为关闭

    /**
     * item的点击事件
     */
    private interface OnMenuItemClickListener {
        void onClick(View view, int position);
    }

    public DrawerMenu(Context context) {
        this(context, null);
    }

    public DrawerMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawerMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 设置默认padding为20dp
        padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DrawerMenu, defStyleAttr, 0);
        // 获取属性中定义的padding值
        padding = (int) typedArray.getDimension(R.styleable.DrawerMenu_padding, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
        // 获取属性xml文件中定义的位置
        int pos = typedArray.getInt(R.styleable.DrawerMenu_position, POS_LEFT_TOP);
        switch (pos) {
            case POS_LEFT_TOP:
                position = Position.LEFT_TOP;
                break;
            case POS_LEFT_BOTTOM:
                position = Position.LEFT_BOTTOM;
                break;
            case POS_RIGHT_TOP:
                position = Position.RIGHT_TOP;
                break;
            case POS_RIGHT_BOTTOM:
                position = Position.RIGHT_BOTTOM;
                break;
        }
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        for (int i = 0; i < getChildCount(); i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            // 首先设置主button
            setMainButton();
            setItemButton();
        }
    }

    /**
     * 设置其他子itemButton
     */
    private void setItemButton() {
        int l = 0;// left
        int t = 0; // top
        int width = mButton.getMeasuredWidth(); // 主button的宽,因为所有的button宽高一致
        int height = mButton.getMeasuredHeight(); // 主button的高,因为所有的button宽高一致
        /**
         * 当主button在左上角或左下角时
         */
        if (position == Position.LEFT_BOTTOM || position == Position.LEFT_TOP)
            l = padding;
        else
            //当主button在右上角或右下角时
            l = getMeasuredWidth() - padding - width;
        /**
         * 因为childCount中包含主button，所以现在从i=1开始取，不取主button
         */
        for (int i = 1; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            // 隐藏所有子item
            childView.setVisibility(View.GONE);
            // 当主button在右上角或左上角时
            if (position == Position.LEFT_TOP || position == Position.RIGHT_TOP) {
                t = i * height + padding;
            } else {
                // 当主button在左下角或右下角时
                t = getMeasuredHeight() - padding - (i + 1) * height;
            }
            childView.layout(l, t, l + width, t + height);
        }
    }

    /**
     * 首先设置主button
     */
    private void setMainButton() {
        // 第一步，得到主button,默认第一个子view为主button
        mButton = getChildAt(0);
        mButton.setOnClickListener(this);
        int l = 0;// left
        int t = 0; // top
        int width = mButton.getMeasuredWidth(); // 主button的宽
        int height = mButton.getMeasuredHeight(); // 主button的高
        // 根据position位置不同，将主button设置到不同位置上
        switch (position) {
            case LEFT_TOP:
                l = padding;
                t = padding;
                break;
            case LEFT_BOTTOM:
                l = padding;
                t = getMeasuredHeight() - padding - height;
                break;
            case RIGHT_TOP:
                l = getMeasuredWidth() - padding - width;
                t = padding;
                break;
            case RIGHT_BOTTOM:
                l = getMeasuredWidth() - padding - width;
                t = getMeasuredHeight() - padding - height;
                break;
        }
        mButton.layout(l, t, l + width, t + height);

    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        toggleMenu(500);
    }

    /**
     * 开关menu
     *
     * @param duration 动画显示时间
     */
    public void toggleMenu(int duration) {
        /**
         * 因为childCount中包含主button，所以现在从i=1开始取，不取主button
         */
        for (int i = 1; i < getChildCount(); i++) {
            final View childView = getChildAt(i);
            // 显示所有子item
            childView.setVisibility(View.VISIBLE);

            // 使用yFlag来标识平移动画方向是坐标轴正方向还是负方向
            int yFlag = 1; // 1:正方向；-1：负方向

            // 当位置在左上角或右上角时，关闭item是负方向
            if (position == Position.LEFT_TOP || position == Position.RIGHT_TOP)
                yFlag = -1;

            //平移的距离为一个view 的高度
            int ct = childView.getMeasuredHeight();

            AnimationSet animaSet = new AnimationSet(true);

            TranslateAnimation transAnim;
            AlphaAnimation alphaAnim;
            // to open
            if (mCurrentStatus == Status.CLOSE) {
                // 打开时动画，应该从隐藏的位置到view的位置
                transAnim = new TranslateAnimation(0, 0, yFlag * ct, 0);
                transAnim.setStartOffset(duration * (i - 1)); //  设置动画开始延时
                alphaAnim = new AlphaAnimation(0f, 1.0f);
                alphaAnim.setStartOffset(duration * (i - 1)); //  设置动画开始延时
            } else {
                // to close
                // 关闭时动画，应该从view的位置到隐藏的位置
                transAnim = new TranslateAnimation(0, 0, 0, yFlag * ct);
                transAnim.setStartOffset(duration * (getChildCount() - 1 - i));//  设置动画开始延时
                alphaAnim = new AlphaAnimation(1.0f, 0f);
                alphaAnim.setStartOffset(duration * (getChildCount() - 1 - i)); //  设置动画开始延时
            }

            animaSet.addAnimation(alphaAnim);
            animaSet.addAnimation(transAnim);

            animaSet.setDuration(duration);
            animaSet.setFillAfter(true);

            animaSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // 动画结束时，隐藏item
                    if (mCurrentStatus == Status.CLOSE) {
                        // 需要先清除动画，否则，childView无法设置隐藏
                        childView.clearAnimation();
                        childView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            childView.startAnimation(animaSet);
            final int pos = i;
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null)
                        mListener.onClick(childView, pos);
                    Toast.makeText(getContext(), pos + ":" + childView.getTag(), Toast.LENGTH_LONG).show();
                }
            });

        }

        changeStatus();
    }

    /**
     * 改变状态
     */
    private void changeStatus() {
        mCurrentStatus = mCurrentStatus == Status.CLOSE ? Status.OPEN : Status.CLOSE;
    }

    /**
     * 设置item点击事件
     *
     * @param menuItemClickListener
     */
    public void setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
        this.mListener = menuItemClickListener;
    }
}
