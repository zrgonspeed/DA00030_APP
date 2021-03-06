package com.bike.ftms.app.base;

import android.content.Context;

import com.bike.ftms.app.BuildConfig;
import com.bike.ftms.app.Debug;
import com.bike.ftms.app.manager.storage.SpManager;
import com.bike.ftms.app.utils.Logger;

import org.litepal.LitePalApplication;

import tech.gujin.toast.ToastUtil;


/**
 * @Description
 * @Author YYH
 * @Date 2021/3/31
 */
public class MyApplication extends LitePalApplication {
    private static final String TAG = MyApplication.class.getSimpleName();

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

        // 每次安装新的版本，都要弹出注意事项
        int nowVersionCode = BuildConfig.VERSION_CODE;
        if (SpManager.getVersionCode() != nowVersionCode) {
            SpManager.setVersionCode(nowVersionCode);
            SpManager.setSkipHint(false);
        }

        ToastUtil.initialize(mContext);
        Debug.initDebug();
    }
}
