package com.kevinlee.customviewdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;

import com.kevinlee.customviewdemo.customview.progressbar.CircleProgressBar;
import com.kevinlee.customviewdemo.customview.progressbar.HorizontalProgressBar;
import com.kevinlee.customviewdemo.customview.progressbar.RoundProgressBar;

/**
 *
 */
public class ProgressBarActivity extends Activity {

    private HorizontalProgressBar hpb;
    private RoundProgressBar rpb;
    private CircleProgressBar cpb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progressbar_activity);
        hpb = (HorizontalProgressBar) findViewById(R.id.hpb);
        rpb = (RoundProgressBar) findViewById(R.id.rpb);
        cpb = (CircleProgressBar) findViewById(R.id.cpb);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int progress = hpb.getProgress();
                while (progress < hpb.getMax()) {
                    SystemClock.sleep(100);
                    hpb.setProgress(++progress);
                    rpb.setProgress(progress);
                    cpb.setProgress(progress);
                }
            }
        }).start();
    }
}
