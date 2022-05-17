package com.bike.ftms.app.ble.bean.rundata.view;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RunInfoVO {
    private int local_id = -1;
    private int server_id = -1;
    private String date;
    private int categoryType;
    private int deviceType;
    private String runMode;
    private int runModeNum;
    private String note;

    private RunInfoItem totalItem;
    private List<RunInfoItem> items;

    public int getRunModeNum() {
        return runModeNum;
    }

    public void setRunModeNum(int runModeNum) {
        this.runModeNum = runModeNum;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getLocal_id() {
        return local_id;
    }

    public void setLocal_id(int local_id) {
        this.local_id = local_id;
    }

    public int getServer_id() {
        return server_id;
    }

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }

    public RunInfoItem getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(RunInfoItem totalItem) {
        this.totalItem = totalItem;
    }

    public List<RunInfoItem> getItems() {
        return items;
    }

    public void setItems(List<RunInfoItem> items) {
        this.items = items;
    }

    public int getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(int categoryType) {
        this.categoryType = categoryType;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getRunMode() {
        return runMode;
    }

    public void setRunMode(String runMode) {
        this.runMode = runMode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @NotNull
    @Override
    public String toString() {
        String s1 = "RunInfoVO{" +
                "local_id=" + local_id +
                ", server_id=" + server_id +
                ", categoryType=" + categoryType +
                ", deviceType=" + deviceType +
                ", runMode='" + runMode + '\'' +
                ", runModeNum=" + runModeNum +
                ", date='" + date + '\'' +
                ", note='" + note + '\'' +
                '}';

        String title = "-------------" + "\n" + "\t" + "time" + "\t\t" + "meters" + "\t" + "cals" + "\t" + "ave_500" + "\t\t" + "sm" + "\t\t" + "ave_one_km" + "\t\t" + "level" + "\t\t" + "cal_hr" + "\t\t" +
                "ave_watts" + "\n";
        String row = "";
        for (int i = 0; i < items.size(); i++) {
            RunInfoItem item = items.get(i);
            row = row + item.getInterval() + "\t" + item.getTime() + "\t" + item.getMeters() + " \t" + item.getCals() + "\t\t" + item.getAve_500() + "\t\t\t" + item.getSm() + "\t\t\t" + item.getAve_one_km() + "\t\t\t" + item.getLevel() + "\t\t\t" + item.getCal_hr() + "\t\t" + item.getAve_watts() + "\n";
        }

        return title + row + s1;
        // return PrintUtils.stringToJSON(GsonUtil.GsonString(this));

        // return printJson("tag",GsonUtil.GsonString(this),"head");
        // return "RunInfoVO{" +
        //         "local_id=" + local_id +
        //         ", server_id=" + server_id +
        //         ", totalItem=" + totalItem +
        //         ", items=" + items +
        //         ", categoryType=" + categoryType +
        //         ", deviceType=" + deviceType +
        //         ", runMode='" + runMode + '\'' +
        //         ", runModeNum=" + runModeNum +
        //         ", date='" + date + '\'' +
        //         ", note='" + note + '\'' +
        //         '}';
    }


}
