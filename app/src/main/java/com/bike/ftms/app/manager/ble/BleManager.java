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
import com.bike.ftms.app.bean.MyScanResult;
import com.bike.ftms.app.bean.RowerDataBean;
import com.bike.ftms.app.bean.RowerDataBean2;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.common.RowerDataParam;
import com.bike.ftms.app.serial.SerialCommand;
import com.bike.ftms.app.serial.SerialData;
import com.bike.ftms.app.utils.CustomTimer;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.utils.ByteArrTransUtil;
import com.bike.ftms.app.utils.ConvertData;
import com.bike.ftms.app.utils.DataTypeConversion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BleManager implements CustomTimer.TimerCallBack {
    private String TAG = "BleManager";
    private final byte IDLE_BEGIN = 0;
    private final byte HEADER_DATA = 1;
    private byte c_state = IDLE_BEGIN;
    private short offset;
    private byte[] readDataBuffer = new byte[SerialCommand.RECEIVE_PACK_LEN_MAX];
    private int rawPackageLen;
    private byte[] ResultBuf = new byte[SerialCommand.RECEIVE_PACK_LEN_MAX];

    private static BleManager instance;
    public final String uuid = "00001826-0000-1000-8000-00805f9b34fb";
    public final String uuidHeartbeat = "0000180d-0000-1000-8000-00805f9b34fb";
    public final String uuidSendData = "0000ffe5-0000-1000-8000-00805f9b34fb";
    private final byte RUN_STATUS_RUNNING = 0x01;
    private final byte RUN_STATUS_STOP = 0x00;
    RowerDataBean rowerDataBean;

    public static boolean isConnect;  //是否连接
    private boolean isHeartbeatConnect;//是否连接蓝牙腰带
    public static boolean isCanning;  //是否正在扫描
    public static boolean isOpen;     //是否打开定位及蓝牙

    private OnScanConnectListener onScanConnectListener;      //扫描回调
    private BluetoothAdapter mBluetoothAdapter; //系统蓝牙适配器
    private List<MyScanResult> mScanResults;      //扫描到的蓝牙设备
    private BluetoothGatt mBluetoothGatt;       //连接蓝牙、及操作
    private BluetoothGatt mBluetoothHrGatt;       //连接蓝牙、及操作
    private List<BluetoothGattService> mBluetoothGattServices;//服务，Characteristic(特征) 的集合。
    private BluetoothGattCharacteristic mBluetoothGattCharacteristic;//特征值(用于收发数据)
    private OnRunDataListener onRunDataListener;//运动数据回调

    private final long SCAN_MAX_COUNT = 20;     //扫描的设备个数限制（停止扫描）
    private final long SCAN_PERIOD = 60000;     //扫描设备时间限制
    private boolean setBleDataInx = false;
    private boolean isToExamine = false;
    private boolean isSendVerifyData = false;
    private boolean isScanHrDevice = false;//是否扫描腰带设备
    private MyScanResult connectScanResult;
    private MyScanResult connectHrScanResult;
    /**
     * 判断是否断开连接
     */
    private CustomTimer isConnectTimer;
    private final String isConnectTag = "isConnect";
    private CustomTimer isHrConnectTimer;
    private final String isHrConnectTag = "isHrConnect";
    private CustomTimer isVerifyConnectTimer;
    private final String isVerifyConnectTag = "isVerifyConnect";
    private byte runStatus = RUN_STATUS_STOP;

    private Handler mHandler = new Handler(Objects.requireNonNull(Looper.myLooper()));
    private int tempInterval = 0;
    private RowerDataBean2 rowerDataBean2;

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
        return mBluetoothAdapter;
    }

    /**
     * 扫描蓝牙设备
     */
    public void scanDevice() {
       /* if(isCanning){
            isCanning = false;
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
        }*/
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
            if (connectScanResult != null && !isScanHrDevice && connectScanResult.getConnectState() == 1) {
                mScanResults.add(connectScanResult);
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
                scanFilter = new ScanFilter.Builder().setServiceUuid(
                        new ParcelUuid(UUID.fromString(uuidHeartbeat))).build();
                scanFilters.add(scanFilter);
                mBluetoothAdapter.getBluetoothLeScanner().startScan(scanFilters, scanSettings, mScanCallback);
            } else {
               /* scanFilter = new ScanFilter.Builder().setServiceUuid(
                        new ParcelUuid(UUID.fromString(uuid))).build();*/
                mBluetoothAdapter.getBluetoothLeScanner().startScan(mScanCallback);
            }
            Logger.i(TAG, "开始扫描设备");
        }
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        if (mBluetoothAdapter != null && isCanning) {
            isCanning = false;
            //mHandler.removeCallbacksAndMessages(null);
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
        if (getScanResults().get(position).getConnectState() == 1) {
            disConnectDevice();
            return;
        }
        //disConnectDevice();
        if (getScanResults() != null && getScanResults().size() != 0) {
            if (position >= 0 && position < getScanResults().size()) {
                getScanResults().get(position).setConnectState(2);
                if (!isScanHrDevice) {
                    rowerDataBean = new RowerDataBean();
                    connectScanResult = new MyScanResult(getScanResults().get(position).getScanResult(), 2);
                    reset();
                    //第二个参数表示是否需要自动连接。如果设置为 true, 表示如果设备断开了，会不断的尝试自动连接。设置为 false 表示只进行一次连接尝试。
                    mBluetoothGatt = getScanResults().get(position).getScanResult().getDevice()
                            .connectGatt(MyApplication.getContext(), false, mGattCallback);
                } else {
                    connectHrScanResult = new MyScanResult(getScanResults().get(position).getScanResult(), 2);
                    mBluetoothHrGatt = getScanResults().get(position).getScanResult().getDevice()
                            .connectGatt(MyApplication.getContext(), true, mHrGattCallback);
                }

                Logger.i(TAG, "connectDevice" + getScanResults().get(position).getScanResult().getDevice().getAddress());
            }
        }
        if (onScanConnectListener != null) {
            onScanConnectListener.onNotifyData();
        }
    }

    /**
     * 断开蓝牙设备
     */
    public void disConnectDevice() {
        Logger.i("disConnectDevice");
        if (mBluetoothGatt != null && !isScanHrDevice) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt = null;
        }
        if (mBluetoothHrGatt != null && isScanHrDevice) {
            mBluetoothHrGatt.disconnect();
            mBluetoothHrGatt = null;
        }
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
                mBluetoothGatt.discoverServices();
                Logger.d("isConnect=" + isConnect + ",isHeartbeatConnect=" + isHeartbeatConnect);
                if (onScanConnectListener != null) {
                    onScanConnectListener.onConnectEvent(true, gatt.getDevice().getName());
                }
                // mBluetoothGatt.discoverServices();//
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                rowerDataBean = new RowerDataBean();
                onRunDataListener.onRunData(rowerDataBean);
                for (MyScanResult myScanResult : mScanResults) {
                    if (myScanResult.getScanResult().getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                        myScanResult.setConnectState(0);
                        isConnectTimer.closeTimer();
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
            }
            if (onScanConnectListener != null) {
                onScanConnectListener.onNotifyData();
            }
        }

        //发现新服务，即调用了mBluetoothGatt.discoverServices()后，返回的数据  (读取 BLE 属性)
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Logger.i(TAG, "onServicesDiscovered status=" + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mBluetoothGattServices = mBluetoothGatt.getServices();
                BluetoothGattService localGattService = mBluetoothGatt.getService(UUID.fromString(uuid));
                List<BluetoothGattCharacteristic> list = new ArrayList<>();
                if (localGattService != null) {
                    list = localGattService.getCharacteristics();
                }
                BluetoothGattService localGattService1 = mBluetoothGatt.getService(UUID.fromString(uuidSendData));
                if (localGattService1 != null) {
                    list.addAll(localGattService1.getCharacteristics());
                }
                for (int i = 0; i < list.size(); i++) {
                    Logger.d(TAG, "gattCharacteristic2=" + list.get(i).getUuid().toString());

                    if (list.get(i).getUuid().toString().contains("2ad1")) {
                        List<BluetoothGattDescriptor> bluetoothGattDescriptors = list.get(i).getDescriptors();
                        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptors) {
                            boolean r = bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
                            Logger.d(TAG, list.get(i).getUuid().toString() + ",bluetoothGattDescriptor " + r);
                        }
                    }
                    if (list.get(i).getUuid().toString().contains("2ad3")) {
                        List<BluetoothGattDescriptor> bluetoothGattDescriptors = list.get(i).getDescriptors();
                        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptors) {
                            boolean r = bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
                            Logger.d(TAG, list.get(i).getUuid().toString() + ",bluetoothGattDescriptor " + r);
                        }
                    }
                    if (list.get(i).getUuid().toString().contains("ffe0")) {
                        List<BluetoothGattDescriptor> bluetoothGattDescriptors = list.get(i).getDescriptors();
                        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptors) {
                            boolean r = bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
                            Logger.d(TAG, list.get(i).getUuid().toString() + ",bluetoothGattDescriptor " + r);
                        }
                    }
                    if (list.get(i).getUuid().toString().contains("2ada")) {
                        List<BluetoothGattDescriptor> bluetoothGattDescriptors = list.get(i).getDescriptors();
                        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptors) {
                            boolean r = bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
                            Logger.d(TAG, list.get(i).getUuid().toString() + ",bluetoothGattDescriptor " + r);
                        }
                    }

                    /*if (list.get(i).getUuid().toString().contains("2a23")) {
                        List<BluetoothGattDescriptor> bluetoothGattDescriptors = list.get(i).getDescriptors();
                        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptors) {
                            boolean r = bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
                            Logger.d(TAG, list.get(i).getUuid().toString() + ",bluetoothGattDescriptor " + r);
                        }
                    }*/
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
            isConnectTimer.setmAllTime(0L);
            Logger.i(TAG, characteristic.getUuid() + ",onCharacteristicChanged::" + ConvertData.byteArrayToHexString(characteristic.getValue(), characteristic.getValue().length));
            byte[] data = characteristic.getValue();
/*            for (int i = 0; i < data.length; i++) {
                switch (c_state) {
                    case IDLE_BEGIN:
                        if ((data[i] & 0xFF) == SerialCommand.PACK_FRAME_HEADER) {
                            readDataBuffer[offset] = data[i];
                            offset++;
                            c_state = HEADER_DATA;
                        }
                        break;
                    case HEADER_DATA:
                        if (offset > SerialCommand.RECEIVE_PACK_LEN_MAX) {
                            reSet();
                            break;
                        }
                        readDataBuffer[offset] = data[i];
                        offset++;
                        if ((data[i] & 0xFF) > SerialCommand.PACK_FRAME_MAX_DATA
                                && (data[i] & 0xFF) != SerialCommand.PACK_FRAME_END) {
                            reSet();
                            break;
                        }
                        if ((data[i] & 0xFF) == SerialCommand.PACK_FRAME_END) {
                            rawPackageLen = SerialData.comUnPackage(readDataBuffer, ResultBuf, offset);
                            reSet();
                            if (rawPackageLen > 0) {
                                byte[] bytes = new byte[rawPackageLen];
                                System.arraycopy(ResultBuf, 0, bytes, 0, rawPackageLen);
                                rxDataPackage(bytes,characteristic.getUuid().toString());
                            }
                        }
                        break;
                }
            }*/

            rxDataPackage(data, characteristic.getUuid().toString());
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {//descriptor读
            super.onDescriptorRead(gatt, descriptor, status);
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

    private BluetoothGattCallback mHrGattCallback = new BluetoothGattCallback() {
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
                isHeartbeatConnect = true;
                mBluetoothHrGatt.discoverServices();
                Logger.d("isConnect=" + isConnect + ",isHeartbeatConnect=" + isHeartbeatConnect);
                if (onScanConnectListener != null) {
                    onScanConnectListener.onConnectEvent(true, gatt.getDevice().getName());
                }
                // mBluetoothGatt.discoverServices();//
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                for (MyScanResult myScanResult : mScanResults) {
                    if (myScanResult.getScanResult().getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                        myScanResult.setConnectState(0);
                        isHrConnectTimer.closeTimer();
                        if (connectHrScanResult.getScanResult().getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                            connectHrScanResult.setConnectState(0);
                        } else {
                            connectHrScanResult = myScanResult;

                        }
                        break;
                    }
                }
                isHeartbeatConnect = false;
                if (onScanConnectListener != null) {
                    onScanConnectListener.onConnectEvent(false, gatt.getDevice().getName());
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
            Logger.i(TAG, "onServicesDiscovered status=" + status);
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
            isHrConnectTimer.setmAllTime(0L);
            Logger.i(TAG, characteristic.getUuid() + ",Hr onCharacteristicChanged::" + ConvertData.byteArrayToHexString(characteristic.getValue(), characteristic.getValue().length));
            if (characteristic.getUuid().toString().contains("2a37")) {
                setHrData(characteristic.getValue());
            }

        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {//descriptor读
            super.onDescriptorRead(gatt, descriptor, status);
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
            if (!isScanHrDevice) {
                gattService = mBluetoothGatt.getService(UUID.fromString(uuid));
                if (gattService == null) {
                    // onRunDataListener.onExit();
                    mBluetoothGatt.disconnect();
                    mBluetoothGatt = null;
                }
                for (BluetoothGattCharacteristic gattCharacteristic : mBluetoothGatt.getService(UUID.fromString(uuidSendData)).getCharacteristics()) {
                    if (gattCharacteristic.getUuid().toString().contains("ffe9")) {
                        mBluetoothGattCharacteristic = gattCharacteristic;
                        Logger.d(TAG, "mBluetoothGattCharacteristic=" + mBluetoothGattCharacteristic);
                    }
                    if (gattCharacteristic.getUuid().toString().contains("ffe0")) {//接收通道
                        boolean enabled = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                        Logger.i(TAG, gattCharacteristic.getUuid().toString() + ",注册通知::" + enabled);
                    }
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isSendVerifyData) {
                            isSendVerifyData = true;
                            sendVerifyData();
                        }
                    }
                }, 2000);

            } else {
                gattService = mBluetoothHrGatt.getService(UUID.fromString(uuidHeartbeat));
            }
           /* for (BluetoothGattService gattService1 : mBluetoothGatt.getServices()) {
                Logger.d(TAG, "=========================================");
                Logger.d(TAG, "getServices=" + gattService1.getUuid().toString());
                for (BluetoothGattCharacteristic gattCharacteristic : gattService1.getCharacteristics()) {
                    Logger.d(TAG, "gattCharacteristic=" + gattCharacteristic.getUuid().toString());
                    for (BluetoothGattDescriptor bluetoothGattDescriptor : gattCharacteristic.getDescriptors()) {
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
                Logger.d(TAG, "gattCharacteristic1=" + gattCharacteristic.getUuid().toString());

                if (gattCharacteristic.getUuid().toString().contains("2ad1")) {//接收通道
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

//                if (gattCharacteristic.getUuid().toString().contains("2a23")) {//接收通道
//                    boolean enabled = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
//                    Logger.i(TAG, "注册通知::" + enabled);
//                }

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
    private boolean sendDescriptorByte(byte[] bytes, int len) {
        boolean r = false;
        byte[] sendBytes = new byte[len];
        System.arraycopy(bytes, 0, sendBytes, 0, len);
        if (mBluetoothGattCharacteristic != null && mBluetoothGatt != null) {
            mBluetoothGattCharacteristic.setValue(sendBytes);
            r = mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
            Logger.d(TAG, mBluetoothGattCharacteristic.getUuid() + ",Send:" + ConvertData.byteArrayToHexString(sendBytes, sendBytes.length) + r);
        } else {
            Logger.d(TAG, "Send:" + ConvertData.byteArrayToHexString(sendBytes, sendBytes.length) + r);
        }
        return r;
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
        if (connectHrScanResult != null) {
            connectHrScanResult = null;
        }
        if (connectScanResult != null) {
            connectScanResult = null;
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

    /**
     * 设置运动数据下标
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

        Logger.d("------------", "s == " + s);

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
    }

    /**
     * 设置运动数据
     *
     * @param data
     */
    private void setRunData(byte[] data) {
        if (onRunDataListener == null || runStatus == RUN_STATUS_STOP) {
            return;
        }

        rowerDataBean.setStrokes(RowerDataParam.STROKE_COUNT_INX == -1 ? 0 : resolveDate(data, RowerDataParam.STROKE_COUNT_INX, RowerDataParam.STROKE_COUNT_LEN));
        rowerDataBean.setDistance(RowerDataParam.TOTAL_DISTANCE_INX == -1 ? 0 : resolveDate(data, RowerDataParam.TOTAL_DISTANCE_INX, RowerDataParam.TOTAL_DISTANCE_LEN));
        rowerDataBean.setSm(RowerDataParam.STROKE_RATE_INX == -1 ? 0 : resolveDate(data, RowerDataParam.STROKE_RATE_INX, RowerDataParam.STROKE_RATE_LEN) / 2);
        rowerDataBean.setFive_hundred(RowerDataParam.INSTANTANEOUS_PACE_INX == -1 ? 0 : resolveDate(data, RowerDataParam.INSTANTANEOUS_PACE_INX, RowerDataParam.INSTANTANEOUS_PACE_LEN));
        rowerDataBean.setCalorie(RowerDataParam.TOTAL_ENERGY_INX == -1 ? 0 : resolveDate(data, RowerDataParam.TOTAL_ENERGY_INX, RowerDataParam.TOTAL_ENERGY_LEN));
        rowerDataBean.setCalories_hr(RowerDataParam.ENERGY_PER_HOUR_INX == -1 ? 0 : resolveDate(data, RowerDataParam.ENERGY_PER_HOUR_INX, RowerDataParam.ENERGY_PER_HOUR_LEN));
        if (!isHeartbeatConnect) {
            rowerDataBean.setHeart_rate(RowerDataParam.HEART_RATE_INX == -1 ? 0 : resolveDate(data, RowerDataParam.HEART_RATE_INX, RowerDataParam.HEART_RATE_LEN));
        }
        rowerDataBean.setWatts(RowerDataParam.INSTANTANEOUS_POWER_INX == -1 ? 0 : resolveDate(data, RowerDataParam.INSTANTANEOUS_POWER_INX, RowerDataParam.INSTANTANEOUS_POWER_LEN));
        rowerDataBean.setAve_watts(RowerDataParam.AVERAGE_POWER_INX == -1 ? 0 : resolveDate(data, RowerDataParam.AVERAGE_POWER_INX, RowerDataParam.AVERAGE_POWER_LEN));
        rowerDataBean.setAve_five_hundred(RowerDataParam.AVERAGE_PACE_INX == -1 ? 0 : resolveDate(data, RowerDataParam.AVERAGE_PACE_INX, RowerDataParam.AVERAGE_PACE_LEN));
        if (RowerDataParam.REMAINING_TIME_INX == -1 || resolveDate(data, RowerDataParam.REMAINING_TIME_INX, RowerDataParam.REMAINING_TIME_LEN) == 0) {
            rowerDataBean.setTime(RowerDataParam.ELAPSED_TIME_INX == -1 ? 0 : resolveDate(data, RowerDataParam.ELAPSED_TIME_INX, RowerDataParam.ELAPSED_TIME_LEN));
        } else {
            rowerDataBean.setTime(RowerDataParam.REMAINING_TIME_INX == -1 ? 0 : resolveDate(data, RowerDataParam.REMAINING_TIME_INX, RowerDataParam.REMAINING_TIME_LEN));
        }
        rowerDataBean.setDate(System.currentTimeMillis());
        onRunDataListener.onRunData(rowerDataBean);
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
        String s = ConvertData.byteArrToBinStr(data);
        if ("0".equals(s.subSequence(7, 8))) {
            rowerDataBean.setHeart_rate(ConvertData.byteToInt(data[1]));
        } else {
            rowerDataBean.setHeart_rate(resolveDate(data, 1, 2));
        }
        onRunDataListener.onRunData(rowerDataBean);
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
        byte[] bytes = new byte[date.length];
        int len = SerialData.comPackage(date, bytes, date.length - 3);
        sendDescriptorByte(bytes, len);
        startTimerOfIsVerifyConnect();
    }

    private void sendRespondData(byte[] data) {
        byte[] bytes = new byte[data.length + 1];
        bytes[0] = (byte) SerialCommand.PACK_FRAME_HEADER;
        bytes[1] = (byte) 0x00;
        System.arraycopy(data, 1, bytes, 2, data.length - 4);
        byte[] respondByte = new byte[64];
        int len = SerialData.comPackage(bytes, respondByte, bytes.length - 3);
        sendDescriptorByte(respondByte, len);
    }

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
                mBluetoothHrGatt = null;
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
        }
        if (uuid.contains("2ad1") && isToExamine) {
            setBleDataInx(new byte[]{data[0], data[1]});
            setRunData(data);
        }
        if (uuid.contains("2ada") && isToExamine) {
            Logger.d("-------------------------------------------------------------------");
            if (data[3] == RUN_STATUS_STOP && runStatus != RUN_STATUS_STOP) {//停止运动
                boolean canSave = false;
                Logger.d("----------------", "mode == " + rowerDataBean.getRunMode());
                if (rowerDataBean.getRunMode() == MyConstant.GOAL_TIME) {
                    // 时间是倒数的，用距离判断
                    if (rowerDataBean.getDistance() >= 10) {
                        canSave = true;
                    }
                } else if (rowerDataBean.getRunMode() == MyConstant.INTERVAL_TIME) {
                    if (rowerDataBean.getInterval() > 0 || rowerDataBean.getDistance() >= 10) {
                        canSave = true;
                    }
                } else if (rowerDataBean.getRunMode() == MyConstant.INTERVAL_DISTANCE) {
                    if (rowerDataBean.getInterval() > 0 || rowerDataBean.getTime() >= 5) {
                        canSave = true;
                    }
                } else if (rowerDataBean.getRunMode() == MyConstant.INTERVAL_CALORIES) {
                    if (rowerDataBean.getInterval() > 0 || rowerDataBean.getTime() >= 5) {
                        canSave = true;
                    }
                } else {
                    if (rowerDataBean.getTime() >= 5) {
                        canSave = true;
                    }
                }

                if (canSave) {
                    Logger.e("1----", "bean1  " + rowerDataBean);
                    rowerDataBean.save();

                    rowerDataBean2 = new RowerDataBean2(rowerDataBean);
                    rowerDataBean2.save();
                    rowerDataBean = new RowerDataBean();
                    Logger.e("1----", "bean1.save    bean2.save");
                    Logger.e("1----", "bean2  " + rowerDataBean2);
                }
            }
            runStatus = data[3];
        }
        if (uuid.contains("ffe0")) {//校对CRC码
            if (data[1] == 0x40 && data[2] == 0x01) {
                String[] dates = getCurDate().split("-");
                byte[] date = new byte[3];
                date[0] = (byte) Integer.parseInt(dates[0], 16);
                date[1] = (byte) Integer.parseInt(dates[1], 16);
                date[2] = (byte) Integer.parseInt(dates[2], 16);
                byte[] calCRCBytes = ConvertData.shortToBytes(SerialData.calCRCByTable(ConvertData.subBytes(date, 0, date.length), date.length));
                if (calCRCBytes[0] == data[3] && calCRCBytes[1] == data[4]) {
                    isToExamine = true;
                    isVerifyConnectTimer.closeTimer();
                }

            }
            if (data[1] == 0x41 && data[2] == 0x02 && isToExamine) {
                sendRespondData(data);
                if (runStatus == RUN_STATUS_RUNNING) {
                    rowerDataBean.setDrag(resolveDate(data, RowerDataParam.DRAG_INX, RowerDataParam.DRAG_LEN));
                    rowerDataBean.setInterval(resolveDate(data, RowerDataParam.INTERVAL_INX, RowerDataParam.INTERVAL_LEN));

                    // 跳段时保存
                    if (rowerDataBean.getInterval() <= tempInterval) {
                        rowerDataBean2 = new RowerDataBean2(rowerDataBean);
                    } else {
                        if (tempInterval >= 1) {
                            rowerDataBean2.save();
                            Logger.e("----", "bean2.save " + rowerDataBean2);
                        }
                    }
                    tempInterval = rowerDataBean.getInterval();

                } else if (runStatus == RUN_STATUS_STOP) {
                    rowerDataBean.setDrag(0);
                    rowerDataBean.setInterval(0);
                    tempInterval = 0;
                }

                if (onRunDataListener != null) {
                    onRunDataListener.onRunData(rowerDataBean);
                }
            }
        }

        if (uuid.contains("2ada") && isToExamine) {
            if (onRunDataListener == null || runStatus == RUN_STATUS_STOP) {
                return;
            }
            // 都要设置的参数
            int runMode = resolveDate(data, RowerDataParam.RUN_MODE_INX, RowerDataParam.RUN_MODE_LEN);
            int intervalStatus = resolveDate(data, RowerDataParam.INTERVAL_STATUS_INX, RowerDataParam.INTERVAL_STATUS_LEN);
            int runStatus = resolveDate(data, RowerDataParam.RUN_STATUS_INX, RowerDataParam.RUN_STATUS_LEN);
            int runInterval = resolveDate(data, RowerDataParam.RUN_INTERVAL_INX, RowerDataParam.RUN_INTERVAL_LEN);
            rowerDataBean.setRunMode(runMode);
            rowerDataBean.setIntervalStatus(intervalStatus);
            rowerDataBean.setRunStatus(runStatus);
            rowerDataBean.setRunInterval(runInterval);

            if (runMode == MyConstant.GOAL_TIME || runMode == MyConstant.GOAL_DISTANCE
                    || runMode == MyConstant.GOAL_CALORIES
            ) {
                switch (runMode) {
                    case MyConstant.GOAL_TIME:
                        int goalTime = resolveDate(data, RowerDataParam.GOAL_TIME_INX + 1, RowerDataParam.GOAL_TIME_LEN);
                        rowerDataBean.setSetGoalTime(goalTime);
                        break;
                    case MyConstant.GOAL_DISTANCE:
                        int goalDistance = resolveDate(data, RowerDataParam.GOAL_DISTANCE_INX + 1, RowerDataParam.GOAL_DISTANCE_LEN);
                        rowerDataBean.setSetGoalDistance(goalDistance);
                        break;
                    case MyConstant.GOAL_CALORIES:
                        int goalCalorie = resolveDate(data, RowerDataParam.GOAL_CALORIE_INX + 1, RowerDataParam.GOAL_CALORIE_LEN);
                        rowerDataBean.setSetGoalCalorie(goalCalorie);
                        break;
                }
            } else if (runMode == MyConstant.INTERVAL_TIME || runMode == MyConstant.INTERVAL_DISTANCE
                    || runMode == MyConstant.INTERVAL_CALORIES) {

                switch (runMode) {
                    case MyConstant.INTERVAL_TIME:
                        int intervalTime = resolveDate(data, RowerDataParam.INTERVAL_TIME_INX + 1, RowerDataParam.INTERVAL_TIME_LEN);
                        rowerDataBean.setSetIntervalTime(intervalTime);
                        break;
                    case MyConstant.INTERVAL_DISTANCE:
                        int intervalDistance = resolveDate(data, RowerDataParam.INTERVAL_DISTANCE_INX + 1, RowerDataParam.INTERVAL_DISTANCE_LEN);
                        rowerDataBean.setSetIntervalDistance(intervalDistance);
                        break;
                    case MyConstant.INTERVAL_CALORIES:
                        int intervalCalorie = resolveDate(data, RowerDataParam.INTERVAL_CALORIE_INX + 1, RowerDataParam.INTERVAL_CALORIE_LEN);
                        rowerDataBean.setSetIntervalCalorie(intervalCalorie);
                        break;
                }

                if (runMode == MyConstant.INTERVAL_DISTANCE) {
                    RowerDataParam.INTERVAL_REST_TIME_INX = 10;
                } else {
                    RowerDataParam.INTERVAL_REST_TIME_INX = 8;
                }
                int intervalRestTime = resolveDate(data, RowerDataParam.INTERVAL_REST_TIME_INX, RowerDataParam.INTERVAL_REST_TIME_LEN);
                rowerDataBean.setReset_time(intervalRestTime);
            }

            onRunDataListener.onRunData(rowerDataBean);
        }

    }

    private synchronized void reSet() {
        c_state = IDLE_BEGIN;
        offset = 0;
    }
}
