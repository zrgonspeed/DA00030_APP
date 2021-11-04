package com.bike.ftms.app.bean;

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

    private List<RunDataResult> runDataResults;

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public List<RunDataResult> getRunDataResults() {
        return runDataResults;
    }

    public void setRunDataResults(List<RunDataResult> runDataResults) {
        this.runDataResults = runDataResults;
    }

    @Override
    public String toString() {
        return "RunDataResultListDTO{" +
                "next='" + next + '\'' +
                ", runDataResults=" + runDataResults +
                '}';
    }
}
