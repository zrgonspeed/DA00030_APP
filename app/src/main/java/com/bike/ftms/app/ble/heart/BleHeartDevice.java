package com.bike.ftms.app.ble.heart;

public interface BleHeartDevice {
    boolean isConnected();

    int getStatus();

    int getHeartInt();
}
