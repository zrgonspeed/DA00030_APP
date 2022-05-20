package com.bike.ftms.app.ble.bean.rundata.raw

import com.bike.ftms.app.common.MyConstant
import org.litepal.crud.LitePalSupport

class RowerDataBean2 : LitePalSupport {
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

    @MyConstant.RunMode
    var runMode = MyConstant.NORMAL
    var reset_time = 0

    @MyConstant.RunStatus
    var runStatus = MyConstant.RUN_STATUS_NO

    @MyConstant.IntervalStatus
    var intervalStatus = MyConstant.INTERVAL_STATUS_REST
    var runInterval = 0 // 各个模式的分段次数  0-255
    var rowerDataBean1: RowerDataBean1? = null

    constructor()
    constructor(bean1: RowerDataBean1) {
        time = bean1.time
        distance = bean1.distance
        calorie = bean1.calorie
        setGoalDistance = bean1.setGoalDistance
        setGoalTime = bean1.setGoalTime
        setGoalCalorie = bean1.setGoalCalorie
        setIntervalCalorie = bean1.setIntervalCalorie
        setIntervalDistance = bean1.setIntervalDistance
        setIntervalTime = bean1.setIntervalTime
        runMode = bean1.runMode
        runInterval = bean1.runInterval
        runStatus = bean1.runStatus
        intervalStatus = bean1.intervalStatus
        reset_time = bean1.reset_time
        sm = bean1.sm
        ave_five_hundred = bean1.ave_five_hundred
        instRpm = bean1.instRpm
        level = bean1.level
        categoryType = bean1.categoryType
        deviceType = bean1.deviceType
        oneKmTime = bean1.oneKmTime
        aveOneKmTime = bean1.aveOneKmTime
        splitCal = bean1.splitCal
        splitOneKmTime = bean1.splitOneKmTime
        ave_watts = bean1.ave_watts
        calories_hr = bean1.calories_hr
        date = bean1.date
        drag = bean1.drag
        five_hundred = bean1.five_hundred
        strokes = bean1.strokes
        watts = bean1.watts
        heart_rate = bean1.heart_rate
        interval = bean1.interval
        note = bean1.note
        rowerDataBean1 = bean1
    }

    fun copy(): RowerDataBean2 {
        val bean = RowerDataBean2()
        bean.id = id
        bean.time = time
        bean.distance = distance
        bean.calorie = calorie
        bean.setGoalDistance = setGoalDistance
        bean.setGoalTime = setGoalTime
        bean.setGoalCalorie = setGoalCalorie
        bean.setIntervalCalorie = setIntervalCalorie
        bean.setIntervalDistance = setIntervalDistance
        bean.setIntervalTime = setIntervalTime
        bean.runMode = runMode
        bean.runInterval = runInterval
        bean.runStatus = runStatus
        bean.intervalStatus = intervalStatus
        bean.reset_time = reset_time
        bean.instRpm = instRpm
        bean.level = level
        bean.categoryType = categoryType
        bean.deviceType = deviceType
        bean.oneKmTime = oneKmTime
        bean.aveOneKmTime = aveOneKmTime
        bean.splitCal = splitCal
        bean.splitOneKmTime = splitOneKmTime
        bean.ave_five_hundred = ave_five_hundred
        bean.ave_watts = ave_watts
        bean.calories_hr = calories_hr
        bean.date = date
        bean.drag = drag
        bean.sm = sm
        bean.five_hundred = five_hundred
        bean.strokes = strokes
        bean.watts = watts
        bean.heart_rate = heart_rate
        bean.interval = interval
        bean.note = note
        bean.rowerDataBean1 = rowerDataBean1
        return bean
    }

    override fun toString(): String {
        return "RowerDataBean2{" +
                "id=" + id +
                ", runMode=" + runMode +
                ", runStatus=" + runStatus +
                ", time=" + time +
                ", distance=" + distance +
                ", calorie=" + calorie +
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
                ", level=" + level +
                ", instRpm=" + instRpm +
                ", instSpeed==" + instSpeed +
                ", oneKmTime==" + oneKmTime +
                ", aveOneKmTime==" + aveOneKmTime +
                ", splitOneKmTime==" + splitOneKmTime +
                ", splitCal==" + splitCal +
                ", deviceType==" + deviceType +
                ", categoryType==" + categoryType +
                ", strokes=" + strokes +
                ", drag=" + drag +
                ", sm=" + sm +
                ", five_hundred=" + five_hundred +
                ", heart_rate=" + heart_rate +
                ", ave_five_hundred=" + ave_five_hundred +
                ", watts=" + watts +
                ", ave_watts=" + ave_watts +
                ", calories_hr=" + calories_hr +
                ", note='" + note + '\'' +
                ", date=" + date +
                '}'
    }
}