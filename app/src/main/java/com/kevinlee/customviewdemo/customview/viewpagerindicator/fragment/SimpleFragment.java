package com.kevinlee.customviewdemo.customview.viewpagerindicator.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kevinlee.customviewdemo.R;

/**
 * ClassName:SimpleFragment
 * Description:
 * Author:KevinLee
 * Date:2016/11/18 0018
 * Time:下午 5:58
 * Email:KevinLeeV@163.com
 */
public class SimpleFragment extends Fragment {

    private TextView tv;
    private static final String BUNDLE_TITLE = "title";

    public static SimpleFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TITLE, title);
        SimpleFragment fragment = new SimpleFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.simple_fragment_layout, null);
        tv = (TextView) view.findViewById(R.id.tv);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String title = bundle.getString(BUNDLE_TITLE);
            tv.setText(title);
        }
        return view;
    }
}
