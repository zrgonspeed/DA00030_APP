package com.bike.ftms.app.bean;

import com.bike.ftms.app.common.MyConstant;

import org.litepal.crud.LitePalSupport;

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/2
 */
public class RowerDataBean extends LitePalSupport {
    private int strokes;
    private int drag;
    private int interval;
    private long distance;
    private int sm;
    private long five_hundred;
    private long time;
    private int heart_rate;
    private long ave_five_hundred;
    private int watts;
    private int ave_watts;
    private int calorie;
    private int calories_hr;
    private String note;
    private long date;
    private long setTime = 0;//间歇模式设定时间
    private long setDistance = 0;//间歇模式设定距离
    private long setCalorie = 0;//间歇模式设定卡路里

    private long setTargetTime = 0;//目标模式设定时间
    private long setTargetDistance = 0;//目标模式设定距离
    private long setTargetCalorie = 0;//目标模式设定卡路里

    private int mode = MyConstant.NORMAL;
    private int reset_time;

    public RowerDataBean() {
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

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
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

    public long getSetTime() {
        return setTime;
    }

    public void setSetTime(long setTime) {
        this.setTime = setTime;
    }

    public long getSetDistance() {
        return setDistance;
    }

    public void setSetDistance(long setDistance) {
        this.setDistance = setDistance;
    }

    public long getSetCalorie() {
        return setCalorie;
    }

    public void setSetCalorie(long setCalorie) {
        this.setCalorie = setCalorie;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(@MyConstant.RunMode int mode) {
        this.mode = mode;
    }

    public int getReset_time() {
        return reset_time;
    }

    public void setReset_time(int reset_time) {
        this.reset_time = reset_time;
    }

    public long getSetTargetTime() {
        return setTargetTime;
    }

    public void setSetTargetTime(long setTargetTime) {
        this.setTargetTime = setTargetTime;
    }

    public long getSetTargetDistance() {
        return setTargetDistance;
    }

    public void setSetTargetDistance(long setTargetDistance) {
        this.setTargetDistance = setTargetDistance;
    }

    public long getSetTargetCalorie() {
        return setTargetCalorie;
    }

    public void setSetTargetCalorie(long setTargetCalorie) {
        this.setTargetCalorie = setTargetCalorie;
    }

    @Override
    public String toString() {
        return "RowerDataBean{" +
                "strokes=" + strokes +
                ", drag=" + drag +
                ", interval=" + interval +
                ", distance=" + distance +
                ", sm=" + sm +
                ", five_hundred=" + five_hundred +
                ", time=" + time +
                ", heart_rate=" + heart_rate +
                ", ave_five_hundred=" + ave_five_hundred +
                ", watts=" + watts +
                ", ave_watts=" + ave_watts +
                ", calorie=" + calorie +
                ", calories_hr=" + calories_hr +
                ", note='" + note + '\'' +
                ", date=" + date +
                ", setTime=" + setTime +
                ", setDistance=" + setDistance +
                ", setCalorie=" + setCalorie +
                ", setTargetTime=" + setTargetTime +
                ", setTargetDistance=" + setTargetDistance +
                ", setTargetCalorie=" + setTargetCalorie +
                ", mode=" + mode +
                ", reset_time=" + reset_time +
                '}';
    }
}
