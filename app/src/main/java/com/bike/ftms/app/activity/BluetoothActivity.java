package com.bike.ftms.app.activity;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.bike.ftms.app.R;
import com.bike.ftms.app.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BluetoothActivity extends BaseActivity {

    @BindView(R.id.cb_switch)
    CheckBox cbSwitch;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bluetooth;
    }

    @Override
    protected void initData() {
        cbSwitch.setChecked(false);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}