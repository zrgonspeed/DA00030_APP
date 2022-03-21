package com.bike.ftms.app.ble.bean.rundata.get;

import com.bike.ftms.app.ble.bean.rundata.raw.RowerDataBean1;
import com.bike.ftms.app.utils.TimeStringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * {
 * "totals": {
 * "time": "00:01:40",
 * "meters": "500M",
 * "efm": "01:35",
 * "cals": "44",
 * "sm": "73",
 * "calhr": "1686",
 * "watts": "403"
 * },
 * "items": [
 * {
 * "time": "00:00:20",
 * "meters": "100M",
 * "efm": "01:35",
 * "cals": "9",
 * "sm": "73",
 * "calhr": "1686",
 * "watts": "403"
 * },
 * ...
 * ]
 * }
 */
public class RunDataResultBO {
    private String workout_id;

    private String date;
    private String type;
    private String result;
    private String remarks;

    private List<RunDataInfoDTO> items = new ArrayList<>();
    private RunDataInfoDTO totals = new RunDataInfoDTO();

    public RunDataResultBO() {
    }

    public RunDataResultBO(RowerDataBean1 bean1) {
        toSelf(bean1);
    }

    public void setTotals(RunDataInfoDTO totals) {
        this.totals = totals;
    }

    public RunDataInfoDTO getTotals() {
        return totals;
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

    public void setItems(List<RunDataInfoDTO> items) {
        this.items = items;
    }

    public List<RunDataInfoDTO> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "RunDataResultDTO{" +
                "workout_id='" + workout_id + '\'' +
                ", date='" + date + '\'' +
                ", type='" + type + '\'' +
                ", result='" + result + '\'' +
                ", remarks='" + remarks + '\'' +
                ", runDataInfoDTOS=" + items +
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
