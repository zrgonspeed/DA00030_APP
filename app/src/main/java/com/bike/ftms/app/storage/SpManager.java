package com.bike.ftms.app.storage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

/**
 * sharedpreferences管理类
 *
 * @author chenyan
 */
public class SpManager {
    private static final String SETTINGS_TREADMILL_DA00030 = "da00030";

    private static final String SET_TREADMILL_FLAG = "set_treadmill_flag";

    public static void init(@NonNull Context c) {
        StorageParam.setContext(c);
        StorageParam.setSpName(SETTINGS_TREADMILL_DA00030);
    }

    public static void setTreadmill_flag(int flag) {
        StorageParam.setParam(SET_TREADMILL_FLAG, flag);
    }

    public static int getTreadmill_flag() {
        return StorageParam.getParam(SET_TREADMILL_FLAG, -1);
    }
}