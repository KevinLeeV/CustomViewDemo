package com.kevinlee.customviewdemo.customview.arcview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.kevinlee.customviewdemo.R;

/**
 * ClassName:ArcMenu
 * Description: 卫星菜单
 * Author:KevinLee
 * Date:2016/11/11 0011
 * Time:上午 11:47
 * Email:KevinLeeV@163.com
 */
public class ArcMenu extends ViewGroup implements View.OnClickListener {

    private static final String TAG = "ArcMenu";

    private Menu_Position position = Menu_Position.RIGHT_BOTTOM;//位置属性
    private int radius;// 子菜单半径
    private Status mCurrentStatus = Status.CLOSE;// 当前菜单的状态
    private OnMenuItemClickListener menuItemClickListener;

    private static final int POS_LEFT_TOP = 0;
    private static final int POS_LEFT_BOTTOM = 1;
    private static final int POS_RIGHT_TOP = 2;
    private static final int POS_RIGHT_BOTTOM = 3;

    public View mCButton;//主菜单按钮

    /**
     * 卫星菜单的状态
     */
    public enum Status {
        OPEN, CLOSE
    }

    /**
     * 菜单在屏幕上的位置枚举类
     */
    public enum Menu_Position {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM
    }

    /**
     * 子菜单的点击接口回调
     */
    public interface OnMenuItemClickListener {
        void onClick(View view, int pos);
    }

    /**
     * 设置接口回调
     *
     * @param menuItemClickListener
     */
    public void setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
        this.menuItemClickListener = menuItemClickListener;
    }

    public ArcMenu(Context context) {
        this(context, null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /**
         * 设置半径的默认值
         */
        radius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        /**
         * 获取自定义属性值
         */
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcMenu, defStyleAttr, 0);
        // 获取属性中的位置
        int pos = a.getInt(R.styleable.ArcMenu_position, POS_RIGHT_BOTTOM);
        switch (pos) {
            case POS_LEFT_BOTTOM:
                position = Menu_Position.LEFT_BOTTOM;
                break;
            case POS_LEFT_TOP:
                position = Menu_Position.LEFT_TOP;
                break;
            case POS_RIGHT_BOTTOM:
                position = Menu_Position.RIGHT_BOTTOM;
                break;
            case POS_RIGHT_TOP:
                position = Menu_Position.RIGHT_TOP;
                break;
        }
        // 获取属性中的半径
        radius = (int) a.getDimension(R.styleable.ArcMenu_radius, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));

        Log.i(TAG, "------>radius=" + radius + ",position=" + position);

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        /**
         * 测量各个子View
         */
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            locCButton();
            locMenuItem();
        }
    }

    /**
     * 定位子菜单按钮
     */
    private void locMenuItem() {
        int childCount = getChildCount();
        // 因为不包含主菜单，所以从下标为1开始取子菜单
        for (int i = 1; i < childCount; i++) {
            View child = getChildAt(i);
            child.setVisibility(View.GONE);
            /**
             * 子菜单的left：radius*Math.sin(a*(i-1))
             * 子菜单的top：radius*Math.cos(a*(i-1))
             * a:每两个子菜单之间的夹角大小
             * Math.PI/2:代表90度角，
             * 夹角个数==子菜单数-1
             *
             * 现在计算的是菜单在左上角的情况
             */
            int cl = (int) (radius * Math.sin(Math.PI / 2 / (childCount - 2) * (i - 1)));// 子菜单的left
            int ct = (int) (radius * Math.cos(Math.PI / 2 / (childCount - 2) * (i - 1)));// 子菜单的top
            int cWidth = child.getMeasuredWidth();
            int cHeight = child.getMeasuredHeight();

            /**
             * 当菜单在左下角或右下角时，ct发生了变化
             */
            if (position == Menu_Position.LEFT_BOTTOM || position == Menu_Position.RIGHT_BOTTOM) {
                ct = getMeasuredHeight() - ct - cHeight;
            }

            /**
             * 当菜单在右上角或右下角时，cl发生了变化
             */
            if (position == Menu_Position.RIGHT_TOP || position == Menu_Position.RIGHT_BOTTOM) {
                cl = getMeasuredWidth() - cl - cWidth;
            }

            child.layout(cl, ct, cl + cWidth, ct + cHeight);

        }
    }

    /**
     * 定位主菜单按钮
     */
    private void locCButton() {
        mCButton = getChildAt(0);
        mCButton.setOnClickListener(this);
        int l = 0;// 左
        int t = 0;// 上
        int width = mCButton.getMeasuredWidth();
        int height = mCButton.getMeasuredHeight();
        switch (position) {
            case LEFT_BOTTOM:
                l = 0;
                t = getMeasuredHeight() - height;
                break;
            case LEFT_TOP:
                l = 0;
                t = 0;
                break;
            case RIGHT_TOP:
                l = getMeasuredWidth() - width;
                t = 0;
                break;
            case RIGHT_BOTTOM:
                l = getMeasuredWidth() - width;
                t = getMeasuredHeight() - height;
                break;
        }
        mCButton.layout(l, t, l + width, t + height);
    }


    @Override
    public void onClick(View v) {
        /**
         * 为主菜单按钮添加旋转动画
         */
        rotateCButton(v, 0f, 360f, 300);
        /**
         * 为menuitem添加平移动画和旋转动画，显示开关效果
         */
        toggleMenu(300);
    }

    /**
     * 为menuitem添加平移动画和旋转动画，显示开关效果
     *
     * @param duration
     */
    public void toggleMenu(int duration) {
        int childCount = getChildCount();
        // 因为不包含主菜单，所以从下标为1开始取子菜单
        for (int i = 1; i < childCount; i++) {
            final View child = getChildAt(i);
            // 先将所有的menuitem显示可见
            child.setVisibility(View.VISIBLE);
            AnimationSet animSet = new AnimationSet(true);
            animSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    /**
                     * to close时，动画结束要将child隐藏
                     */
                    if (mCurrentStatus == Status.CLOSE)
                        child.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            /**
             * 子菜单的left：radius*Math.sin(a*(i-1))
             * 子菜单的top：radius*Math.cos(a*(i-1))
             * a:每两个子菜单之间的夹角大小
             * Math.PI/2:代表90度角，
             * 夹角个数==子菜单数-1
             *
             * 现在计算的是菜单在左上角的情况
             */
            int cl = (int) (radius * Math.sin(Math.PI / 2 / (childCount - 2) * (i - 1)));// 子菜单的left
            int ct = (int) (radius * Math.cos(Math.PI / 2 / (childCount - 2) * (i - 1)));// 子菜单的top
            int xflag = 1; // 默认为1，当子菜单的平移动画x坐标需要向负方向移动时就设为-1
            int yflag = 1;// 默认为1，当子菜单的平移动画y坐标需要向负方向移动时就设为-1
            /**
             * 当菜单在左上角或左下角时，需要将x坐标移动设为负值
             */
            if (position == Menu_Position.LEFT_TOP || position == Menu_Position.LEFT_BOTTOM) {
                xflag = -1;
            }

            /**
             * 当菜单在左上角或右上角时，需要将y坐标移动设为负值
             */
            if (position == Menu_Position.LEFT_TOP || position == Menu_Position.RIGHT_TOP) {
                yflag = -1;
            }
            Animation translateAnim = null;
            // to open
            if (mCurrentStatus == Status.CLOSE) {
                translateAnim = new TranslateAnimation(xflag * cl, 0, yflag * ct, 0);
                child.setClickable(true);
                child.setFocusable(true);
            } else {
                // to close
                translateAnim = new TranslateAnimation(0, xflag * cl, 0, yflag * ct);
                child.setClickable(false);
                child.setFocusable(false);
            }
            translateAnim.setFillAfter(true);
            translateAnim.setDuration(duration);
            translateAnim.setStartOffset((i - 1) * 100 / childCount);

            RotateAnimation rotateAnim = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnim.setFillAfter(true);
            rotateAnim.setDuration(duration);

            animSet.addAnimation(rotateAnim);
            animSet.addAnimation(translateAnim);

            child.startAnimation(animSet);

            final int pos = i;
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (menuItemClickListener != null)
                        menuItemClickListener.onClick(child, pos);
                    // 子item被点击时的动画
                    menuItemAnim(pos);
                    // 改变状态
                    changeStatus();
                }
            });

        }

        /**
         * 改变当前的状态
         */
        changeStatus();
    }

    /**
     * 子item被点击时的动画
     *
     * @param pos
     */
    private void menuItemAnim(int pos) {
        for (int i = 1; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (i == pos) {
                // 被点击的item设置放大动画
                childView.startAnimation(scaleBigAnim());
            } else
                // 其他的item设置缩小动画
                childView.startAnimation(scaleSmallAnim());

        }
    }

    /**
     * 其他的item设置缩小动画
     */
    private Animation scaleSmallAnim() {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(300);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    /**
     * 被点击的item设置放大动画
     */
    private Animation scaleBigAnim() {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(300);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    /**
     * 改变当前的状态
     */
    private void changeStatus() {
        mCurrentStatus = mCurrentStatus == Status.CLOSE ? Status.OPEN : Status.CLOSE;
    }

    /**
     * 当前状态是否打开
     */
    public boolean isOpen(){
        return mCurrentStatus==Status.OPEN;
    }

    /**
     * 为主菜单按钮添加旋转动画
     */
    private void rotateCButton(View v, float start, float end, int duration) {
        RotateAnimation anim = new RotateAnimation(start, end, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        // 动画执行一次就停止
        anim.setFillAfter(true);
        anim.setDuration(duration);
        v.startAnimation(anim);
    }

}
