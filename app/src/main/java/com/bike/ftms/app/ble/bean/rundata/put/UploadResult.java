package com.bike.ftms.app.ble.bean.rundata.put;

/*
HTTP/1.1 200 OK
{
  "workout_id": "111"
}
 */
public class UploadResult {
    private String workout_id;

    public String getWorkout_id() {
        return workout_id;
    }

    public void setWorkout_id(String workout_id) {
        this.workout_id = workout_id;
    }

    @Override
    public String toString() {
        return "UploadResult{" +
                "workout_id='" + workout_id + '\'' +
                '}';
    }
}
