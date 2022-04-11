package com.bike.ftms.app.activity.bluetooth;

import android.os.Bundle;

import com.bike.ftms.app.R;
import com.bike.ftms.app.ble.BaseBleManager;
import com.bike.ftms.app.ble.BleHeartDeviceManager;

public class HeartRateMonitorActivity extends BaseBluetoothActivity {
    private static final String TAG = HeartRateMonitorActivity.class.getSimpleName();

    @Override
    protected String getTAG() {
        return TAG;
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
    protected boolean isOpenBle() {
        return BleHeartDeviceManager.isOpen;
    }

    @Override
    protected BaseBleManager getBleManager() {
        return BleHeartDeviceManager.getInstance();
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    public void onConnectEvent(boolean isconnect, String name) {

    }
}