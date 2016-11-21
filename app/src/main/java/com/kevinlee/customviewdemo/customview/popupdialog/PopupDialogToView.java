package com.kevinlee.customviewdemo.customview.popupdialog;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * PopupDialog类似于地图上的气泡对话框功能，
 * PopupDialogToView是相对于平时使用View去显示
 */
public class PopupDialogToView extends PopupDialog {

    // 对话框显示在这个view的上面或者下面
    private View view;

    public PopupDialogToView(Context context) {
        this(context, null);
    }

    public PopupDialogToView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopupDialogToView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置View
     */
    public void setView(View view) {
        this.view = view;
        this.view.setOnClickListener(clickListener);
    }

    //view的点击事件监听
    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isShow) {
                showViewTop(v);
                isShow = true;
            } else {
                isShow = false;
            }
        }
    };

    // 判断对话框显示在View的上面还是下面
    private void showViewTop(View view) {
        int[] loc = new int[2];
        view.getLocationInWindow(loc);
        int viewWidth = view.getMeasuredWidth();
        int viewHeight = view.getMeasuredHeight();
        Log.i("MainActivity", "--------->x=" + loc[0] + ",y=" + loc[1] + ",width=" + viewWidth + ",height=" + viewHeight);
        if (loc[1] - getMeasuredHeight() > 10) {
            setLocation(loc[0] + viewWidth / 2, loc[1] - viewHeight);
            isTriangleBottom = true;
        } else {
            setLocation(loc[0] + viewWidth / 2, viewHeight);
            isTriangleBottom = false;
        }
    }

    /**
     * 计算最终y坐标
     *
     * @param y
     * @return
     */
    @Override
    protected float calculateY(float y) {
        if (isTriangleBottom)
            triangleY = getMeasuredHeight();
        else
            triangleY = 0;
        return y;
    }

    @Override
    public void show() {
    }

}
