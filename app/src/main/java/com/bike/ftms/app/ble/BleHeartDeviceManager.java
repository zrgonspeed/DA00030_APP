package com.bike.ftms.app.ble;

import static com.bike.ftms.app.utils.DataTypeConversion.resolveData;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.os.Handler;
import android.os.Looper;

import com.bike.ftms.app.base.MyApplication;
import com.bike.ftms.app.ble.base.OnRunDataListener;
import com.bike.ftms.app.ble.base.OnScanConnectListener;
import com.bike.ftms.app.ble.bean.MyScanResult;
import com.bike.ftms.app.ble.bean.rundata.raw.RowerDataBean1;
import com.bike.ftms.app.ble.help.UuidHelp;
import com.bike.ftms.app.common.RowerDataParam;
import com.bike.ftms.app.utils.ConvertData;
import com.bike.ftms.app.utils.CustomTimer;
import com.bike.ftms.app.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import dev.xesam.android.toolbox.timer.CountDownTimer;

@SuppressLint("MissingPermission")
public class BleHeartDeviceManager extends BaseBleManager implements BleHeartDevice, CustomTimer.TimerCallBack {
    private static BleHeartDeviceManager instance;
    public final String uuidHeartbeat = "0000180d-0000-1000-8000-00805f9b34fb"; // 标准服务： 180d
    /**
     * https://www.bluetooth.com/zh-cn/specifications/assigned-numbers/   GATT Specification Supplement
     * <p>
     * 2a37    Heart Rate Measurement	心率测量
     * 0x16 0x6c 0xda 0x03
     * 0x16 0x63 0x8d 0x03 0xeb 0x01
     * 0x16 0x72 0x3f 0x01 0xb7 0x01 0xc3 0x01
     * 0x16 0x75 0x33 0x01 0x0b 0x01
     * heart_rate=117	(0x75)
     */
    /**
     * 2a38  Body Sensor Location  人体感应器位置
     */
    public static BleHeartDeviceManager getInstance() {
        if (instance == null) {
            synchronized (BleHeartDeviceManager.class) {
                if (instance == null) {
                    instance = new BleHeartDeviceManager();
                }
            }
        }
        return instance;
    }

    private BleHeartDeviceManager() {
    }

    private RowerDataBean1 rowerDataBean1 = new RowerDataBean1();

    // 心率设备
    private BluetoothGatt mBluetoothGatt;       //连接蓝牙、及操作
    private MyScanResult connectScanResult;
    private CustomTimer isConnectTimer;
    private final String isConnectTag = "isConnect";
    private short heart_rate = 0;   // 腰带心跳值，需要传给电子表
    public static boolean isConnect;//是否连接蓝牙腰带
    public static boolean isCanning;  //是否正在扫描
    public static boolean isOpen;     //是否打开定位及蓝牙

    private BluetoothAdapter mBluetoothAdapter; //系统蓝牙适配器
    private OnScanConnectListener onScanConnectListener;      //扫描回调
    private List<MyScanResult> mScanResults = new ArrayList<>();      //扫描到的蓝牙设备
    private List<BluetoothGattService> mBluetoothGattServices;//服务，Characteristic(特征) 的集合。
    private BluetoothGattCharacteristic mBluetoothGattCharacteristic;//特征值(用于收发数据)   当前是ffe9发
    private OnRunDataListener onRunDataListener;//运动数据回调

    public OnRunDataListener getOnRunDataListener() {
        return onRunDataListener;
    }

    private static final long SCAN_MAX_COUNT = 20;     //扫描的设备个数限制（停止扫描）
    public static long SCAN_PERIOD = 60 * 1000;     //扫描设备时间限制
    private final long SCAN_PERIOD_INTERVAL = 1000;     //隔多久回调1次
    private static final long START_SCAN_DELAY_TIME = 3000; // 扫描设备延迟时间

    private final Handler mHandler = new Handler(Looper.getMainLooper());


    /**
     * 搜索设备时间计时
     */
    private CountDownTimer countDownTimer;

    private BleManager.CountDownTime countDownTime;


    /**
     * 蓝牙打开与关闭回调 -> BluetoothActivity
     */
    private BleManager.BleOpenCallBack bleOpenCallBack;
    private BleManager.BleClosedCallBack bleClosedCallBack;

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

    public void setBleOpenCallBack(BleManager.BleOpenCallBack bleOpenCallBack) {
        this.bleOpenCallBack = bleOpenCallBack;
    }

    public void setBleClosedCallBack(BleManager.BleClosedCallBack bleClosedCallBack) {
        this.bleClosedCallBack = bleClosedCallBack;
    }

    public void setCountDownTime(BleManager.CountDownTime countDownTime) {
        this.countDownTime = countDownTime;
    }


    /**
     * 扫描回调 -> BluetoothActivity
     */
    public void setOnScanConnectListener(OnScanConnectListener onScanConnectListener) {
        this.onScanConnectListener = onScanConnectListener;
    }

    /**
     * 运动数据回调
     */
    public void setOnRunDataListener(OnRunDataListener onRunDataListener) {
        this.onRunDataListener = onRunDataListener;
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
        Logger.i("scanDevice()");

        Logger.e("mBluetoothAdapter == " + mBluetoothAdapter + "    isCanning == " + isCanning);
        if (mBluetoothAdapter != null && !isCanning) {
            boolean enabled = mBluetoothAdapter.isEnabled();
            Logger.i("2 enabled == " + enabled);
            if (!enabled) {
                boolean enable = mBluetoothAdapter.enable();
                Logger.e("2_1 enable == " + enable);
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
            mBluetoothAdapter.getBluetoothLeScanner().startScan(scanFilters, scanSettings, mScanCallback);
            Logger.i("1------开始扫描设备" );
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
        Logger.i("getScanResults(): " + getScanResults().size());

        if (getScanResults().get(position).getConnectState() == 1) {
            disableCharacterNotifiy();
            disConnectDevice();
            Logger.e("2------disConnectDevice()");
            BleManager.getInstance().mPosition = -1;

            if (onScanConnectListener != null) {
                onScanConnectListener.onNotifyData();
            }
            return;
        }
        if (getScanResults() != null && getScanResults().size() != 0) {
            if (position >= 0 && position < getScanResults().size()) {
                getScanResults().get(position).setConnectState(2);
                BluetoothDevice device = getScanResults().get(position).getScanResult().getDevice();
                // rowerDataBean1 = new RowerDataBean1();
                // connectScanResult = new MyScanResult(getScanResults().get(position).getScanResult(), 2);
                // 
                // //第二个参数表示是否需要自动连接。如果设置为 true, 表示如果设备断开了，会不断的尝试自动连接。设置为 false 表示只进行一次连接尝试。
                // mBluetoothGatt = device.connectGatt(MyApplication.getContext(), false, mGattCallback);
                // boolean b = refreshDeviceCache(mBluetoothGatt);
                // Logger.i("清除蓝牙内部缓存 " + b);
                //
                // //处理超时连接的方法
                // // mHandler.postDelayed(mConnTimeOutRunnable, 5 * 1000);

                connectScanResult = new MyScanResult(getScanResults().get(position).getScanResult(), 2);
                mBluetoothGatt = device.connectGatt(MyApplication.getContext(), false, mGattCallback);

                Logger.i("connectDevice " + device.getAddress());
            }
        }
        if (onScanConnectListener != null) {
            onScanConnectListener.onNotifyData();
        }
    }


    /**
     * 禁用特征值通知
     */
    public void disableCharacterNotifiy() {
        UuidHelp.disableCharacterNotifiy(mBluetoothGatt, mBluetoothGattServices);
    }

    /**
     * 断开蓝牙设备
     */
    public void disConnectDevice() {
        Logger.e("disConnectDevice()");
        resetDeviceType();
        isConnect = false;
        if (mBluetoothGatt != null) {
            Logger.e("断开心跳设备");
            mBluetoothGatt.disconnect();
//            mBluetoothGatt = null;
        }
    }

    private void resetDeviceType() {
        isConnect = false;

        if (onRunDataListener != null) {
            onRunDataListener.disConnect();
        }
    }

    /**
     * APP退出时
     */
    public void destroy() {
        mBluetoothGatt = null;

        bleClosedCallBack = null;
        bleOpenCallBack = null;

        onRunDataListener = null;
        onScanConnectListener = null;
//        mScanCallback = null;

//        instance = null;
    }


    @Override
    public boolean isConnected() {
        return isConnect;
    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public int getHeartInt() {
        return 0;
    }


    /**
     * 心率设备连接回调
     */
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

            if (status != BluetoothGatt.GATT_SUCCESS) {
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
                for (MyScanResult myScanResult : mScanResults) {
                    if (myScanResult.getScanResult().getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                        myScanResult.setConnectState(1);
                        startTimerOfisConnect();
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
                    mBluetoothGatt.discoverServices();
                }
                Logger.e("isConnect=" + isConnect + ",isHeartbeatConnect=" + isConnect);
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
            Logger.i("Hr onServicesDiscovered status=" + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mBluetoothGattServices = mBluetoothGatt.getServices();
                BluetoothGattService localGattService = mBluetoothGatt.getService(UUID.fromString(uuidHeartbeat));
                List<BluetoothGattCharacteristic> list = new ArrayList<>();
                if (localGattService != null) {
                    list = localGattService.getCharacteristics();
                }
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getUuid().toString().contains("2a37")) {
                        List<BluetoothGattDescriptor> bluetoothGattDescriptors = list.get(i).getDescriptors();
                        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptors) {
                            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
                            Logger.d("bluetoothGattDescriptor" + bluetoothGattDescriptor.getUuid());
                        }
                    }
                    if (list.get(i).getUuid().toString().contains("2a38")) {
                        List<BluetoothGattDescriptor> bluetoothGattDescriptors = list.get(i).getDescriptors();
                        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptors) {
                            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            boolean r = mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
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
            isConnectTimer.setmAllTime(0L);
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
     */
    private void registrationGattCharacteristic() {
        if (mBluetoothGattServices != null) {
            BluetoothGattService gattService = mBluetoothGatt.getService(UUID.fromString(uuidHeartbeat));
            if (gattService != null) {
                List<BluetoothGattCharacteristic> list = gattService.getCharacteristics();
                UuidHelp.setCharacterNotification(mBluetoothGatt, list, "2a37");
                UuidHelp.setCharacterNotification(mBluetoothGatt, list, "2a38");
            }
        }
    }

    /**
     * 释放资源
     */
    public void whenBTClosed() {
        // 蓝牙关闭后的操作
        isOpen = false;
        isCanning = false;
        resetDeviceType();
        isConnect = false;

    }

    /**
     * 打开BLE  外部调用
     */
    public void openBLE(BleManager.BleOpenCallBack bleOpenCallBack) {
        this.bleOpenCallBack = bleOpenCallBack;
        boolean enabled = mBluetoothAdapter.isEnabled();
        Logger.i("enable == " + enabled);
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
                Logger.i("mBluetoothAdapter.enable()");
                Logger.i("isOpen == " + isOpen);

                if (!isOpen) {
                    isOpen = mBluetoothAdapter.isEnabled();
                }
                Logger.i("isOpen == " + isOpen);

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
     * 关闭BLE 外部调用
     */
    public void closeBLE(BleManager.BleClosedCallBack bleClosedCallBack) {
        this.bleClosedCallBack = bleClosedCallBack;

        if (mBluetoothAdapter != null) {
            new Thread(() -> {

                BleManager.getInstance().stopScan();
                BleManager.getInstance().disableCharacterNotifiy();
                BleManager.getInstance().disConnectDevice();
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


    /**
     * 心跳数据  0x16 -> 0001 0110  从右往左
     * 0心跳值是1字节 还是2字节       0
     * 1检测到传感器接触            1
     * 2支持传感器触点             1
     * 3目前消耗的能量：            0
     * 4存在的 RR 间隔：              1
     * 5–7 保留供将来使用          000
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
        if ("0".contentEquals(s.subSequence(7, 8))) {
            rowerDataBean1.setHeart_rate(ConvertData.byteToInt(data[1]));
        } else {
            rowerDataBean1.setHeart_rate(resolveData(data, 1, 2));
        }

        heart_rate = (short) rowerDataBean1.getHeart_rate();
        if (onRunDataListener != null) {
            onRunDataListener.onRunData(rowerDataBean1);
        }
    }

    private void startTimerOfisConnect() {
        if (isConnectTimer == null) {
            isConnectTimer = new CustomTimer();
        }
        isConnectTimer.closeTimer();
        isConnectTimer.setTag(isConnectTag);
        isConnectTimer.startTimer(1000, 1000, this);
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
    }

    /**
     * 接收设备数据
     */
    private void rxDataPackage(byte[] data, String uuid) {
        // 单独显示电子表的心跳
        rowerDataBean1.setHeart_rate(RowerDataParam.HEART_RATE_INX == -1 ? 0 : resolveData(data, RowerDataParam.HEART_RATE_INX, RowerDataParam.HEART_RATE_LEN));
        if (onRunDataListener != null) {
            onRunDataListener.onRunData(rowerDataBean1);
        }
    }
}
