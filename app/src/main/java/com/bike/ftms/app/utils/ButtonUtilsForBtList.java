package com.bike.ftms.app.utils;

import android.os.SystemClock;

public class ButtonUtilsForBtList {
    private static boolean canClickGuding = true;

    public static boolean canResponse() {
        if (canClickGuding) {
            canClickGuding = false;
            new Thread(() -> {
                SystemClock.sleep(3000);
                canClickGuding = true;
            }).start();
            Logger.d("canResponse: " + true);
            return true;
        }

        Logger.d("canResponse: " + false);
        return false;
    }


    private static boolean canClick = true;

    public static void setCanClick(boolean canClick) {
        ButtonUtilsForBtList.canClick = canClick;
    }

    public static boolean getCanClick() {
        // Logger.i("canClick: " + canClick);
        return canClick;
    }

    public static void startCanClickTimer() {
        setCanClick(false);

        new Thread(() -> {
            SystemClock.sleep(2000);

            setCanClick(true);
        }).start();
    }
}
