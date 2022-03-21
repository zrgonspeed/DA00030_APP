package com.bike.ftms.app.ble.bean.rundata.put;

public class RemarksBO {
    private String remarks;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "RemarksBO{" +
                "remarks='" + remarks + '\'' +
                '}';
    }
}
