package com.bike.ftms.app.ble.bean.rundata.put;

import com.bike.ftms.app.ble.bean.rundata.raw.RowerDataBean2;
import com.bike.ftms.app.utils.TimeStringUtil;

/*
{
        "time": "00:01:40",
        "meters": "500M",
        "efm": "01:35",   /500M
        "cals": "44",
        "sm": "73",
        "calhr": "1686",
        "watts": "403"
    }
 */
public class RunDataItemBO {
    private String time;
    private String meters;
    private String cals;

    private String efm;     // /500M

    private String sm;
    private String calhr;
    private String watts;

    public RunDataItemBO() {

    }

    public RunDataItemBO(RowerDataBean2 bean2) {
        toSelf(bean2);
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

    public String getEfm() {
        return efm;
    }

    public void setEfm(String efm) {
        this.efm = efm;
    }

    public String getSm() {
        return sm;
    }

    public void setSm(String sm) {
        this.sm = sm;
    }

    public String getCalhr() {
        return calhr;
    }

    public void setCalhr(String calhr) {
        this.calhr = calhr;
    }

    public String getWatts() {
        return watts;
    }

    public void setWatts(String watts) {
        this.watts = watts;
    }

    @Override
    public String toString() {
        return "RunDataItemBO{" +
                "time='" + time + '\'' +
                ", meters='" + meters + '\'' +
                ", cals='" + cals + '\'' +
                ", efm='" + efm + '\'' +
                ", sm='" + sm + '\'' +
                ", calhr='" + calhr + '\'' +
                ", watts='" + watts + '\'' +
                '}';
    }

    private void toSelf(RowerDataBean2 bean2) {
        setTime(TimeStringUtil.getSToHourMinSecValue(bean2.getTime()));
        setMeters(bean2.getDistance() + "M");
        setCals(bean2.getCalorie() + "");

        setEfm(TimeStringUtil.getSToMinSecValue(bean2.getAve_five_hundred()));
        setSm(bean2.getSm() + "");
        setCalhr(bean2.getCalories_hr() + "");
        setWatts(bean2.getWatts() + "");
    }
}
