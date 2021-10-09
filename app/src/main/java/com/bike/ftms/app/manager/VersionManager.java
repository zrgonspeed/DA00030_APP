package com.bike.ftms.app.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class VersionManager {

    /**
     * 获取App版本号
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * @return 获取SDK版本号
     */
    public static String getSdkVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * @return 获取当前系统的版本号
     */
    public static String getFireWareVersion() {
        return android.os.Build.VERSION.INCREMENTAL + "V";
    }

    /**
     * @return 返回当前系统的android版本号
     */
    public static String getSDKVersion() {
        return String.valueOf(android.os.Build.VERSION.SDK_INT);
    }

    /**
     * CORE-VER
     * 内核版本
     * return String
     */
    public static String getLinuxCore_Ver() {
        Process process = null;
        String kernelVersion = "";
        try {
            process = Runtime.getRuntime().exec("cat /proc/version");
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream outs = process.getInputStream();
        InputStreamReader isrout = new InputStreamReader(outs);
        BufferedReader brout = new BufferedReader(isrout, 8 * 1024);

        String result = "";
        String line;
        try {
            while ((line = brout.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (result != "") {
                String Keyword = "version ";
                int index = result.indexOf(Keyword);
                line = result.substring(index + Keyword.length());
                index = line.indexOf(" ");
                kernelVersion = line.substring(0, index);
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return kernelVersion;
    }

}
