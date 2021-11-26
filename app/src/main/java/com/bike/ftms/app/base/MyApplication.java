package com.bike.ftms.app.base;

import android.content.Context;

import com.bike.ftms.app.storage.SpManager;
import com.bike.ftms.app.utils.Logger;
import com.squareup.leakcanary.LeakCanary;

import org.litepal.LitePalApplication;

import tech.gujin.toast.ToastUtil;


/**
 * @Description
 * @Author YYH
 * @Date 2021/3/31
 */
public class MyApplication extends LitePalApplication {
    private static String TAG = MyApplication.class.getSimpleName();

    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.e("onCreate()--------------------------------------------------");
        mContext = MyApplication.this;
        SpManager.init(mContext);

        ToastUtil.initialize(mContext);
        LeakCanary.install(this);
    }
}
