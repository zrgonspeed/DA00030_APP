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
import android.util.Log;

import com.bike.ftms.app.R;
import com.bike.ftms.app.base.MyApplication;
import com.bike.ftms.app.bean.FormatBean;
import com.bike.ftms.app.bean.RowerDataBean;
import com.bike.ftms.app.common.RowerDataParam;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.utils.ByteArrTransUtil;
import com.bike.ftms.app.utils.ConvertData;
import com.bike.ftms.app.utils.DataTypeConversion;
import com.bike.ftms.app.utils.TimeStringUtil;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BleManager {
    private String TAG = "BleManager";
    private static BleManager instance;
    public final String uuid = "00001826-0000-1000-8000-00805f9b34fb";
    public final String uuidHeartbeat = "0000180d-0000-1000-8000-00805f9b34fb";

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
    private boolean isHeartbeatConnect;//是否连接蓝牙腰带
    public static boolean isCanning;  //是否正在扫描
    public static boolean isOpen;     //是否打开定位及蓝牙

    private OnScanConnectListener onScanConnectListener;      //扫描回调
    private BluetoothAdapter mBluetoothAdapter; //系统蓝牙适配器
    private List<ScanResult> mScanResults;      //扫描到的蓝牙设备
    private BluetoothGatt mBluetoothGatt;       //连接蓝牙、及操作
    private BluetoothGatt mBluetoothHrGatt;       //连接蓝牙、及操作
    private List<BluetoothGattService> mBluetoothGattServices;//服务，Characteristic(特征) 的集合。
    private BluetoothGattCharacteristic mBluetoothGattCharacteristic;//特征值(用于收发数据)
    private OnRunDataListener onRunDataListener;//运动数据回调

    private final long SCAN_MAX_COUNT = 20;     //扫描的设备个数限制（停止扫描）
    private final long SCAN_PERIOD = 60000;     //扫描设备时间限制
    private boolean setBleDataInx = false;
    private boolean isToExamine = true;
    private boolean isScanHrDevice = false;//是否扫描腰带设备

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
     * 运动数据回调
     *
     * @param onRunDataListener
     */
    public void setonRunDataListener(OnRunDataListener onRunDataListener) {
        this.onRunDataListener = onRunDataListener;
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
                        Logger.d(TAG, "No devices were found");
                    }
                }
            }, SCAN_PERIOD);
            isCanning = true;
            mScanResults.clear();
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
                scanFilter = new ScanFilter.Builder().setServiceUuid(
                        new ParcelUuid(UUID.fromString(uuidHeartbeat))).build();
            } else {
                scanFilter = new ScanFilter.Builder().setServiceUuid(
                        new ParcelUuid(UUID.fromString(uuid))).build();
            }

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
            String deviceAddress = result.getDevice().getAddress();
            String deviceName = result.getDevice().getName();
            if (deviceName != null && deviceAddress != null) {
                boolean isAdd = true;//第一次无需查重
                for (int i = 0; i < getScanResults().size(); i++) {//查重
                    if (getScanResults().get(i).getDevice().getAddress().equals(deviceAddress)) {
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
        disConnectDevice();

        if (getScanResults() != null && getScanResults().size() != 0) {
            if (position >= 0 && position < getScanResults().size()) {
                if (!isScanHrDevice) {
                    setBleDataInx = false;
                    //第二个参数表示是否需要自动连接。如果设置为 true, 表示如果设备断开了，会不断的尝试自动连接。设置为 false 表示只进行一次连接尝试。
                    mBluetoothGatt = getScanResults().get(position).getDevice()
                            .connectGatt(MyApplication.getContext(), true, mGattCallback);
                } else {
                    mBluetoothHrGatt = getScanResults().get(position).getDevice()
                            .connectGatt(MyApplication.getContext(), true, mGattCallback);
                }

                Logger.i(TAG, "connectDevice" + getScanResults().get(position).getDevice().getAddress());
            }
        }

    }

    /**
     * 断开蓝牙设备
     */
    public void disConnectDevice() {
        if (mBluetoothGatt != null && !isScanHrDevice) {
            mBluetoothGatt.disconnect();
        }
        if (mBluetoothHrGatt != null && isScanHrDevice) {
            mBluetoothHrGatt.disconnect();
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
            Logger.i(TAG, "onConnectionStateChange status " + status);
            Logger.i(TAG, "onConnectionStateChange newState " + newState);
            Logger.i(TAG, "onConnectionStateChange " + gatt.getDevice().getName());
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (!isScanHrDevice) {
                    isConnect = true;
                    mBluetoothGatt.discoverServices();
                }else  {
                    isHeartbeatConnect = true;
                    mBluetoothHrGatt.discoverServices();
                }
                Logger.d("isConnect="+isConnect+",isHeartbeatConnect="+isHeartbeatConnect);
                if (onScanConnectListener != null) {
                    onScanConnectListener.onConnectEvent(true, gatt.getDevice().getName());
                }
                // mBluetoothGatt.discoverServices();//
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (gatt.getService(UUID.fromString(uuid)) != null) {
                    isConnect = false;
                }
                if (gatt.getService(UUID.fromString(uuidHeartbeat)) != null) {
                    isHeartbeatConnect = false;
                }
                if (onScanConnectListener != null) {
                    onScanConnectListener.onConnectEvent(false, gatt.getDevice().getName());
                }
            }
        }

        //发现新服务，即调用了mBluetoothGatt.discoverServices()后，返回的数据  (读取 BLE 属性)
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Logger.i(TAG, "onServicesDiscovered status=" + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //mBluetoothGatt.getServices().size()==gatt.getServices().size()
                Logger.i(TAG, "mBluetoothGatt.getServices()::" + mBluetoothGatt.getServices().size());
                BluetoothGattService localGattService;
                if (!isScanHrDevice){
                    mBluetoothGattServices = mBluetoothGatt.getServices();
                    localGattService = mBluetoothGatt.getService(UUID.fromString(uuid));
                }else {
                    mBluetoothGattServices = mBluetoothHrGatt.getServices();
                     localGattService = mBluetoothHrGatt.getService(UUID.fromString(uuidHeartbeat));
                }
                List<BluetoothGattCharacteristic> list = new ArrayList<>();
                if (localGattService != null) {
                    list = localGattService.getCharacteristics();
                }
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getUuid().toString().contains("2ad1")) {
                        List<BluetoothGattDescriptor> bluetoothGattDescriptors = list.get(i).getDescriptors();
                        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptors) {
                            boolean r = bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
                            Logger.d(TAG, bluetoothGattDescriptor.getUuid().toString() + ",bluetoothGattDescriptor " + r);
                        }
                    }
                    if (list.get(i).getUuid().toString().contains("2ad9")) {
                        List<BluetoothGattDescriptor> bluetoothGattDescriptors = list.get(i).getDescriptors();
                        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptors) {
                            boolean r = bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
                            Logger.d(TAG, bluetoothGattDescriptor.getUuid().toString() + ",bluetoothGattDescriptor " + r);
                        }
                    }
                    if (list.get(i).getUuid().toString().contains("2a37")) {
                        List<BluetoothGattDescriptor> bluetoothGattDescriptors = list.get(i).getDescriptors();
                        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptors) {
                            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBluetoothHrGatt.writeDescriptor(bluetoothGattDescriptor);
                            Logger.d(TAG, "bluetoothGattDescriptor" + bluetoothGattDescriptor.getUuid());
                        }
                    }
                    if (list.get(i).getUuid().toString().contains("2a38")) {
                        List<BluetoothGattDescriptor> bluetoothGattDescriptors = list.get(i).getDescriptors();
                        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptors) {
                            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            boolean r = mBluetoothHrGatt.writeDescriptor(bluetoothGattDescriptor);
                            Logger.d(TAG, "bluetoothGattDescriptor" + bluetoothGattDescriptor.getUuid() + "," + r);
                        }
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
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mHandler.removeCallbacksAndMessages(null);
            }
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
            Logger.i(TAG, characteristic.getUuid() + ",onCharacteristicChanged::" + ConvertData.byteArrayToHexString(characteristic.getValue(), characteristic.getValue().length));
            if (characteristic.getUuid().toString().contains("2ad1") && isToExamine) {
                setBleDataInx(new byte[]{characteristic.getValue()[0], characteristic.getValue()[1]});
                setRunData(characteristic.getValue());
            } else if (characteristic.getUuid().toString().contains("2a37")) {
                setHrData(characteristic.getValue());
            }

        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {//descriptor读
            super.onDescriptorRead(gatt, descriptor, status);
            if (descriptor.getUuid().toString().contains("2902")) {

            }
            Logger.i(TAG, "onDescriptorRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {//descriptor写
            super.onDescriptorWrite(gatt, descriptor, status);
            Logger.i(TAG, "onDescriptorWrite " + ConvertData.byteArrayToHexString(descriptor.getValue(), descriptor.getValue().length));
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
            BluetoothGattService gattService;
            if (!isScanHrDevice){
                 gattService = mBluetoothGatt.getService(UUID.fromString(uuid));
            }else {
                gattService = mBluetoothHrGatt.getService(UUID.fromString(uuidHeartbeat));
            }
          /* for (BluetoothGattService gattService1:mBluetoothGatt.getServices()){
               Logger.d(TAG, "getServices=" + gattService1.getUuid().toString());
               for (BluetoothGattCharacteristic gattCharacteristic : gattService1.getCharacteristics()) {
                   Logger.d(TAG, "gattCharacteristic=" + gattCharacteristic.getUuid().toString());
                   for (BluetoothGattDescriptor bluetoothGattDescriptor:gattCharacteristic.getDescriptors()){
                       Logger.d(TAG, "getDescriptors=" + bluetoothGattDescriptor.getUuid().toString());
                   }
               }
           }*/
            for (BluetoothGattCharacteristic gattCharacteristic : gattService.getCharacteristics()) {
                //除了通过 BluetoothGatt#setCharacteristicNotification 开启 Android 端接收通知的开关，
                // 还需要往 Characteristic 的 Descriptor 属性写入开启通知的数据开关使得当硬件的数据改变时，主动往手机发送数据。
               /* if (gattCharacteristic.getUuid().toString().contains("ab01")) {//发送通道
                    mBluetoothGattCharacteristic = gattCharacteristic;
                    Logger.i(TAG, "发送通道::ab01");
                }*/
                Logger.d(TAG, "gattCharacteristic=" + gattCharacteristic.getUuid().toString());

                if (gattCharacteristic.getUuid().toString().contains("2ad1")) {//接收通道
                    boolean enabled = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                    Logger.i(TAG, "注册通知::" + enabled);
                }
                if (gattCharacteristic.getUuid().toString().contains("2ad9")) {//接收通道
                    boolean enabled = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                    Logger.i(TAG, "注册通知::" + enabled);
                }
                if (gattCharacteristic.getUuid().toString().contains("d18d2c10")) {
                    mBluetoothGattCharacteristic = gattCharacteristic;
                }

                if (gattCharacteristic.getUuid().toString().contains("2a37")) {//接收通道
                    boolean enabled = mBluetoothHrGatt.setCharacteristicNotification(gattCharacteristic, true);
                    Logger.i(TAG, "注册通知::" + enabled);
                }
                if (gattCharacteristic.getUuid().toString().contains("2a38")) {//接收通道
                    boolean enabled = mBluetoothHrGatt.setCharacteristicNotification(gattCharacteristic, true);
                    Logger.i(TAG, "注册通知::" + enabled);
                }


                if (gattCharacteristic.getUuid().toString().contains("2ad6")) {
                    boolean enabled = mBluetoothGatt.readCharacteristic(gattCharacteristic);
                    Logger.i(TAG, "读::" + enabled);
                }

                Logger.i(TAG, "GattCharacteristic" + gattCharacteristic.getUuid().toString());

            }

        }
    }

    /**
     * 发送数据
     *
     * @param bytes 指令
     */
    private void sendDescriptorByte(byte[] bytes) {
        if (mBluetoothGattCharacteristic != null) {
            mBluetoothGattCharacteristic.setValue(bytes);
            boolean r = mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
            Logger.d(TAG, mBluetoothGattCharacteristic.getUuid() + ",Send:" + ConvertData.byteArrayToHexString(bytes, bytes.length) + r);

        }
    }

    /**
     * 释放资源
     */
    public void close() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        if (mBluetoothHrGatt != null) {
            mBluetoothHrGatt.close();
            mBluetoothHrGatt = null;
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

    private RowerDataBean bytesToData(byte[] date) {
        RowerDataBean rowerDataBean = new RowerDataBean();


        return rowerDataBean;
    }

    /**
     * 获取date 数据中第offSet 长度为len  的结果
     *
     * @param date
     * @param offSet
     * @param len
     * @return
     */
    private int resolveDate(byte[] date, int offSet, int len) {
        int result;
        if (len == 4) {
            result = DataTypeConversion.bytesToIntLitter(date, offSet);
        } else if (len == 3) {
            result = DataTypeConversion.Byte2Int(date, offSet);

        } else if (len == 2) {
            result = DataTypeConversion.bytesToShortLiterEnd(date, offSet);
        } else if (len == 1) {
            result = DataTypeConversion.byteToInt(date[offSet]);
        } else {
            result = 0;
        }
        return result;
    }


    private void setBleDataInx(byte[] data) {
        if (setBleDataInx) {
            return;
        }
/*        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
        sendVerifyData();
 /*               }
            }
        }).start();*/


        setBleDataInx = true;
        int inxLen = 2;
        String s = ConvertData.byteArrToBinStr(data);
        String[] strings = s.split(",");
        StringBuffer stringBuffer = new StringBuffer();
        for (int j = 0; j < strings.length; j++) {
            for (int i = strings[j].length() - 1; i >= 0; i--) {
                stringBuffer.append(strings[j].subSequence(i, i + 1));
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
                        Logger.d(TAG, "setBleDataInx  STROKE_RATE_INX=" + RowerDataParam.STROKE_RATE_INX);
                    }
                    break;
                case 1:
                    RowerDataParam.AVERAGE_STROKE_RATE_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.AVERAGE_STROKE_RATE_LEN;
                    Logger.d(TAG, "setBleDataInx  Average_Stroke_Rate=" + RowerDataParam.AVERAGE_STROKE_RATE_INX);
                    break;
                case 2:
                    RowerDataParam.TOTAL_DISTANCE_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.TOTAL_DISTANCE_LEN;
                    Logger.d(TAG, "setBleDataInx  Total_Distance=" + RowerDataParam.TOTAL_DISTANCE_INX);
                    break;
                case 3:
                    RowerDataParam.INSTANTANEOUS_PACE_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.INSTANTANEOUS_PACE_LEN;
                    Logger.d(TAG, "setBleDataInx  Instantaneous_Pace=" + RowerDataParam.INSTANTANEOUS_PACE_INX);
                    break;
                case 4:
                    RowerDataParam.AVERAGE_PACE_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.AVERAGE_PACE_LEN;
                    Logger.d(TAG, "setBleDataInx  Average_Pace=" + RowerDataParam.AVERAGE_PACE_INX);
                    break;
                case 5:
                    RowerDataParam.INSTANTANEOUS_POWER_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.INSTANTANEOUS_POWER_LEN;
                    Logger.d(TAG, "setBleDataInx  Instantaneous_Power=" + RowerDataParam.INSTANTANEOUS_POWER_INX);

                    break;
                case 6:
                    RowerDataParam.AVERAGE_POWER_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.AVERAGE_POWER_LEN;
                    Logger.d(TAG, "setBleDataInx  Average_Power=" + RowerDataParam.AVERAGE_POWER_INX);
                    break;
                case 7:
                    RowerDataParam.RESISTANCE_LEVEL_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.RESISTANCE_LEVEL_LEN;
                    Logger.d(TAG, "setBleDataInx  Resistance_Level=" + RowerDataParam.RESISTANCE_LEVEL_INX);
                    break;
                case 8:
                    RowerDataParam.TOTAL_ENERGY_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.TOTAL_ENERGY_LEN;
                    RowerDataParam.ENERGY_PER_HOUR_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.ENERGY_PER_HOUR_LEN;
                    RowerDataParam.ENERGY_PER_MINUTE_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.ENERGY_PER_MINUTE_LEN;
                    Logger.d(TAG, "setBleDataInx  TOTAL_ENERGY_INX=" + RowerDataParam.TOTAL_ENERGY_INX);
                    break;
                case 9:
                    RowerDataParam.HEART_RATE_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.HEART_RATE_LEN;
                    Logger.d(TAG, "setBleDataInx  Heart_Rate=" + RowerDataParam.HEART_RATE_INX);
                    break;
                case 10:
                    RowerDataParam.METABOLIC_EQUIVALENT_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.METABOLIC_EQUIVALENT_LEN;
                    Logger.d(TAG, "setBleDataInx  Metabolic_Equivalent=" + RowerDataParam.METABOLIC_EQUIVALENT_INX);
                    break;
                case 11:
                    RowerDataParam.ELAPSED_TIME_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.ELAPSED_TIME_LEN;
                    Logger.d(TAG, "setBleDataInx  Elapsed_Time=" + RowerDataParam.ELAPSED_TIME_INX);
                    break;
                case 12:
                    RowerDataParam.REMAINING_TIME_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.REMAINING_TIME_LEN;
                    Logger.d(TAG, "setBleDataInx  Remaining_Time=" + RowerDataParam.REMAINING_TIME_INX);
                    break;
            }

        }
    }

    RowerDataBean rowerDataBean = new RowerDataBean();

    private void setRunData(byte[] data) {
        if (onRunDataListener == null) {
            return;
        }
        rowerDataBean.setStrokes(resolveDate(data, RowerDataParam.STROKE_COUNT_INX, RowerDataParam.STROKE_COUNT_LEN));
        rowerDataBean.setDistance(resolveDate(data, RowerDataParam.TOTAL_DISTANCE_INX, RowerDataParam.TOTAL_DISTANCE_LEN));
        rowerDataBean.setSm(resolveDate(data, RowerDataParam.STROKE_RATE_INX, RowerDataParam.STROKE_RATE_LEN));
        rowerDataBean.setFive_hundred(resolveDate(data, RowerDataParam.INSTANTANEOUS_PACE_INX, RowerDataParam.INSTANTANEOUS_PACE_LEN));
        rowerDataBean.setCalorie(resolveDate(data, RowerDataParam.ENERGY_PER_HOUR_INX, RowerDataParam.ENERGY_PER_HOUR_LEN));
        rowerDataBean.setCalories_hr(resolveDate(data, RowerDataParam.ENERGY_PER_HOUR_INX, RowerDataParam.ENERGY_PER_HOUR_LEN));
        rowerDataBean.setDrag(resolveDate(data, RowerDataParam.ENERGY_PER_MINUTE_INX, RowerDataParam.ENERGY_PER_MINUTE_LEN));
        if (!isHeartbeatConnect) {
            //rowerDataBean.setHeart_rate(resolveDate(data, RowerDataParam.HEART_RATE_INX, RowerDataParam.HEART_RATE_LEN));
        }
        rowerDataBean.setWatts(resolveDate(data, RowerDataParam.INSTANTANEOUS_POWER_INX, RowerDataParam.INSTANTANEOUS_POWER_LEN));
        rowerDataBean.setAve_watts(resolveDate(data, RowerDataParam.AVERAGE_POWER_INX, RowerDataParam.AVERAGE_POWER_LEN));
        rowerDataBean.setAve_five_hundred(resolveDate(data, RowerDataParam.AVERAGE_PACE_INX, RowerDataParam.AVERAGE_PACE_LEN));
        if (resolveDate(data, RowerDataParam.ELAPSED_TIME_INX, RowerDataParam.ELAPSED_TIME_LEN) != 0) {
            rowerDataBean.setTime(RowerDataParam.ELAPSED_TIME_INX == 0 ? 0 : resolveDate(data, RowerDataParam.ELAPSED_TIME_INX, RowerDataParam.ELAPSED_TIME_LEN));
        } else {
            rowerDataBean.setTime(RowerDataParam.REMAINING_TIME_INX == 0 ? 0 : resolveDate(data, RowerDataParam.REMAINING_TIME_INX, RowerDataParam.REMAINING_TIME_LEN));
        }
        rowerDataBean.setInterval(RowerDataParam.RESISTANCE_LEVEL_INX == 0 ? 0 : resolveDate(data, RowerDataParam.RESISTANCE_LEVEL_INX, RowerDataParam.RESISTANCE_LEVEL_LEN));
        onRunDataListener.onRunData(rowerDataBean);
    }

    private void setHrData(byte[] data) {
        if (onRunDataListener == null) {
            return;
        }
        String s = ConvertData.byteArrToBinStr(data);
        if ("0".equals(s.subSequence(7, 8))) {
            rowerDataBean.setHeart_rate(ConvertData.byteToInt(data[1]));
        } else {
            rowerDataBean.setHeart_rate(resolveDate(data, 1, 2));
        }
        onRunDataListener.onRunData(rowerDataBean);
    }

    private void sendVerifyData() {
        byte[] bytes = new byte[9];
        bytes[0] = 0x03;
        bytes[1] = 0x00;
        System.arraycopy(DataTypeConversion.intToBytesLitter(Integer.valueOf(getCurDate())), 0, bytes, 2, 4);
        bytes[6] = 0x00;
        bytes[7] = 0x00;
        bytes[8] = (byte) 0xb0;
        sendDescriptorByte(bytes);
    }

    public String getCurDate() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("YYMMdd");
        return sDateFormat.format(new java.util.Date());
    }


    public void setIsScanHrDevice(boolean isScanHrDevice) {
        this.isScanHrDevice = isScanHrDevice;
    }
}
