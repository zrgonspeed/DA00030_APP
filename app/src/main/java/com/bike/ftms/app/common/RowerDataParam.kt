package com.bike.ftms.app.common

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/2
 */
object RowerDataParam {
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
     */
    @JvmField
    var INSTANTANEOUS_SPEED_INX = -1

    @JvmField
    var INSTANTANEOUS_SPEED_LEN = 2

    @JvmField
    var AVERAGE_SPEED_INX = -1

    @JvmField
    var AVERAGE_SPEED_LEN = 2

    @JvmField
    var INSTANTANEOUS_RPM_INX = -1

    @JvmField
    var INSTANTANEOUS_RPM_LEN = 2

    @JvmField
    var AVERAGE_RPM_INX = -1

    @JvmField
    var AVERAGE_RPM_LEN = 2

    @JvmField
    var STROKE_RATE_INX = -1

    @JvmField
    var STROKE_RATE_LEN = 1

    @JvmField
    var STROKE_COUNT_INX = -1

    @JvmField
    var STROKE_COUNT_LEN = 2

    @JvmField
    var AVERAGE_STROKE_RATE_INX = -1

    @JvmField
    var AVERAGE_STROKE_RATE_LEN = 1

    @JvmField
    var TOTAL_DISTANCE_INX = -1

    @JvmField
    var TOTAL_DISTANCE_LEN = 3

    @JvmField
    var INSTANTANEOUS_PACE_INX = -1

    @JvmField
    var INSTANTANEOUS_PACE_LEN = 2

    @JvmField
    var AVERAGE_PACE_INX = -1

    @JvmField
    var AVERAGE_PACE_LEN = 2

    @JvmField
    var INSTANTANEOUS_POWER_INX = -1

    @JvmField
    var INSTANTANEOUS_POWER_LEN = 2

    @JvmField
    var AVERAGE_POWER_INX = -1

    @JvmField
    var AVERAGE_POWER_LEN = 2

    @JvmField
    var RESISTANCE_LEVEL_INX = -1

    @JvmField
    var RESISTANCE_LEVEL_LEN = 2

    @JvmField
    var HEART_RATE_INX = -1

    @JvmField
    var HEART_RATE_LEN = 1

    @JvmField
    var METABOLIC_EQUIVALENT_INX = -1

    @JvmField
    var METABOLIC_EQUIVALENT_LEN = 1

    // 消耗时间
    @JvmField
    var ELAPSED_TIME_INX = -1

    @JvmField
    var ELAPSED_TIME_LEN = 2

    // 剩余时间
    @JvmField
    var REMAINING_TIME_INX = -1

    @JvmField
    var REMAINING_TIME_LEN = 2

    //
    @JvmField
    var TOTAL_ENERGY_INX = -1

    @JvmField
    var TOTAL_ENERGY_LEN = 2

    @JvmField
    var ENERGY_PER_HOUR_INX = -1

    @JvmField
    var ENERGY_PER_HOUR_LEN = 2

    @JvmField
    var ENERGY_PER_MINUTE_INX = -1

    @JvmField
    var ENERGY_PER_MINUTE_LEN = 1

    /**
     * FTMS协议 自定义部分  2ada
     */
    @JvmField
    var RUN_MODE_INX = 1

    @JvmField
    var RUN_MODE_LEN = 1

    @JvmField
    var INTERVAL_STATUS_INX = 2

    @JvmField
    var INTERVAL_STATUS_LEN = 1

    @JvmField
    var RUN_STATUS_INX = 3

    @JvmField
    var RUN_STATUS_LEN = 1

    @JvmField
    var RUN_INTERVAL_INX = 4

    @JvmField
    var RUN_INTERVAL_LEN = 1

    @JvmField
    var GOAL_TIME_INX = 5

    @JvmField
    var GOAL_TIME_LEN = 2

    @JvmField
    var GOAL_DISTANCE_INX = 5

    @JvmField
    var GOAL_DISTANCE_LEN = 4

    @JvmField
    var GOAL_CALORIE_INX = 5

    @JvmField
    var GOAL_CALORIE_LEN = 2

    @JvmField
    var INTERVAL_TIME_INX = 5

    @JvmField
    var INTERVAL_TIME_LEN = 2

    @JvmField
    var INTERVAL_DISTANCE_INX = 5

    @JvmField
    var INTERVAL_DISTANCE_LEN = 4

    @JvmField
    var INTERVAL_CALORIE_INX = 5

    @JvmField
    var INTERVAL_CALORIE_LEN = 2

    // todo
    @JvmField
    var INTERVAL_REST_TIME_INX = -1

    @JvmField
    var INTERVAL_REST_TIME_LEN = 2

    /**
     * 自定义协议 ffe0接收通知
     */
    @JvmField
    var DRAG_INX = 3

    @JvmField
    var DRAG_LEN = 2

    @JvmField
    var INTERVAL_INX = 5

    @JvmField
    var INTERVAL_LEN = 2
    var SET_TIME_INX = 7
    var SET_TIME_LEN = 2
    var SET_DISTANCE_INX = 9
    var SET_DISTANCE_LEN = 4
    var SET_CALORIE_INX = 13
    var SET_CALORIE_LEN = 2
    var REST_TIME_INX = 15
    var REST_TIME_LEN = 2
    var RUN_REST_TIME_INX = 15
    var RUN_REST_TIME_LEN = 2

    // 间歇模式在运行中的时间 AA01990特有
    var RUN_INTERVAL_TIME_INX = 17
    var RUN_INTERVAL_TIME_LEN = 4

    // 间歇模式在运行中的休息时间 AA01990特有
    var RUN_INTERVAL_REST_TIME_INX = 17
    var RUN_INTERVAL_REST_TIME_LEN = 2

    // bike
    @JvmField
    var ONE_KM_TIME_INX = 19

    @JvmField
    var ONE_KM_TIME_LEN = 2

    @JvmField
    var AVERAGE_ONE_KM_TIME_INX = 21

    @JvmField
    var AVERAGE_ONE_KM_TIME_LEN = 2

    @JvmField
    var SPLIT_ONE_KM_TIME_INX = 23

    @JvmField
    var SPLIT_ONE_KM_TIME_LEN = 2

    @JvmField
    var SPLIT_CAL_INX = 25

    @JvmField
    var SPLIT_CAL_LEN = 2
}