package com.kevinlee.customviewdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.kevinlee.customviewdemo.customview.viewpagerindicator.VPAdapter;
import com.kevinlee.customviewdemo.customview.viewpagerindicator.ViewPagerIndicator;
import com.kevinlee.customviewdemo.customview.viewpagerindicator.fragment.SimpleFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:
 * Description:
 * Author:KevinLee
 * Date:2016/11/18 0018
 * Time:下午 5:52
 * Email:KevinLeeV@163.com
 */
public class ViewPagerIndicatorActivity extends FragmentActivity {

    private ViewPagerIndicator vpIndicator;
    private ViewPager vp;
    private String[] datas = new String[]{"短信", "收藏", "推荐", "短信", "收藏", "推荐", "短信", "收藏", "推荐"};
    private List<Fragment> fragmentList;
    private VPAdapter vpAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpagerindicator_activity);
        initView();
        initData();
        initViewPagerIndicator();
    }

    // 初始化View
    private void initView() {
        vp = (ViewPager) findViewById(R.id.vp);
        vpIndicator = (ViewPagerIndicator) findViewById(R.id.vp_indicator);
    }

    // 初始化数据
    private void initData() {
        fragmentList = new ArrayList<>();
        for (String title : datas) {
            SimpleFragment fragment = SimpleFragment.newInstance(title);
            fragmentList.add(fragment);
        }
        vpAdapter = new VPAdapter(getSupportFragmentManager(), fragmentList);
        vp.setAdapter(vpAdapter);
    }

    /**
     * 初始化viewpager指示器
     */
    private void initViewPagerIndicator() {
        vpIndicator.setVisibleTabCount(5);
        vpIndicator.setTabItemTitles(datas);
        vpIndicator.setViewPager(vp);
    }
}
