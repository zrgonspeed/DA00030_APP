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

import com.bike.ftms.app.base.MyApplication;
import com.bike.ftms.app.bean.FormatBean;
import com.bike.ftms.app.bean.RowerDataBean;
import com.bike.ftms.app.common.RowerDataParam;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.utils.ByteArrTransUtil;
import com.bike.ftms.app.utils.ConvertData;
import com.bike.ftms.app.utils.DataTypeConversion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BleManager {
    private String TAG = "BleManager";
    private static BleManager instance;
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
    private OnRunDataListener onRunDataListener;//运动数据回调

    private final long SCAN_MAX_COUNT = 15;     //扫描的设备个数限制（停止扫描）
    private final long SCAN_PERIOD = 60000;     //扫描设备时间限制
    private boolean setBleDataInx = false;

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
                setBleDataInx = false;
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
            Logger.i(TAG, "onConnectionStateChange" + gatt.getDevice().getName());
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                isConnect = true;
                if (onScanConnectListener != null) {
                    onScanConnectListener.onConnectEvent(true, gatt.getDevice().getName());
                }
                Logger.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());
                // mBluetoothGatt.discoverServices();//
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                isConnect = false;
                if (onScanConnectListener != null) {
                    onScanConnectListener.onConnectEvent(false, gatt.getDevice().getName());
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
                    if (list.get(i).getUuid().toString().contains("2ad1")) {
                        List<BluetoothGattDescriptor> bluetoothGattDescriptors = list.get(i).getDescriptors();
                        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptors) {
                            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
                            Logger.d(TAG, "getDescriptors=" + bluetoothGattDescriptor.getUuid());
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
            Logger.i(TAG, "onCharacteristicChanged::" + ConvertData.byteArrayToHexString(characteristic.getValue(), characteristic.getValue().length));
            setBleDataInx(new byte[]{characteristic.getValue()[0], characteristic.getValue()[1]}, characteristic.getValue()[2]);
            setRunData(characteristic.getValue());
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
                /*if (gattCharacteristic.getUuid().toString().contains("2acd")) {//接收通道
                    boolean enabled = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                    Logger.i(TAG, "注册通知::" + enabled);
                }
                if (gattCharacteristic.getUuid().toString().contains("2ace")) {//接收通道
                    boolean enabled = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                    Logger.i(TAG, "注册通知::" + enabled);
                }*/
                if (gattCharacteristic.getUuid().toString().contains("2ad1")) {//接收通道
                    boolean enabled = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                    Logger.i(TAG, "注册通知::" + enabled);
                }
               /* if (gattCharacteristic.getUuid().toString().contains("2ad2")) {//接收通道
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
                }*/

                if (gattCharacteristic.getUuid().toString().contains("2ad6")) {
                    boolean enabled = mBluetoothGatt.readCharacteristic(gattCharacteristic);
                    Logger.i(TAG, "读::" + enabled);
                }
//                if (gattCharacteristic.getUuid().toString().contains("d18d2c10")) {//接收通道
//                    boolean enabled = mBluetoothGatt.writeCharacteristic(gattCharacteristic);
//                    Logger.i(TAG, "写::" + enabled);
//                }
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


    private void setBleDataInx(byte[] data, byte moreData1) {
        if (setBleDataInx) {
            return;
        }
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
                        Logger.d("setBleDataInx  STROKE_RATE_INX=" + RowerDataParam.STROKE_RATE_INX);
                    }
                    break;
                case 1:
                    RowerDataParam.AVERAGE_STROKE_RATE_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.AVERAGE_STROKE_RATE_LEN;
                    Logger.d("setBleDataInx  Average_Stroke_Rate=" + RowerDataParam.AVERAGE_STROKE_RATE_INX);
                    break;
                case 2:
                    RowerDataParam.TOTAL_DISTANCE_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.TOTAL_DISTANCE_LEN ;
                    Logger.d("setBleDataInx  Total_Distance=" + RowerDataParam.TOTAL_DISTANCE_INX);
                    break;
                case 3:
                    RowerDataParam.INSTANTANEOUS_PACE_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.INSTANTANEOUS_PACE_LEN;
                    Logger.d("setBleDataInx  Instantaneous_Pace=" + RowerDataParam.INSTANTANEOUS_PACE_INX);
                    break;
                case 4:
                    RowerDataParam.AVERAGE_PACE_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.AVERAGE_PACE_LEN;
                    Logger.d("setBleDataInx  Average_Pace=" + RowerDataParam.AVERAGE_PACE_INX);
                    break;
                case 5:
                    RowerDataParam.INSTANTANEOUS_POWER_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.INSTANTANEOUS_POWER_LEN;
                    Logger.d("setBleDataInx  Instantaneous_Power=" + RowerDataParam.INSTANTANEOUS_POWER_INX);

                    break;
                case 61:
                    RowerDataParam.AVERAGE_POWER_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.AVERAGE_POWER_LEN;
                    Logger.d("setBleDataInx  Average_Power=" + RowerDataParam.AVERAGE_POWER_INX);
                    break;
                case 7:
                    RowerDataParam.RESISTANCE_LEVEL_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.RESISTANCE_LEVEL_LEN;
                    Logger.d("setBleDataInx  Resistance_Level=" + RowerDataParam.RESISTANCE_LEVEL_INX);
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
                    Logger.d("setBleDataInx  Heart_Rate=" + RowerDataParam.HEART_RATE_INX);
                    break;
                case 10:
                    RowerDataParam.METABOLIC_EQUIVALENT_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.METABOLIC_EQUIVALENT_LEN;
                    Logger.d("setBleDataInx  Metabolic_Equivalent=" + RowerDataParam.METABOLIC_EQUIVALENT_INX);
                    break;
                case 11:
                    RowerDataParam.ELAPSED_TIME_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.ELAPSED_TIME_LEN;
                    Logger.d("setBleDataInx  Elapsed_Time=" + RowerDataParam.ELAPSED_TIME_INX);
                    break;
                case 12:
                    RowerDataParam.REMAINING_TIME_INX = inxLen;
                    inxLen = inxLen + RowerDataParam.REMAINING_TIME_LEN;
                    Logger.d("setBleDataInx  Remaining_Time=" + RowerDataParam.REMAINING_TIME_INX);
                    break;
            }

        }
    }

    private void setRunData(byte[] data) {
        if (onRunDataListener == null) {
            return;
        }
        RowerDataBean rowerDataBean = new RowerDataBean();
        rowerDataBean.setStrokes(resolveDate(data, RowerDataParam.STROKE_COUNT_INX, RowerDataParam.STROKE_COUNT_LEN));
        rowerDataBean.setDistance(resolveDate(data, RowerDataParam.TOTAL_DISTANCE_INX, RowerDataParam.TOTAL_DISTANCE_LEN));
        rowerDataBean.setSm(resolveDate(data, RowerDataParam.STROKE_RATE_INX, RowerDataParam.STROKE_RATE_LEN));
        rowerDataBean.setFive_hundred(resolveDate(data, RowerDataParam.INSTANTANEOUS_PACE_INX, RowerDataParam.INSTANTANEOUS_PACE_LEN));
        rowerDataBean.setCalorie(resolveDate(data, RowerDataParam.ENERGY_PER_HOUR_INX, RowerDataParam.ENERGY_PER_HOUR_LEN));
        rowerDataBean.setCalories_hr(resolveDate(data, RowerDataParam.ENERGY_PER_MINUTE_INX, RowerDataParam.ENERGY_PER_MINUTE_LEN));
        rowerDataBean.setHeart_rate(resolveDate(data, RowerDataParam.HEART_RATE_INX, RowerDataParam.HEART_RATE_LEN));
        rowerDataBean.setWatts(resolveDate(data, RowerDataParam.INSTANTANEOUS_POWER_INX, RowerDataParam.INSTANTANEOUS_POWER_LEN));
        rowerDataBean.setAve_watts(resolveDate(data, RowerDataParam.TOTAL_ENERGY_INX, RowerDataParam.TOTAL_ENERGY_LEN));
        rowerDataBean.setAve_five_hundred(resolveDate(data, RowerDataParam.AVERAGE_PACE_INX, RowerDataParam.AVERAGE_PACE_LEN));
        onRunDataListener.onRunData(rowerDataBean);
    }
}
