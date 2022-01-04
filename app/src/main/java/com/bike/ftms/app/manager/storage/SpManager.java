package com.bike.ftms.app.manager.storage;

import android.content.Context;
import android.os.storage.StorageManager;

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


    private static final String SET_USERNAME = "set_username";
    private static final String SET_PASSWORD = "set_password";
    private static final String SET_SKIP_HINT = "set_skip_hint";
    private static final String SET_VERSION_CODE = "set_version_code";

    public static String getUsername() {
        return StorageParam.getParam(SET_USERNAME, "");
    }

    public static void setUsername(String username) {
        StorageParam.setParam(SET_USERNAME, username);
    }

    public static String getPassword() {
        return StorageParam.getParam(SET_PASSWORD, "");
    }

    public static void setPassword(String password) {
        StorageParam.setParam(SET_PASSWORD, password);
    }

    public static void setSkipHint(boolean skip) {
        StorageParam.setParam(SET_SKIP_HINT, skip);
    }

    public static boolean getSkipHint() {
        return StorageParam.getParam(SET_SKIP_HINT, false);
    }

    public static void setVersionCode(int versionCode) {
        StorageParam.setParam(SET_VERSION_CODE, versionCode);
    }

    public static int getVersionCode() {
        return StorageParam.getParam(SET_VERSION_CODE, 0);
    }
}