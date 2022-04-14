package com.bike.ftms.app.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;

import com.bike.ftms.app.Debug;
import com.bike.ftms.app.ble.base.OnScanConnectListener;
import com.bike.ftms.app.ble.bean.MyScanResult;
import com.bike.ftms.app.utils.CustomTimer;
import com.bike.ftms.app.utils.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dev.xesam.android.toolbox.timer.CountDownTimer;

@SuppressLint({"MissingPermission", "WrongConstant"})
public abstract class BaseBleManager implements CustomTimer.TimerCallBack {

    protected BluetoothAdapter mBluetoothAdapter; //系统蓝牙适配器

    /**
     * @return 系统蓝牙适配器
     */
    public BluetoothAdapter getBluetoothAdapter() {
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        Logger.e("mBluetoothAdapter == null ? " + (mBluetoothAdapter == null));
        return mBluetoothAdapter;
    }

    protected OnScanConnectListener onScanConnectListener;      //扫描回调
    protected List<MyScanResult> mScanResults = new ArrayList<>();      //扫描到的蓝牙设备
    protected List<BluetoothGattService> mBluetoothGattServices;//服务，Characteristic(特征) 的集合。

    protected final Handler mHandler = new Handler(Looper.getMainLooper());

    // 电子表
    protected BluetoothGatt mBluetoothGatt;       //连接蓝牙、及操作
    protected MyScanResult connectScanResult;       // 已经连接的扫描结果

    public MyScanResult getConnectScanResult() {
        return connectScanResult;
    }

    protected CustomTimer isConnectTimer;

    protected boolean isConnect;  //是否连接
    protected boolean isCanning;  //是否正在扫描
    protected boolean isOpen;     //是否打开定位及蓝牙


    private static final long SCAN_MAX_COUNT = 20;     //扫描的设备个数限制（停止扫描）
    private static long SCAN_PERIOD = 60 * 1000;     //扫描设备时间限制
    private final long SCAN_PERIOD_INTERVAL = 1000;     //隔多久回调1次

    private static final long START_SCAN_DELAY_TIME = 3000; // 扫描设备延迟时间

    /**
     * 搜索设备时间计时
     */
    protected CountDownTimer countDownTimer;

    protected CountDownTime countDownTime;

    /**
     * 蓝牙打开与关闭回调 -> BluetoothActivity
     */
    protected BleOpenCallBack bleOpenCallBack;
    protected BleClosedCallBack bleClosedCallBack;


    public interface BleOpenCallBack {
        void isOpen(boolean open);
    }

    public interface BleClosedCallBack {
        void isClosed(boolean disable);
    }

    public interface CountDownTime {
        void onTick(long time);

        void onFinish();

        void onStart();
    }

    public void setBleOpenCallBack(BleOpenCallBack bleOpenCallBack) {
        this.bleOpenCallBack = bleOpenCallBack;
    }

    public void setBleClosedCallBack(BleClosedCallBack bleClosedCallBack) {
        this.bleClosedCallBack = bleClosedCallBack;
    }

    public void setCountDownTime(CountDownTime countDownTime) {
        this.countDownTime = countDownTime;
    }

    public boolean getIsOpen() {
        return isOpen;
    }

    /**
     * 扫描回调 -> BluetoothActivity
     */
    public void setOnScanConnectListener(OnScanConnectListener onScanConnectListener) {
        this.onScanConnectListener = onScanConnectListener;
    }

    /**
     * @return 扫描到的设备
     */
    public List<MyScanResult> getScanResults() {
        if (mScanResults == null) {
            mScanResults = new ArrayList<>();
        }
        return mScanResults;
    }

    /**
     * 扫描蓝牙设备
     */
    public void scanDevice() {
        Logger.i("scanDevice()");

        // Logger.e("mBluetoothAdapter == " + mBluetoothAdapter + "    isCanning == " + isCanning);
        if (mBluetoothAdapter != null && !isCanning) {
            boolean enabled = mBluetoothAdapter.isEnabled();
            Logger.i("scanDevice() enabled == " + enabled);
            if (!enabled) {
                boolean enable = mBluetoothAdapter.enable();
                Logger.e("scanDevice() adapter.enable() enable == " + enable);
                return;
            }

            startSearchCountDownTimer();

            isCanning = true;
            mScanResults.clear();
            if (connectScanResult != null && connectScanResult.getConnectState() == 1) {
                mScanResults.add(connectScanResult);
            }

            if (onScanConnectListener != null) {
                onScanConnectListener.onScanSuccess();
            }

            //创建ScanSettings的build对象用于设置参数
            ScanSettings.Builder builder = new ScanSettings.Builder()
                    //设置高功耗模式(低延时)
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
            //android 6.0添加设置回调类型、匹配模式等
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                //定义回调类型
                builder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
                //设置蓝牙LE扫描滤波器硬件匹配的匹配模式
                builder.setMatchMode(ScanSettings.MATCH_MODE_STICKY);
            }
            // 若设备支持批处理扫描，可以选择使用批处理，但此时扫描结果仅触发onBatchScanResults()
            if (mBluetoothAdapter.isOffloadedScanBatchingSupported()) {
                //设置蓝牙LE扫描的报告延迟的时间（以毫秒为单位）
                //设置为0以立即通知结果
                builder.setReportDelay(0L);
            }

            ScanSettings scanSettings = builder.build();
            List<ScanFilter> scanFilters = new ArrayList<>();
            if (!Debug.canScanAllDevice) {
                ScanFilter scanFilter = new ScanFilter.Builder().setServiceUuid(new ParcelUuid(UUID.fromString(getUuid()))).build();
                scanFilters.add(scanFilter);
            }
            mBluetoothAdapter.getBluetoothLeScanner().startScan(scanFilters, scanSettings, mScanCallback);

        }
    }

    /**
     * 扫描时间刷新计时
     */
    private void startSearchCountDownTimer() {
        if (countDownTimer == null) {
            // + 200 毫秒是应该的
            countDownTimer = new CountDownTimer(SCAN_PERIOD + 200, SCAN_PERIOD_INTERVAL) {
                @Override
                protected void onStart(long millisUntilFinished) {
                    Logger.i("开始扫描");
                    if (countDownTime != null) {
                        countDownTime.onStart();
                    }
                }

                @Override
                protected void onTick(long millisUntilFinished) {
                    long seconds = Math.round((double) millisUntilFinished / 1000);
                    if (countDownTime != null) {
                        countDownTime.onTick(seconds);
                    }
                }

                @Override
                public void onFinish() {
                    Logger.e("扫描时间到了，停止扫描");
                    stopScan();
                    if (mScanResults.size() == 0) {//没有搜索到设备
                        Logger.e("No devices were found");
                    }
                }

                @Override
                protected void onCancel(long millisUntilFinished) {
                    Logger.e("扫描取消");
                }
            };

        }

        countDownTimer.cancel();
        countDownTimer.start();
    }

    /**
     * 扫描时间刷新结束
     */
    private void stopSearchCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        if (mBluetoothAdapter != null && isCanning) {
            isCanning = false;
            //mHandler.removeCallbacksAndMessages(null);
            if (mBluetoothAdapter.getBluetoothLeScanner() != null && mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
            }

            if (onScanConnectListener != null) {
                Logger.e("thread == " + Thread.currentThread().toString());
                mHandler.post(() -> {
                    onScanConnectListener.onStopScan();
                });
            }

            stopSearchCountDownTimer();

            Logger.e("停止扫描设备");
        }
    }

    /**
     * 扫描结果回调
     */
    private final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            //Logger.i("onScanResult:" + result.getDevice().getName());
           /* Logger.i("onScanResult" + result.getDevice().toString());          //D5:EA:80:77:23:39
            Logger.i("onScanResult" + result.getDevice().getUuids());            //null
            Logger.i("onScanResult" + result.getDevice().getType());             //2
            Logger.i("onScanResult" + result.getDevice().getName());             //PZ-PA051BA
            Logger.i("onScanResult" + result.getDevice().getAddress());          //D5:EA:80:77:23:39
            Logger.i("onScanResult" + result.getDevice().getBluetoothClass());   //1f00
            Logger.i("onScanResult" + result.getDevice().getAlias());            //null
            Logger.i("onScanResult" + result.getDevice().getBondState());        //10
            Logger.i("onScanResult" + result.toString());
            //设备广播（ScanRecord）
            //result.getScanRecord().getServiceUuids()   mServiceUuids=[0000ab00-0000-1000-8000-00805f9b34fb]*/

            String deviceAddress = result.getDevice().getAddress();
            String deviceName = result.getDevice().getName();

            if (deviceName != null && !deviceName.trim().equals("") && deviceAddress != null) {
                boolean isAdd = true;//第一次无需查重
                for (int i = 0; i < getScanResults().size(); i++) {//查重
                    if (getScanResults().get(i).getScanResult().getDevice().getAddress().equals(deviceAddress)) {
                        isAdd = false;
                    }
                }
                if (isAdd) {
                    getScanResults().add(new MyScanResult(result, 0));
                    if (onScanConnectListener != null) {
                        onScanConnectListener.onScanSuccess();
                    }
                    if (getScanResults().size() >= SCAN_MAX_COUNT) { //达到限制后停止扫描
                        Logger.e("getScanResults().size() == " + getScanResults().size());
                        stopScan();
                    }
                }

            }

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Logger.i("onBatchScanResults");
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Logger.i("onScanFailed" + errorCode);
        }

    };

    /**
     * 强制清除内部缓存
     */
    protected boolean refreshDeviceCache(BluetoothGatt gatt) {
        if (gatt == null) {
            return false;
        }
        try {
            BluetoothGatt localBluetoothGatt = gatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
            if (localMethod != null) {
                boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                return bool;
            }
        } catch (Exception localException) {
            Logger.e("An exception occurred while refreshing device:");
            localException.printStackTrace();
        }
        return false;
    }


    /**
     * 打开BLE  外部调用
     */
    public void openBLE(BleOpenCallBack bleOpenCallBack) {
        this.bleOpenCallBack = bleOpenCallBack;
        boolean enabled = mBluetoothAdapter.isEnabled();
        Logger.i("adapter.isEnabled() == " + enabled);
        if (enabled) {
            isOpen = true;
            mHandler.post(() -> bleOpenCallBack.isOpen(isOpen));
            getScanResults().clear();
            mHandler.postDelayed(() -> {
                scanDevice();
            }, START_SCAN_DELAY_TIME);
            return;
        }

        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            new Thread(() -> {
                isOpen = mBluetoothAdapter.enable();
                Logger.i("mBluetoothAdapter.enable()");
                Logger.i("isOpen == " + isOpen);

                if (!isOpen) {
                    isOpen = mBluetoothAdapter.isEnabled();
                }
                Logger.i("isOpen == " + isOpen);

                if (isOpen) {
                    mHandler.postDelayed(() -> {
                        scanDevice();
                    }, START_SCAN_DELAY_TIME);
                    getScanResults().clear();
                } else {
                }

                mHandler.post(() -> bleOpenCallBack.isOpen(isOpen));
            }).start();
        }
    }

    /**
     * 关闭BLE 外部调用
     */
    public void closeBLE(BleClosedCallBack bleClosedCallBack) {
        this.bleClosedCallBack = bleClosedCallBack;

        if (mBluetoothAdapter != null) {
            new Thread(() -> {
                stopScan();
                disableCharacterNotifiy();
                disConnectDevice();
                mHandler.removeCallbacksAndMessages(null);

                // true 表示适配器关闭已开始，或 false 表示立即错误
                boolean disable = mBluetoothAdapter.disable();
                Logger.e("mBluetoothAdapter.disable() == " + disable);

                if (disable) {
                    isOpen = false;
                    Logger.e("断开蓝牙成功");
                } else {
                    Logger.e("断开蓝牙失败");
                }

                mHandler.post(() -> bleClosedCallBack.isClosed(disable));
            }).start();
        } else {
            Logger.e("mBluetoothAdapter == null");
        }
    }

    protected void startTimerOfIsConnect() {
        if (isConnectTimer == null) {
            isConnectTimer = new CustomTimer();
        }
        isConnectTimer.closeTimer();
        isConnectTimer.setTag(getConnectTag());
        isConnectTimer.startTimer(1000, 1000, this);
    }

    public abstract void whenBTClosed();

    public abstract void disableCharacterNotifiy();

    public abstract void connectDevice(MyScanResult scanResult);

    public boolean isConnected() {
        return isConnect;
    }

    protected abstract String getConnectTag();

    protected abstract void reset();

    protected abstract void disConnectDevice();

    protected abstract void destroy();

    protected abstract String getUuid();

}
