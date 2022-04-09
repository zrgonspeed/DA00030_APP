package com.bike.ftms.app.ble;

public interface BleHeartData {
    void rxDataPackage(byte[] data, String uuid);


}
