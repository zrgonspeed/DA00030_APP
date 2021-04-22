package com.bike.ftms.app.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description 这里用一句话描述
 * @Author GaleLiu
 * @Time 2019/07/26
 */
public class VersionUtil {

    /**
     * @param
     * @explain 获取App版本号
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Logger.e("VersionInfo", "Exception: " + e);
        }
        return versionName;
    }

    public static String getSpecAppVersionName(Context context, String name) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(name, 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Logger.e("VersionInfo", "Exception" + e);
            return "0.0";
        }
        if (name.equals("com.netflix.mediaclient")) {
            versionName = getNumAndPoint(versionName);
            String[] oldVs = versionName.split("\\ ");
            if (oldVs.length > 0) {
                return oldVs[0];
            }
        }
        if (name.equals("com.run.treadmill")) {
            versionName = versionName.replace("V", ".");
        }
        return getNumAndPoint(versionName);
    }

    /**
     * 判断是否是新版本
     *
     * @param curVersion 当前版本号
     * @param newVersion 新版本号
     * @return
     */
    public static boolean isNewVersion(String curVersion, String newVersion) {
        if (curVersion.isEmpty()) {
            return true;
        }
        if (curVersion.equals(newVersion)) {
            return false;
        }
        curVersion = curVersion.replace("V", ".");
        newVersion = newVersion.replace("V", ".");

        String curDataStr = curVersion.split(" ")[0];
        String newDataStr = newVersion.split(" ")[0];
        String[] curData = curDataStr.split("\\.");
        String[] newData = newDataStr.split("\\.");
        for (int i = 0; i < Math.min(curData.length, newData.length); i++) {
            if (Integer.parseInt(newData[i].trim()) > Integer.parseInt(curData[i].trim())) {
                return true;
            }
            if (Integer.parseInt(newData[i].trim()) < Integer.parseInt(curData[i].trim())) {
                return false;
            }
        }
        return (curData.length < newData.length);
    }

    /**
     * 判断是否是新版本
     *
     * @param curVersion        当前版本号
     * @param newVersion        新版本号
     * @param regularExpression 切割版本号的间隔
     * @return
     */
    public static boolean isNewVersion(String curVersion, String newVersion, String regularExpression) {
        if (curVersion.isEmpty()) {
            return true;
        }
        String[] curData = curVersion.split(regularExpression);
        String[] newData = newVersion.split(regularExpression);
        for (int i = 0; i < Math.min(curData.length, newData.length); i++) {
            if (Integer.valueOf(newData[i]) > Integer.valueOf(curData[i])) {
                return true;
            }
        }
        return (curData.length < newData.length);
    }

    /**
     * 字串中只提取数字和小数点
     *
     * @param str
     * @return
     */
    public static String getNumAndPoint(String str) {
        Pattern pattern = Pattern.compile("[a-zA-Z_-]");
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll("").trim();
    }

    /**
     * @return 获取SDK版本号 旧方式
     */
    public static String getSdkVersion_1() {
        String versionOS = android.os.Build.VERSION.RELEASE;
        return versionOS;
    }

    /**
     * @return 获取SDK版本号 新方式
     */
    public static String getSdkVersion_2() {
        String value = "";
        try {
            Class<?> classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", new Class<?>[]{String.class});
            value = (String) getMethod.invoke(classType, new Object[]{"ro.anplus.version"});
            Log.i("===", "---" + value);
        } catch (Exception e) {
            Log.e("===", "---" + e.getMessage(),e);
        }
        return value;
    }
    /**
     * @return 获取当前系统的版本号
     */
    public static String getFireWareVersion() {
        String versionOS = android.os.Build.VERSION.INCREMENTAL + "V10";

        return versionOS;
    }

    /**
     * @return 返回当前系统的android版本号
     */
    public static String getSDKVersion() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        return String.valueOf(currentapiVersion);
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // get the output line
        InputStream outs = process.getInputStream();
        InputStreamReader isrout = new InputStreamReader(outs);
        BufferedReader brout = new BufferedReader(isrout, 8 * 1024);

        String result = "";
        String line;
        // get the whole standard output string
        try {
            while ((line = brout.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
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