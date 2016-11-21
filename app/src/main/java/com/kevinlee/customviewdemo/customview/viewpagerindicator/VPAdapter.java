package com.kevinlee.customviewdemo.customview.viewpagerindicator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * ClassName:VPAdapter
 * Description:
 * Author:KevinLee
 * Date:2016/11/18 0018
 * Time:下午 6:12
 * Email:KevinLeeV@163.com
 */
public class VPAdapter extends FragmentPagerAdapter {

    private List<Fragment> mList;

    public VPAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.mList = list;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
