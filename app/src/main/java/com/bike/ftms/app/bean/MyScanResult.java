package com.bike.ftms.app.bean;

import android.bluetooth.le.ScanResult;

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/13
 */
public class MyScanResult {
    ScanResult scanResult;
    int connectState;//0=未连接 1=已连接 2=连接中

    public MyScanResult(ScanResult scanResult, int connectState) {
        this.scanResult = scanResult;
        this.connectState = connectState;
    }

    public ScanResult getScanResult() {
        return scanResult;
    }

    public void setScanResult(ScanResult scanResult) {
        this.scanResult = scanResult;
    }

    public int getConnectState() {
        return connectState;
    }

    public void setConnectState(int state) {
        connectState = state;
    }
}
