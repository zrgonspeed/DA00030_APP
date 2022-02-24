package com.bike.ftms.app.manager.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.os.SystemClock;

import androidx.annotation.RequiresApi;

import com.bike.ftms.app.R;
import com.bike.ftms.app.base.MyApplication;
import com.bike.ftms.app.bean.bluetooth.MyScanResult;
import com.bike.ftms.app.bean.rundata.RowerDataBean1;
import com.bike.ftms.app.bean.rundata.RowerDataBean2;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.common.RowerDataParam;
import com.bike.ftms.app.manager.storage.SpManager;
import com.bike.ftms.app.serial.SerialCommand;
import com.bike.ftms.app.serial.SerialData;
import com.bike.ftms.app.utils.ConvertData;
import com.bike.ftms.app.utils.CustomTimer;
import com.bike.ftms.app.utils.DataTypeConversion;
import com.bike.ftms.app.utils.Logger;

import org.litepal.crud.LitePalSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import dev.xesam.android.toolbox.timer.CountDownTimer;
import tech.gujin.toast.ToastUtil;

public class BleManager implements CustomTimer.TimerCallBack {
    private String TAG = BleManager.class.getSimpleName();
    private static BleManager instance;

    public final String uuid = "00001826-0000-1000-8000-00805f9b34fb";      //  标准服务：   Fitness Machine	    健康设备     1826
    public final String uuidHeartbeat = "0000180d-0000-1000-8000-00805f9b34fb"; // 标准服务： Heart Rate	        心率         180d
    public final String uuidSendData = "0000ffe5-0000-1000-8000-00805f9b34fb";  // 自定义服务uuid

    /**
     * 这些uuid用于收发运动数据
     * 2ada   2ad1   2ad3   ffe0    ffe9
     * 2ad1     Rower Data    桨手数据
     * 2ada     Fitness Machine Status 	健身设备状态
     * 2ad3     Training Status	    训练状况
     *
     * ffe0     自定义特征,  中心设备发
     * ffe9     自定义特征, 手机发
     */

    /**
     * 2a37    Heart Rate Measurement	心率测量
     */

    // 2ada 用
    private static final byte RUN_STATUS_RUNNING = 0x01;
    private static final byte RUN_STATUS_STOP = 0x00;

    // 2ad3 用
    public static final byte STATUS_IDLE = 0x01;
    public static final byte STATUS_RUNNING = 0x0D;
    public static final byte STATUS_POST = 0x0F;
    public static int status = STATUS_IDLE;

    private byte runStatus = RUN_STATUS_STOP;

    public static boolean isConnect;  //是否连接
    public static boolean isHrConnect;//是否连接蓝牙腰带
    public static boolean isCanning;  //是否正在扫描
    public static boolean isOpen;     //是否打开定位及蓝牙

    private BluetoothAdapter mBluetoothAdapter; //系统蓝牙适配器
    private OnScanConnectListener onScanConnectListener;      //扫描回调
    private List<MyScanResult> mScanResults = new ArrayList<>();      //扫描到的蓝牙设备
    private List<BluetoothGattService> mBluetoothGattServices;//服务，Characteristic(特征) 的集合。
    private BluetoothGattCharacteristic mBluetoothGattCharacteristic;//特征值(用于收发数据)
    private OnRunDataListener onRunDataListener;//运动数据回调

    // 电子表
    private BluetoothGatt mBluetoothGatt;       //连接蓝牙、及操作
    private MyScanResult connectScanResult;
    private CustomTimer isConnectTimer;
    private final String isConnectTag = "isConnect";

    // 心率设备
    private BluetoothGatt mBluetoothHrGatt;       //连接蓝牙、及操作
    private MyScanResult connectHrScanResult;
    private CustomTimer isHrConnectTimer;
    private final String isHrConnectTag = "isHrConnect";
    private short heart_rate = 0;   // 腰带心跳值，需要传给电子表
    private boolean isScanHrDevice = false;

    private static final long SCAN_MAX_COUNT = 20;     //扫描的设备个数限制（停止扫描）
    public static long SCAN_PERIOD = 60 * 1000;     //扫描设备时间限制
    private final long SCAN_PERIOD_INTERVAL = 1000;     //隔多久回调1次
    private static final long SEND_VERIFY_TIME = 2000; // 发送校验码延迟时间
    private static final long START_SCAN_DELAY_TIME = 2000; // 扫描设备延迟时间

    private boolean setBleDataInx = false;
    private boolean isToExamine = false;
    private boolean isSendVerifyData = false;
    private boolean onlyHr = false;

    private CustomTimer isVerifyConnectTimer;
    private final String isVerifyConnectTag = "isVerifyConnect";
    private final Handler mHandler = new Handler(Objects.requireNonNull(Looper.myLooper()));

    private RowerDataBean1 rowerDataBean1 = new RowerDataBean1();
    private RowerDataBean2 rowerDataBean2 = new RowerDataBean2();
    private int tempInterval1 = 0;
    private int tempInterval2 = 0;
    private boolean canSave = false;

    /**
     * 搜索设备时间计时
     */
    private CountDownTimer countDownTimer;

    private BleOpenCallBack bleOpenCallBack;
    private BleClosedCallBack bleClosedCallBack;
    private CountDownTime countDownTime;

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


    /**
     * 扫描回调
     *
     * @param onScanConnectListener
     */
    public void setOnScanConnectListener(OnScanConnectListener onScanConnectListener) {
        this.onScanConnectListener = onScanConnectListener;
    }

    /**
     * 运动数据回调
     *
     * @param onRunDataListener
     */
    public void setOnRunDataListener(OnRunDataListener onRunDataListener) {
        this.onRunDataListener = onRunDataListener;
    }

    private BleManager() {
    }

    public static BleManager getInstance() {
        if (instance == null) {
            synchronized (BleManager.class) {
                if (instance == null) {
                    instance = new BleManager();
                }
            }
        }
        return instance;
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
     * @return 系统蓝牙适配器
     */
    public BluetoothAdapter getBluetoothAdapter() {
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        Logger.e("mBluetoothAdapter == null ? " + (mBluetoothAdapter == null));
        return mBluetoothAdapter;
    }


    /**
     * 扫描蓝牙设备
     */
    public void scanDevice() {
        Logger.e("mBluetoothAdapter == " + mBluetoothAdapter + "    isCanning == " + isCanning);
        if (mBluetoothAdapter != null && !isCanning) {
            boolean enabled = mBluetoothAdapter.isEnabled();
            Logger.e("2 enabled == " + enabled);
            if (!enabled) {
                boolean enable = mBluetoothAdapter.enable();
                Logger.e("2_1 enable == " + enable);
                return;
            }

/*            mHandler.postDelayed(() -> {
                Logger.e("扫描时间到了，停止扫描");
                stopScan();
                if (mScanResults.size() == 0) {//没有搜索到设备
                    Logger.e("No devices were found");
                }
            }, SCAN_PERIOD);*/

            startSearchCountDownTimer();

            isCanning = true;
            mScanResults.clear();
            if (connectScanResult != null && !isScanHrDevice && connectScanResult.getConnectState() == 1) {
                mScanResults.add(connectScanResult);
                /*Logger.e("sssssssssssssss");
                try {
                    mScanResults.add((MyScanResult) connectScanResult.clone());
                    mScanResults.add((MyScanResult) connectScanResult.clone());
                    mScanResults.add((MyScanResult) connectScanResult.clone());
                    mScanResults.add((MyScanResult) connectScanResult.clone());
                    mScanResults.add((MyScanResult) connectScanResult.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }*/
            }
            if (connectHrScanResult != null && isScanHrDevice && connectHrScanResult.getConnectState() == 1) {
                mScanResults.add(connectHrScanResult);
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
            ScanFilter scanFilter;
            if (isScanHrDevice) {
                scanFilter = new ScanFilter.Builder().setServiceUuid(new ParcelUuid(UUID.fromString(uuidHeartbeat))).build();
                scanFilters.add(scanFilter);
                mBluetoothAdapter.getBluetoothLeScanner().startScan(scanFilters, scanSettings, mScanCallback);
            } else {
               /* scanFilter = new ScanFilter.Builder().setServiceUuid(
                        new ParcelUuid(UUID.fromString(uuid))).build();*/

                scanFilter = new ScanFilter.Builder().setServiceUuid(new ParcelUuid(UUID.fromString(uuid))).build();
                scanFilters.add(scanFilter);
                mBluetoothAdapter.getBluetoothLeScanner().startScan(scanFilters, scanSettings, mScanCallback);
            }
            Logger.i("1------开始扫描设备 isHr == " + isScanHrDevice);
        }
    }

    private void startSearchCountDownTimer() {
        if (countDownTimer == null) {
            // + 200 毫秒是应该的
            countDownTimer = new CountDownTimer(SCAN_PERIOD + 200, SCAN_PERIOD_INTERVAL) {
                @Override
                protected void onStart(long millisUntilFinished) {
                    Logger.e("开始扫描");
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

            Logger.i("停止扫描设备");
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

            //             Logger.i("2------扫描结果回调");
//            Logger.e("name=" + deviceName + " address=" + deviceAddress);

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

    public int mPosition = -1;

    /**
     * 连接蓝牙设备
     *
     * @param position
     */
    public void connectDevice(int position) {
        Logger.i("2------connectDevice(" + position + ")");
        if (mBluetoothGattServices != null) {
            Logger.e("mBluetoothGattServices.size == " + mBluetoothGattServices.size());
        }
        Logger.e("getScanResults(): " + getScanResults().size());

        if (getScanResults().get(position).getConnectState() == 1) {
            disableCharacterNotifiy();
            disConnectDevice();
            Logger.e("2------disConnectDevice()");
            BleManager.getInstance().mPosition = -1;
            return;
        }
        if (getScanResults() != null && getScanResults().size() != 0) {
            if (position >= 0 && position < getScanResults().size()) {
                getScanResults().get(position).setConnectState(2);
                if (!isScanHrDevice) {
                    rowerDataBean1 = new RowerDataBean1();
                    connectScanResult = new MyScanResult(getScanResults().get(position).getScanResult(), 2);
                    reset();
                    //第二个参数表示是否需要自动连接。如果设置为 true, 表示如果设备断开了，会不断的尝试自动连接。设置为 false 表示只进行一次连接尝试。
                    mBluetoothGatt = getScanResults().get(position).getScanResult().getDevice()
                            .connectGatt(MyApplication.getContext(), false, mGattCallback);
                } else {
                    connectHrScanResult = new MyScanResult(getScanResults().get(position).getScanResult(), 2);
                    mBluetoothHrGatt = getScanResults().get(position).getScanResult().getDevice()
                            .connectGatt(MyApplication.getContext(), false, mHrGattCallback);
                }

                Logger.e("connectDevice " + getScanResults().get(position).getScanResult().getDevice().getAddress());
            }
        }
        if (onScanConnectListener != null) {
            onScanConnectListener.onNotifyData();
        }
    }

    public void disableCharacterNotifiy() {
        Logger.i("disableCharacterNotifiy()");
        if (mBluetoothGattServices == null || mBluetoothGatt == null) {
            return;
        }
        for (BluetoothGattService service : mBluetoothGattServices) {
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                if (characteristic.getUuid().toString().contains("ffe0") ||
                        characteristic.getUuid().toString().contains("2ada") ||
                        characteristic.getUuid().toString().contains("2ad1") ||
                        characteristic.getUuid().toString().contains("2ad3")
                ) {
                    mBluetoothGatt.setCharacteristicNotification(characteristic, false);
                }
            }
        }
    }

    /**
     * 断开蓝牙设备
     */
    public void disConnectDevice() {
        Logger.i("disConnectDevice");
        isConnect = false;
        if (mBluetoothGatt != null && !isScanHrDevice) {
            Logger.e("断开设备");
            mBluetoothGatt.disconnect();
//            mBluetoothGatt = null;
        }

        isHrConnect = false;
        if (mBluetoothHrGatt != null && isScanHrDevice) {
            Logger.e("断开心跳设备");
            mBluetoothHrGatt.disconnect();
//            mBluetoothHrGatt = null;
        }
    }

    public void destroy() {
        mBluetoothGatt = null;
        mBluetoothHrGatt = null;

        bleClosedCallBack = null;
        bleOpenCallBack = null;

        onRunDataListener = null;
        onScanConnectListener = null;
//        mScanCallback = null;

//        instance = null;
    }

    /**
     * 断开蓝牙设备
     */
    public void disConnectAllDevice() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt = null;
        }
        if (mBluetoothHrGatt != null) {
            mBluetoothHrGatt.disconnect();
            mBluetoothHrGatt = null;
        }
    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
            Logger.i("onPhyUpdate");
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
            Logger.i("onPhyRead");
        }

        //当连接状态发生改变
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Logger.i("onConnectionStateChange status " + status);
            Logger.i("onConnectionStateChange newState " + newState);
            Logger.i("onConnectionStateChange name " + gatt.getDevice().getName());

            // 防止多次收发数据
//            if (isConnect) {
//                return;
//            }

            if (status != BluetoothGatt.GATT_SUCCESS) {
                BleManager.getInstance().mPosition = -1;
                if (mBluetoothGatt != null) {
                    Logger.e("mBluetoothGatt.close();");
                    mBluetoothGatt.close();
                }

                rowerDataBean1 = new RowerDataBean1();
                if (onRunDataListener != null) {
                    onRunDataListener.onRunData(rowerDataBean1);
                }
                for (MyScanResult myScanResult : mScanResults) {
                    if (myScanResult.getScanResult().getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                        myScanResult.setConnectState(0);
                        if (isConnectTimer != null) {
                            isConnectTimer.closeTimer();
                        }
                        if (connectScanResult.getScanResult().getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                            connectScanResult.setConnectState(0);
                        } else {
                            connectScanResult = myScanResult;
                        }
                        break;
                    }
                }
                isConnect = false;
                if (onScanConnectListener != null) {
                    onScanConnectListener.onConnectEvent(false, gatt.getDevice().getName());
                }

                if (onScanConnectListener != null) {
                    onScanConnectListener.onNotifyData();
                }
                return;
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Logger.i("3------连接成功");
                // 为对应的扫描结果设置连接状态
                for (MyScanResult myScanResult : mScanResults) {
                    if (myScanResult.getScanResult().getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                        myScanResult.setConnectState(1);
                        startTimerOfIsConnect();
                        if (connectScanResult.getScanResult().getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                            connectScanResult.setConnectState(1);
                        } else {
                            connectScanResult = myScanResult;
                        }
                        break;
                    }
                }
                isConnect = true;
                if (mBluetoothGatt != null) {
                    Logger.i("4------寻找服务");
                    mBluetoothGatt.discoverServices();
                }
                Logger.e("isConnect=" + isConnect + ",isHeartbeatConnect=" + isHrConnect);
                if (onScanConnectListener != null) {
                    onScanConnectListener.onConnectEvent(true, gatt.getDevice().getName());
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Logger.e("断开设备回调");

                // 保存运动数据
                saveRowDataBean1();

                BleManager.getInstance().mPosition = -1;
                rowerDataBean1 = new RowerDataBean1();
                if (onRunDataListener != null) {
                    onRunDataListener.onRunData(rowerDataBean1);
                }
                for (MyScanResult myScanResult : mScanResults) {
                    if (myScanResult.getScanResult().getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                        myScanResult.setConnectState(0);
                        if (isConnectTimer != null) {
                            isConnectTimer.closeTimer();
                        }
                        if (connectScanResult.getScanResult().getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                            connectScanResult.setConnectState(0);
                        } else {
                            connectScanResult = myScanResult;
                        }
                        break;
                    }
                }
                isConnect = false;
//                isOpen = false;
                if (onScanConnectListener != null) {
                    onScanConnectListener.onConnectEvent(false, gatt.getDevice().getName());
                }
                //++++
                if (mBluetoothGatt != null) {
                    Logger.e("mBluetoothGatt.close();");
                    mBluetoothGatt.close();
                }
            }
            if (onScanConnectListener != null) {
                onScanConnectListener.onNotifyData();
            }
        }

        //发现新服务，即调用了mBluetoothGatt.discoverServices()后，返回的数据  (读取 BLE 属性)
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Logger.i("onServicesDiscovered status=" + status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Logger.i("5------发现服务 status " + status);

                // 这个蓝牙设备的所有service
                mBluetoothGattServices = mBluetoothGatt.getServices();
                for (BluetoothGattService service : mBluetoothGattServices) {
                    Logger.e("service uuid = " + service.getUuid());
                }

                // 指定一个service
                BluetoothGattService localGattService = mBluetoothGatt.getService(UUID.fromString(uuid));
                // 获取这个service下的所有character
                List<BluetoothGattCharacteristic> list = new ArrayList<>();
                if (localGattService != null) {
                    list = localGattService.getCharacteristics();
                    Logger.d("localGattService = " + localGattService.getUuid());
                    for (BluetoothGattCharacteristic c : list) {
                        Logger.e("c = " + c.getUuid());
                    }
                }

                // 指定一个发送相关的service
                BluetoothGattService localGattService1 = mBluetoothGatt.getService(UUID.fromString(uuidSendData));
                if (localGattService1 != null) {
                    List<BluetoothGattCharacteristic> characteristics = localGattService1.getCharacteristics();
                    list.addAll(characteristics);

                    Logger.d("localGattService1 = " + localGattService1.getUuid());
                    for (BluetoothGattCharacteristic c : characteristics) {
                        Logger.e("c = " + c.getUuid());
                    }
                }

//                Logger.i("c列表 = ");
                for (int i = 0; i < list.size(); i++) {
                    Logger.e("c = " + list.get(i).getUuid().toString());

                    if (list.get(i).getUuid().toString().contains("2ad1")) {
                        List<BluetoothGattDescriptor> bluetoothGattDescriptors = list.get(i).getDescriptors();
                        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptors) {
                            boolean r = bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
                            Logger.d("" + list.get(i).getUuid().toString() + ",bluetoothGattDescriptor " + r);
                        }
                    }
                    if (list.get(i).getUuid().toString().contains("2ad3")) {
                        List<BluetoothGattDescriptor> bluetoothGattDescriptors = list.get(i).getDescriptors();
                        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptors) {
                            boolean r = bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
                            Logger.d("" + list.get(i).getUuid().toString() + ",bluetoothGattDescriptor " + r);
                        }
                    }
                    if (list.get(i).getUuid().toString().contains("ffe0")) {
                        List<BluetoothGattDescriptor> bluetoothGattDescriptors = list.get(i).getDescriptors();
                        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptors) {
                            boolean r = bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
                            Logger.d("" + list.get(i).getUuid().toString() + ",bluetoothGattDescriptor " + r);
                        }
                    }
                    if (list.get(i).getUuid().toString().contains("2ada")) {
                        List<BluetoothGattDescriptor> bluetoothGattDescriptors = list.get(i).getDescriptors();
                        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptors) {
                            boolean r = bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
                            Logger.d("" + list.get(i).getUuid().toString() + ",bluetoothGattDescriptor " + r);
                        }
                    }

                    /*if (list.get(i).getUuid().toString().contains("2a23")) {
                        List<BluetoothGattDescriptor> bluetoothGattDescriptors = list.get(i).getDescriptors();
                        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptors) {
                            boolean r = bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
                            Logger.e("" +list.get(i).getUuid().toString() + ",bluetoothGattDescriptor " + r);
                        }
                    }*/
                }
                registrationGattCharacteristic();//注册通知
            } else {
                Logger.e("onServicesDiscovered received: " + status);
            }

        }

        //发送数据后的回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
//            Logger.i("onCharacteristicWrite::" + ByteArrTransUtil.toHexValue(characteristic.getValue()));
        }

        //调用mBluetoothGatt.readCharacteristic(characteristic)读取数据回调，在这里面接收数据
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Logger.i("onCharacteristicRead::" + ConvertData.byteArrayToHexString(characteristic.getValue(), characteristic.getValue().length));

        }

        //特征值的通知回调(异步，远程设备上的特征发生更改时回调)
        // (需要设置特征的通知：bluetoothGatt.setCharacteristicNotification(characteristic, enabled))
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            isConnectTimer.setmAllTime(0L);
            String uuid = characteristic.getUuid().toString().substring(0, 8);
            if (uuid.contains("ffe0")) {
                Logger.i(uuid + ",::" + ConvertData.byteArrayToHexString(characteristic.getValue(), characteristic.getValue().length));
            } else {
                // Logger.i(Arrays.toString(characteristic.getValue()) + "");
                Logger.i(uuid + ",::" + ConvertData.byteArrayToHexString(characteristic.getValue(), characteristic.getValue().length));
            }

            byte[] data = characteristic.getValue();
            rxDataPackage(data, characteristic.getUuid().toString());
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {//descriptor读
            super.onDescriptorRead(gatt, descriptor, status);
            Logger.i("onDescriptorRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {//descriptor写
            super.onDescriptorWrite(gatt, descriptor, status);
            // 写入 0x01 0x00
            // 写入到关联的远程设备上
            Logger.i("onDescriptorWrite " + ConvertData.byteArrayToHexString(descriptor.getValue(), descriptor.getValue().length));
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            Logger.i("onReliableWriteCompleted");
        }

        //调用mBluetoothGatt.readRemoteRssi()时的回调，rssi即信号强度
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {//读Rssi
            super.onReadRemoteRssi(gatt, rssi, status);
            Logger.i("onReadRemoteRssi");
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Logger.i("onMtuChanged");
        }
    };

    private BluetoothGattCallback mHrGattCallback = new BluetoothGattCallback() {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
            Logger.i("onPhyUpdate");
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
            Logger.i("onPhyRead");
        }

        //当连接状态发生改变
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Logger.i("onConnectionStateChange status " + status);
            Logger.i("onConnectionStateChange newState " + newState);
            Logger.i("onConnectionStateChange name " + gatt.getDevice().getName());

            if (status != BluetoothGatt.GATT_SUCCESS) {
                if (mBluetoothHrGatt != null) {
                    Logger.e("mBluetoothHrGatt.close();");
                    mBluetoothHrGatt.close();
                }

                rowerDataBean1 = new RowerDataBean1();
                if (onRunDataListener != null) {
                    onRunDataListener.onRunData(rowerDataBean1);
                }
                for (MyScanResult myScanResult : mScanResults) {
                    if (myScanResult.getScanResult().getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                        myScanResult.setConnectState(0);
                        if (isHrConnectTimer != null) {
                            isHrConnectTimer.closeTimer();
                        }
                        if (connectHrScanResult.getScanResult().getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                            connectHrScanResult.setConnectState(0);
                        } else {
                            connectHrScanResult = myScanResult;
                        }
                        break;
                    }
                }
                isHrConnect = false;
                if (onScanConnectListener != null) {
                    onScanConnectListener.onConnectEvent(false, gatt.getDevice().getName());
                }

                if (onScanConnectListener != null) {
                    onScanConnectListener.onNotifyData();
                }
                return;
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                for (MyScanResult myScanResult : mScanResults) {
                    if (myScanResult.getScanResult().getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                        myScanResult.setConnectState(1);
                        startTimerOfIsHrConnect();
                        if (connectHrScanResult.getScanResult().getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                            connectHrScanResult.setConnectState(1);
                        } else {
                            connectHrScanResult = myScanResult;
                        }
                        break;
                    }
                }
                isHrConnect = true;
                if (mBluetoothHrGatt != null) {
                    mBluetoothHrGatt.discoverServices();
                }
                Logger.e("isConnect=" + isConnect + ",isHeartbeatConnect=" + isHrConnect);
                if (onScanConnectListener != null) {
                    onScanConnectListener.onConnectEvent(true, gatt.getDevice().getName());
                }
                // mBluetoothGatt.discoverServices();//
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Logger.e("断开心跳设备回调");
                rowerDataBean1 = new RowerDataBean1();
                if (onRunDataListener != null) {
                    onRunDataListener.onRunData(rowerDataBean1);
                }
                for (MyScanResult myScanResult : mScanResults) {
                    if (myScanResult.getScanResult().getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                        myScanResult.setConnectState(0);
                        if (isHrConnectTimer != null) {
                            isHrConnectTimer.closeTimer();
                        }
                        if (connectHrScanResult.getScanResult().getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                            connectHrScanResult.setConnectState(0);
                        } else {
                            connectHrScanResult = myScanResult;
                        }
                        break;
                    }
                }
                isHrConnect = false;
                if (onScanConnectListener != null) {
                    onScanConnectListener.onConnectEvent(false, gatt.getDevice().getName());
                }

                //++++
                if (mBluetoothHrGatt != null) {
                    Logger.e("mBluetoothHrGatt.close();");
                    mBluetoothHrGatt.close();
                }
            }
            if (onScanConnectListener != null) {
                onScanConnectListener.onNotifyData();
            }
        }

        //发现新服务，即调用了mBluetoothGatt.discoverServices()后，返回的数据  (读取 BLE 属性)
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Logger.i("Hr onServicesDiscovered status=" + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mBluetoothGattServices = mBluetoothHrGatt.getServices();
                BluetoothGattService localGattService = mBluetoothHrGatt.getService(UUID.fromString(uuidHeartbeat));
                List<BluetoothGattCharacteristic> list = new ArrayList<>();
                if (localGattService != null) {
                    list = localGattService.getCharacteristics();
                }
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getUuid().toString().contains("2a37")) {
                        List<BluetoothGattDescriptor> bluetoothGattDescriptors = list.get(i).getDescriptors();
                        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptors) {
                            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBluetoothHrGatt.writeDescriptor(bluetoothGattDescriptor);
                            Logger.d("bluetoothGattDescriptor" + bluetoothGattDescriptor.getUuid());
                        }
                    }
                    if (list.get(i).getUuid().toString().contains("2a38")) {
                        List<BluetoothGattDescriptor> bluetoothGattDescriptors = list.get(i).getDescriptors();
                        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptors) {
                            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            boolean r = mBluetoothHrGatt.writeDescriptor(bluetoothGattDescriptor);
                            Logger.d("bluetoothGattDescriptor" + bluetoothGattDescriptor.getUuid() + "," + r);
                        }
                    }
                }
                registrationGattCharacteristic();//注册通知
            } else {
                Logger.d("Hr onServicesDiscovered received: " + status);
            }
        }

        //发送数据后的回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
//            Logger.i("Hr onCharacteristicWrite::" + ByteArrTransUtil.toHexValue(characteristic.getValue()));
        }

        //调用mBluetoothGatt.readCharacteristic(characteristic)读取数据回调，在这里面接收数据
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Logger.i("Hr onCharacteristicRead::" + ConvertData.byteArrayToHexString(characteristic.getValue(), characteristic.getValue().length));

        }

        //特征值的通知回调(异步，远程设备上的特征发生更改时回调)
        // (需要设置特征的通知：bluetoothGatt.setCharacteristicNotification(characteristic, enabled))
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            // 10秒内     这里没执行，就会断开连接
            isHrConnectTimer.setmAllTime(0L);
            Logger.i("" + characteristic.getUuid() + ",Hr onCharacteristicChanged::" + ConvertData.byteArrayToHexString(characteristic.getValue(), characteristic.getValue().length));

            if (characteristic.getUuid().toString().contains("2a37")) {
                setHrData(characteristic.getValue());
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {//descriptor读
            super.onDescriptorRead(gatt, descriptor, status);
            Logger.i("onDescriptorRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {//descriptor写
            super.onDescriptorWrite(gatt, descriptor, status);
            Logger.i("onDescriptorWrite " + ConvertData.byteArrayToHexString(descriptor.getValue(), descriptor.getValue().length));
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            Logger.i("onReliableWriteCompleted");
        }

        //调用mBluetoothGatt.readRemoteRssi()时的回调，rssi即信号强度
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {//读Rssi
            super.onReadRemoteRssi(gatt, rssi, status);
            Logger.i("onReadRemoteRssi");
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Logger.i("onMtuChanged");
        }
    };

    /**
     * 注册特征GATT 通知
     * ab01//发送通道
     * ab02//接收通道
     */
    private void registrationGattCharacteristic() {
        if (mBluetoothGattServices != null) {
            BluetoothGattService gattService;
            if (!isScanHrDevice) {
                gattService = mBluetoothGatt.getService(UUID.fromString(uuid));
                if (gattService == null) {
                    // onRunDataListener.onExit();
                    mBluetoothGatt.disconnect();
                    mBluetoothGatt = null;
                }
                if (mBluetoothGatt != null) {
                    for (BluetoothGattCharacteristic gattCharacteristic : mBluetoothGatt.getService(UUID.fromString(uuidSendData)).getCharacteristics()) {
                        if (gattCharacteristic.getUuid().toString().contains("ffe9")) {
                            mBluetoothGattCharacteristic = gattCharacteristic;
                            Logger.d("mBluetoothGattCharacteristic=ffe9  " + mBluetoothGattCharacteristic.getUuid());
                        }
                        if (gattCharacteristic.getUuid().toString().contains("ffe0")) {//接收通道
                            boolean enabled = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
//                            Logger.i(TAG, gattCharacteristic.getUuid().toString() + ",注册通知::" + enabled);
                        }
                    }
                }
                mHandler.postDelayed(() -> {
                    if (!isSendVerifyData) {
                        isSendVerifyData = true;
                        sendVerifyData();
                    }
                }, SEND_VERIFY_TIME);

            } else {
                gattService = mBluetoothHrGatt.getService(UUID.fromString(uuidHeartbeat));
            }
           /* for (BluetoothGattService gattService1 : mBluetoothGatt.getServices()) {
                Logger.d("=========================================");
                Logger.d("getServices=" + gattService1.getUuid().toString());
                for (BluetoothGattCharacteristic gattCharacteristic : gattService1.getCharacteristics()) {
                    Logger.d("gattCharacteristic=" + gattCharacteristic.getUuid().toString());
                    for (BluetoothGattDescriptor bluetoothGattDescriptor : gattCharacteristic.getDescriptors()) {
                        Logger.d("getDescriptors=" + bluetoothGattDescriptor.getUuid().toString());
                    }
                }
            }*/
            if (gattService != null) {
                for (BluetoothGattCharacteristic gattCharacteristic : gattService.getCharacteristics()) {
                    //除了通过 BluetoothGatt#setCharacteristicNotification 开启 Android 端接收通知的开关，
                    // 还需要往 Characteristic 的 Descriptor 属性写入开启通知的数据开关使得当硬件的数据改变时，主动往手机发送数据。
               /* if (gattCharacteristic.getUuid().toString().contains("ab01")) {//发送通道
                    mBluetoothGattCharacteristic = gattCharacteristic;
                    Logger.i("发送通道::ab01");
                }*/
//                Logger.d("gattCharacteristic1=" + gattCharacteristic.getUuid().toString());

                    if (gattCharacteristic.getUuid().toString().contains("2ad1")) {//接收通道
                        boolean enabled = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
//                        Logger.i("注册通知::" + enabled);
                    }
                    if (gattCharacteristic.getUuid().toString().contains("2ad3")) {//接收通道
                        boolean enabled = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
//                        Logger.i("注册通知::" + enabled);
                    }
                    if (gattCharacteristic.getUuid().toString().contains("2ada")) {//接收通道
                        boolean enabled = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
//                        Logger.i("注册通知::" + enabled);
                    }

//                if (gattCharacteristic.getUuid().toString().contains("2a23")) {//接收通道
//                    boolean enabled = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
//                    Logger.i("注册通知::" + enabled);
//                }

                    if (gattCharacteristic.getUuid().toString().contains("2a37")) {//接收通道
                        boolean enabled = mBluetoothHrGatt.setCharacteristicNotification(gattCharacteristic, true);
//                        Logger.i("注册通知::" + enabled);
                    }
                    if (gattCharacteristic.getUuid().toString().contains("2a38")) {//接收通道
                        boolean enabled = mBluetoothHrGatt.setCharacteristicNotification(gattCharacteristic, true);
//                        Logger.i("注册通知::" + enabled);
                    }

                    if (gattCharacteristic.getUuid().toString().contains("2ad6")) {
                        boolean enabled = mBluetoothGatt.readCharacteristic(gattCharacteristic);
                        Logger.i("2ad6 读::" + enabled);
                    }

//                    Logger.i("GattCharacteristic " + gattCharacteristic.getUuid().toString());

                }
            }

        }
    }

    /**
     * 发送数据
     *
     * @param bytes 指令
     */
    private boolean sendDescriptorByte(byte[] bytes, int len) {
        boolean r = false;
        byte[] sendBytes = new byte[len];
        System.arraycopy(bytes, 0, sendBytes, 0, len);
        if (mBluetoothGattCharacteristic != null && mBluetoothGatt != null) {
            mBluetoothGattCharacteristic.setValue(sendBytes);
            r = mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
            Logger.d("" + mBluetoothGattCharacteristic.getUuid().toString().substring(0, 8) + ",::" + ConvertData.byteArrayToHexString(sendBytes, sendBytes.length));
        } else {
//            Logger.d("Send:" + ConvertData.byteArrayToHexString(sendBytes, sendBytes.length) + r);
            Logger.e("发送到电子表失败");
        }
        return r;
    }

    /**
     * 释放资源
     */
    public void closed() {
        // 蓝牙关闭后的操作
        BleManager.getInstance().isOpen = false;
        BleManager.getInstance().isCanning = false;
        BleManager.getInstance().isConnect = false;
        BleManager.getInstance().isHrConnect = false;

//        if (mBluetoothHrGatt != null) {
//            mBluetoothHrGatt.disconnect();
//        }
//
//        if (mBluetoothGatt != null) {
//            mBluetoothGatt.close();
//            mBluetoothGatt = null;
//        }
//        if (mBluetoothHrGatt != null) {
//            mBluetoothHrGatt.close();
//            mBluetoothHrGatt = null;
//        }
//        if (connectHrScanResult != null) {
//            connectHrScanResult = null;
//        }
//        if (connectScanResult != null) {
//            connectScanResult = null;
//        }

    }

    /**
     * 打开BLE
     */
    public void openBLE(BleOpenCallBack bleOpenCallBack) {
        this.bleOpenCallBack = bleOpenCallBack;
        boolean enabled = mBluetoothAdapter.isEnabled();
        Logger.e("enable == " + enabled);
        if (enabled) {
            isOpen = enabled;
            mHandler.post(() -> bleOpenCallBack.isOpen(isOpen));
            BleManager.getInstance().getScanResults().clear();
            mHandler.postDelayed(() -> {
                BleManager.getInstance().scanDevice();
            }, START_SCAN_DELAY_TIME);
            return;
        }

        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            new Thread(() -> {
                isOpen = mBluetoothAdapter.enable();
                Logger.e("isOpen == " + isOpen);

                if (!isOpen) {
                    isOpen = mBluetoothAdapter.isEnabled();
                }
                Logger.e("isOpen == " + isOpen);

                if (isOpen) {
                    mHandler.postDelayed(() -> {
                        BleManager.getInstance().scanDevice();
                    }, START_SCAN_DELAY_TIME);
                    BleManager.getInstance().getScanResults().clear();
                } else {
                }

                mHandler.post(() -> bleOpenCallBack.isOpen(isOpen));
            }).start();
        }
    }

    /**
     * 关闭BLE
     */
    public void closeBLE(BleClosedCallBack bleClosedCallBack) {
        this.bleClosedCallBack = bleClosedCallBack;

        if (mBluetoothAdapter != null) {
            new Thread(() -> {
                // true 表示适配器关闭已开始，或 false 表示立即错误
                boolean disable = mBluetoothAdapter.disable();
                Logger.e("mBluetoothAdapter.disable() == " + disable);

                if (disable) {
                    isOpen = false;

                    Logger.e("断开蓝牙");
                    BleManager.getInstance().disableCharacterNotifiy();
                    BleManager.getInstance().disConnectDevice();
                    BleManager.getInstance().stopScan();

                    mHandler.removeCallbacksAndMessages(null);
                } else {
                    Logger.e("断开蓝牙失败");
                }

                mHandler.post(() -> bleClosedCallBack.isClosed(disable));
            }).start();
        } else {
            Logger.e("mBluetoothAdapter == null");
        }
    }

    /**
     * 获取data 数据中第offSet 长度为len  的结果
     *
     * @param data
     * @param offSet
     * @param len
     * @return result
     */
    private static int resolveData(byte[] data, int offSet, int len) {
        int result;
        if (len == 4) {
            result = DataTypeConversion.bytesToIntLitter(data, offSet);
        } else if (len == 3) {
            result = DataTypeConversion.Byte2Int(data, offSet);
        } else if (len == 2) {
            result = DataTypeConversion.doubleBytesToIntLiterEnd(data, offSet);
        } else if (len == 1) {
            result = DataTypeConversion.byteToInt(data[offSet]);
        } else {
            result = 0;
        }
        return result;
    }

    /**
     * 设置运动数据下标  ftms协议
     *
     * @param data
     */
    private void setBleDataInx(byte[] data) {
        if (setBleDataInx) {
            return;
        }
        setBleDataInx = true;
        int inxLen = 2;
        String s = ConvertData.byteArrToBinStr(data);

        Logger.d("------------s == " + s);

        String[] strings = s.split(",");
        StringBuffer stringBuffer = new StringBuffer();
        for (String string : strings) {
            for (int i = string.length() - 1; i >= 0; i--) {
                stringBuffer.append(string.subSequence(i, i + 1));
            }
        }
        s = stringBuffer.toString();
        for (int i = 0; i < s.length(); i++) {
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

    /**
     * 恢复默认值
     */
    private void reset() {
        RowerDataParam.STROKE_RATE_INX = -1;
        RowerDataParam.STROKE_COUNT_INX = -1;
        RowerDataParam.AVERAGE_STROKE_RATE_INX = -1;
        RowerDataParam.TOTAL_DISTANCE_INX = -1;
        RowerDataParam.INSTANTANEOUS_PACE_INX = -1;
        RowerDataParam.AVERAGE_PACE_INX = -1;
        RowerDataParam.INSTANTANEOUS_POWER_INX = -1;
        RowerDataParam.AVERAGE_POWER_INX = -1;
        RowerDataParam.RESISTANCE_LEVEL_INX = -1;
        RowerDataParam.HEART_RATE_INX = -1;
        RowerDataParam.METABOLIC_EQUIVALENT_INX = -1;
        RowerDataParam.ELAPSED_TIME_INX = -1;
        RowerDataParam.REMAINING_TIME_INX = -1;
        RowerDataParam.ENERGY_PER_HOUR_INX = -1;
        RowerDataParam.TOTAL_ENERGY_INX = -1;
        RowerDataParam.ENERGY_PER_MINUTE_INX = -1;
        setBleDataInx = false;
        isSendVerifyData = false;
        isToExamine = false;
        canSave = false;
    }

    /**
     * 心跳数据
     *
     * @param data
     */
    private void setHrData(byte[] data) {
        if (onRunDataListener == null) {
            return;
        }

        if (rowerDataBean1 == null) {
            rowerDataBean1 = new RowerDataBean1();
        }

        String s = ConvertData.byteArrToBinStr(data);
        // 0x16 == 22   0001 0110
        if ("0".equals(s.subSequence(7, 8))) {
            rowerDataBean1.setHeart_rate(ConvertData.byteToInt(data[1]));
        } else {
            rowerDataBean1.setHeart_rate(resolveData(data, 1, 2));
        }

        heart_rate = (short) rowerDataBean1.getHeart_rate();
        if (onRunDataListener != null) {
            onRunDataListener.onRunData(rowerDataBean1);
        }
    }

    /**
     * 校验命令
     */
    private void sendVerifyData() {
        String[] dates = getCurDate().split("-");
        byte[] date = new byte[9];
        date[0] = (byte) SerialCommand.PACK_FRAME_HEADER;
        date[1] = (byte) 0x40;
        date[2] = 0x01;
        date[3] = (byte) Integer.parseInt(dates[0], 16);
        date[4] = (byte) Integer.parseInt(dates[1], 16);
        date[5] = (byte) Integer.parseInt(dates[2], 16);
        byte[] bytes = new byte[64];
        int len = SerialData.comPackage(date, bytes, date.length - 3);
        sendDescriptorByte(bytes, len);
        startTimerOfIsVerifyConnect();
    }

    private void sendRespondData(byte[] data) {
//        byte[] bytes = new byte[data.length + 1];
//        bytes[0] = (byte) SerialCommand.PACK_FRAME_HEADER;
//        bytes[1] = (byte) 0x00;
//        System.arraycopy(data, 1, bytes, 2, data.length - 4);
//        byte[] respondByte = new byte[64];
//        int len = SerialData.comPackage(bytes, respondByte, bytes.length - 3);
//        sendDescriptorByte(respondByte, len);

        // 0xfe 0x00 0x41 0x02  0xb4 0x00 0xcf 0x26 0xff
        byte[] bytes = new byte[9];
        bytes[0] = (byte) SerialCommand.PACK_FRAME_HEADER;
        bytes[1] = (byte) 0x00;
        System.arraycopy(data, 1, bytes, 2, 2);

        // 把腰带心跳值发给电子表
        byte[] hrBytes = ConvertData.shortToBytes(heart_rate);
        bytes[4] = hrBytes[0]; // 心跳  低位在前，高位在后
        bytes[5] = hrBytes[1];

        byte[] respondByte = new byte[64];
        int len = SerialData.comPackage(bytes, respondByte, bytes.length - 3);
        sendDescriptorByte(respondByte, len);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getCurDate() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("YY-MM-dd");
        return sDateFormat.format(new java.util.Date());
    }

    public void setIsScanHrDevice(boolean isScanHrDevice) {
        this.isScanHrDevice = isScanHrDevice;
    }

    private void startTimerOfIsConnect() {
        if (isConnectTimer == null) {
            isConnectTimer = new CustomTimer();
        }
        isConnectTimer.closeTimer();
        isConnectTimer.setTag(isConnectTag);
        isConnectTimer.startTimer(1000, 1000, this);
    }

    private void startTimerOfIsHrConnect() {
        if (isHrConnectTimer == null) {
            isHrConnectTimer = new CustomTimer();
        }
        isHrConnectTimer.closeTimer();
        isHrConnectTimer.setTag(isHrConnectTag);
        isHrConnectTimer.startTimer(1000, 1000, this);
    }

    private void startTimerOfIsVerifyConnect() {
        if (isVerifyConnectTimer == null) {
            isVerifyConnectTimer = new CustomTimer();
        }
        isVerifyConnectTimer.closeTimer();
        isVerifyConnectTimer.setTag(isVerifyConnectTag);
        isVerifyConnectTimer.startTimer(1000, 1000, this);
    }

    @Override
    public void timerComply(long lastTime, String tag) {
        if (lastTime == 10 && isConnectTag.equals(tag)) {
            if (mBluetoothGatt != null) {
                mBluetoothGatt.disconnect();
                mBluetoothGatt = null;
            }
            isConnectTimer.closeTimer();
            if (onRunDataListener != null) {
                onRunDataListener.disConnect();
            }
            Logger.d("断开连接");
            return;
        }
        if (lastTime == 10 && isHrConnectTag.equals(tag)) {
            if (mBluetoothHrGatt != null) {
                mBluetoothHrGatt.disconnect();
//                mBluetoothHrGatt = null;
            }

            isHrConnectTimer.closeTimer();
            Logger.d("断开连接Hr");
            return;
        }
        if (lastTime == 3 && isVerifyConnectTag.equals(tag)) {
            if (onRunDataListener != null) {
                //onRunDataListener.onExit();
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.disconnect();
                    mBluetoothGatt = null;
                }
                isVerifyConnectTimer.closeTimer();
            }
        }
    }

    private void rxDataPackage(byte[] data, String uuid) {
        if (!isSendVerifyData) {
            isSendVerifyData = true;
            sendVerifyData();
            return;
        }

        // 我2ad3又回来了
        if (uuid.contains("2ad3") && isToExamine) {
            // 1 idle       d running       f post
            status = data[1];
            if (status == STATUS_POST) {
                // 保存数据
                saveRowDataBean1();
            }

//            if (data[1] == STATUS_IDLE && runStatus != RUN_STATUS_STOP) {//停止运动
//                rowerDataBean.save();
//                rowerDataBean = new RowerDataBean();
//            }
//            runStatus = data[1];
        }

        if (uuid.contains("2ad1") && isToExamine) {
            setBleDataInx(new byte[]{data[0], data[1]});
            setRunData_2AD1(data);
            return;
        }

        if (uuid.contains("2ada") && isToExamine) {
            setRunData_2ADA(data);
            return;
        }

        if (uuid.contains("ffe0")) {
            setRunData_FFE0(data);
            return;
        }
    }

    /**
     * 设置运动数据
     *
     * @param data
     */
    private void setRunData_2AD1(byte[] data) {
        if (onlyHr) {
            if (!isHrConnect) {
                rowerDataBean1.setHeart_rate(RowerDataParam.HEART_RATE_INX == -1 ? 0 : resolveData(data, RowerDataParam.HEART_RATE_INX, RowerDataParam.HEART_RATE_LEN));
                if (onRunDataListener != null) {
                    onRunDataListener.onRunData(rowerDataBean1);
                }
            }
            return;
        }

        if (onRunDataListener == null) {
            return;
        }

        if (runStatus == RUN_STATUS_STOP) {
            return;
        }

        rowerDataBean1.setStrokes(RowerDataParam.STROKE_COUNT_INX == -1 ? 0 : resolveData(data, RowerDataParam.STROKE_COUNT_INX, RowerDataParam.STROKE_COUNT_LEN));
        rowerDataBean1.setDistance(RowerDataParam.TOTAL_DISTANCE_INX == -1 ? 0 : resolveData(data, RowerDataParam.TOTAL_DISTANCE_INX, RowerDataParam.TOTAL_DISTANCE_LEN));
        rowerDataBean1.setSm(RowerDataParam.STROKE_RATE_INX == -1 ? 0 : resolveData(data, RowerDataParam.STROKE_RATE_INX, RowerDataParam.STROKE_RATE_LEN) / 2);
        rowerDataBean1.setFive_hundred(RowerDataParam.INSTANTANEOUS_PACE_INX == -1 ? 0 : resolveData(data, RowerDataParam.INSTANTANEOUS_PACE_INX, RowerDataParam.INSTANTANEOUS_PACE_LEN));
        rowerDataBean1.setCalorie(RowerDataParam.TOTAL_ENERGY_INX == -1 ? 0 : resolveData(data, RowerDataParam.TOTAL_ENERGY_INX, RowerDataParam.TOTAL_ENERGY_LEN));
        rowerDataBean1.setCalories_hr(RowerDataParam.ENERGY_PER_HOUR_INX == -1 ? 0 : resolveData(data, RowerDataParam.ENERGY_PER_HOUR_INX, RowerDataParam.ENERGY_PER_HOUR_LEN));
        if (!isHrConnect) {
            rowerDataBean1.setHeart_rate(RowerDataParam.HEART_RATE_INX == -1 ? 0 : resolveData(data, RowerDataParam.HEART_RATE_INX, RowerDataParam.HEART_RATE_LEN));
        }
        rowerDataBean1.setWatts(RowerDataParam.INSTANTANEOUS_POWER_INX == -1 ? 0 : resolveData(data, RowerDataParam.INSTANTANEOUS_POWER_INX, RowerDataParam.INSTANTANEOUS_POWER_LEN));
        rowerDataBean1.setAve_watts(RowerDataParam.AVERAGE_POWER_INX == -1 ? 0 : resolveData(data, RowerDataParam.AVERAGE_POWER_INX, RowerDataParam.AVERAGE_POWER_LEN));
        rowerDataBean1.setAve_five_hundred(RowerDataParam.AVERAGE_PACE_INX == -1 ? 0 : resolveData(data, RowerDataParam.AVERAGE_PACE_INX, RowerDataParam.AVERAGE_PACE_LEN));
        if (RowerDataParam.REMAINING_TIME_INX == -1 || resolveData(data, RowerDataParam.REMAINING_TIME_INX, RowerDataParam.REMAINING_TIME_LEN) == 0) {
            rowerDataBean1.setTime(RowerDataParam.ELAPSED_TIME_INX == -1 ? 0 : resolveData(data, RowerDataParam.ELAPSED_TIME_INX, RowerDataParam.ELAPSED_TIME_LEN));
        } else {
            rowerDataBean1.setTime(RowerDataParam.REMAINING_TIME_INX == -1 ? 0 : resolveData(data, RowerDataParam.REMAINING_TIME_INX, RowerDataParam.REMAINING_TIME_LEN));
        }
        // 只精确到秒，毫秒域为 000
        rowerDataBean1.setDate(System.currentTimeMillis() / 1000 * 1000);
        if (onRunDataListener != null) {
            onRunDataListener.onRunData(rowerDataBean1);
        }
    }

    private void setRunData_2ADA(byte[] data) {
        // 单独显示心跳
        if (data[3] == 0 && data[4] == 1) {
            onlyHr = true;
        } else {
            onlyHr = false;
        }

        Logger.d("---↑------------------------2ada----------------------↑------------------");
        //停止运动
        if (data[3] == RUN_STATUS_STOP && runStatus != RUN_STATUS_STOP) {
            Logger.d("----------------data[3] == STOP-----------mode == " + rowerDataBean1.getRunMode());
            canSave = false;
            if (rowerDataBean1.getRunMode() == MyConstant.GOAL_TIME) {
                // 时间是倒数的，用距离判断
                if (rowerDataBean1.getDistance() >= 10) {
                    canSave = true;
                }
            } else if (rowerDataBean1.getRunMode() == MyConstant.INTERVAL_TIME) {
                if (rowerDataBean1.getInterval() > 0 || rowerDataBean1.getDistance() >= 10) {
                    canSave = true;
                }
            } else if (rowerDataBean1.getRunMode() == MyConstant.INTERVAL_DISTANCE) {
                if (rowerDataBean1.getInterval() > 0 || rowerDataBean1.getTime() >= 5) {
                    canSave = true;
                }
            } else if (rowerDataBean1.getRunMode() == MyConstant.INTERVAL_CALORIES) {
                if (rowerDataBean1.getInterval() > 0 || rowerDataBean1.getTime() >= 5) {
                    canSave = true;
                }
            } else {
                if (rowerDataBean1.getTime() >= 5) {
                    canSave = true;
                }
            }

            if (canSave) {
                rowerDataBean1.setFlag(3);
                runStatus = data[3];
                return;
            }
        }
        runStatus = data[3];
        rowerDataBean1.setRunStatus(runStatus);

        if (onRunDataListener == null) {
            return;
        }

        if (rowerDataBean1.getCanSave()) {

//            return;
        }
        // 都要设置的参数
        int runMode = resolveData(data, RowerDataParam.RUN_MODE_INX, RowerDataParam.RUN_MODE_LEN);
        int intervalStatus = resolveData(data, RowerDataParam.INTERVAL_STATUS_INX, RowerDataParam.INTERVAL_STATUS_LEN);
        int runStatus = resolveData(data, RowerDataParam.RUN_STATUS_INX, RowerDataParam.RUN_STATUS_LEN);
        int runInterval = resolveData(data, RowerDataParam.RUN_INTERVAL_INX, RowerDataParam.RUN_INTERVAL_LEN);
        rowerDataBean1.setRunMode(runMode);
        rowerDataBean1.setIntervalStatus(intervalStatus);
        rowerDataBean1.setRunStatus(runStatus);
        // TODO: 2021/11/17 重复了
        rowerDataBean1.setRunInterval(runInterval);

        // 设置目标模式或间歇模式的指定值
        if (MyConstant.isGoalMode(runMode)) {
            switch (runMode) {
                case MyConstant.GOAL_TIME:
                    int goalTime = resolveData(data, RowerDataParam.GOAL_TIME_INX + 1, RowerDataParam.GOAL_TIME_LEN);
                    rowerDataBean1.setSetGoalTime(goalTime);
                    break;
                case MyConstant.GOAL_DISTANCE:
                    int goalDistance = resolveData(data, RowerDataParam.GOAL_DISTANCE_INX + 1, RowerDataParam.GOAL_DISTANCE_LEN);
                    rowerDataBean1.setSetGoalDistance(goalDistance);
                    break;
                case MyConstant.GOAL_CALORIES:
                    int goalCalorie = resolveData(data, RowerDataParam.GOAL_CALORIE_INX + 1, RowerDataParam.GOAL_CALORIE_LEN);
                    rowerDataBean1.setSetGoalCalorie(goalCalorie);
                    break;
            }
        } else if (MyConstant.isIntervalMode(runMode)) {
            switch (runMode) {
                case MyConstant.INTERVAL_TIME:
                    int intervalTime = resolveData(data, RowerDataParam.INTERVAL_TIME_INX + 1, RowerDataParam.INTERVAL_TIME_LEN);
                    rowerDataBean1.setSetIntervalTime(intervalTime);
                    break;
                case MyConstant.INTERVAL_DISTANCE:
                    int intervalDistance = resolveData(data, RowerDataParam.INTERVAL_DISTANCE_INX + 1, RowerDataParam.INTERVAL_DISTANCE_LEN);
                    rowerDataBean1.setSetIntervalDistance(intervalDistance);
                    break;
                case MyConstant.INTERVAL_CALORIES:
                    int intervalCalorie = resolveData(data, RowerDataParam.INTERVAL_CALORIE_INX + 1, RowerDataParam.INTERVAL_CALORIE_LEN);
                    rowerDataBean1.setSetIntervalCalorie(intervalCalorie);
                    break;
            }

            // 休息时间
            if (runMode == MyConstant.INTERVAL_DISTANCE) {
                RowerDataParam.INTERVAL_REST_TIME_INX = 10;
            } else {
                RowerDataParam.INTERVAL_REST_TIME_INX = 8;
            }
            int intervalRestTime = resolveData(data, RowerDataParam.INTERVAL_REST_TIME_INX, RowerDataParam.INTERVAL_REST_TIME_LEN);
            rowerDataBean1.setReset_time(intervalRestTime);
        }

        if (onRunDataListener != null) {
            onRunDataListener.onRunData(rowerDataBean1);
        }
    }

    private void setRunData_FFE0(byte[] data) {
        if (data.length < 6) {
            return;
        }

        // 校对CRC码 和 获取机种标识
        // ffe9   0xfe 0x40 0x01 0x21 0x11 0x25 0x10 0x4e 0xff
        // ffe0   0xfe 0x40 0x01 0x32 0x9a 0x01 0xa3 0x2b 0xff
        if (data.length > 8 && data[1] == 0x40 && data[2] == 0x01) {
            String[] dates = getCurDate().split("-");
            byte[] date = new byte[3];
            date[0] = (byte) Integer.parseInt(dates[0], 16);
            date[1] = (byte) Integer.parseInt(dates[1], 16);
            date[2] = (byte) Integer.parseInt(dates[2], 16);
            byte[] calCRCBytes = ConvertData.shortToBytes(SerialData.calCRCByTable(ConvertData.subBytes(date, 0, date.length), date.length));
            Logger.i("calCRCBytes[0] == " + ConvertData.toHexString(calCRCBytes[0]) + " calCRCBytes[1] == " + ConvertData.toHexString(calCRCBytes[1]));
            if (calCRCBytes[0] == data[3] && calCRCBytes[1] == data[4]) {
                isToExamine = true;
                isVerifyConnectTimer.closeTimer();
            }

            // 机种标识 当前是 0x01
            byte flag = data[5];
            SpManager.setTreadmill_flag(flag);
            return;
        }
        if (data[1] == 0x41 && data[2] == 0x02 && isToExamine) {
            sendRespondData(data);

            if (runStatus == RUN_STATUS_STOP || rowerDataBean1.getCanSave()) {
//                saveRowDataBean1();
            }

            if (runStatus == RUN_STATUS_RUNNING) {
                rowerDataBean1.setDrag(resolveData(data, RowerDataParam.DRAG_INX, RowerDataParam.DRAG_LEN));
                rowerDataBean1.setInterval(resolveData(data, RowerDataParam.INTERVAL_INX, RowerDataParam.INTERVAL_LEN));

                if (MyConstant.isIntervalMode(rowerDataBean1.getRunMode())) {
                    // 跳段时保存
                    if (rowerDataBean1.getInterval() <= tempInterval1) {
                        rowerDataBean2 = new RowerDataBean2(rowerDataBean1);
                    } else {
                        if (tempInterval1 >= 1) {
                            tempSave(rowerDataBean2);
                            Logger.e("间歇运动 " + rowerDataBean2.getInterval() + "   bean2.save " + rowerDataBean2);
                        }
                    }
                    tempInterval1 = rowerDataBean1.getInterval();
                } else if (MyConstant.isGoalMode(rowerDataBean1.getRunMode())) {
                    // 跳段时保存
                    if (rowerDataBean1.getRunInterval() <= tempInterval2) {
                        rowerDataBean2 = new RowerDataBean2(rowerDataBean1);
                    } else {
                        // 防止跳段时保存错误 12345 -> 23455
                        int runIntervalXXX = rowerDataBean2.getRunInterval();
                        rowerDataBean2 = new RowerDataBean2(rowerDataBean1);
                        rowerDataBean2.setRunInterval(runIntervalXXX);

                        tempSave(rowerDataBean2);
                        Logger.e("目标运动 " + rowerDataBean2.getRunInterval() + "    bean2.save " + rowerDataBean2);
                    }
                    tempInterval2 = rowerDataBean1.getRunInterval();
                }
            }

            if (onRunDataListener != null) {
                onRunDataListener.onRunData(rowerDataBean1);
            }
        }
    }

    private void saveRowDataBean1() {
        // TODO: 2021/11/12 应该判断是否符合保存  如 运动不足5秒，等

        if (rowerDataBean1.getRunMode() == MyConstant.GOAL_TIME) {
            // 时间是倒数的，用距离判断
            if (rowerDataBean1.getDistance() >= 10) {
                canSave = true;
            }
        } else if (rowerDataBean1.getRunMode() == MyConstant.INTERVAL_TIME) {
            if (rowerDataBean1.getInterval() > 0 || rowerDataBean1.getDistance() >= 10) {
                canSave = true;
            }
        } else if (rowerDataBean1.getRunMode() == MyConstant.INTERVAL_DISTANCE) {
            if (rowerDataBean1.getInterval() > 0 || rowerDataBean1.getTime() >= 5) {
                canSave = true;
            }
        } else if (rowerDataBean1.getRunMode() == MyConstant.INTERVAL_CALORIES) {
            if (rowerDataBean1.getInterval() > 0 || rowerDataBean1.getTime() >= 5) {
                canSave = true;
            }
        } else {
            if (rowerDataBean1.getTime() >= 5) {
                canSave = true;
            }
        }

        Logger.e("saveRowDataBean1() -- canSave " + canSave + "       getCanSave() --- " + rowerDataBean1.getCanSave());
        if (canSave && rowerDataBean1.getCanSave()) {
            Logger.e("1----RUN_STATUS_STOP----" + "bean1  save : " + rowerDataBean1);
            Logger.e("1----RUN_STATUS_STOP----" + "bean1.list: " + rowerDataBean1.getList());
            tempSave(rowerDataBean1);

            // TODO: 2021/11/17
            if (rowerDataBean2 != null) {
                int runIntervalXXX = rowerDataBean2.getRunInterval();
                rowerDataBean2 = new RowerDataBean2(rowerDataBean1);
                rowerDataBean2.setRunInterval(runIntervalXXX);
            } else {
                rowerDataBean2 = new RowerDataBean2(rowerDataBean1);
            }

            Logger.e("2----RUN_STATUS_STOP----" + "bean2  save : " + rowerDataBean2);

            // mode 0没有 bean2，会多出
            tempSave(rowerDataBean2);
            Logger.e("2----RUN_STATUS_STOP----" + "bean1.list: " + rowerDataBean1.getList());

            rowerDataBean1 = new RowerDataBean1();
            canSave = false;

            new Thread(() -> {
                Looper.prepare();
                new Handler().post(() -> {
                    ToastUtil.show(R.string.save_success, true, ToastUtil.Mode.REPLACEABLE);
                });
                Looper.loop();
            }).start();

            rowerDataBean1.setDrag(0);
            rowerDataBean1.setInterval(0);
            rowerDataBean1.setRunInterval(0);
            rowerDataBean1.setFlag(1);
            tempInterval1 = 0;
            tempInterval2 = 0;
        }
    }

    public static void tempSave(LitePalSupport support) {
/*        new Thread() {
            public void run() {
                Looper.prepare();
                new Handler().post(() -> {
                    ToastUtil.show("保存成功！", true, ToastUtil.Mode.REPLACEABLE);
                });//在子线程中直接去new 一个handler
                Looper.loop();    //这种情况下，Runnable对象是运行在子线程中的，可以进行联网操作，但是不能更新UI
            }
        }.start();*/
        support.save();
    }

    /*public void startSend() {
        rowerDataBean1 = new RowerDataBean1();

        // 2ada
        // 模拟
        isToExamine = true;
        // 2ada
        // 0x20 0x00 0x05 0x01 0x00
        byte[] data = new byte[5];
        data[0] = 0x20;
        data[1] = 0x00;
        data[2] = 0x05;
        data[3] = 0x01;
        data[4] = 0x00;

        byte[] finalData = data;
        new Thread(() -> {
            while (true) {
                rxDataPackage(finalData, "2ada");
                SystemClock.sleep(500);
            }
        }).start();


        // 2ad1  ftms
        // 0x7c 0x0b 0x92 0x53 0x02 0x5d 0x0a 0x00 0x5e 0x00 0x5e 0x00 0x93 0x01 0x93 0x01 0xeb 0x00 0x96 0x06 0x73 0x3c 0xb9 0x02
        byte[] data2 = {0x7c, 0x0b, 0x70, 0x53, 0x02, 0x5d, 0x0a, 0x00, 0x5e, 0x00, 0x5e, 0x00, 0x70, 0x01, 0x70, 0x01, 0x70, 0x00, 0x70, 0x06, 0x73, 0x3c, 0x70, 0x02};
        new Thread(() -> {
            while (true) {
                rxDataPackage(data2, "2ad1");
                SystemClock.sleep(500);
            }
        }).start();

        byte[] data3 = {0x16, 0x75, 0x33, 0x01, 0x0b, 0x01};
        isHeartbeatConnect = true;
        new Thread(() -> {
            while (true) {
                setHrData(data3);
                SystemClock.sleep(500);
            }
        }).start();
    }*/

    public void startThread() {
        rowerDataBean1 = new RowerDataBean1();

        new Thread(() -> {
            while (true) {
                rowerDataBean1.setDistance((long) (Math.random() * 66 + 1));
                rowerDataBean1.setTime(new Date().getTime());
                rowerDataBean1.setCalorie((long) (Math.random() * 10 + 1));
                onRunDataListener.onRunData(rowerDataBean1);
                SystemClock.sleep(1000);
            }
        }).start();

    }
}
