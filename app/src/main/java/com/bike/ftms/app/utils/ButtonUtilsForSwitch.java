package com.bike.ftms.app.utils;

public class ButtonUtilsForSwitch {
    // 两次点击按钮之间的点击间隔不能少于   xxxx    毫秒
    private static final int MIN_CLICK_DELAY_TIME = 2000;
    private static long lastClickTime;

    public static boolean canResponse() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if (Math.abs(curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }
}
