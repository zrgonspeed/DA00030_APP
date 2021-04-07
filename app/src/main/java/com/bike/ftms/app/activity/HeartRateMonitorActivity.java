package com.bike.ftms.app.activity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.bike.ftms.app.R;
import com.bike.ftms.app.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class HeartRateMonitorActivity extends BaseActivity {

    @BindView(R.id.ll_loading)
    LinearLayout llLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_heart_rate_monitor;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}