package com.bike.ftms.app.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class UIUtils {
    public static int getWidth(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getRealMetrics(metric);
        int width = metric.widthPixels; // 宽度（PX）
        return width;
    }

    public static int getHeight(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getRealMetrics(metric);
        int height = metric.heightPixels; // 高度（PX）
        return height;
    }

    public static float getDensity(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float density = metrics.density;
        int dpi = metrics.densityDpi;

        // density == 2.5    dpi == 400
        // density == 2.0    dpi == 320
        Logger.e("density == " + density + "    dpi == " + dpi);
        return density;
    }

    public static int getDPI(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float density = metrics.density;
        int dpi = metrics.densityDpi;

        // density == 2.5    dpi == 400
        // density == 2.0    dpi == 320
        Logger.e("density == " + density + "    dpi == " + dpi);
        return dpi;
    }
}
