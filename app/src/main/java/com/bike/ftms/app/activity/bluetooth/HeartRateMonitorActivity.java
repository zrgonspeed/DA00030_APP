package com.bike.ftms.app.activity.bluetooth;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.bike.ftms.app.R;
import com.bike.ftms.app.ble.BaseBleManager;
import com.bike.ftms.app.ble.BleHeartDeviceManager;
import com.bike.ftms.app.ble.BleManager;

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
        return getBleManager().isOpen;
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

    @Override
    public void onItemClickListener(int position, View v, int connectState) {
        if (!isClicked) {
            if (position != getBleManager().getPosition() && getBleManager().getPosition() != -1) {
                // 断开旧连接
                getBleManager().connectDevice(getBleManager().getPosition());
                getBleManager().setPosition(-1);
            } else {
                getBleManager().connectDevice(position);
                getBleManager().setPosition(position);
            }
            new Handler().postDelayed(() -> {
                isClicked = false;
            }, 2000);
        }
        isClicked = true;
    }
}