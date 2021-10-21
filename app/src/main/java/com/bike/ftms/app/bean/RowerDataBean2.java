package com.bike.ftms.app.bean;

import com.bike.ftms.app.common.MyConstant;

import org.litepal.crud.LitePalSupport;

public class RowerDataBean2 extends LitePalSupport {
    private int id;
    private int strokes;
    private int drag;
    private int interval;
    private int sm;
    private long five_hundred;
    private int heart_rate;
    private long ave_five_hundred;
    private int watts;
    private int ave_watts;
    private int calories_hr;
    private String note;
    private long date;

    private long time;
    private long distance;
    private long calorie;
    private long setIntervalTime = 0;//间歇模式设定时间
    private long setIntervalDistance = 0;//间歇模式设定距离
    private long setIntervalCalorie = 0;//间歇模式设定卡路里
    private long setGoalTime = 0;//目标模式设定时间
    private long setGoalDistance = 0;//目标模式设定距离
    private long setGoalCalorie = 0;//目标模式设定卡路里

    @MyConstant.RunMode
    private int runMode = MyConstant.NORMAL;
    private int reset_time;
    @MyConstant.RunStatus
    private int runStatus = MyConstant.RUN_STATUS_NO;
    @MyConstant.IntervalStatus
    private int intervalStatus = MyConstant.INTERVAL_STATUS_REST;
    private int runInterval = 0;        // 各个模式的分段次数  0-255
    private RowerDataBean1 rowerDataBean1;

    public RowerDataBean2() {
    }

    public RowerDataBean2(RowerDataBean1 bean1) {
        setTime(bean1.getTime());
        setDistance(bean1.getDistance());
        setCalorie(bean1.getCalorie());

        setSetGoalDistance(bean1.getSetGoalDistance());
        setSetGoalTime(bean1.getSetGoalTime());
        setSetGoalCalorie(bean1.getSetGoalCalorie());

        setSetIntervalCalorie(bean1.getSetIntervalCalorie());
        setSetIntervalDistance(bean1.getSetIntervalDistance());
        setSetIntervalTime(bean1.getSetIntervalTime());

        setRunMode(bean1.getRunMode());
        setRunInterval(bean1.getRunInterval());
        setRunStatus(bean1.getRunStatus());
        setIntervalStatus(bean1.getIntervalStatus());
        setReset_time(bean1.getReset_time());

        setAve_five_hundred(bean1.getAve_five_hundred());
        setAve_watts(bean1.getAve_watts());
        setCalories_hr(bean1.getCalories_hr());
        setDate(bean1.getDate());
        setDrag(bean1.getDrag());
        setSm(bean1.getSm());
        setFive_hundred(bean1.getFive_hundred());
        setStrokes(bean1.getStrokes());
        setWatts(bean1.getWatts());
        setHeart_rate(bean1.getHeart_rate());
        setInterval(bean1.getInterval());
        setNote(bean1.getNote());

        setRowerDataBean1(bean1);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStrokes() {
        return strokes;
    }

    public void setStrokes(int strokes) {
        this.strokes = strokes;
    }

    public int getDrag() {
        return drag;
    }

    public void setDrag(int drag) {
        this.drag = drag;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public int getSm() {
        return sm;
    }

    public void setSm(int sm) {
        this.sm = sm;
    }

    public long getFive_hundred() {
        return five_hundred;
    }

    public void setFive_hundred(long five_hundred) {
        this.five_hundred = five_hundred;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getHeart_rate() {
        return heart_rate;
    }

    public void setHeart_rate(int heart_rate) {
        this.heart_rate = heart_rate;
    }

    public long getAve_five_hundred() {
        return ave_five_hundred;
    }

    public void setAve_five_hundred(long ave_five_hundred) {
        this.ave_five_hundred = ave_five_hundred;
    }

    public int getWatts() {
        return watts;
    }

    public void setWatts(int watts) {
        this.watts = watts;
    }

    public int getAve_watts() {
        return ave_watts;
    }

    public void setAve_watts(int ave_watts) {
        this.ave_watts = ave_watts;
    }

    public long getCalorie() {
        return calorie;
    }

    public void setCalorie(long calorie) {
        this.calorie = calorie;
    }

    public int getCalories_hr() {
        return calories_hr;
    }

    public void setCalories_hr(int calories_hr) {
        this.calories_hr = calories_hr;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getSetIntervalTime() {
        return setIntervalTime;
    }

    public void setSetIntervalTime(long setIntervalTime) {
        this.setIntervalTime = setIntervalTime;
    }

    public long getSetIntervalDistance() {
        return setIntervalDistance;
    }

    public void setSetIntervalDistance(long setIntervalDistance) {
        this.setIntervalDistance = setIntervalDistance;
    }

    public long getSetIntervalCalorie() {
        return setIntervalCalorie;
    }

    public void setSetIntervalCalorie(long setIntervalCalorie) {
        this.setIntervalCalorie = setIntervalCalorie;
    }

    public int getRunMode() {
        return runMode;
    }

    public void setRunMode(@MyConstant.RunMode int runMode) {
        this.runMode = runMode;
    }

    public int getReset_time() {
        return reset_time;
    }

    public void setReset_time(int reset_time) {
        this.reset_time = reset_time;
    }

    public long getSetGoalTime() {
        return setGoalTime;
    }

    public void setSetGoalTime(long setGoalTime) {
        this.setGoalTime = setGoalTime;
    }

    public long getSetGoalDistance() {
        return setGoalDistance;
    }

    public void setSetGoalDistance(long setGoalDistance) {
        this.setGoalDistance = setGoalDistance;
    }

    public long getSetGoalCalorie() {
        return setGoalCalorie;
    }

    public void setSetGoalCalorie(long setGoalCalorie) {
        this.setGoalCalorie = setGoalCalorie;
    }

    public RowerDataBean1 getRowerDataBean1() {
        return rowerDataBean1;
    }

    public void setRowerDataBean1(RowerDataBean1 rowerDataBean1) {
        this.rowerDataBean1 = rowerDataBean1;
    }

    public int getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(int runStatus) {
        this.runStatus = runStatus;
    }

    public int getIntervalStatus() {
        return intervalStatus;
    }

    public void setIntervalStatus(int intervalStatus) {
        this.intervalStatus = intervalStatus;
    }

    public int getRunInterval() {
        return runInterval;
    }

    public void setRunInterval(int runInterval) {
        this.runInterval = runInterval;
    }

    public RowerDataBean2 copy() {
        RowerDataBean2 bean = new RowerDataBean2();
        bean.setId(getId());

        bean.setTime(getTime());
        bean.setDistance(getDistance());
        bean.setCalorie(getCalorie());

        bean.setSetGoalDistance(getSetGoalDistance());
        bean.setSetGoalTime(getSetGoalTime());
        bean.setSetGoalCalorie(getSetGoalCalorie());

        bean.setSetIntervalCalorie(getSetIntervalCalorie());
        bean.setSetIntervalDistance(getSetIntervalDistance());
        bean.setSetIntervalTime(getSetIntervalTime());

        bean.setRunMode(getRunMode());
        bean.setRunInterval(getRunInterval());
        bean.setRunStatus(getRunStatus());
        bean.setIntervalStatus(getIntervalStatus());
        bean.setReset_time(getReset_time());

        bean.setAve_five_hundred(getAve_five_hundred());
        bean.setAve_watts(getAve_watts());
        bean.setCalories_hr(getCalories_hr());
        bean.setDate(getDate());
        bean.setDrag(getDrag());
        bean.setSm(getSm());
        bean.setFive_hundred(getFive_hundred());
        bean.setStrokes(getStrokes());
        bean.setWatts(getWatts());
        bean.setHeart_rate(getHeart_rate());
        bean.setInterval(getInterval());
        bean.setNote(getNote());

        bean.setRowerDataBean1(getRowerDataBean1());

        return bean;
    }

    @Override
    public String toString() {
        return "RowerDataBean2{" +
                "id=" + id +
//                ", rowerDataBean1.id=" + rowerDataBean1.getId() +
                ", strokes=" + strokes +
                ", drag=" + drag +
                ", interval=" + interval +
                ", sm=" + sm +
                ", five_hundred=" + five_hundred +
                ", heart_rate=" + heart_rate +
                ", ave_five_hundred=" + ave_five_hundred +
                ", watts=" + watts +
                ", ave_watts=" + ave_watts +
                ", calories_hr=" + calories_hr +
                ", note='" + note + '\'' +
                ", date=" + date +
                ", time=" + time +
                ", distance=" + distance +
                ", calorie=" + calorie +
                ", setIntervalTime=" + setIntervalTime +
                ", setIntervalDistance=" + setIntervalDistance +
                ", setIntervalCalorie=" + setIntervalCalorie +
                ", setGoalTime=" + setGoalTime +
                ", setGoalDistance=" + setGoalDistance +
                ", setGoalCalorie=" + setGoalCalorie +
                ", runMode=" + runMode +
                ", reset_time=" + reset_time +
                ", runStatus=" + runStatus +
                ", intervalStatus=" + intervalStatus +
                ", runInterval=" + runInterval +
                '}';
    }
}
