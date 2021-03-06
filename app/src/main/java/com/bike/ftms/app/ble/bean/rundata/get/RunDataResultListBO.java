package com.bike.ftms.app.ble.bean.rundata.get;

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
public class RunDataResultListBO {
    private String next;

    private List<RunDataResultDTO> items;

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public List<RunDataResultDTO> getItems() {
        return items;
    }

    public void setItems(List<RunDataResultDTO> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "RunDataResultListBO{" +
                "next='" + next + '\'' +
                ", runDataResultDTOS=" + items +
                '}';
    }
}
