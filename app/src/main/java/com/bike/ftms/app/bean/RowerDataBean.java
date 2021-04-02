package com.bike.ftms.app.bean;

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/2
 */
public class RowerDataBean {
    private int strokes;
    private int drag;
    private int interval;
    private long distance;
    private int sm;
    private int five_hundred;
    private int time;
    private int heart_rate;
    private int ave_five_hundred;
    private int watts;
    private int ave_watts;
    private int calorie;
    private int calories_hr;

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

    public int getFive_hundred() {
        return five_hundred;
    }

    public void setFive_hundred(int five_hundred) {
        this.five_hundred = five_hundred;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getHeart_rate() {
        return heart_rate;
    }

    public void setHeart_rate(int heart_rate) {
        this.heart_rate = heart_rate;
    }

    public int getAve_five_hundred() {
        return ave_five_hundred;
    }

    public void setAve_five_hundred(int ave_five_hundred) {
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
}
