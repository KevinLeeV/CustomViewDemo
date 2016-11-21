package com.kevinlee.customviewdemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.kevinlee.customviewdemo.customview.popupdialog.PopupDialog;
import com.kevinlee.customviewdemo.customview.popupdialog.PopupDialogToView;
import com.kevinlee.customviewdemo.customview.randomcode.CodeType;
import com.kevinlee.customviewdemo.customview.randomcode.RandomCode;
import com.kevinlee.customviewdemo.customview.randomcode.OnGetTextListener;

import org.w3c.dom.Text;

public class MainActivity extends Activity {

    private RandomCode randomCode;// 随机验证码
    private PopupDialogToView pd;
    private TextView tv;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        initRandomCode();
        initPopupDialog();
        tv = (TextView) findViewById(R.id.info);
        tv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                pd.setLocation(x, y);
                return false;
            }
        });
        btn = (Button) findViewById(R.id.btn);
        pd.setView(btn);
    }

    /**
     * 随机验证码初始化
     */
    private void initRandomCode() {
        randomCode = (RandomCode) findViewById(R.id.ctv);
        // 设置随机验证码的类型，包括大小写与数字混合，大写字母，小写字母，全数字，大小写字母
        //randomCode.setCodeType(CodeType.UPPERCASE);
        // 设置随机码的颜色，默认为每个字符随机取色
        //randomCode.setmTextColor(Color.RED);
        // 设置随机码的大小，默认为60px，单位：px
        //randomCode.setmTextSize(80);
        // 设置接口回调
        randomCode.setOnGetTextListener(new OnGetTextListener() {
            @Override
            public void getText(String text) {
                // 获取到每次的随机码，以便实现对比的功能
            }
        });

        // 是有调用show()方法，才能显示
        randomCode.show();
    }

    private void initPopupDialog() {
        pd = (PopupDialogToView) findViewById(R.id.pd);
        // 设置显示的文本
        pd.setmTextValue("来得快解放路的使肌的解放路快圣诞节饭");
        // 设置字体大小
//        pd.setmTextSize(40);
        // 设置字体颜色
//        pd.setmTextColor(Color.RED);
        // 设置对话框颜色
//        pd.setmDialogColor(Color.YELLOW);
        // 设置显示的位置（小三角形的位置）
//        pd.setLocation(200, 500);
        pd.initDialog();
    }
}
