package com.bike.ftms.app.ble.base;

/**
 * @Description
 * @Author YYH
 * @Date 2021/3/31
 */
public interface OnScanConnectListener {
    void onScanSuccess();

    void onStopScan();

    void onConnectEvent(boolean isconnect, String tag);

    void onNotifyData();
}
