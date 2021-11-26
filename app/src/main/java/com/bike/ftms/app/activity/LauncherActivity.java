package com.bike.ftms.app.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import com.bike.ftms.app.R;
import com.bike.ftms.app.base.BaseActivity;
import com.bike.ftms.app.utils.Logger;


public class LauncherActivity extends BaseActivity {
    // 动态跟着类名改 TAG
    private static final String TAG = LauncherActivity.class.getSimpleName();
    private Handler mHandler = new Handler();

    @Override
    protected String getTAG() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_launcher;
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initView() {
    }

    /**
     * 跳转到主页面，并且把当前页面关闭掉
     */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent: 点击启动界面马上进主页面:" + event.getAction());
        startMainActivity();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 解决点击启动页面，然后快速点返回键，界面又出现的bug
        mHandler.removeCallbacksAndMessages(null);
    }

    protected void init() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 两秒后执行到这里
                // 执行在主线程中,因为该Handler在主线程new
                startMainActivity();
                Logger.i("当前线程名称 " + Thread.currentThread().getName());
            }
        }, 2000);
    }
}
