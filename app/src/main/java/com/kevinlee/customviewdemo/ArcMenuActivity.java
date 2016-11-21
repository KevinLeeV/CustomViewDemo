package com.kevinlee.customviewdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.kevinlee.customviewdemo.customview.arcview.ArcMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:
 * Description:
 * Author:KevinLee
 * Date:2016/11/11 0011
 * Time:上午 11:50
 * Email:KevinLeeV@163.com
 */
public class ArcMenuActivity extends Activity {

    private ArcMenu arcMenu;
    private ListView lv;
    private List<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arcmenu_activity);
        initViews();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        data = new ArrayList<>();
        for (int i = 'A'; i < 'Z'; i++) {
            data.add((char) i + "");
        }
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data));
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (arcMenu.isOpen())
                    arcMenu.toggleMenu(300);
            }
        });
    }

    // 初始化View
    private void initViews() {
        lv = (ListView) findViewById(R.id.lv);
        arcMenu = (ArcMenu) findViewById(R.id.menu);
        arcMenu.setOnMenuItemClickListener(new ArcMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                Toast.makeText(ArcMenuActivity.this, "" + pos, Toast.LENGTH_LONG).show();
            }
        });
    }
}
