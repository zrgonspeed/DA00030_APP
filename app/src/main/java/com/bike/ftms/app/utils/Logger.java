package com.bike.ftms.app.utils;

import android.util.Log;

/**
 * @Description Log统一管理类
 * @Author GaleLiu
 * @Time 2019/01/09
 */
public class Logger {

    /**
     * 是否需要打印bug，可以在application的onCreate函数里面初始化
     */
    public static boolean isDebug = false;
    private static final String TAG = "tag";

    private Logger() {
        throw new UnsupportedOperationException("不可以被实例！");
    }

    public static void i(String msg) {
        if (isDebug) {
            Log.i(getTAG(), msg);
        }
    }

    public static void d(String msg) {
        if (isDebug) {
            Log.d(getTAG(), msg);
        }
    }

    public static void e(String msg) {
        if (isDebug) {
            Log.e(getTAG(), msg);
        }
    }

    public static void v(String msg) {
        if (isDebug) {
            Log.v(getTAG(), msg);
        }
    }

    public static void w(String msg) {
        if (isDebug) {
            Log.w(getTAG(), msg);
        }
    }

    /*************************  下面是传入自定义tag的函数  ******************************/
    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (isDebug) {
            Log.v(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isDebug) {
            Log.w(tag, msg);
        }
    }

    private static String getTAG() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts != null) {
            for (StackTraceElement st : sts) {
                if (st.isNativeMethod()) {
                    continue;
                }
                if (st.getClassName().equals(Thread.class.getName())) {
                    continue;
                }
                if (st.getClassName().equals(Logger.class.getName())) {
                    continue;
                }
                String className = st.getClassName();
                int i = className.lastIndexOf(".");
                return "(" + st.getFileName() + ":" + st.getLineNumber() + ")";
            }
        }
        return null;
    }
}