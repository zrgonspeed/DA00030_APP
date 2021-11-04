package com.bike.ftms.app.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * "date": "2021-10-20 10:07:00",
 * "type": "100C",
 * "result": "73M",
 * "remarks":"备注信息"
 */
public class RunDataResult {
    private String workout_id;

    private String date;
    private String type;
    private String result;
    private String remarks; // 备注信息

    private List<RunDataInfo> runDataInfos = new ArrayList<>();

    public String getWorkout_id() {
        return workout_id;
    }

    public void setWorkout_id(String workout_id) {
        this.workout_id = workout_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setRunDataInfos(List<RunDataInfo> runDataInfos) {
        this.runDataInfos = runDataInfos;
    }

    public List<RunDataInfo> getRunDataInfos() {
        return runDataInfos;
    }

    @Override
    public String toString() {
        return "RunDataResult{" +
                "workout_id='" + workout_id + '\'' +
                ", date='" + date + '\'' +
                ", type='" + type + '\'' +
                ", result='" + result + '\'' +
                ", remarks='" + remarks + '\'' +
                ", runDataInfos=" + runDataInfos +
                '}';
    }
}
