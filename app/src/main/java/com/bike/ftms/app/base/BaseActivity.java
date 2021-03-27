package com.bike.ftms.app.base;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bike.ftms.app.utils.SystemUiUtils;

import butterknife.ButterKnife;

/**
 * @Description
 * @Author YeYueHong
 * @Date 2021/3/27
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUiUtils.reMoveTitle(this);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected abstract int getLayoutId();
    protected abstract void init();
    protected abstract void initView();
}
