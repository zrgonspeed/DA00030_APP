package com.bike.ftms.app.ble.bean.rundata.raw

import org.litepal.crud.LitePalSupport
import com.bike.ftms.app.common.MyConstant.RunMode
import com.bike.ftms.app.common.MyConstant
import com.bike.ftms.app.common.MyConstant.RunStatus
import com.bike.ftms.app.common.MyConstant.IntervalStatus
import org.litepal.annotation.Column
import com.bike.ftms.app.utils.TimeStringUtil
import java.util.ArrayList

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/2
 */
open class RowerDataBean1 : LitePalSupport() {
    var id = 0
    var strokes = 0
    var drag = 0
    var interval = 0
    var sm = 0
    var five_hundred: Long = 0
    var heart_rate = 0
    var ave_five_hundred: Long = 0
    var watts = 0
    var ave_watts = 0
    var calories_hr = 0
    var note: String? = null
    var date: Long = 0
    var time: Long = 0
    var distance: Long = 0
    var calorie: Long = 0
    var setIntervalTime: Long = 0 //间歇模式设定时间
    var setIntervalDistance: Long = 0 //间歇模式设定距离
    var setIntervalCalorie: Long = 0 //间歇模式设定卡路里
    var setGoalTime: Long = 0 //目标模式设定时间
    var setGoalDistance: Long = 0 //目标模式设定距离
    var setGoalCalorie: Long = 0 //目标模式设定卡路里
    var instSpeed = 0
    var instRpm = 0
    var level = 0
    var oneKmTime = 0
    var aveOneKmTime = 0
    var splitOneKmTime = 0
    var splitCal = 0
    var deviceType = 0
    var categoryType = 0

    @RunMode
    var runMode = MyConstant.NORMAL
    var reset_time = 0

    @RunStatus
    var runStatus = MyConstant.RUN_STATUS_NO

    @IntervalStatus
    var intervalStatus = MyConstant.INTERVAL_STATUS_REST
    var runInterval = 0 // 电子表实际分段 各个模式的分段次数  0-255
    var list: List<RowerDataBean2> = ArrayList()
    override fun toString(): String {
        return "RowerDataBean1{" +
                "id=" + id +
                ", runMode=" + runMode +
                ", runStatus=" + runStatus +
                ", time=" + time +
                ", distance=" + distance +
                ", calorie=" + calorie +
                ", watts=" + watts +
                ", interval=" + interval +
                ", setIntervalTime=" + setIntervalTime +
                ", setIntervalDistance=" + setIntervalDistance +
                ", setIntervalCalorie=" + setIntervalCalorie +
                ", intervalStatus=" + intervalStatus +
                ", reset_time=" + reset_time +
                ", runInterval=" + runInterval +
                ", setGoalTime=" + setGoalTime +
                ", setGoalDistance=" + setGoalDistance +
                ", setGoalCalorie=" + setGoalCalorie +
                ", heart_rate=" + heart_rate +
                ", strokes=" + strokes +
                ", drag=" + drag +
                ", sm=" + sm +
                ", five_hundred=" + five_hundred +
                ", ave_five_hundred=" + ave_five_hundred +
                ", level=" + level +
                ", instRpm=" + instRpm +
                ", instSpeed==" + instSpeed +
                ", oneKmTime==" + oneKmTime +
                ", aveOneKmTime==" + aveOneKmTime +
                ", splitOneKmTime==" + splitOneKmTime +
                ", splitCal==" + splitCal +
                ", deviceType==" + deviceType +
                ", categoryType==" + categoryType +
                ", ave_watts=" + ave_watts +
                ", calories_hr=" + calories_hr +
                ", note='" + note + '\'' +
                ", date=" + date +
                ", list.size =" + list.size +
                '}'
    }

    @Column(ignore = true)
    private var flag = 1
    fun setFlag(newFlag: Int) {
        if (newFlag == 2) {
            flag = newFlag
            return
        }
        if (flag == 3) {
            return
        }
        flag = newFlag
    }

    val canSave: Boolean
        get() = flag == 3

    // 特有
    var result: String? = null
    var type: String? = null
    fun setTypeAndResult() {
        when (runMode) {
            MyConstant.NORMAL -> {
                type = distance.toString() + "M"
                result = distance.toString() + "M"
            }
            MyConstant.GOAL_TIME -> {
                type = TimeStringUtil.getSToMinSecValue(setGoalTime.toFloat())
                result = distance.toString() + "M"
            }
            MyConstant.GOAL_CALORIES -> {
                type = setGoalCalorie.toString() + "C"
                result = distance.toString() + "M"
            }
            MyConstant.GOAL_DISTANCE -> {
                type = setGoalDistance.toString() + "M"
                result = TimeStringUtil.getSToMinSecValue(time.toFloat())
            }
            MyConstant.INTERVAL_TIME -> {
                type = interval.toString() + "x:" + setIntervalTime + "/:" + reset_time + "R"
                // 总距离
                val list = list
                if (list.size > 1) {
                    var totalMeter: Long = 0
                    for (bean2 in list) {
                        totalMeter += bean2.distance
                    }
                    result = totalMeter.toString() + "M"
                } else {
                    result = distance.toString() + "M"
                }
            }
            MyConstant.INTERVAL_CALORIES -> {
                type = interval.toString() + "x" + setIntervalCalorie + "C" + "/:" + reset_time + "R"
                // 总距离
                val list = list
                if (list.size > 1) {
                    var totalMeter: Long = 0
                    for (bean2 in list) {
                        totalMeter += bean2.distance
                    }
                    result = totalMeter.toString() + "M"
                } else {
                    result = distance.toString() + "M"
                }
            }
            MyConstant.INTERVAL_DISTANCE -> {
                type = interval.toString() + "x" + setIntervalDistance + "M" + "/:" + reset_time + "R"

                // 总时间
                val list = list
                if (list.size > 1) {
                    var totalTime: Long = 0
                    for (bean2 in list) {
                        totalTime += bean2.time
                    }
                    result = TimeStringUtil.getSToMinSecValue(totalTime.toFloat())
                } else {
                    result = TimeStringUtil.getSToMinSecValue(time.toFloat())
                }
            }
            else -> {
            }
        }
    }

    var totalsItem: RowerDataBean2? = null
        private set

    private fun setTotalsItem(bean2: RowerDataBean2) {
        totalsItem = bean2
    }

    fun setTotalsItem() {
        var bb = RowerDataBean2()
        run {
            val list = ArrayList<RowerDataBean2>()
            for (bean2 in list) {
                list.add(bean2.copy())
            }
            if (list.size == 0) {
                val rowerDataBean2 = RowerDataBean2(this)
                list.add(rowerDataBean2)
                setTotalsItem(rowerDataBean2)
                return
            }
            when (runMode) {
                MyConstant.GOAL_TIME -> {
                    bb = RowerDataBean2()
                    bb.runMode = runMode
                    bb.calories_hr = calories_hr
                    bb.watts = watts
                    var initDistance: Long = 0
                    var initTime = setGoalTime
                    var initCal: Long = 0
                    for (bean2 in list) {
                        // 平均
                        bb.ave_five_hundred = bean2.ave_five_hundred + bb.ave_five_hundred
                        bb.sm = bean2.sm + bb.sm

                        // 每段的运动时间
                        bean2.time = initTime - bean2.time
                        bb.time = bb.time + bean2.time
                        initTime = initTime - bean2.time

                        // 每段的卡路里
                        bean2.calorie = bean2.calorie - initCal
                        bb.calorie = bb.calorie + bean2.calorie
                        initCal = initCal + bean2.calorie

                        // 每段的运动距离 倒数
                        bean2.distance = bean2.distance - initDistance
                        bb.distance = bb.distance + bean2.distance
                        initDistance = initDistance + bean2.distance
                    }
                    bb.ave_five_hundred = bb.ave_five_hundred / list.size
                    bb.sm = bb.sm / list.size
                    bb.interval = -1
                    list.add(0, bb)
                }
                MyConstant.GOAL_DISTANCE -> {
                    bb = RowerDataBean2()
                    bb.runMode = runMode
                    bb.calories_hr = calories_hr
                    bb.watts = watts
                    var initDistance = setGoalDistance
                    var initTime: Long = 0
                    var initCal: Long = 0
                    for (bean2 in list) {
                        // 平均
                        bb.ave_five_hundred = bean2.ave_five_hundred + bb.ave_five_hundred
                        bb.sm = bean2.sm + bb.sm

                        // 每段的运动距离 倒数
                        bean2.distance = initDistance - bean2.distance
                        bb.distance = bb.distance + bean2.distance
                        initDistance = initDistance - bean2.distance

                        // 每段的运动时间
                        bean2.time = bean2.time - initTime
                        bb.time = bb.time + bean2.time
                        initTime = initTime + bean2.time

                        // 每段的卡路里
                        bean2.calorie = bean2.calorie - initCal
                        bb.calorie = bb.calorie + bean2.calorie
                        initCal = initCal + bean2.calorie
                    }
                    bb.ave_five_hundred = bb.ave_five_hundred / list.size
                    bb.sm = bb.sm / list.size
                    bb.interval = -1
                    list.add(0, bb)
                }
                MyConstant.GOAL_CALORIES -> {
                    bb = RowerDataBean2()
                    bb.runMode = runMode
                    bb.calories_hr = calories_hr
                    bb.watts = watts
                    var initDistance: Long = 0
                    var initTime: Long = 0
                    var initCal = setGoalCalorie
                    for (bean2 in list) {
                        // 平均
                        bb.ave_five_hundred = bean2.ave_five_hundred + bb.ave_five_hundred
                        bb.sm = bean2.sm + bb.sm

                        // 每段的卡路里
                        bean2.calorie = initCal - bean2.calorie
                        bb.calorie = bb.calorie + bean2.calorie
                        initCal = initCal - bean2.calorie

                        // 每段的运动时间
                        bean2.time = bean2.time - initTime
                        bb.time = bb.time + bean2.time
                        initTime = initTime + bean2.time

                        // 每段的运动距离 倒数
                        bean2.distance = bean2.distance - initDistance
                        bb.distance = bb.distance + bean2.distance
                        initDistance = initDistance + bean2.distance
                    }
                    bb.ave_five_hundred = bb.ave_five_hundred / list.size
                    bb.sm = bb.sm / list.size
                    bb.interval = -1
                    list.add(0, bb)
                }
                MyConstant.INTERVAL_TIME -> {
                    bb = RowerDataBean2()
                    bb.runMode = runMode
                    bb.calories_hr = calories_hr
                    bb.watts = watts + bb.watts
                    for (bean2 in list) {
                        // 平均
                        bb.ave_five_hundred = bean2.ave_five_hundred + bb.ave_five_hundred
                        bb.sm = bean2.sm + bb.sm

                        // 总和
                        if (list.indexOf(bean2) == list.size - 1) {
                            if (bean2.setIntervalTime == bean2.time) {
                                bb.setIntervalTime = bean2.setIntervalTime + bb.setIntervalTime
                            } else {
                                bb.setIntervalTime = bean2.setIntervalTime - bean2.time + bb.setIntervalTime
                            }
                        } else {
                            bb.setIntervalTime = bean2.setIntervalTime + bb.setIntervalTime
                        }
                        bb.calorie = bean2.calorie + bb.calorie
                        bb.distance = bean2.distance + bb.distance
                    }
                    bb.ave_five_hundred = bb.ave_five_hundred / list.size
                    bb.sm = bb.sm / list.size
                    bb.interval = -1
                    list.add(0, bb)
                }
                MyConstant.INTERVAL_DISTANCE -> {
                    bb = RowerDataBean2()
                    bb.runMode = runMode
                    bb.calories_hr = calories_hr
                    bb.watts = watts + bb.watts
                    for (bean2 in list) {
                        // 平均
                        bb.ave_five_hundred = bean2.ave_five_hundred + bb.ave_five_hundred
                        bb.sm = bean2.sm + bb.sm

                        // 总和
                        if (list.indexOf(bean2) == list.size - 1) {
/*                    if (bean2.getSetIntervalDistance() == bean2.getDistance()) {
//                        bb.setSetIntervalDistance(bean2.getSetIntervalDistance() + bb.getSetIntervalDistance());
                        bb.setDistance(bean2.getDistance() + bb.getDistance());
                    } else {
//                        bb.setSetIntervalDistance((bean2.getSetIntervalDistance() - bean2.getDistance()) + bb.getSetIntervalDistance());
                        bb.setDistance((bean2.getDistance() - bean2.getDistance()) + bb.getDistance());
                    }*/
                            bb.distance = bean2.distance + bb.distance
                        } else {
//                    bb.setSetIntervalDistance(bean2.getSetIntervalDistance() + bb.getSetIntervalDistance());
                            bb.distance = bean2.distance + bb.distance
                        }
                        bb.time = bean2.time + bb.time
                        bb.calorie = bean2.calorie + bb.calorie
                    }
                    bb.ave_five_hundred = bb.ave_five_hundred / list.size
                    bb.sm = bb.sm / list.size
                    bb.interval = -1
                    list.add(0, bb)
                }
                MyConstant.INTERVAL_CALORIES -> {
                    bb = RowerDataBean2()
                    bb.runMode = runMode
                    bb.calories_hr = calories_hr
                    bb.watts = watts + bb.watts
                    for (bean2 in list) {
                        // 平均
                        bb.ave_five_hundred = bean2.ave_five_hundred + bb.ave_five_hundred
                        bb.sm = bean2.sm + bb.sm

                        // 总和
                        if (list.indexOf(bean2) == list.size - 1) {
//                    if (bean2.getSetIntervalCalorie() == bean2.getCalorie()) {
//                        bb.setSetIntervalCalorie(bean2.getSetIntervalCalorie() + bb.getSetIntervalCalorie());
//                    } else {
//                        bb.setSetIntervalCalorie((bean2.getSetIntervalCalorie() - bean2.getCalorie()) + bb.getSetIntervalCalorie());
//                    }
                            bb.calorie = bean2.calorie + bb.calorie
                        } else {
//                    bb.setSetIntervalCalorie(bean2.getSetIntervalCalorie() + bb.getSetIntervalCalorie());
                            bb.calorie = bean2.calorie + bb.calorie
                        }
                        bb.time = bean2.time + bb.time
                        bb.distance = bean2.distance + bb.distance
                    }
                    bb.ave_five_hundred = bb.ave_five_hundred / list.size
                    bb.sm = bb.sm / list.size
                    bb.interval = -1
                    list.add(0, bb)
                }
                else -> {
                }
            }
        }
        setTotalsItem(bb)
    }
}