package com.bike.ftms.app.ble.heart;

import static com.bike.ftms.app.utils.DataTypeConversion.resolveData;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;

import com.bike.ftms.app.base.MyApplication;
import com.bike.ftms.app.ble.BaseBleManager;
import com.bike.ftms.app.ble.BleManager;
import com.bike.ftms.app.ble.bean.MyScanResult;
import com.bike.ftms.app.ble.help.UuidHelp;
import com.bike.ftms.app.utils.ConvertData;
import com.bike.ftms.app.utils.CustomTimer;
import com.bike.ftms.app.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class BleHeartDeviceManager extends BaseBleManager implements CustomTimer.TimerCallBack {
    private String TAG = BleHeartDeviceManager.class.getSimpleName();
    private static BleHeartDeviceManager instance;

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

    protected final String isConnectTag = "isHrConnect";

    public String getUuid() {
        return UuidHelp.uuidServiceHeartRate;
    }

    @Override
    public void disableCharacterNotifiy() {
        UuidHelp.disableCharacterNotifiy2(mBluetoothGatt, mBluetoothGattServices);
    }

    @Override
    protected String getConnectTag() {
        return isConnectTag;
    }


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


    private short heart_rate = 0;   // 腰带心跳值，需要传给电子表


    /**
     * 连接蓝牙设备
     *
     */
    public void connectDevice(MyScanResult scanResult) {
        Logger.i("2------connectDevice(" + scanResult + ")");
        if (mBluetoothGattServices != null) {
            Logger.e("mBluetoothGattServices.size == " + mBluetoothGattServices.size());
        }
        Logger.i("getScanResults(): " + getScanResults().size());

        if (scanResult.getConnectState() == 1) {
            disableCharacterNotifiy();
            disConnectDevice();
            Logger.e("2------disConnectDevice()");

            if (onScanConnectListener != null) {
                onScanConnectListener.onNotifyData();
            }
            return;
        }
        if (getScanResults() != null && getScanResults().size() != 0) {
            scanResult.setConnectState(2);
            BluetoothDevice device = scanResult.getScanResult().getDevice();
            connectScanResult = new MyScanResult(scanResult.getScanResult(), 2);

            boolean b = refreshDeviceCache(mBluetoothGatt);
            Logger.i("清除蓝牙内部缓存 " + b);
            closeGatt();

            mBluetoothGatt = device.connectGatt(MyApplication.getContext(), false, mGattCallback);
            Logger.i("connectDevice " + device.getAddress());
        }
        if (onScanConnectListener != null) {
            onScanConnectListener.onNotifyData();
        }
    }

    // 释放上次gatt连接资源
    private void closeGatt() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    public int getHeartInt() {
        return heart_rate;
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

                // 设置扫描结果连接状态
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

                disConnectDevice();


                if (onScanConnectListener != null) {
                    onScanConnectListener.onConnectEvent(false, gatt.getDevice().getName());
                }

                if (onScanConnectListener != null) {
                    onScanConnectListener.onNotifyData();
                }
                return;
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
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
                    mBluetoothGatt.discoverServices();
                }
                Logger.e("isHeartbeatConnect=" + isConnect);
                if (onScanConnectListener != null) {
                    onScanConnectListener.onConnectEvent(true, gatt.getDevice().getName());
                }
                bleHeartData.onHRConnected();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Logger.e("断开心跳设备回调");
                disableCharacterNotifiy();

                // 设置扫描结果中的连接状态
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
                disConnectDevice();
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
                BluetoothGattService localGattService = mBluetoothGatt.getService(UUID.fromString(UuidHelp.uuidServiceHeartRate));
                List<BluetoothGattCharacteristic> list = new ArrayList<>();
                if (localGattService != null) {
                    list = localGattService.getCharacteristics();
                }
                UuidHelp.enableCharacteristic(mBluetoothGatt, list, UuidHelp.HR_2A37);
                // UuidHelp.enableCharacteristic(mBluetoothGatt, list, "2a38");
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

            if (characteristic.getUuid().toString().contains(UuidHelp.HR_2A37)) {
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
            BluetoothGattService gattService = mBluetoothGatt.getService(UUID.fromString(getUuid()));
            if (gattService == null) {
                mBluetoothGatt.disconnect();
                mBluetoothGatt = null;
            } else {
                List<BluetoothGattCharacteristic> list = gattService.getCharacteristics();
                UuidHelp.setCharacterNotification(mBluetoothGatt, list, UuidHelp.HR_2A37);
                // UuidHelp.setCharacterNotification(mBluetoothGatt, list, "2a38");
            }
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
        String s = ConvertData.byteArrToBinStr(data);
        // 0x16 == 22   0001 0110
        if ("0".contentEquals(s.subSequence(7, 8))) {
            heart_rate = (short) ConvertData.byteToInt(data[1]);
        } else {
            heart_rate = (short) resolveData(data, 1, 2);
        }

        Logger.i("心跳: " + heart_rate);
        BleManager.getInstance().setHrInt(heart_rate);

        if (bleHeartData != null) {
            bleHeartData.onHeartData(heart_rate);
        }
    }

    protected BleHeartData bleHeartData;

    public void setBleHeartData(BleHeartData bleHeartData) {
        this.bleHeartData = bleHeartData;
    }

    @Override
    public void timerComply(long lastTime, String tag) {
        if (lastTime == 20 && isConnectTag.equals(tag)) {
            if (mBluetoothGatt != null) {
                mBluetoothGatt.disconnect();
                // mBluetoothGatt = null;
            }
            isConnectTimer.closeTimer();
            if (bleHeartData != null) {
                bleHeartData.disHRConnect();
            }
            Logger.d("断开连接");
            return;
        }
    }

    /**
     * 断开蓝牙设备
     */
    protected void disConnectDevice() {
        Logger.e("disConnectDevice()");
        isConnect = false;
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
        }
        if (bleHeartData != null) {
            bleHeartData.onHeartData(0);
        }
        if (bleHeartData != null) {
            bleHeartData.disHRConnect();
        }
    }

    /**
     * APP退出时
     */
    public void destroy() {
        disableCharacterNotifiy();
        disConnectDevice();
        mBluetoothGatt = null;
        bleClosedCallBack = null;
        bleOpenCallBack = null;
        onScanConnectListener = null;
    }

    /**
     * 释放资源
     */
    public void whenBTClosed() {
        // 蓝牙关闭后的操作
        isOpen = false;
        isCanning = false;
        isConnect = false;
    }


    @Override
    protected void reset() {

    }
}
