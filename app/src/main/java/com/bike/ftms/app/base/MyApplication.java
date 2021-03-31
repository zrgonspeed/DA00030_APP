package com.bike.ftms.app.base;

import android.app.Application;
import android.content.Context;

/**
 * @Description
 * @Author YYH
 * @Date 2021/3/31
 */
public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = MyApplication.this;
    }

    public static Context getContext() {
        return mContext;
    }
}
