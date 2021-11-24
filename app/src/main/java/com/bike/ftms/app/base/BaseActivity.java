package com.bike.ftms.app.base;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bike.ftms.app.bean.RowerDataBean1;
import com.bike.ftms.app.manager.ble.BleManager;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.utils.SystemUiUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * @Description
 * @Author YeYueHong
 * @Date 2021/3/27
 */
public abstract class BaseActivity extends AppCompatActivity {
    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("%s - onCreate", this.getClass().getSimpleName());

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
        Timber.i("%s - onStart", this.getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.i("%s - onResume", this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Timber.i("%s - onPause", this.getClass().getSimpleName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Timber.i("%s - onStop", this.getClass().getSimpleName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.i("%s - onDestroy", this.getClass().getSimpleName());
        unbinder.unbind();
    }
}
