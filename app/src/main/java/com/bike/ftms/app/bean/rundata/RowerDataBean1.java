package com.bike.ftms.app.bean.rundata;


import com.bike.ftms.app.bean.rundata.RowerDataBean2;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.utils.TimeStringUtil;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/2
 */
public class RowerDataBean1 extends LitePalSupport {
    private int id;
    private int strokes;
    private int drag;
    private int interval;
    private int sm;
    private long five_hundred;
    private int heart_rate;
    private long ave_five_hundred;
    private int watts;
    private int ave_watts;
    private int calories_hr;
    private String note;
    private long date;

    private long time;
    private long distance;
    private long calorie;
    private long setIntervalTime = 0;//间歇模式设定时间
    private long setIntervalDistance = 0;//间歇模式设定距离
    private long setIntervalCalorie = 0;//间歇模式设定卡路里
    private long setGoalTime = 0;//目标模式设定时间
    private long setGoalDistance = 0;//目标模式设定距离
    private long setGoalCalorie = 0;//目标模式设定卡路里

    @MyConstant.RunMode
    private int runMode = MyConstant.NORMAL;
    private int reset_time;
    @MyConstant.RunStatus
    private int runStatus = MyConstant.RUN_STATUS_NO;
    @MyConstant.IntervalStatus
    private int intervalStatus = MyConstant.INTERVAL_STATUS_REST;
    private int runInterval = 0;        // 各个模式的分段次数  0-255
    private List<RowerDataBean2> list = new ArrayList<>();

    public RowerDataBean1() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStrokes() {
        return strokes;
    }

    public void setStrokes(int strokes) {
        this.strokes = strokes;
    }

    public int getDrag() {
        return drag;
    }

    public void setDrag(int drag) {
        this.drag = drag;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public int getSm() {
        return sm;
    }

    public void setSm(int sm) {
        this.sm = sm;
    }

    public long getFive_hundred() {
        return five_hundred;
    }

    public void setFive_hundred(long five_hundred) {
        this.five_hundred = five_hundred;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getHeart_rate() {
        return heart_rate;
    }

    public void setHeart_rate(int heart_rate) {
        this.heart_rate = heart_rate;
    }

    public long getAve_five_hundred() {
        return ave_five_hundred;
    }

    public void setAve_five_hundred(long ave_five_hundred) {
        this.ave_five_hundred = ave_five_hundred;
    }

    public int getWatts() {
        return watts;
    }

    public void setWatts(int watts) {
        this.watts = watts;
    }

    public int getAve_watts() {
        return ave_watts;
    }

    public void setAve_watts(int ave_watts) {
        this.ave_watts = ave_watts;
    }

    public long getCalorie() {
        return calorie;
    }

    public void setCalorie(long calorie) {
        this.calorie = calorie;
    }

    public int getCalories_hr() {
        return calories_hr;
    }

    public void setCalories_hr(int calories_hr) {
        this.calories_hr = calories_hr;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getSetIntervalTime() {
        return setIntervalTime;
    }

    public void setSetIntervalTime(long setIntervalTime) {
        this.setIntervalTime = setIntervalTime;
    }

    public long getSetIntervalDistance() {
        return setIntervalDistance;
    }

    public void setSetIntervalDistance(long setIntervalDistance) {
        this.setIntervalDistance = setIntervalDistance;
    }

    public long getSetIntervalCalorie() {
        return setIntervalCalorie;
    }

    public void setSetIntervalCalorie(long setIntervalCalorie) {
        this.setIntervalCalorie = setIntervalCalorie;
    }

    public int getRunMode() {
        return runMode;
    }

    public void setRunMode(@MyConstant.RunMode int runMode) {
        this.runMode = runMode;
    }

    public int getReset_time() {
        return reset_time;
    }

    public void setReset_time(int reset_time) {
        this.reset_time = reset_time;
    }

    public long getSetGoalTime() {
        return setGoalTime;
    }

    public void setSetGoalTime(long setGoalTime) {
        this.setGoalTime = setGoalTime;
    }

    public long getSetGoalDistance() {
        return setGoalDistance;
    }

    public void setSetGoalDistance(long setGoalDistance) {
        this.setGoalDistance = setGoalDistance;
    }

    public long getSetGoalCalorie() {
        return setGoalCalorie;
    }

    public void setSetGoalCalorie(long setGoalCalorie) {
        this.setGoalCalorie = setGoalCalorie;
    }

    public List<RowerDataBean2> getList() {
        return list;
    }

    public void setList(List<RowerDataBean2> list) {
        this.list = list;
    }

    public int getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(int runStatus) {
        this.runStatus = runStatus;
    }

    public int getIntervalStatus() {
        return intervalStatus;
    }

    public void setIntervalStatus(int intervalStatus) {
        this.intervalStatus = intervalStatus;
    }

    public int getRunInterval() {
        return runInterval;
    }

    public void setRunInterval(int runInterval) {
        this.runInterval = runInterval;
    }

    @Override
    public String toString() {
        return "RowerDataBean1{" +
                "id=" + id +
                ", runMode=" + runMode +
                ", runStatus=" + runStatus +

                ", time=" + time +
                ", distance=" + distance +
                ", calorie=" + calorie +

                ", interval=" + interval +
                ", setIntervalTime=" + setIntervalTime +
                ", setIntervalDistance=" + setIntervalDistance +
                ", setIntervalCalorie=" + setIntervalCalorie +
                ", intervalStatus=" + intervalStatus +
                ", reset_time=" + reset_time +

                ", runInterval=" + runInterval +
                ", setGoalTime=" + setGoalTime +
                ", setGoalDistance=" + setGoalDistance +
                ", setGoalCalorie=" + setGoalCalorie +

                ", strokes=" + strokes +
                ", drag=" + drag +
                ", sm=" + sm +
                ", five_hundred=" + five_hundred +
                ", heart_rate=" + heart_rate +
                ", ave_five_hundred=" + ave_five_hundred +
                ", watts=" + watts +
                ", ave_watts=" + ave_watts +
                ", calories_hr=" + calories_hr +
                ", note='" + note + '\'' +
                ", date=" + date +
                ", list.size =" + list.size() +
                '}';
    }

    @Column(ignore = true)
    private int flag = 1;

    public void setFlag(int newFlag) {
        if (newFlag == 2) {
            this.flag = 2;
            return;
        }
        if (this.flag == 3) {
            return;
        }
        this.flag = newFlag;
    }

    public boolean getCanSave() {
        return this.flag == 3;
    }

    // 特有
    private String result;
    private String type;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setTypeAndResult() {
        switch (getRunMode()) {
            case MyConstant.NORMAL:
                setType(getDistance() + "M");
                setResult(getDistance() + "M");
                break;
            case MyConstant.GOAL_TIME:
                setType(TimeStringUtil.getSToMinSecValue(getSetGoalTime()));
                setResult(getDistance() + "M");
                break;
            case MyConstant.GOAL_CALORIES:
                setType(getSetGoalCalorie() + "C");
                setResult(getDistance() + "M");
                break;
            case MyConstant.GOAL_DISTANCE:
                setType(getSetGoalDistance() + "M");
                setResult(TimeStringUtil.getSToMinSecValue(getTime()));
                break;
            case MyConstant.INTERVAL_TIME: {
                setType((getInterval() + "x:" + getSetIntervalTime() + "/:" + getReset_time() + "R"));
                // 总距离

                List<RowerDataBean2> list = getList();
                if (list.size() > 1) {
                    long totalMeter = 0;
                    for (RowerDataBean2 bean2 : list) {
                        totalMeter += bean2.getDistance();
                    }
                    setResult(totalMeter + "M");
                } else {
                    setResult(getDistance() + "M");
                }
            }
            break;
            case MyConstant.INTERVAL_CALORIES: {
                setType((getInterval() + "x" + getSetIntervalCalorie() + "C" + "/:" + getReset_time() + "R"));
                // 总距离
                List<RowerDataBean2> list = getList();
                if (list.size() > 1) {
                    long totalMeter = 0;
                    for (RowerDataBean2 bean2 : list) {
                        totalMeter += bean2.getDistance();
                    }
                    setResult(totalMeter + "M");
                } else {
                    setResult(getDistance() + "M");
                }
            }
            break;
            case MyConstant.INTERVAL_DISTANCE: {
                setType((getInterval() + "x" + getSetIntervalDistance() + "M" + "/:" + getReset_time() + "R"));

                // 总时间
                List<RowerDataBean2> list = getList();
                if (list.size() > 1) {
                    long totalTime = 0;
                    for (RowerDataBean2 bean2 : list) {
                        totalTime += bean2.getTime();
                    }
                    setResult(TimeStringUtil.getSToMinSecValue(totalTime));
                } else {
                    setResult(TimeStringUtil.getSToMinSecValue(getTime()));
                }
            }
            break;
            default:
                break;
        }
    }

    private RowerDataBean2 totalsItem;

    public RowerDataBean2 getTotalsItem() {
        return totalsItem;
    }

    private void setTotalsItem(RowerDataBean2 bean2) {
        this.totalsItem = bean2;
    }

    public void setTotalsItem() {
        RowerDataBean2 bb = new RowerDataBean2();
        {
            ArrayList<RowerDataBean2> list = new ArrayList<>();
            for (RowerDataBean2 bean2 : getList()) {
                list.add(bean2.copy());
            }

            if (list.size() == 0) {
                RowerDataBean2 rowerDataBean2 = new RowerDataBean2(this);
                list.add(rowerDataBean2);

                setTotalsItem(rowerDataBean2);
                return;
            }

            switch (getRunMode()) {
                case MyConstant.GOAL_TIME: {
                    bb = new RowerDataBean2();
                    bb.setRunMode(getRunMode());
                    bb.setCalories_hr(getCalories_hr());
                    bb.setWatts(getWatts());

                    long initDistance = 0;
                    long initTime = getSetGoalTime();
                    long initCal = 0;
                    for (RowerDataBean2 bean2 : list) {
                        // 平均
                        bb.setAve_five_hundred(bean2.getAve_five_hundred() + bb.getAve_five_hundred());
                        bb.setSm(bean2.getSm() + bb.getSm());

                        // 每段的运动时间
                        bean2.setTime(initTime - bean2.getTime());
                        bb.setTime(bb.getTime() + bean2.getTime());
                        initTime = initTime - bean2.getTime();

                        // 每段的卡路里
                        bean2.setCalorie(bean2.getCalorie() - initCal);
                        bb.setCalorie(bb.getCalorie() + bean2.getCalorie());
                        initCal = initCal + bean2.getCalorie();

                        // 每段的运动距离 倒数
                        bean2.setDistance(bean2.getDistance() - initDistance);
                        bb.setDistance(bb.getDistance() + bean2.getDistance());
                        initDistance = initDistance + bean2.getDistance();
                    }
                    bb.setAve_five_hundred(bb.getAve_five_hundred() / list.size());
                    bb.setSm(bb.getSm() / list.size());

                    bb.setInterval(-1);
                    list.add(0, bb);
                }
                break;
                case MyConstant.GOAL_DISTANCE: {
                    bb = new RowerDataBean2();
                    bb.setRunMode(getRunMode());
                    bb.setCalories_hr(getCalories_hr());
                    bb.setWatts(getWatts());

                    long initDistance = getSetGoalDistance();
                    long initTime = 0;
                    long initCal = 0;
                    for (RowerDataBean2 bean2 : list) {
                        // 平均
                        bb.setAve_five_hundred(bean2.getAve_five_hundred() + bb.getAve_five_hundred());
                        bb.setSm(bean2.getSm() + bb.getSm());

                        // 每段的运动距离 倒数
                        bean2.setDistance(initDistance - bean2.getDistance());
                        bb.setDistance(bb.getDistance() + bean2.getDistance());
                        initDistance = initDistance - bean2.getDistance();

                        // 每段的运动时间
                        bean2.setTime(bean2.getTime() - initTime);
                        bb.setTime(bb.getTime() + bean2.getTime());
                        initTime = initTime + bean2.getTime();

                        // 每段的卡路里
                        bean2.setCalorie(bean2.getCalorie() - initCal);
                        bb.setCalorie(bb.getCalorie() + bean2.getCalorie());
                        initCal = initCal + bean2.getCalorie();
                    }
                    bb.setAve_five_hundred(bb.getAve_five_hundred() / list.size());
                    bb.setSm(bb.getSm() / list.size());

                    bb.setInterval(-1);
                    list.add(0, bb);
                }
                break;
                case MyConstant.GOAL_CALORIES: {
                    bb = new RowerDataBean2();
                    bb.setRunMode(getRunMode());
                    bb.setCalories_hr(getCalories_hr());
                    bb.setWatts(getWatts());

                    long initDistance = 0;
                    long initTime = 0;
                    long initCal = getSetGoalCalorie();
                    for (RowerDataBean2 bean2 : list) {
                        // 平均
                        bb.setAve_five_hundred(bean2.getAve_five_hundred() + bb.getAve_five_hundred());
                        bb.setSm(bean2.getSm() + bb.getSm());

                        // 每段的卡路里
                        bean2.setCalorie(initCal - bean2.getCalorie());
                        bb.setCalorie(bb.getCalorie() + bean2.getCalorie());
                        initCal = initCal - bean2.getCalorie();

                        // 每段的运动时间
                        bean2.setTime(bean2.getTime() - initTime);
                        bb.setTime(bb.getTime() + bean2.getTime());
                        initTime = initTime + bean2.getTime();

                        // 每段的运动距离 倒数
                        bean2.setDistance(bean2.getDistance() - initDistance);
                        bb.setDistance(bb.getDistance() + bean2.getDistance());
                        initDistance = initDistance + bean2.getDistance();
                    }
                    bb.setAve_five_hundred(bb.getAve_five_hundred() / list.size());
                    bb.setSm(bb.getSm() / list.size());

                    bb.setInterval(-1);
                    list.add(0, bb);
                }
                break;
                case MyConstant.INTERVAL_TIME: {
                    bb = new RowerDataBean2();
                    bb.setRunMode(getRunMode());
                    bb.setCalories_hr(getCalories_hr());
                    bb.setWatts(getWatts() + bb.getWatts());

                    for (RowerDataBean2 bean2 : list) {
                        // 平均
                        bb.setAve_five_hundred(bean2.getAve_five_hundred() + bb.getAve_five_hundred());
                        bb.setSm(bean2.getSm() + bb.getSm());

                        // 总和
                        if (list.indexOf(bean2) == list.size() - 1) {
                            if (bean2.getSetIntervalTime() == bean2.getTime()) {
                                bb.setSetIntervalTime(bean2.getSetIntervalTime() + bb.getSetIntervalTime());
                            } else {
                                bb.setSetIntervalTime((bean2.getSetIntervalTime() - bean2.getTime()) + bb.getSetIntervalTime());
                            }
                        } else {
                            bb.setSetIntervalTime(bean2.getSetIntervalTime() + bb.getSetIntervalTime());
                        }
                        bb.setCalorie(bean2.getCalorie() + bb.getCalorie());
                        bb.setDistance(bean2.getDistance() + bb.getDistance());

                    }
                    bb.setAve_five_hundred(bb.getAve_five_hundred() / list.size());
                    bb.setSm(bb.getSm() / list.size());

                    bb.setInterval(-1);
                    list.add(0, bb);
                }
                break;
                case MyConstant.INTERVAL_DISTANCE: {
                    bb = new RowerDataBean2();
                    bb.setRunMode(getRunMode());
                    bb.setCalories_hr(getCalories_hr());
                    bb.setWatts(getWatts() + bb.getWatts());

                    for (RowerDataBean2 bean2 : list) {
                        // 平均
                        bb.setAve_five_hundred(bean2.getAve_five_hundred() + bb.getAve_five_hundred());
                        bb.setSm(bean2.getSm() + bb.getSm());

                        // 总和
                        if (list.indexOf(bean2) == list.size() - 1) {
/*                    if (bean2.getSetIntervalDistance() == bean2.getDistance()) {
//                        bb.setSetIntervalDistance(bean2.getSetIntervalDistance() + bb.getSetIntervalDistance());
                        bb.setDistance(bean2.getDistance() + bb.getDistance());
                    } else {
//                        bb.setSetIntervalDistance((bean2.getSetIntervalDistance() - bean2.getDistance()) + bb.getSetIntervalDistance());
                        bb.setDistance((bean2.getDistance() - bean2.getDistance()) + bb.getDistance());
                    }*/

                            bb.setDistance(bean2.getDistance() + bb.getDistance());

                        } else {
//                    bb.setSetIntervalDistance(bean2.getSetIntervalDistance() + bb.getSetIntervalDistance());
                            bb.setDistance(bean2.getDistance() + bb.getDistance());
                        }
                        bb.setTime(bean2.getTime() + bb.getTime());
                        bb.setCalorie(bean2.getCalorie() + bb.getCalorie());

                    }
                    bb.setAve_five_hundred(bb.getAve_five_hundred() / list.size());
                    bb.setSm(bb.getSm() / list.size());

                    bb.setInterval(-1);
                    list.add(0, bb);
                }
                break;
                case MyConstant.INTERVAL_CALORIES: {
                    bb = new RowerDataBean2();
                    bb.setRunMode(getRunMode());
                    bb.setCalories_hr(getCalories_hr());
                    bb.setWatts(getWatts() + bb.getWatts());

                    for (RowerDataBean2 bean2 : list) {
                        // 平均
                        bb.setAve_five_hundred(bean2.getAve_five_hundred() + bb.getAve_five_hundred());
                        bb.setSm(bean2.getSm() + bb.getSm());

                        // 总和
                        if (list.indexOf(bean2) == list.size() - 1) {
//                    if (bean2.getSetIntervalCalorie() == bean2.getCalorie()) {
//                        bb.setSetIntervalCalorie(bean2.getSetIntervalCalorie() + bb.getSetIntervalCalorie());
//                    } else {
//                        bb.setSetIntervalCalorie((bean2.getSetIntervalCalorie() - bean2.getCalorie()) + bb.getSetIntervalCalorie());
//                    }
                            bb.setCalorie(bean2.getCalorie() + bb.getCalorie());

                        } else {
//                    bb.setSetIntervalCalorie(bean2.getSetIntervalCalorie() + bb.getSetIntervalCalorie());
                            bb.setCalorie(bean2.getCalorie() + bb.getCalorie());
                        }
                        bb.setTime(bean2.getTime() + bb.getTime());
                        bb.setDistance(bean2.getDistance() + bb.getDistance());

                    }
                    bb.setAve_five_hundred(bb.getAve_five_hundred() / list.size());
                    bb.setSm(bb.getSm() / list.size());

                    bb.setInterval(-1);
                    list.add(0, bb);
                }
                break;
                default:
                    // idle模式?
                    break;
            }
        }
        setTotalsItem(bb);
    }

}
