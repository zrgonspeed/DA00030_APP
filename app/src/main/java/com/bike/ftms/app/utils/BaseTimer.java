package com.bike.ftms.app.utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ChuHui on 2018/3/22.
 */

public class BaseTimer {
    private TimerCallBack timerCallBack = null;
    private Timer mTimer;
    private TimerTask mTimerTask;

    /**
     * 回调接口定义
     */
    public interface TimerCallBack {
        void callBack();
    }

    /**
     * 回调接口定义
     */
    public interface TimerCheckBleStateCallBack {
        void checkBack();
    }

    class LoopTask extends TimerTask {
        @Override
        public void run() {
            handleTimerOutEvent();
        }
    }

    private void handleTimerOutEvent() {
        if (timerCallBack != null) {
            timerCallBack.callBack();
        }
    }

    public void startTimer(long delay, long period, TimerCallBack cb) {
        if (mTimer == null && mTimerTask == null) {
            mTimer = new Timer(true);
            mTimerTask = new LoopTask();
            timerCallBack = cb;
            mTimer.schedule(mTimerTask, delay, period);
        }
    }

    public void startTimer(long delay, TimerCallBack cb) {
        if (mTimer == null && mTimerTask == null) {
            mTimer = new Timer(true);
            mTimerTask = new LoopTask();
            timerCallBack = cb;
            mTimer.schedule(mTimerTask, delay);
        }
    }

    public void closeTimer() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}
