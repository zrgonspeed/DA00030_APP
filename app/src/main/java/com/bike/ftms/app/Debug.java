package com.bike.ftms.app;

public class Debug {
    public static final boolean canLogin = false;
    public static final boolean canShowSearchTime = false;
    public static final boolean canShowLog = true;
    public static final boolean canScanAllDevice = true;

    // 动态
    public static boolean canShowItemDeviceName = false;
    public static boolean canShowItemLocalId = false;

    public static void initDebug() {
//        BleManager.SCAN_PERIOD = 10 * 1000;
    }
}
