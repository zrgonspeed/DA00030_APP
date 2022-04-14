package com.bike.ftms.app.activity.bluetooth;

import android.os.Handler;
import android.view.View;

import com.bike.ftms.app.R;
import com.bike.ftms.app.ble.BaseBleManager;
import com.bike.ftms.app.ble.bean.MyScanResult;
import com.bike.ftms.app.ble.heart.BleHeartDeviceManager;

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
        return getBleManager().getIsOpen();
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
    public void onItemClickListener(MyScanResult clickScanResult) {
        if (!isClicked) {
            getBleManager().stopScan();
            getBleManager().connectDevice(clickScanResult);
            new Handler().postDelayed(() -> {
                isClicked = false;
            }, 2000);
        }
        isClicked = true;
    }
}