package com.bike.ftms.app.common

import android.graphics.drawable.Drawable
import androidx.annotation.IntDef
import androidx.core.content.res.ResourcesCompat
import com.bike.ftms.app.R
import com.bike.ftms.app.base.MyApplication
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

object MyConstant {
    // 支持的机型
    const val DEVICE_UNKNOW = 0

    const val DEVICE_AA02020_00R_01 = 1
    const val DEVICE_AA01990 = 2
    const val DEVICE_AA02020_00F_01 = 3
    const val DEVICE_AA02020_00F_02 = 4
    const val DEVICE_AA02230_00R_01 = 5

    const val DEVICE_AA02290_00R_01 = 6
    const val DEVICE_AA02290_00F_01 = 7
    const val DEVICE_AA02290_00R_02 = 8
    const val DEVICE_AA02290_00R_03 = 9
    const val DEVICE_AA02020_00R_03 = 10

    const val DEVICE_AA02230_00F_01 = 11
    const val DEVICE_AA02020_00F_03 = 12
    const val DEVICE_AA02020_00F_04 = 13
    const val DEVICE_AA02320_00R_01 = 14
    const val DEVICE_AA02020_00R_05 = 15

    const val DEVICE_AA02020_00F_05 = 16

    const val DEVICE_AA02400_00R_01 = 192   // 划船器
    const val DEVICE_AA02320_00F_01 = 17    // 滑雪机

    const val maxType = 255;

    @JvmField
    val nameMap = HashMap<Int, String>()

    init {
        nameMap[DEVICE_UNKNOW] = "DEVICE_UNKNOW"

        nameMap[DEVICE_AA02020_00R_01] = "AA02020-00R-01"
        nameMap[DEVICE_AA01990] = "AA01990"
        nameMap[DEVICE_AA02020_00F_01] = "AA02020-00F-01"
        nameMap[DEVICE_AA02020_00F_02] = "AA02020-00F-02"
        nameMap[DEVICE_AA02230_00R_01] = "AA02230-00R-01"

        nameMap[DEVICE_AA02290_00R_01] = "AA02290-00R-01"
        nameMap[DEVICE_AA02290_00F_01] = "AA02290-00F-01"
        nameMap[DEVICE_AA02290_00R_02] = "AA02290-00R-02"
        nameMap[DEVICE_AA02290_00R_03] = "AA02290-00R-03"
        nameMap[DEVICE_AA02020_00R_03] = "AA02020-00R-03"

        nameMap[DEVICE_AA02230_00F_01] = "AA02230-00F-01"
        nameMap[DEVICE_AA02020_00F_03] = "AA02020-00F-03"
        nameMap[DEVICE_AA02020_00F_04] = "AA02020-00F-04"
        nameMap[DEVICE_AA02320_00R_01] = "AA02320-00R-01"
        nameMap[DEVICE_AA02020_00R_05] = "AA02020-00R-05"

        nameMap[DEVICE_AA02020_00F_05] = "AA02020-00F-05"

        nameMap[DEVICE_AA02400_00R_01] = "AA02400-00R-01"
        nameMap[DEVICE_AA02320_00F_01] = "AA02320-00F-01"
    }

    // 大类机型 ---------------------------------------------------------------------------------
    const val CATEGORY_BOAT = 1000
    const val CATEGORY_BIKE = 1001
    const val CATEGORY_SKI = 1002
    const val CATEGORY_STEP = 1003
    private val boat_arr = intArrayOf(
        DEVICE_AA02020_00R_01,
        DEVICE_AA01990,
        DEVICE_AA02230_00R_01,
        DEVICE_AA02290_00R_01,
        DEVICE_AA02290_00R_02,
        DEVICE_AA02290_00R_03,
        DEVICE_AA02020_00R_03,
        DEVICE_AA02020_00R_05,
        DEVICE_AA02320_00R_01,
        DEVICE_AA02400_00R_01
    )
    private val bike_arr = intArrayOf(
        DEVICE_AA02020_00F_01,
        DEVICE_AA02020_00F_02,
        DEVICE_AA02230_00F_01
    )
    private val ski_arr = intArrayOf(
        DEVICE_AA02290_00F_01,
        DEVICE_AA02020_00F_03,
        DEVICE_AA02020_00F_05,
        DEVICE_AA02020_00F_04,
        DEVICE_AA02320_00F_01
    )
    private val step_arr = intArrayOf()

    @JvmStatic
    @CategoryType
    fun getCategory(@DeviceType deviceType: Int): Int {
        for (device in boat_arr) {
            if (device == deviceType) {
                return CATEGORY_BOAT
            }
        }
        for (device in bike_arr) {
            if (device == deviceType) {
                return CATEGORY_BIKE
            }
        }
        for (device in ski_arr) {
            if (device == deviceType) {
                return CATEGORY_SKI
            }
        }
        for (device in step_arr) {
            if (device == deviceType) {
                return CATEGORY_STEP
            }
        }
        return CATEGORY_BOAT
    }

    @JvmStatic
    fun getCategoryImg(@CategoryType category: Int): Drawable? {
        if (category == CATEGORY_BOAT) {
            return ResourcesCompat.getDrawable(
                MyApplication.getContext().resources,
                R.drawable.boat,
                null
            )
        }
        if (category == CATEGORY_BIKE) {
            return ResourcesCompat.getDrawable(
                MyApplication.getContext().resources,
                R.drawable.bike,
                null
            )
        }
        if (category == CATEGORY_SKI) {
            return ResourcesCompat.getDrawable(
                MyApplication.getContext().resources,
                R.drawable.ski,
                null
            )
        }
        return if (category == CATEGORY_STEP) {
            ResourcesCompat.getDrawable(
                MyApplication.getContext().resources,
                R.drawable.climb,
                null
            )
        } else ResourcesCompat.getDrawable(
            MyApplication.getContext().resources,
            R.drawable.unknow,
            null
        )
    }

    /********************** 运动模式  */
    const val NORMAL = 0
    const val GOAL_TIME = 1  // 也是游戏模式
    const val GOAL_DISTANCE = 2
    const val GOAL_CALORIES = 3
    const val INTERVAL_TIME = 5
    const val INTERVAL_DISTANCE = 6
    const val INTERVAL_CALORIES = 7

    // 机型2(AA01990)新增的3个模式:
    const val CUSTOM_INTERVAL_TIME = 0x15
    const val CUSTOM_INTERVAL_DISTANCE = 0x16
    const val CUSTOM_INTERVAL_CALORIES = 0x17
    fun isNormalMode(mode: Int): Boolean {
        return mode == NORMAL
    }

    @JvmStatic
    fun isGoalMode(mode: Int): Boolean {
        return mode == GOAL_TIME || mode == GOAL_DISTANCE || mode == GOAL_CALORIES
    }

    @JvmStatic
    fun isIntervalMode(mode: Int): Boolean {
        return mode == INTERVAL_TIME || mode == INTERVAL_DISTANCE || mode == INTERVAL_CALORIES
    }

    @JvmStatic
    fun isCustomIntervalMode(mode: Int): Boolean {
        return mode == CUSTOM_INTERVAL_TIME || mode == CUSTOM_INTERVAL_DISTANCE || mode == CUSTOM_INTERVAL_CALORIES
    }

    /********************** 运动状态  */
    const val RUN_STATUS_NO = 0
    const val RUN_STATUS_YES = 1

    /********************** 间隙模式状态  */
    const val INTERVAL_STATUS_REST = 5
    const val INTERVAL_STATUS_RUNNING = 6

    @IntDef(
        DEVICE_AA02020_00R_01,
        DEVICE_AA01990,
        DEVICE_AA02020_00F_01,
        DEVICE_AA02020_00F_02,
        DEVICE_AA02230_00R_01,
        DEVICE_AA02290_00R_01,
        DEVICE_AA02290_00F_01,
        DEVICE_AA02290_00R_02,
        DEVICE_AA02290_00R_03,
        DEVICE_AA02020_00R_03,
        DEVICE_AA02230_00F_01,
        DEVICE_AA02020_00F_03,

        DEVICE_AA02020_00F_04,
        DEVICE_AA02320_00R_01,
        DEVICE_AA02020_00R_05,
        DEVICE_AA02020_00F_05,

        DEVICE_AA02400_00R_01,
        DEVICE_AA02320_00F_01
    )
    @Retention(RetentionPolicy.SOURCE)
    annotation class DeviceType

    @IntDef(CATEGORY_BOAT, CATEGORY_BIKE, CATEGORY_SKI, CATEGORY_STEP)
    @Retention(RetentionPolicy.SOURCE)
    annotation class CategoryType

    @IntDef(
        NORMAL,
        GOAL_TIME,
        GOAL_DISTANCE,
        GOAL_CALORIES,
        INTERVAL_TIME,
        INTERVAL_DISTANCE,
        INTERVAL_CALORIES
    )
    @Retention(RetentionPolicy.SOURCE)
    annotation class RunMode

    @IntDef(RUN_STATUS_NO, RUN_STATUS_YES)
    @Retention(RetentionPolicy.SOURCE)
    annotation class RunStatus

    @IntDef(INTERVAL_STATUS_REST, INTERVAL_STATUS_RUNNING)
    @Retention(RetentionPolicy.SOURCE)
    annotation class IntervalStatus
}