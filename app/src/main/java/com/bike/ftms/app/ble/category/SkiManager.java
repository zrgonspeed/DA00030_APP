package com.bike.ftms.app.ble.category;

import com.bike.ftms.app.ble.bean.rundata.raw.RowerDataBean1;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.common.RowerDataParam;
import com.bike.ftms.app.ble.BleManager;
import com.bike.ftms.app.ble.base.CategoryType;
import com.bike.ftms.app.utils.ConvertData;
import com.bike.ftms.app.utils.Logger;

import static com.bike.ftms.app.utils.DataTypeConversion.resolveData;

public class SkiManager extends CategoryType {
    private static SkiManager instance;

    private SkiManager() {
    }

    public static SkiManager getInstance() {
        if (instance == null) {
            synchronized (SkiManager.class) {
                if (instance == null) {
                    instance = new SkiManager();
                }
            }
        }
        return instance;
    }

    @Override
    public void setRunData(byte[] data, RowerDataBean1 rowerDataBean1) {
        setBleDataInxOfSki(new byte[]{data[0], data[1]});
        setRunData_2AD1(data, rowerDataBean1);
    }

    // 浆手数据
    private void setRunData_2AD1(byte[] data, RowerDataBean1 rowerDataBean1) {
        if (BleManager.getInstance().getOnRunDataListener() == null) {
            return;
        }

        if (BleManager.getInstance().getRunStatus() == BleManager.RUN_STATUS_STOP) {
            return;
        }

        rowerDataBean1.setStrokes(RowerDataParam.STROKE_COUNT_INX == -1 ? 0 : resolveData(data, RowerDataParam.STROKE_COUNT_INX, RowerDataParam.STROKE_COUNT_LEN));
        rowerDataBean1.setDistance(RowerDataParam.TOTAL_DISTANCE_INX == -1 ? 0 : resolveData(data, RowerDataParam.TOTAL_DISTANCE_INX, RowerDataParam.TOTAL_DISTANCE_LEN));
        rowerDataBean1.setSm(RowerDataParam.STROKE_RATE_INX == -1 ? 0 : resolveData(data, RowerDataParam.STROKE_RATE_INX, RowerDataParam.STROKE_RATE_LEN) / 2);
        rowerDataBean1.setFive_hundred(RowerDataParam.INSTANTANEOUS_PACE_INX == -1 ? 0 : resolveData(data, RowerDataParam.INSTANTANEOUS_PACE_INX, RowerDataParam.INSTANTANEOUS_PACE_LEN));
        rowerDataBean1.setCalorie(RowerDataParam.TOTAL_ENERGY_INX == -1 ? 0 : resolveData(data, RowerDataParam.TOTAL_ENERGY_INX, RowerDataParam.TOTAL_ENERGY_LEN));
        rowerDataBean1.setCalories_hr(RowerDataParam.ENERGY_PER_HOUR_INX == -1 ? 0 : resolveData(data, RowerDataParam.ENERGY_PER_HOUR_INX, RowerDataParam.ENERGY_PER_HOUR_LEN));
        if (!BleManager.isHrConnect) {
            rowerDataBean1.setHeart_rate(RowerDataParam.HEART_RATE_INX == -1 ? 0 : resolveData(data, RowerDataParam.HEART_RATE_INX, RowerDataParam.HEART_RATE_LEN));
        }
        rowerDataBean1.setWatts(RowerDataParam.INSTANTANEOUS_POWER_INX == -1 ? 0 : resolveData(data, RowerDataParam.INSTANTANEOUS_POWER_INX, RowerDataParam.INSTANTANEOUS_POWER_LEN));
        rowerDataBean1.setAve_watts(RowerDataParam.AVERAGE_POWER_INX == -1 ? 0 : resolveData(data, RowerDataParam.AVERAGE_POWER_INX, RowerDataParam.AVERAGE_POWER_LEN));
        rowerDataBean1.setAve_five_hundred(RowerDataParam.AVERAGE_PACE_INX == -1 ? 0 : resolveData(data, RowerDataParam.AVERAGE_PACE_INX, RowerDataParam.AVERAGE_PACE_LEN));

        if (!MyConstant.isCustomIntervalMode(rowerDataBean1.getRunMode())) {

            // 2020R没有多出的remain time位置，共用elapsed time位置

            // 时间下数
            if (rowerDataBean1.getRunMode() == MyConstant.GOAL_TIME) {
                if (rowerDataBean1.getDeviceType() == MyConstant.DEVICE_AA02020_00R_01 ||
                        rowerDataBean1.getDeviceType() == MyConstant.DEVICE_AA02230_00R_01
                ) {
                    rowerDataBean1.setTime(RowerDataParam.ELAPSED_TIME_INX == -1 ? 0 : resolveData(data, RowerDataParam.ELAPSED_TIME_INX, RowerDataParam.ELAPSED_TIME_LEN));
                } else {
                    rowerDataBean1.setTime(RowerDataParam.REMAINING_TIME_INX == -1 ? 0 : resolveData(data, RowerDataParam.REMAINING_TIME_INX, RowerDataParam.REMAINING_TIME_LEN));
                }
            }
            // 时间下数
            if (rowerDataBean1.getRunMode() == MyConstant.INTERVAL_TIME) {
                if (rowerDataBean1.getDeviceType() == MyConstant.DEVICE_AA02020_00R_01 ||
                        rowerDataBean1.getDeviceType() == MyConstant.DEVICE_AA02230_00R_01) {
                    rowerDataBean1.setTime(RowerDataParam.ELAPSED_TIME_INX == -1 ? 0 : resolveData(data, RowerDataParam.ELAPSED_TIME_INX, RowerDataParam.ELAPSED_TIME_LEN));
                } else {
                    rowerDataBean1.setTime(RowerDataParam.REMAINING_TIME_INX == -1 ? 0 : resolveData(data, RowerDataParam.REMAINING_TIME_INX, RowerDataParam.REMAINING_TIME_LEN));
                }
            }

            // 时间上数
            if (rowerDataBean1.getRunMode() == MyConstant.GOAL_DISTANCE ||
                    rowerDataBean1.getRunMode() == MyConstant.GOAL_CALORIES ||
                    rowerDataBean1.getRunMode() == MyConstant.INTERVAL_DISTANCE ||
                    rowerDataBean1.getRunMode() == MyConstant.INTERVAL_CALORIES ||
                    rowerDataBean1.getRunMode() == MyConstant.NORMAL
            ) {
                rowerDataBean1.setTime(RowerDataParam.ELAPSED_TIME_INX == -1 ? 0 : resolveData(data, RowerDataParam.ELAPSED_TIME_INX, RowerDataParam.ELAPSED_TIME_LEN));
            }
        } else {
            // 时间下数,使用ffe0中的4字节
            // if (rowerDataBean1.getRunMode() == MyConstant.CUSTOM_INTERVAL_TIME) {
            //     rowerDataBean1.setTime(RowerDataParam.REMAINING_TIME_INX == -1 ? 0 : resolveData(data, RowerDataParam.REMAINING_TIME_INX, RowerDataParam.REMAINING_TIME_LEN));
            // }
            // 时间上数
            if (rowerDataBean1.getRunMode() == MyConstant.CUSTOM_INTERVAL_DISTANCE ||
                    rowerDataBean1.getRunMode() == MyConstant.CUSTOM_INTERVAL_CALORIES
            ) {
                rowerDataBean1.setTime(RowerDataParam.ELAPSED_TIME_INX == -1 ? 0 : resolveData(data, RowerDataParam.ELAPSED_TIME_INX, RowerDataParam.ELAPSED_TIME_LEN));
            }
        }

        // 只精确到秒，毫秒域为 000
        rowerDataBean1.setDate(System.currentTimeMillis() / 1000 * 1000);
        if (BleManager.getInstance().getOnRunDataListener() != null) {
            BleManager.getInstance().getOnRunDataListener().onRunData(rowerDataBean1);
        }
    }

    private void setBleDataInxOfSki(byte[] data) {
        if (BleManager.getInstance().setBleDataInx) {
            return;
        }
        BleManager.getInstance().setBleDataInx = true;
        int inxLen = 2;
        String s = ConvertData.byteArrToBinStr(data);

        Logger.d("FTMS协议--ROWER---低位在前高位在后-------s == " + s);

        String[] strings = s.split(",");
        StringBuffer stringBuffer = new StringBuffer();
        for (String string : strings) {
            for (int i = string.length() - 1; i >= 0; i--) {
                stringBuffer.append(string.subSequence(i, i + 1));
            }
        }
        s = stringBuffer.toString();
        for (int i = 0; i < s.length(); i++) {
            // Logger.i(s.subSequence(i, i + 1) + "");
            if (i != 0 && !"1".equals(s.subSequence(i, i + 1))) {
                continue;
            }
            switch (i) {
                case 0:
                    if ("0".equals(s.subSequence(i, i + 1))) {
                        RowerDataParam.STROKE_RATE_INX = inxLen;
                        inxLen = inxLen + RowerDataParam.STROKE_RATE_LEN;
                        RowerDataParam.STROKE_COUNT_INX = inxLen;
                        inxLen = inxLen + RowerDataParam.STROKE_COUNT_LEN;
                        Logger.d("setBleDataInx  STROKE_RATE_INX=" + RowerDataParam.STROKE_RATE_INX);
                        Logger.d("setBleDataInx  STROKE_COUNT_INX=" + RowerDataParam.STROKE_COUNT_INX);
                    }
                    break;
                case 1:
                    RowerDataParam.AVERAGE_STROKE_RATE_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.AVERAGE_STROKE_RATE_LEN;
                    Logger.d("setBleDataInx  AVERAGE_STROKE_RATE_INX=" + RowerDataParam.AVERAGE_STROKE_RATE_INX);
                    break;
                case 2:
                    RowerDataParam.TOTAL_DISTANCE_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.TOTAL_DISTANCE_LEN;
                    Logger.d("setBleDataInx  TOTAL_DISTANCE_INX=" + RowerDataParam.TOTAL_DISTANCE_INX);
                    break;
                case 3:
                    RowerDataParam.INSTANTANEOUS_PACE_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.INSTANTANEOUS_PACE_LEN;
                    Logger.d("setBleDataInx  INSTANTANEOUS_PACE_INX=" + RowerDataParam.INSTANTANEOUS_PACE_INX);
                    break;
                case 4:
                    RowerDataParam.AVERAGE_PACE_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.AVERAGE_PACE_LEN;
                    Logger.d("setBleDataInx  AVERAGE_PACE_INX=" + RowerDataParam.AVERAGE_PACE_INX);
                    break;
                case 5:
                    RowerDataParam.INSTANTANEOUS_POWER_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.INSTANTANEOUS_POWER_LEN;
                    Logger.d("setBleDataInx  INSTANTANEOUS_POWER_INX=" + RowerDataParam.INSTANTANEOUS_POWER_INX);

                    break;
                case 6:
                    RowerDataParam.AVERAGE_POWER_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.AVERAGE_POWER_LEN;
                    Logger.d("setBleDataInx  AVERAGE_POWER_INX=" + RowerDataParam.AVERAGE_POWER_INX);
                    break;
                case 7:
                    RowerDataParam.RESISTANCE_LEVEL_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.RESISTANCE_LEVEL_LEN;
                    Logger.d("setBleDataInx  RESISTANCE_LEVEL_INX=" + RowerDataParam.RESISTANCE_LEVEL_INX);
                    break;
                case 8:
                    RowerDataParam.TOTAL_ENERGY_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.TOTAL_ENERGY_LEN;
                    RowerDataParam.ENERGY_PER_HOUR_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.ENERGY_PER_HOUR_LEN;
                    RowerDataParam.ENERGY_PER_MINUTE_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.ENERGY_PER_MINUTE_LEN;
                    Logger.d("setBleDataInx  TOTAL_ENERGY_INX=" + RowerDataParam.TOTAL_ENERGY_INX);
                    Logger.d("setBleDataInx  ENERGY_PER_HOUR_INX=" + RowerDataParam.ENERGY_PER_HOUR_INX);
                    Logger.d("setBleDataInx  ENERGY_PER_MINUTE_INX=" + RowerDataParam.ENERGY_PER_MINUTE_INX);
                    break;
                case 9:
                    RowerDataParam.HEART_RATE_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.HEART_RATE_LEN;
                    Logger.d("setBleDataInx  HEART_RATE_INX=" + RowerDataParam.HEART_RATE_INX);
                    break;
                case 10:
                    RowerDataParam.METABOLIC_EQUIVALENT_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.METABOLIC_EQUIVALENT_LEN;
                    Logger.d("setBleDataInx  METABOLIC_EQUIVALENT_INX=" + RowerDataParam.METABOLIC_EQUIVALENT_INX);
                    break;
                case 11:
                    RowerDataParam.ELAPSED_TIME_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.ELAPSED_TIME_LEN;
                    Logger.d("setBleDataInx  ELAPSED_TIME_INX=" + RowerDataParam.ELAPSED_TIME_INX);
                    break;
                case 12:
                    RowerDataParam.REMAINING_TIME_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.REMAINING_TIME_LEN;
                    Logger.d("setBleDataInx  REMAINING_TIME_INX=" + RowerDataParam.REMAINING_TIME_INX);
                    break;
            }

        }
    }

}
