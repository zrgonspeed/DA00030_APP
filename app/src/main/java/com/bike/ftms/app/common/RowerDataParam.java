package com.bike.ftms.app.common;

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/2
 */
public class RowerDataParam {
    /*
    01111100,00001011
    setBleDataInx  STROKE_RATE_INX=2
    setBleDataInx  TOTAL_DISTANCE_INX=5
    setBleDataInx  INSTANTANEOUS_PACE_INX=8
    setBleDataInx  AVERAGE_PACE_INX=10
    setBleDataInx  INSTANTANEOUS_POWER_INX=12
    setBleDataInx  AVERAGE_POWER_INX=14
    setBleDataInx  TOTAL_ENERGY_INX=16
    setBleDataInx  HEART_RATE_INX=21
    setBleDataInx  ELAPSED_TIME_INX=22
     */

    /**
     * FTMS协议
     **/
    public static int INSTANTANEOUS_SPEED_INX = -1;
    public static int INSTANTANEOUS_SPEED_LEN = 2;

    public static int AVERAGE_SPEED_INX = -1;
    public static int AVERAGE_SPEED_LEN = 2;

    public static int INSTANTANEOUS_RPM_INX = -1;
    public static int INSTANTANEOUS_RPM_LEN = 2;

    public static int AVERAGE_RPM_INX = -1;
    public static int AVERAGE_RPM_LEN = 2;


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

    // 消耗时间
    public static int ELAPSED_TIME_INX = -1;
    public static int ELAPSED_TIME_LEN = 2;
    // 剩余时间
    public static int REMAINING_TIME_INX = -1;
    public static int REMAINING_TIME_LEN = 2;

    //
    public static int TOTAL_ENERGY_INX = -1;
    public static int TOTAL_ENERGY_LEN = 2;
    public static int ENERGY_PER_HOUR_INX = -1;
    public static int ENERGY_PER_HOUR_LEN = 2;
    public static int ENERGY_PER_MINUTE_INX = -1;
    public static int ENERGY_PER_MINUTE_LEN = 1;

    /**
     * FTMS协议 自定义部分
     **/
    public static int RUN_MODE_INX = 1;
    public static int RUN_MODE_LEN = 1;

    public static int INTERVAL_STATUS_INX = 2;
    public static int INTERVAL_STATUS_LEN = 1;

    public static int RUN_STATUS_INX = 3;
    public static int RUN_STATUS_LEN = 1;

    public static int RUN_INTERVAL_INX = 4;
    public static int RUN_INTERVAL_LEN = 1;

    public static int GOAL_TIME_INX = 5;
    public static int GOAL_TIME_LEN = 2;

    public static int GOAL_DISTANCE_INX = 5;
    public static int GOAL_DISTANCE_LEN = 4;

    public static int GOAL_CALORIE_INX = 5;
    public static int GOAL_CALORIE_LEN = 2;

    public static int INTERVAL_TIME_INX = 5;
    public static int INTERVAL_TIME_LEN = 2;

    public static int INTERVAL_DISTANCE_INX = 5;
    public static int INTERVAL_DISTANCE_LEN = 4;

    public static int INTERVAL_CALORIE_INX = 5;
    public static int INTERVAL_CALORIE_LEN = 2;

    // todo
    public static int INTERVAL_REST_TIME_INX = -1;
    public static int INTERVAL_REST_TIME_LEN = 2;

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

    public static int REST_TIME_INX = 15;
    public static int REST_TIME_LEN = 2;

    public static int RUN_REST_TIME_INX = 15;
    public static int RUN_REST_TIME_LEN = 2;

    // 间歇模式在运行中的时间 AA01990特有
    public static int RUN_INTERVAL_TIME_INX = 17;
    public static int RUN_INTERVAL_TIME_LEN = 4;

    // 间歇模式在运行中的休息时间 AA01990特有
    public static int RUN_INTERVAL_REST_TIME_INX = 17;
    public static int RUN_INTERVAL_REST_TIME_LEN = 2;

    // bike
    public static int ONE_KM_TIME_INX = 19;
    public static int ONE_KM_TIME_LEN = 2;

    public static int AVERAGE_ONE_KM_TIME_INX = 21;
    public static int AVERAGE_ONE_KM_TIME_LEN = 2;

    public static int SPLIT_ONE_KM_TIME_INX = 23;
    public static int SPLIT_ONE_KM_TIME_LEN = 2;

    public static int SPLIT_CAL_INX = 25;
    public static int SPLIT_CAL_LEN = 2;
}
