package com.bike.ftms.app.bean.rundata;

public class HttpRowerDataBean1 extends RowerDataBean1 {
    private String workout_id;

    // 0 没上传， 1 已上传
    private int status;

    public HttpRowerDataBean1() {

    }

    public HttpRowerDataBean1(RowerDataBean1 bean1) {
        setId(bean1.getId());

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

        setInstRpm(bean1.getInstRpm());
        setLevel(bean1.getLevel());
        setCategoryType(bean1.getCategoryType());
        setDeviceType(bean1.getDeviceType());
        setOneKmTime(bean1.getOneKmTime());
        setAveOneKmTime(bean1.getAveOneKmTime());
        setSplitCal(bean1.getSplitCal());
        setSplitOneKmTime(bean1.getSplitOneKmTime());

        setList(bean1.getList());
    }

    public void setWorkout_id(String workout_id) {
        this.workout_id = workout_id;
    }

    public String getWorkout_id() {
        return workout_id;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "HttpRowerDataBean1{" +
                "workout_id='" + workout_id + '\'' +
                ", status=" + status +
                '}';
    }
}
