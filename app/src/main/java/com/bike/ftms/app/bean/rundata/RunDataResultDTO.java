package com.bike.ftms.app.bean.rundata;

import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.utils.TimeStringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * "date": "2021-10-20 10:07:00",
 * "type": "100C",
 * "result": "73M",
 * "remarks":"备注信息"
 */
public class RunDataResultDTO {
    private String workout_id;

    private String date;
    private String type;
    private String result;
    private String remarks; // 备注信息

    private List<RunDataInfoDTO> runDataInfoDTOS = new ArrayList<>();

    public RunDataResultDTO() {
    }

    public RunDataResultDTO(RowerDataBean1 bean1) {
        toSelf(bean1);
    }

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

    public void setRunDataInfoDTOS(List<RunDataInfoDTO> runDataInfoDTOS) {
        this.runDataInfoDTOS = runDataInfoDTOS;
    }

    public List<RunDataInfoDTO> getRunDataInfoDTOS() {
        return runDataInfoDTOS;
    }

    @Override
    public String toString() {
        return "RunDataResultDTO{" +
                "workout_id='" + workout_id + '\'' +
                ", date='" + date + '\'' +
                ", type='" + type + '\'' +
                ", result='" + result + '\'' +
                ", remarks='" + remarks + '\'' +
                ", runDataInfoDTOS=" + runDataInfoDTOS +
                '}';
    }

    /**
     * 上传到服务器的数据对象
     *
     * @param bean1
     */
    private void toSelf(RowerDataBean1 bean1) {
        setDate(TimeStringUtil.getDate2String(bean1.getDate(), "yyyy-MM-dd HH:mm:ss"));
        setRemarks(bean1.getNote());

        bean1.setTypeAndResult();
        setType(bean1.getType());
        setResult(bean1.getResult());


    }
}
