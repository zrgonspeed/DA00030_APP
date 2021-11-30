package com.bike.ftms.app.bean.rundata;

import java.util.List;

/*
 {
     "next": "-1",
     "items":[{
         "date": "2021-10-20 10:07:00",
         "type": "100C",
         "result": "73M",
         "remarks":"备注信息"
     },...]
 }
 */
public class RunDataResultListDTO {
    private String next;

    private List<RunDataResultDTO> runDataResultDTOS;

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public List<RunDataResultDTO> getRunDataResultDTOS() {
        return runDataResultDTOS;
    }

    public void setRunDataResultDTOS(List<RunDataResultDTO> runDataResultDTOS) {
        this.runDataResultDTOS = runDataResultDTOS;
    }

    @Override
    public String toString() {
        return "RunDataResultListDTO{" +
                "next='" + next + '\'' +
                ", runDataResultDTOS=" + runDataResultDTOS +
                '}';
    }
}
