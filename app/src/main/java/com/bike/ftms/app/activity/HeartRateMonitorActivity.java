package com.bike.ftms.app.activity;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class HeartRateMonitorActivity extends BluetoothActivity {
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
        super.initData();

    }

    @Override
    protected void initView() {
        super.initView();
    }
}