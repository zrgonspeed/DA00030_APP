package com.bike.ftms.app.common;

import android.graphics.drawable.Drawable;

import androidx.annotation.IntDef;
import androidx.core.content.res.ResourcesCompat;

import com.bike.ftms.app.R;
import com.bike.ftms.app.base.MyApplication;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MyConstant {
    // 支持的机型
    public static final int DEVICE_UNKNOW = 0;
    public static final int DEVICE_AA02020R = 1;
    public static final int DEVICE_AA01990 = 2;
    public static final int DEVICE_AA02020_00F_01 = 3;
    public static final int DEVICE_AA02020_00F_02 = 4;

    @IntDef({
            DEVICE_AA02020R,
            DEVICE_AA01990,
            DEVICE_AA02020_00F_01,
            DEVICE_AA02020_00F_02
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DeviceType {
    }

    public static final String[] deviceNames = {
            "DEVICE_UNKNOW", "AA02020-00R-01", "AA01990", "AA02020-00F-01", "AA02020-00F-02"
    };

    // 大类机型 ---------------------------------------------------------------------------------
    public final static int CATEGORY_BOAT = 1000;
    public final static int CATEGORY_BIKE = 1001;
    public final static int CATEGORY_SKI = 1002;
    public final static int CATEGORY_STEP = 1003;

    @IntDef({CATEGORY_BOAT,
            CATEGORY_BIKE,
            CATEGORY_SKI,
            CATEGORY_STEP
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface CategoryType {
    }

    private static final int[] boat_arr = {
            DEVICE_AA02020R, DEVICE_AA01990
    };
    private static final int[] bike_arr = {
            DEVICE_AA02020_00F_01, DEVICE_AA02020_00F_02
    };
    private static final int[] ski_arr = {
    };
    private static final int[] step_arr = {
    };

    @CategoryType
    public static int getCategory(@DeviceType int deviceType) {
        for (int device : boat_arr) {
            if (device == deviceType) {
                return CATEGORY_BOAT;
            }
        }

        for (int device : bike_arr) {
            if (device == deviceType) {
                return CATEGORY_BIKE;
            }
        }

        for (int device : ski_arr) {
            if (device == deviceType) {
                return CATEGORY_SKI;
            }
        }

        for (int device : step_arr) {
            if (device == deviceType) {
                return CATEGORY_STEP;
            }
        }

        return CATEGORY_BOAT;
    }

    public static Drawable getCategoryImg(@CategoryType int category) {
        if (category == CATEGORY_BOAT) {
            return ResourcesCompat.getDrawable(MyApplication.getContext().getResources(), R.drawable.boat, null);
        }
        if (category == CATEGORY_BIKE) {
            return ResourcesCompat.getDrawable(MyApplication.getContext().getResources(), R.drawable.bike, null);
        }
        if (category == CATEGORY_SKI) {
            return ResourcesCompat.getDrawable(MyApplication.getContext().getResources(), R.drawable.ski, null);
        }
        if (category == CATEGORY_STEP) {
            return ResourcesCompat.getDrawable(MyApplication.getContext().getResources(), R.drawable.climb, null);
        }
        return ResourcesCompat.getDrawable(MyApplication.getContext().getResources(), R.drawable.unknow, null);
    }

    /********************** 运动模式 ************************/
    public static final int NORMAL = 0;

    public static final int GOAL_TIME = 1;
    public static final int GOAL_DISTANCE = 2;
    public static final int GOAL_CALORIES = 3;

    public static final int INTERVAL_TIME = 5;
    public static final int INTERVAL_DISTANCE = 6;
    public static final int INTERVAL_CALORIES = 7;

    // 机型2(AA01990)新增的3个模式:
    public static final int CUSTOM_INTERVAL_TIME = 0x15;
    public static final int CUSTOM_INTERVAL_DISTANCE = 0x16;
    public static final int CUSTOM_INTERVAL_CALORIES = 0x17;

    @IntDef({NORMAL, GOAL_TIME, GOAL_DISTANCE, GOAL_CALORIES, INTERVAL_TIME, INTERVAL_DISTANCE, INTERVAL_CALORIES})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RunMode {
    }

    public static boolean isNormalMode(int mode) {
        return mode == NORMAL;
    }

    public static boolean isGoalMode(int mode) {
        return mode == GOAL_TIME || mode == GOAL_DISTANCE || mode == GOAL_CALORIES;
    }

    public static boolean isIntervalMode(int mode) {
        return mode == INTERVAL_TIME || mode == INTERVAL_DISTANCE || mode == INTERVAL_CALORIES;
    }

    public static boolean isCustomIntervalMode(int mode) {
        return mode == CUSTOM_INTERVAL_TIME || mode == CUSTOM_INTERVAL_DISTANCE || mode == CUSTOM_INTERVAL_CALORIES;
    }

    /********************** 运动状态 ************************/
    public static final int RUN_STATUS_NO = 0;
    public static final int RUN_STATUS_YES = 1;

    @IntDef({RUN_STATUS_NO, RUN_STATUS_YES})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RunStatus {
    }


    /********************** 间隙模式状态 ************************/
    public static final int INTERVAL_STATUS_REST = 5;
    public static final int INTERVAL_STATUS_RUNNING = 6;

    @IntDef({INTERVAL_STATUS_REST, INTERVAL_STATUS_RUNNING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface IntervalStatus {
    }

    public static String homeFragment = "";
    public static String oneObject = "";
    public static String twoObject = "";
    public static String threeObject = "";
}
