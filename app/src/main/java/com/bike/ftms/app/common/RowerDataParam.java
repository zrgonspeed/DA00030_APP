package com.bike.ftms.app.common;

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/2
 */
public class RowerDataParam {
    /**
     * FTMS协议
     **/
    public static int STROKE_RATE_INX = -1;
    public static int STROKE_RATE_LEN = 1;

    public static int STROKE_COUNT_INX = -1;
    public static int STROKE_COUNT_LEN = 2;

    public static int AVERAGE_STROKE_RATE_INX = -1;
    public static int AVERAGE_STROKE_RATE_LEN = 1;

    public static int TOTAL_DISTANCE_INX = -1;
    public static int TOTAL_DISTANCE_LEN = 3;

    public static int INSTANTANEOUS_PACE_INX = -1;
    public static int INSTANTANEOUS_PACE_LEN = 2;

    public static int AVERAGE_PACE_INX = -1;
    public static int AVERAGE_PACE_LEN = 2;

    public static int INSTANTANEOUS_POWER_INX = -1;
    public static int INSTANTANEOUS_POWER_LEN = 2;

    public static int AVERAGE_POWER_INX = -1;
    public static int AVERAGE_POWER_LEN = 2;

    public static int RESISTANCE_LEVEL_INX = -1;
    public static int RESISTANCE_LEVEL_LEN = 2;

    public static int HEART_RATE_INX = -1;
    public static int HEART_RATE_LEN = 1;

    public static int METABOLIC_EQUIVALENT_INX = -1;
    public static int METABOLIC_EQUIVALENT_LEN = 1;

    public static int ELAPSED_TIME_INX = -1;
    public static int ELAPSED_TIME_LEN = 2;

    public static int REMAINING_TIME_INX = -1;
    public static int REMAINING_TIME_LEN = 2;

    public static int ENERGY_PER_HOUR_INX = -1;
    public static int ENERGY_PER_HOUR_LEN = 2;

    public static int TOTAL_ENERGY_INX = -1;
    public static int TOTAL_ENERGY_LEN = 2;

    public static int ENERGY_PER_MINUTE_INX = -1;
    public static int ENERGY_PER_MINUTE_LEN = 1;

    /**
     * 自定义协议 ffe0接收通知
     **/
    public static int DRAG_INX = 3;
    public static int DRAG_LEN = 2;

    public static int INTERVAL_INX = 5;
    public static int INTERVAL_LEN = 2;

    public static int SET_TIME_INX = 7;
    public static int SET_TIME_LEN = 2;

    public static int SET_DISTANCE_INX = 9;
    public static int SET_DISTANCE_LEN = 4;

    public static int SET_CALORIE_INX = 13;
    public static int SET_CALORIE_LEN = 2;
}
