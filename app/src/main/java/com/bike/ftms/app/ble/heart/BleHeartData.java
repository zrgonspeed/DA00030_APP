package com.bike.ftms.app.ble.heart;

public interface BleHeartData {
    void onHeartData(int heart);

    void setHRConnectStatus(boolean connected);

    void onHRConnected();

    void disHRConnect();
}
