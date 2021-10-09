package com.bike.ftms.app.common;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MyConstant {
    /********************** 运动模式 ************************/
    public static final int NORMAL = 10000;

    public static final int GOAL_TIME = 10001;
    public static final int GOAL_DISTANCE = 10002;
    public static final int GOAL_CALORIES = 10003;

    public static final int INTERVAL_TIME = 10004;
    public static final int INTERVAL_DISTANCE = 10005;
    public static final int INTERVAL_CALORIES = 10006;

    @IntDef({NORMAL, GOAL_TIME, GOAL_DISTANCE, GOAL_CALORIES, INTERVAL_TIME, INTERVAL_DISTANCE, INTERVAL_CALORIES})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RunMode {
    }
}
