package com.bike.ftms.app.base;

import android.content.Context;

import org.litepal.LitePalApplication;

import tech.gujin.toast.ToastUtil;


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
        ToastUtil.initialize(mContext);
    }

    public static Context getContext() {
        return mContext;
    }
}
