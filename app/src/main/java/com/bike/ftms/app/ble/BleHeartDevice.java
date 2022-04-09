package com.bike.ftms.app.ble;

public interface BleHeartDevice {
    boolean isConnected();

    int getStatus();

    int getHeartInt();
}
