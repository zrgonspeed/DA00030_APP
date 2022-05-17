package com.bike.ftms.app.ble.bean

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/13
 */
class MyScanResult(var scanResult: ScanResult, //0=未连接 1=已连接 2=连接中 3=连接上但未验证设备
                   var connectState: Int) {

    @SuppressLint("MissingPermission")
    override fun toString(): String {
        return "MyScanResult{" +
                "scanResult=" + scanResult.device.name + "---" + scanResult.device.address +
                ", connectState=" + connectState +
                ", super.toString()=" + super.toString() +
                '}'
    }
}