package com.bike.ftms.app.common;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MyConstant {
    /********************** 运动模式 ************************/
    public static final int NORMAL = 0;

    public static final int GOAL_TIME = 1;
    public static final int GOAL_DISTANCE = 2;
    public static final int GOAL_CALORIES = 3;

    public static final int INTERVAL_TIME = 5;
    public static final int INTERVAL_DISTANCE = 6;
    public static final int INTERVAL_CALORIES = 7;

    @IntDef({NORMAL, GOAL_TIME, GOAL_DISTANCE, GOAL_CALORIES, INTERVAL_TIME, INTERVAL_DISTANCE, INTERVAL_CALORIES})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RunMode {
    }

    public static boolean isGoalMode(int mode) {
        return mode == GOAL_TIME || mode == GOAL_DISTANCE || mode == GOAL_CALORIES;
    }

    public static boolean isIntervalMode(int mode) {
        return mode == INTERVAL_TIME || mode == INTERVAL_DISTANCE || mode == INTERVAL_CALORIES;
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
