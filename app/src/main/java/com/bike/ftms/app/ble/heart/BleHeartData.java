package com.bike.ftms.app.ble.heart;

public interface BleHeartData {
    void rxDataPackage(byte[] data, String uuid);


}
