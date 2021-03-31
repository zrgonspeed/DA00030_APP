package com.bike.ftms.app.activity;

import android.os.Bundle;
import android.view.View;

import com.bike.ftms.app.R;
import com.bike.ftms.app.base.BaseActivity;
import com.bike.ftms.app.manager.ble.BleManager;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

    }

    @OnClick({R.id.iv_back, R.id.iv_setting_login, R.id.iv_setting_bluetooth, R.id.iv_setting_hr, R.id.iv_setting_version})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_setting_login:
                break;
            case R.id.iv_setting_bluetooth:
                break;
            case R.id.iv_setting_hr:
                break;
            case R.id.iv_setting_version:
                break;
        }
    }


}