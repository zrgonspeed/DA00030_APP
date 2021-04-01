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
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;

import com.bike.ftms.app.base.MyApplication;
import com.bike.ftms.app.bean.FormatBean;
import com.bike.ftms.app.util.Logger;
import com.bike.ftms.app.utils.ByteArrTransUtil;
import com.bike.ftms.app.utils.ConvertData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BleManager {
    private String TAG = "BleManager";
    private static BleManager instance;
    /**
     * 00002acc-0000-1000-8000-00805f9b34fb
     * 00002acd-0000-1000-8000-00805f9b34fb
     * 00002ace-0000-1000-8000-00805f9b34fb
     * 00002acf-0000-1000-8000-00805f9b34fb
     * 00002ad0-0000-1000-8000-00805f9b34fb
     * 00002ad1-0000-1000-8000-00805f9b34fb
     * 00002ad2-0000-1000-8000-00805f9b34fb
     * 00002ad3-0000-1000-8000-00805f9b34fb
     * 00002ad4-0000-1000-8000-00805f9b34fb
     * 00002ad5-0000-1000-8000-00805f9b34fb
     * 00002ad6-0000-1000-8000-00805f9b34fb
     * 00002ad8-0000-1000-8000-00805f9b34fb
     * 00002ad7-0000-1000-8000-00805f9b34fb
     * 00002ad9-0000-1000-8000-00805f9b34fb
     * 00002ada-0000-1000-8000-00805f9b34fb
     * d18d2c10-c44c-11e8-a355-529269fb1459
     */
    public final String[] UUID_LIST = {
            "6e400001-b5a3-f393-e0a9-e50e24dcca9e;6e400003-b5a3-f393-e0a9-e50e24dcca9e;6e400002-b5a3-f393-e0a9-e50e24dcca9e",
            "0000ffe5-0000-1000-8000-00805f9b34fb;0000ffe0-0000-1000-8000-00805f9b34fb;0000ffe9-0000-1000-8000-00805f9b34fb"
    };

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

    public static boolean isConnect;  //是否连接
    public static boolean isCanning;  //是否正在扫描
    public static boolean isOpen;     //是否打开定位及蓝牙

    private OnScanConnectListener onScanConnectListener;      //扫描回调
    private BluetoothAdapter mBluetoothAdapter; //系统蓝牙适配器
    private List<ScanResult> mScanResults;      //扫描到的蓝牙设备
    private BluetoothGatt mBluetoothGatt;       //连接蓝牙、及操作
    private List<BluetoothGattService> mBluetoothGattServices;//服务，Characteristic(特征) 的集合。
    private BluetoothGattCharacteristic mBluetoothGattCharacteristic;//特征值(用于收发数据)

    private final long SCAN_MAX_COUNT = 15;     //扫描的设备个数限制（停止扫描）
    private final long SCAN_PERIOD = 60000;     //扫描设备时间限制

    private Handler mHandler = new Handler(Objects.requireNonNull(Looper.myLooper()));

    /**
     * 扫描回调
     *
     * @param onScanConnectListener
     */
    public void setOnScanConnectListener(OnScanConnectListener onScanConnectListener) {
        this.onScanConnectListener = onScanConnectListener;
    }

    /**
     * @return 扫描到的设备
     */
    public List<ScanResult> getScanResults() {
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
        return mBluetoothAdapter;
    }

    /**
     * @return 连接的设备
     */
    public BluetoothGatt getBluetoothGatt() {
        return mBluetoothGatt;
    }

    /**
     * @return //服务，Characteristic(特征) 的集合。
     */
    public List<BluetoothGattService> getBluetoothGattServices() {
        return mBluetoothGattServices;
    }

    /**
     * @return 特征值
     */
    public BluetoothGattCharacteristic getBluetoothGattCharacteristic() {
        return mBluetoothGattCharacteristic;
    }

    /**
     * 扫描蓝牙设备
     */
    public void scanDevice() {
        if (mBluetoothAdapter != null && !isCanning) {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scanDevice();
                    }
                }, 3000);
                return;
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScan();
                    if (mScanResults.size() == 0) {//没有搜索到设备
                        Logger.d("No devices were found");
                    }
                }
            }, SCAN_PERIOD);
            isCanning = true;
            //if (!isConnect) {
            mScanResults.clear();
           /* } else {
                for (ScanResult scanResult : mScanResults) {
                    if (!scanResult.getDevice().getName().equals(mBluetoothGatt.getDevice().getName())) {
                        mScanResults.remove(scanResult);
                    }
                }
            }*/
            if (onScanConnectListener != null) {
                onScanConnectListener.onScanSuccess();
            }
            //startLeScan(UUID[], BluetoothAdapter.LeScanCallback)  //设置过滤条件：特定类型的uuid

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
            ScanFilter scanFilter = new ScanFilter.Builder().setServiceUuid(
                    new ParcelUuid(UUID.fromString("00001826-0000-1000-8000-00805f9b34fb"))).build();
            scanFilters.add(scanFilter);
            mBluetoothAdapter.getBluetoothLeScanner().startScan(scanFilters, scanSettings, mScanCallback);
            //mBluetoothAdapter.getBluetoothLeScanner().startScan(mScanCallback);
            Logger.i(TAG, "开始扫描设备");
        }
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        if (mBluetoothAdapter != null && isCanning) {
            isCanning = false;
            mHandler.removeCallbacksAndMessages(null);
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
            if (onScanConnectListener != null) {
                onScanConnectListener.onStopScan();
            }
            Logger.i(TAG, "停止扫描设备");
        }
    }

    /**
     * 扫描结果回调
     */
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            //Logger.i(TAG, "onScanResult:" + result.getDevice().getName());
           /* Logger.i(TAG, "onScanResult" + result.getDevice().toString());          //D5:EA:80:77:23:39
            Logger.i(TAG, "onScanResult" + result.getDevice().getUuids());            //null
            Logger.i(TAG, "onScanResult" + result.getDevice().getType());             //2
            Logger.i(TAG, "onScanResult" + result.getDevice().getName());             //PZ-PA051BA
            Logger.i(TAG, "onScanResult" + result.getDevice().getAddress());          //D5:EA:80:77:23:39
            Logger.i(TAG, "onScanResult" + result.getDevice().getBluetoothClass());   //1f00
            Logger.i(TAG, "onScanResult" + result.getDevice().getAlias());            //null
            Logger.i(TAG, "onScanResult" + result.getDevice().getBondState());        //10
            Logger.i(TAG, "onScanResult" + result.toString());
            //设备广播（ScanRecord）
            //result.getScanRecord().getServiceUuids()   mServiceUuids=[0000ab00-0000-1000-8000-00805f9b34fb]*/
            String deviceName = result.getDevice().getName();
            if (deviceName != null) {
                boolean isAdd = true;//第一次无需查重
                for (int i = 0; i < getScanResults().size(); i++) {//查重
                    if (getScanResults().get(i).getDevice().getName().equals(deviceName)) {
                        isAdd = false;
                    }
                }
                if (isAdd) {
                    getScanResults().add(result);
                    if (onScanConnectListener != null) {
                        onScanConnectListener.onScanSuccess();
                    }
                    if (getScanResults().size() >= SCAN_MAX_COUNT) { //达到限制后停止扫描
                        stopScan();
                    }
                }

            }

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Logger.i(TAG, "onBatchScanResults");
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Logger.i(TAG, "onScanFailed" + errorCode);
        }

    };

    /**
     * 连接蓝牙设备
     *
     * @param position
     */
    public void connectDevice(int position) {
        if (getScanResults() != null && getScanResults().size() != 0) {
            if (position >= 0 && position < getScanResults().size()) {
                //第二个参数表示是否需要自动连接。如果设置为 true, 表示如果设备断开了，会不断的尝试自动连接。设置为 false 表示只进行一次连接尝试。
                mBluetoothGatt = getScanResults().get(position).getDevice()
                        .connectGatt(MyApplication.getContext(), false, mGattCallback);
              /*  BluetoothGattService bluetoothGattService=  mBluetoothGatt.getService(UUID.fromString("00001826-0000-1000-8000-00805f9b34fb"));
                Logger.i(TAG, "onScanResult:" + getScanResults().get(position).getDevice().getName()+", getUuids=" + getScanResults().get(position).getScanRecord().getServiceUuids()+
                        ",mBluetoothGatt"+bluetoothGattService.getCharacteristics().toString());*/
                Logger.i(TAG, "connectDevice" + position);
            }
        }

    }

    /**
     * 断开蓝牙设备
     *
     * @param position
     */
    public void disConnectDevice(int position) {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            Logger.i(TAG, "disConnectDevice" + position);
        }
    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
            Logger.i(TAG, "onPhyUpdate");
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
            Logger.i(TAG, "onPhyRead");
        }

        //当连接状态发生改变
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Logger.i(TAG, "onConnectionStateChange" + status);
            Logger.i(TAG, "onConnectionStateChange" + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                isConnect = true;
                if (onScanConnectListener != null) {
                    onScanConnectListener.onConnectEvent(true, "CONNECTED");
                }
                Logger.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());
                // mBluetoothGatt.discoverServices();//
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                isConnect = false;
                if (onScanConnectListener != null) {
                    onScanConnectListener.onConnectEvent(false, "DISCONNECTED");
                }
            }
        }

        //发现新服务，即调用了mBluetoothGatt.discoverServices()后，返回的数据  (读取 BLE 属性)
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Logger.i(TAG, "onServicesDiscovered");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //mBluetoothGatt.getServices().size()==gatt.getServices().size()
                Logger.i(TAG, "mBluetoothGatt.getServices()::" + mBluetoothGatt.getServices().size());
                mBluetoothGattServices = mBluetoothGatt.getServices();
                BluetoothGattService localGattService = mBluetoothGatt.getService(UUID.fromString("00001826-0000-1000-8000-00805f9b34fb"));
                List<BluetoothGattCharacteristic> list = localGattService.getCharacteristics();
                for (int i = 0; i < list.size(); i++) {
                    Logger.d(TAG, "getCharacteristics=" + list.get(i).getUuid());
                    List<BluetoothGattDescriptor> bluetoothGattDescriptors = list.get(i).getDescriptors();
                    for (BluetoothGattDescriptor bluetoothGattDescriptor:bluetoothGattDescriptors) {
                       /* bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);*/
                        Logger.d(TAG, "getDescriptors=" + bluetoothGattDescriptor.getUuid());
                    }

                }
                registrationGattCharacteristic();//注册通知
            } else {
                Logger.d(TAG, "onServicesDiscovered received: " + status);
            }
        }

        //发送数据后的回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Logger.i(TAG, "onCharacteristicWrite::" + ByteArrTransUtil.toHexValue(characteristic.getValue()));
            if (status == BluetoothGatt.GATT_SUCCESS)
                mHandler.removeCallbacksAndMessages(null);
            Logger.i(TAG, "onCharacteristicWrite::" + "removeCallbacksAndMessages");
        }

        //调用mBluetoothGatt.readCharacteristic(characteristic)读取数据回调，在这里面接收数据
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Logger.i(TAG, "onCharacteristicRead::" + ConvertData.byteArrayToHexString(characteristic.getValue(), characteristic.getValue().length));

        }

        //特征值的通知回调(异步，远程设备上的特征发生更改时回调)
        // (需要设置特征的通知：bluetoothGatt.setCharacteristicNotification(characteristic, enabled))
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            // sendEvent(new FormatBean(characteristic.getValue()));  //向activity发送数据
            Logger.i(TAG, "onCharacteristicChanged::" + ByteArrTransUtil.toHexValue(characteristic.getValue()));
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {//descriptor读
            super.onDescriptorRead(gatt, descriptor, status);
            Logger.i(TAG, "onDescriptorRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {//descriptor写
            super.onDescriptorWrite(gatt, descriptor, status);
            Logger.i(TAG, "onDescriptorWrite");
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            Logger.i(TAG, "onReliableWriteCompleted");
        }

        //调用mBluetoothGatt.readRemoteRssi()时的回调，rssi即信号强度
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {//读Rssi
            super.onReadRemoteRssi(gatt, rssi, status);
            Logger.i(TAG, "onReadRemoteRssi");
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Logger.i(TAG, "onMtuChanged");
        }
    };

    /**
     * 注册特征GATT 通知
     * ab01//发送通道
     * ab02//接收通道
     */
    private void registrationGattCharacteristic() {
        if (mBluetoothGattServices != null) {
            BluetoothGattService gattService = mBluetoothGatt.getService(UUID.fromString("00001826-0000-1000-8000-00805f9b34fb"));
            //for (BluetoothGattService gattService : mBluetoothGattServices) {
            for (BluetoothGattCharacteristic gattCharacteristic : gattService.getCharacteristics()) {
                //除了通过 BluetoothGatt#setCharacteristicNotification 开启 Android 端接收通知的开关，
                // 还需要往 Characteristic 的 Descriptor 属性写入开启通知的数据开关使得当硬件的数据改变时，主动往手机发送数据。
               /* if (gattCharacteristic.getUuid().toString().contains("ab01")) {//发送通道
                    mBluetoothGattCharacteristic = gattCharacteristic;
                    Logger.i(TAG, "发送通道::ab01");
                }*/
                if (gattCharacteristic.getUuid().toString().contains("2acd")) {//接收通道
                    boolean enabled = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                    Logger.i(TAG, "注册通知::" + enabled);
                }
                if (gattCharacteristic.getUuid().toString().contains("2ace")) {//接收通道
                    boolean enabled = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                    Logger.i(TAG, "注册通知::" + enabled);
                }
                if (gattCharacteristic.getUuid().toString().contains("2ad1")) {//接收通道
                    boolean enabled = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                    Logger.i(TAG, "注册通知::" + enabled);
                }
                if (gattCharacteristic.getUuid().toString().contains("2ad2")) {//接收通道
                    boolean enabled = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                    Logger.i(TAG, "注册通知::" + enabled);
                }
                if (gattCharacteristic.getUuid().toString().contains("2ad3")) {//接收通道
                    boolean enabled = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                    Logger.i(TAG, "注册通知::" + enabled);
                }
                if (gattCharacteristic.getUuid().toString().contains("2ada")) {//接收通道
                    boolean enabled = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                    Logger.i(TAG, "注册通知::" + enabled);
                }

                if (gattCharacteristic.getUuid().toString().contains("2ad6")) {
                    boolean enabled = mBluetoothGatt.readCharacteristic(gattCharacteristic);
                    Logger.i(TAG, "读::" + enabled);
                }
               /* if (gattCharacteristic.getUuid().toString().contains("2ad3")) {//接收通道
                    boolean enabled = mBluetoothGatt.readCharacteristic(gattCharacteristic);
                    Logger.i(TAG, "读::" + enabled);
                }*/
                Logger.i(TAG, "GattCharacteristic" + gattCharacteristic.getUuid().toString());
                    /*00002a00-0000-1000-8000-00805f9b34fb
                    0000ab01-0000-1000-8000-00805f9b34fb
                    0000ab02-0000-1000-8000-00805f9b34fb
                    0000ab03-0000-1000-8000-00805f9b34fb*/
                //  }
            }

        }
    }

    private int again = 3;

    /**
     * 发送指令数据
     *
     * @param bean 指令类
     */
    public void sendData(FormatBean bean) {
        if (mBluetoothGattCharacteristic == null) return;
        again = 3;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (again > 0) {
                    sendByte(bean.toByteArray());
                    again--;
                    mHandler.postDelayed(this, 2000);
                } else {
                    mHandler.removeCallbacksAndMessages(null);
                    Logger.d(TAG, "Device is unresponsive");
                    //sendEvent(new FormatBean(characteristic.getValue()));  //向activity发送数据
                }

            }
        });
    }

    /**
     * 发送数据
     *
     * @param bytes 指令
     */
    private void sendByte(byte[] bytes) {

        if (mBluetoothGattCharacteristic != null) {
            mBluetoothGattCharacteristic.setValue(bytes);
            mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
        }
    }

    /**
     * 向activity发送接收到的蓝牙数据
     *
     * @param bean 接收到的蓝牙数据
     */
  /*  private void sendEvent(FormatBean bean) {
        EventBus.getDefault().post(bean);
    }*/

    /**
     * 释放资源
     */
    public void close() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }

    }

    /**
     * 打开BLE
     */
    public void openBLE() {
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            isOpen = mBluetoothAdapter.enable();
        }
    }

    /**
     * 关闭BLE
     */
    public void closeBLE() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.disable();
        }
    }
}
