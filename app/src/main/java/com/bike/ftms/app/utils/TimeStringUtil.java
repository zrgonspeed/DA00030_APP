package com.bike.ftms.app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间转换为字符串
 */
public class TimeStringUtil {

    private TimeStringUtil() {

    }

    /**
     * 将毫秒转换为00:00(分钟:秒);
     * 分钟数会突破99
     *
     * @param value 毫秒
     * @return
     */
    public static String getMsToMinSecValue(float value) {
        long time = Math.round(value / 1000.0);
        long minute = time / 60;
        long hour = minute / 60;
        long second = time % 60;
        minute %= 60;
        minute += hour * 60;
        return String.format("%02d:%02d", minute, second);
    }

    /**
     * 将秒转换为00:00(分钟:秒);[文件：Spotify.apk]
     * 分钟数会突破99
     *
     * @param value 毫秒
     * @return
     */
    public static String getSToMinSecValue(float value) {
        long time = (long) value;
        long minute = time / 60;
        long hour = minute / 60;
        long second = time % 60;
        minute %= 60;
        minute += hour * 60;
        return String.format("%02d:%02d", minute, second);
    }

    /**
     * 将秒转换为00:00(分钟:秒);
     * 分钟数会突破99
     *
     * @param value 毫秒
     * @return
     */
    public static String getSToHourMinSecValue(float value) {
        long time = (long) value;
        long minute = time / 60;
        long hour = minute / 60;
        long second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    /**
     * 将毫秒转换为00:00(分钟:秒);
     * 分钟数会突破99
     *
     * @param value 毫秒
     * @return
     */
    public static String getMsToMinSecValueOnSummary(float value) {
        long time = Math.round(value / 1000.0);
        long minute = time / 60;
        long hour = minute / 60;
        long second = time % 60;
        minute %= 60;
        minute += hour * 60;
        if (minute >= 99999) {
            minute = 99999;
        }
        return String.format("%02d:%02d", minute, second);
    }

    /**
     * 将毫秒转换为00:00(分钟:秒);
     * 分钟数超过100会重置
     *
     * @param value 毫秒
     * @return
     */
    public static String getMsToMinSecValueHasUp(float value) {
        long time = Math.round(value / 1000.0);
        time = time % (1000 * 60);
        long minute = time / 60;
        long hour = minute / 60;
        long second = time % 60;
        minute %= 60;
        minute += hour * 60;
        return String.format("%02d:%02d", minute, second);
    }

    /**
     * 将毫秒转换为00:00(小时:分钟);
     * 小时数会突破99
     *
     * @param value 毫秒
     * @return
     */
    public static String getMsToHourMinValue(float value) {
        long time = Math.round(value / 1000.0);
        long minute = time / 60;
        long hour = minute / 60;
        long second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", hour, minute);
    }

    /**
     * 毫秒转换为时:分:秒;
     * 小时数会突破99
     *
     * @param value
     * @return
     */
    public static String getMsToTimeValueOnRunning(float value) {
        long time = Math.round(value / 1000.0);
        long minute = time / 60;
        long hour = minute / 60;
        long second = time % 60;
        minute %= 60;
        return String.format("%01d:%02d:%02d", hour, minute, second);
    }

    /**
     * 毫秒转换为时:分:秒;
     * 小时数会突破99
     *
     * @param value
     * @return
     */
    public static String getMsToTimeValue(float value) {
        long time = Math.round(value / 1000.0);
        long minute = time / 60;
        long hour = minute / 60;
        long second = time % 60;
        minute %= 60;
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            return "00:" + String.format("%02d:%02d", minute, second);
        }
    }

    public static String getHourToTimeValue(float value) {
        long time = Math.round(value * 60 * 60);
        long minute = time / 60;
        long hour = minute / 60;
        long second = time % 60;
        minute %= 60;
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format("%02d:%02d", minute, second);
        }
    }

    /**
     * 秒数转换为 hh HR:mm MIN(带单位)
     *
     * @param value
     * @return
     */
    public static String getSecToRemainHourMin(long value) {
        long time = Math.round(value);
        long minute = time / 60;
        long hour = minute / 60;
//        long second = time % 60;
        minute %= 60;
        return String.format("%d HR:%02d MIN", hour, minute);
//        if (hour > 0) {
//            return String.format("%d HR:%02d MIN", hour, minute);
//
//        } else {
//            return String.format("%d MIN:%02d SEC", minute, second);
//        }
    }

    /**
     * 秒转小时分钟 （xx hr xx min）
     *
     * @param sec
     * @return
     */
    public static String getSecToHrMin(long sec) {
        long hr = sec / 3600;
        long min = (sec - hr * 3600) / 60;
        return hr + " hr " + min + " min";
    }

    /**
     * 秒数转换为 hh HR :mm MIN :ss SEC
     *
     * @param value
     * @return
     */
    public static String getSecToRemainTime(long value) {
        long time = Math.round(value);
        long minute = time / 60;
        long hour = minute / 60;
        long second = time % 60;
        minute %= 60;
        if (hour > 0) {
            return String.format("%d HR:%02d MIN:%d SEC", hour, minute, second);

        } else {
            return String.format("%d MIN:%02d SEC", minute, second);
        }
    }

    public static String getSecToHour(long value) {
        long time = Math.round(value);
        long minute = time / 60;
        long hour = minute / 60;
        long second = time % 60;
        minute %= 60;
        return hour + "";
    }

    /**
     * 将秒转为小时分钟或者分钟秒
     *
     * @param value   秒
     * @param format1 时间格式(小时  分钟)
     * @param format2 时间格式（分钟  秒）
     * @return
     */
    public static String getsecToHrMinOrMinSec(long value, String format1, String format2) {
        long time = Math.round(value);
        long hour = time / 60 / 60;
        String timeValue;
        if (hour > 0) {
            timeValue = String.format(format1, hour, (value - hour * 60 * 60) / 60);//"%02d:%02d"
        } else {
            timeValue = String.format(format2, value / 60, value % 60);//"%02d:%02d"
        }
        return timeValue;
    }

    /**
     * @param time    1541569323155
     * @param pattern yyyy-MM-dd HH:mm:ss
     * @return 2018-11-07 13:42:03
     */
    public static String getDate2String(long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        return format.format(date);
    }


    /**
     * 将字符串时间转为Long时间
     *
     * @param time yyyy-MM-dd HH:mm:ss
     */
    public static Long getLongTime(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(time);
            return date.getTime();
        } catch (Exception e) {
        }
        return 0L;
    }

    /**
     * 将字符串时间转为Long时间，单位 秒
     * 10:01:35  ->  36095
     */
    public static Long getLongTimeHHMMSS(String time) {
        String[] split = time.split(":");
        long longtime = Integer.parseInt(split[0]) * 3600 + Integer.parseInt(split[1]) * 60 + Integer.parseInt(split[2]);
        return longtime;
    }

    /**
     * 将字符串时间转为Long时间，单位 秒
     * 01:35  ->  95
     */
    public static Long getLongTimeMMSS(String time) {
        String[] split = time.split(":");
        long longtime = Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[1]);
        return longtime;
    }
}