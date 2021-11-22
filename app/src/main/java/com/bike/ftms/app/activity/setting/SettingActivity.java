package com.bike.ftms.app.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.bike.ftms.app.R;
import com.bike.ftms.app.activity.bluetooth.BluetoothActivity;
import com.bike.ftms.app.activity.bluetooth.HeartRateMonitorActivity;
import com.bike.ftms.app.activity.user.LoginActivity;
import com.bike.ftms.app.base.BaseActivity;
import com.bike.ftms.app.common.ParamData;

import butterknife.OnClick;

public class SettingActivity extends BaseActivity {
    private final int HR_REQUEST_CODE = 100;

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
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.iv_setting_bluetooth:
                startActivity(new Intent(this, BluetoothActivity.class));
                break;
            case R.id.iv_setting_hr:
                Intent intent = new Intent(this, HeartRateMonitorActivity.class);
                intent.putExtra("isScanHrDevice", true);
                startActivityForResult(intent, HR_REQUEST_CODE);
                break;
            case R.id.iv_setting_version:
                startActivity(new Intent(this, VersionActivity.class));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ParamData.REQUEST_IS_FINISH_CODE && HR_REQUEST_CODE == requestCode) {
            finish();
        }
    }
}