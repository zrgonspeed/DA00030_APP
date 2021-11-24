package com.bike.ftms.app.base;

import android.app.Application;
import android.content.Context;

import com.bike.ftms.app.utils.DebugLoggerTree;

import org.litepal.LitePalApplication;

import tech.gujin.toast.ToastUtil;
import timber.log.Timber;

/**
 * @Description
 * @Author YYH
 * @Date 2021/3/31
 */
public class MyApplication extends LitePalApplication {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = MyApplication.this;
        Timber.plant(new DebugLoggerTree());
        ToastUtil.initialize(mContext);
    }

    public static Context getContext() {
        return mContext;
    }
}
