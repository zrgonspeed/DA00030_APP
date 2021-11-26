package com.bike.ftms.app.base;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bike.ftms.app.bean.RowerDataBean1;
import com.bike.ftms.app.manager.ble.BleManager;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.utils.SystemUiUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * @Description
 * @Author YeYueHong
 * @Date 2021/3/27
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static String TAG = BaseActivity.class.getSimpleName();

    private Unbinder unbinder;

    protected abstract String getTAG();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i(getTAG() + " - " + "onCreate()");

        Logger.e(getTAG(), "onCreate() savedInstanceState == " + savedInstanceState);

        SystemUiUtils.reMoveTitle(this);
        setContentView(getLayoutId());
        unbinder = ButterKnife.bind(this);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
        initData();
    }

    /**
     * 该抽象方法就是 onCreate中需要的layoutID
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 执行数据的加载
     */
    protected abstract void initData();

    /**
     * 初始化view
     */
    protected abstract void initView();


    @Override
    protected void onStart() {
        super.onStart();
        Logger.i(getTAG() + " - " + "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.i(getTAG() + " - " + "onResume()");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.i(getTAG() + " - " + "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.i(getTAG() + " - " + "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.i(getTAG() + " - " + "onDestroy()");
        unbinder.unbind();
    }

    /**
     * onStop() 和  onDestroy()之间
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Logger.i(getTAG() + " - " + "onSaveInstanceState()");
    }

    /**
     * onStart() 和 onResume()之间
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Logger.i(getTAG() + " - " + "onRestoreInstanceState()");
    }
}
