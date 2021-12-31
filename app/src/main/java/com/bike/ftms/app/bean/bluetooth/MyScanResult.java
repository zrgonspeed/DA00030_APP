package com.bike.ftms.app.bean.bluetooth;

import android.bluetooth.le.ScanResult;

import androidx.annotation.NonNull;

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

    @Override
    public String toString() {
        return "MyScanResult{" +
                "scanResult=" + scanResult +
                ", connectState=" + connectState +
                '}';
    }

}
