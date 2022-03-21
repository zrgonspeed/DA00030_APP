package com.bike.ftms.app.ble.bean.rundata.view;

public class RunInfoItem {
    private String interval;

    private String time;
    private String meters;
    private String cals;
    private String cal_hr;
    private String ave_watts;

    private String ave_500;
    private String sm;

    private String ave_one_km;
    private String level;

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMeters() {
        return meters;
    }

    public void setMeters(String meters) {
        this.meters = meters;
    }

    public String getCals() {
        return cals;
    }

    public void setCals(String cals) {
        this.cals = cals;
    }

    public String getCal_hr() {
        return cal_hr;
    }

    public void setCal_hr(String cal_hr) {
        this.cal_hr = cal_hr;
    }

    public String getAve_watts() {
        return ave_watts;
    }

    public void setAve_watts(String ave_watts) {
        this.ave_watts = ave_watts;
    }

    public String getAve_500() {
        return ave_500;
    }

    public void setAve_500(String ave_500) {
        this.ave_500 = ave_500;
    }

    public String getSm() {
        return sm;
    }

    public void setSm(String sm) {
        this.sm = sm;
    }

    public String getAve_one_km() {
        return ave_one_km;
    }

    public void setAve_one_km(String ave_one_km) {
        this.ave_one_km = ave_one_km;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "RunInfoItem{" +
                "interval=" + interval +
                ", time='" + time + '\'' +
                ", meters='" + meters + '\'' +
                ", cals='" + cals + '\'' +
                ", cal_hr='" + cal_hr + '\'' +
                ", ave_watts='" + ave_watts + '\'' +
                ", ave_500='" + ave_500 + '\'' +
                ", sm='" + sm + '\'' +
                ", ave_one_km='" + ave_one_km + '\'' +
                ", level='" + level + '\'' +
                '}';
    }
}
