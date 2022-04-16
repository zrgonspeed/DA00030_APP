package com.bike.ftms.app.ble;

import static com.bike.ftms.app.ble.help.UuidHelp.uuidServiceFTMS;
import static com.bike.ftms.app.utils.DataTypeConversion.resolveData;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Handler;
import android.os.Looper;

import com.bike.ftms.app.R;
import com.bike.ftms.app.base.MyApplication;
import com.bike.ftms.app.ble.base.OnRunDataListener;
import com.bike.ftms.app.ble.bean.MyScanResult;
import com.bike.ftms.app.ble.bean.rundata.raw.RowerDataBean1;
import com.bike.ftms.app.ble.bean.rundata.raw.RowerDataBean2;
import com.bike.ftms.app.ble.category.BikeManager;
import com.bike.ftms.app.ble.category.BoatManager;
import com.bike.ftms.app.ble.category.SkiManager;
import com.bike.ftms.app.ble.heart.BleHeartDeviceManager;
import com.bike.ftms.app.ble.help.UuidHelp;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.common.RowerDataParam;
import com.bike.ftms.app.manager.storage.SpManager;
import com.bike.ftms.app.serial.SerialCommand;
import com.bike.ftms.app.serial.SerialData;
import com.bike.ftms.app.utils.BasisTimesUtils;
import com.bike.ftms.app.utils.ConvertData;
import com.bike.ftms.app.utils.CustomTimer;
import com.bike.ftms.app.utils.DataTypeConversion;
import com.bike.ftms.app.utils.Logger;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import tech.gujin.toast.ToastUtil;

@SuppressLint({"MissingPermission", "WrongConstant"})
public class BleManager extends BaseBleManager {
    private String TAG = BleManager.class.getSimpleName();
    private static BleManager instance;

    protected final String isConnectTag = "isConnect";

    // 2ada 用
    public static final byte RUN_STATUS_RUNNING = 0x01;
    public static final byte RUN_STATUS_STOP = 0x00;

    // 2ad3 用
    public static final byte STATUS_IDLE = 0x01;
    public static final byte STATUS_RUNNING = 0x0D;
    public static final byte STATUS_POST = 0x0F;
    public static int status = STATUS_IDLE;

    private byte runStatus = RUN_STATUS_STOP;

    public static int deviceType = -1;  // 电子表机型
    public static int categoryType = -1;    // 电子表分类

    private static final long SEND_VERIFY_TIME = 2000; // 发送校验码延迟时间
    public boolean setBleDataInx = false;
    private boolean isToExamine = false;
    private boolean isSendVerifyData = false;
    private boolean onlyShowDzbHr = false;

    private CustomTimer isVerifyConnectTimer;
    private final String isVerifyConnectTag = "isVerifyConnect";

    private RowerDataBean1 rowerDataBean1 = new RowerDataBean1();
    private RowerDataBean2 rowerDataBean2 = new RowerDataBean2();
    private int tempInterval1 = 0;
    private int tempInterval2 = 0;
    private boolean canSave = false;

    protected BluetoothGattCharacteristic mBluetoothGattCharacteristic;//特征值(用于收发数据)   当前是ffe9发

    public byte getRunStatus() {
        return runStatus;
    }

    /**
     * 连接超时，回调
     */
/*    private Runnable mConnTimeOutRunnable = () -> {
        Logger.e("连接设备超时");
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
        }
    };*/

    protected OnRunDataListener onRunDataListener;//运动数据回调

    public OnRunDataListener getOnRunDataListener() {
        return onRunDataListener;
    }

    /**
     * 运动数据回调
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

    public void connectDevice(MyScanResult scanResult) {
        Logger.i("2------connectDevice(" + scanResult + ")");

        if (connectScanResult != null && connectScanResult.getConnectState() == 1) {
            Logger.e("2------disConnectDevice()");

            disableCharacterNotifiy();
            boolean b = refreshDeviceCache(mBluetoothGatt);
            Logger.e("断开 清除蓝牙内部缓存 " + b);
            closeGatt();
            disConnectDevice();

            // printScanResults();
            // printConnectedScanResult();

            if (connectScanResult != null) {
                connectScanResult.setConnectState(0);
            }
            if (onScanConnectListener != null) {
                onScanConnectListener.onNotifyData();
            }
            return;
        }
        if (getScanResults() != null && getScanResults().size() != 0) {
            scanResult.setConnectState(2);
            connectScanResult = scanResult;

            rowerDataBean1 = new RowerDataBean1();
            reset();
            boolean b = refreshDeviceCache(mBluetoothGatt);
            Logger.i("连接 清除蓝牙内部缓存 " + b);
            closeGatt();

            //第二个参数表示是否需要自动连接。如果设置为 true, 表示如果设备断开了，会不断的尝试自动连接。设置为 false 表示只进行一次连接尝试。
            BluetoothDevice device = scanResult.getScanResult().getDevice();
            mBluetoothGatt = device.connectGatt(MyApplication.getContext(), false, mGattCallback);

            //处理超时连接的方法
            // mHandler.postDelayed(mConnTimeOutRunnable, 5 * 1000);
            Logger.i("connectDevice " + device.getAddress() + "  " + device.getName() + "  ++++++++++++++++++++++++++++++++++++");

        }
        if (onScanConnectListener != null) {
            onScanConnectListener.onNotifyData();
        }
    }

    private void resetDeviceType() {
        isConnect = false;
        deviceType = -1;
        categoryType = -1;
        isToExamine = false;

        if (onRunDataListener != null) {
            onRunDataListener.disConnect();
        }
    }


    /**
     * 电子表设备连接回调
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

            // mHandler.removeCallbacks(mConnTimeOutRunnable);

            if (status != BluetoothGatt.GATT_SUCCESS) {
                if (mBluetoothGatt != null) {
                    Logger.e("mBluetoothGatt.close();");
                    mBluetoothGatt.close();
                }

                // 界面数据清0
                rowerDataBean1 = new RowerDataBean1();
                if (onRunDataListener != null) {
                    onRunDataListener.onRunData(rowerDataBean1);
                }

                // 设置扫描结果状态 0
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
                resetDeviceType();

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
                // 为对应的扫描结果设置连接状态 3, 连接上但在校验中
                for (MyScanResult myScanResult : mScanResults) {
                    if (myScanResult.getScanResult().getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                        myScanResult.setConnectState(3);
                        startTimerOfIsConnect();
                        if (connectScanResult.getScanResult().getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                            connectScanResult.setConnectState(3);
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
                // 暂时没用
                if (onScanConnectListener != null) {
                    onScanConnectListener.onConnectEvent(true, gatt.getDevice().getName());
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Logger.e("断开设备回调");
                mHandler.removeCallbacksAndMessages(null);
                disableCharacterNotifiy();

                // 保存运动数据
                saveRowDataBean1();

                rowerDataBean1 = new RowerDataBean1();
                if (onRunDataListener != null) {
                    onRunDataListener.onRunData(rowerDataBean1);
                }
                // 设置扫描结果中的状态 0
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
                resetDeviceType();
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
                Logger.i("5------发现服务");

                mBluetoothGattServices = mBluetoothGatt.getServices();
                UuidHelp.printBleServices(mBluetoothGatt);

                /// 将指定service的character加入list
                BluetoothGattService ftmsService = mBluetoothGatt.getService(UUID.fromString(uuidServiceFTMS));
                List<BluetoothGattCharacteristic> list;
                if (ftmsService != null) {
                    list = ftmsService.getCharacteristics();
                    // 指定一个发送相关的service, 把需要的character加入list
                    BluetoothGattService sendService = mBluetoothGatt.getService(UUID.fromString(UuidHelp.uuidSendData));
                    if (sendService != null) {
                        list.addAll(sendService.getCharacteristics());
                        UuidHelp.enableCharacteristic(mBluetoothGatt, list, UuidHelp.CUSTOM_FFE0);
                        UuidHelp.enableCharacteristic(mBluetoothGatt, list, UuidHelp.FTMS_2AD3);
                        UuidHelp.enableCharacteristic(mBluetoothGatt, list, UuidHelp.FTMS_2ADA);
                        registrationGattCharacteristic();//注册通知
                    }
                }
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

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Logger.i("onCharacteristicRead::" + ConvertData.byteArrayToHexString(characteristic.getValue(), characteristic.getValue().length));

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            isConnectTimer.setmAllTime(0L);
            byte[] data = characteristic.getValue();

            String uuid = characteristic.getUuid().toString().substring(0, 8);
            Logger.i(uuid + ",::" + ConvertData.byteArrayToHexString(data, data.length));
            Logger.d(Arrays.toString(data));

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
            Logger.i("descriptor.getUuid().toString() " + descriptor.getUuid().toString());

            byte[] value = descriptor.getValue();
            if (value == null) {
                Logger.i("onDescriptorWrite() value数组null");
            } else {
                Logger.i("onDescriptorWrite " + ConvertData.byteArrayToHexString(value, value.length));
            }
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
            // Logger.i("onMtuChanged");
        }
    };


    /**
     * 注册特征GATT 通知
     */
    private void registrationGattCharacteristic() {
        if (mBluetoothGattServices != null) {
            BluetoothGattService ftmsService = mBluetoothGatt.getService(UUID.fromString(UuidHelp.uuidServiceFTMS));
            if (ftmsService == null) {
                mBluetoothGatt.disconnect();
                mBluetoothGatt = null;
                return;
            }

            BluetoothGattService sendService = mBluetoothGatt.getService(UUID.fromString(UuidHelp.uuidSendData));
            if (sendService != null) {
                for (BluetoothGattCharacteristic gattCharacteristic : sendService.getCharacteristics()) {
                    if (gattCharacteristic.getUuid().toString().contains(UuidHelp.CUSTOM_FFE9)) {
                        mBluetoothGattCharacteristic = gattCharacteristic;
                        Logger.i(UuidHelp.CUSTOM_FFE9 + ",设置为发送::");
                    }
                }

                UuidHelp.setCharacterNotification(mBluetoothGatt, sendService.getCharacteristics(), UuidHelp.CUSTOM_FFE0);
                UuidHelp.setCharacterNotification(mBluetoothGatt, ftmsService.getCharacteristics(), UuidHelp.FTMS_2AD3);
                UuidHelp.setCharacterNotification(mBluetoothGatt, ftmsService.getCharacteristics(), UuidHelp.FTMS_2ADA);

                mHandler.postDelayed(() -> {
                    if (!isSendVerifyData) {
                        isSendVerifyData = true;
                        sendVerifyData();
                    }
                }, SEND_VERIFY_TIME);
            }
        }

    }

    /**
     * 发送数据 ffe9
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
            Logger.w("send" + mBluetoothGattCharacteristic.getUuid().toString().substring(4, 8) + ",::" + ConvertData.byteArrayToHexString(sendBytes, sendBytes.length));
        } else {
            Logger.e("发送到电子表失败: mBluetoothGatt == " + mBluetoothGatt + "    mBluetoothGattCharacteristic == " + mBluetoothGattCharacteristic);
        }
        return r;
    }


    /**
     * 校验命令
     */
    private void sendVerifyData() {
        String[] dates = BasisTimesUtils.getCurDate().split("-");    //22-02-26
        Logger.i("年月日" + Arrays.toString(dates));

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

    /**
     * 响应ffe0
     *
     * @param data
     */
    private void sendRespondData(byte[] data) {
        // 0xfe 0x00 0x41 0x02  0xb4 0x00 0xcf 0x26 0xff
        byte[] bytes = new byte[9];
        bytes[0] = (byte) SerialCommand.PACK_FRAME_HEADER;
        bytes[1] = (byte) 0x00;
        System.arraycopy(data, 1, bytes, 2, 2);

        // 把腰带心跳值发给电子表
        byte[] hrBytes = ConvertData.shortToBytes((short) BleHeartDeviceManager.getInstance().getHeartInt());
        bytes[4] = hrBytes[0]; // 心跳  低位在前，高位在后
        bytes[5] = hrBytes[1];

        byte[] respondByte = new byte[64];
        int len = SerialData.comPackage(bytes, respondByte, bytes.length - 3);
        sendDescriptorByte(respondByte, len);
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

    /**
     * 接收设备数据
     */
    private void rxDataPackage(byte[] data, String uuid) {
        rowerDataBean1.setDeviceType(deviceType);
        rowerDataBean1.setCategoryType(categoryType);

        // 是否发送校正数据
        if (!isSendVerifyData) {
            isSendVerifyData = true;
            sendVerifyData();
            return;
        }

        if (uuid.contains(UuidHelp.CUSTOM_FFE0)) {
            setRunData_FFE0(data);
            return;
        }

        if (!isToExamine) {
            Logger.i("未校正通过，不接收数据");
            return;
        }

        if (uuid.contains(UuidHelp.FTMS_2ADA)) {
            setRunData_2ADA(data);
            return;
        }

        if (uuid.contains(UuidHelp.FTMS_2AD3)) {
            status = data[1];
            if (status == STATUS_POST) {
                // 保存数据
                saveRowDataBean1();
            }
            return;
        }

        // 单独显示电子表的心跳
        if (onlyShowDzbHr) {
            if (!BleHeartDeviceManager.getInstance().isConnected()) {
                rowerDataBean1.setHeart_rate(RowerDataParam.HEART_RATE_INX == -1 ? 0 : resolveData(data, RowerDataParam.HEART_RATE_INX, RowerDataParam.HEART_RATE_LEN));
                if (onRunDataListener != null) {
                    onRunDataListener.onRunData(rowerDataBean1);
                }
            }

            return;
        }

        //-------------------------------------------------------------------------------

        switch (categoryType) {
            case MyConstant.CATEGORY_BOAT: {
                BoatManager.getInstance().setRunData(data, rowerDataBean1);
            }
            break;
            case MyConstant.CATEGORY_BIKE: {
                BikeManager.getInstance().setRunData(data, rowerDataBean1);
            }
            break;
            case MyConstant.CATEGORY_SKI: {
                SkiManager.getInstance().setRunData(data, rowerDataBean1);
            }
            break;
            case MyConstant.CATEGORY_STEP: {
            }
            break;
        }
    }


    private void setRunData_2ADA(byte[] data) {
        // 单独显示心跳
        if (data[3] == 0 && data[4] == 1) {
            onlyShowDzbHr = true;
        } else {
            onlyShowDzbHr = false;
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
            } else if (rowerDataBean1.getRunMode() == MyConstant.INTERVAL_TIME || rowerDataBean1.getRunMode() == MyConstant.CUSTOM_INTERVAL_TIME) {
                if (rowerDataBean1.getInterval() > 0 || rowerDataBean1.getDistance() >= 10) {
                    canSave = true;
                }
            } else if (rowerDataBean1.getRunMode() == MyConstant.INTERVAL_DISTANCE || rowerDataBean1.getRunMode() == MyConstant.CUSTOM_INTERVAL_DISTANCE) {
                if (rowerDataBean1.getInterval() > 0 || rowerDataBean1.getTime() >= 5) {
                    canSave = true;
                }
            } else if (rowerDataBean1.getRunMode() == MyConstant.INTERVAL_CALORIES || rowerDataBean1.getRunMode() == MyConstant.CUSTOM_INTERVAL_CALORIES) {
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
            // 数据太少不管它
            return;
        }

        // 校对CRC码 和 获取机种标识
        // ffe9   0xfe 0x40 0x01 0x22 0x02 0x28 0x68 0xc5 0xff
        // ffe0   0xfe 0x40 0x01 0x4a 0x11 0x01 0xdd 0x05 0xff                  机型1个字节
        // ffe0   0xfe 0x40 0x01 0x4a 0x11 0x01 0x00 0xdd 0x05 0xff             机型2个字节
        if (data.length > 8 && data[1] == 0x40 && data[2] == 0x01) {
            byte[] unPackData = new byte[32];
            int len = SerialData.comUnPackage(data, unPackData, data.length);
            Logger.i(ConvertData.byteArrayToHexString(unPackData, len));

            byte[] resultData = new byte[len];
            System.arraycopy(unPackData, 0, resultData, 0, len);
            Logger.i(ConvertData.byteArrayToHexString(resultData, resultData.length));

            data = resultData;
            // 日期和机型校验
            {
                String[] dates = BasisTimesUtils.getCurDate().split("-");
                byte[] date = new byte[3];
                date[0] = (byte) Integer.parseInt(dates[0], 16);
                date[1] = (byte) Integer.parseInt(dates[1], 16);
                date[2] = (byte) Integer.parseInt(dates[2], 16);
                byte[] calCRCBytes = ConvertData.shortToBytes(SerialData.calCRCByTable(ConvertData.subBytes(date, 0, date.length), date.length));
                Logger.i("calCRCBytes[0] == " + ConvertData.toHexString(calCRCBytes[0]) + " calCRCBytes[1] == " + ConvertData.toHexString(calCRCBytes[1]));
                Logger.i("raw calCRCBytes[0] == " + calCRCBytes[0] + " calCRCBytes[1] == " + calCRCBytes[1]);

                boolean dateOK = false;
                boolean deviceTypeOK = false;

                if (calCRCBytes[0] == data[3] && calCRCBytes[1] == data[4]) {
                    dateOK = true;
                    isVerifyConnectTimer.closeTimer();
                }

                if (!dateOK) {
                    Logger.i("日期校验错误");
                    return;
                }

                // 获取机型
                int tempDeviceType = initDeviceType(data);
                if (tempDeviceType > 0 && tempDeviceType < MyConstant.deviceNames.length) {
                    deviceTypeOK = true;
                }

                if (!deviceTypeOK) {
                    Logger.i("机型校验错误");
                    return;
                }

                connectScanResult.setConnectState(1);
                for (MyScanResult result : mScanResults) {
                    if (result.getScanResult().getDevice().getAddress().equals(connectScanResult.getScanResult().getDevice().getAddress())) {
                        result.setConnectState(1);
                    }
                }
                if (onScanConnectListener != null) {
                    onScanConnectListener.onNotifyData();
                }

                isToExamine = true;
                deviceType = tempDeviceType;
                categoryType = MyConstant.getCategory(deviceType);


                rowerDataBean1.setDeviceType(deviceType);
                rowerDataBean1.setCategoryType(categoryType);
                // 没用
                SpManager.setTreadmill_flag(deviceType);
                Logger.i("当前机型: " + deviceType);
            }

            // 注册特征值
            {
                // 指定一个service
                BluetoothGattService localGattService = mBluetoothGatt.getService(UUID.fromString(uuidServiceFTMS));
                // 获取这个service下的所有character
                List<BluetoothGattCharacteristic> list = new ArrayList<>();
                if (localGattService != null) {
                    list = localGattService.getCharacteristics();
                }

                // 注册不同机型需要的特征值


                switch (MyConstant.getCategory(deviceType)) {
                    case MyConstant.CATEGORY_BOAT: {
                        // 2ad1  划船器
                        UuidHelp.enableCharacteristic(mBluetoothGatt, list, UuidHelp.FTMS_2AD1);
                        UuidHelp.setCharacterNotification(mBluetoothGatt, list, UuidHelp.FTMS_2AD1);
                    }
                    break;
                    case MyConstant.CATEGORY_BIKE: {
                        // 2ad2  室内自行车
                        UuidHelp.enableCharacteristic(mBluetoothGatt, list, UuidHelp.FTMS_2AD2);
                        UuidHelp.setCharacterNotification(mBluetoothGatt, list, UuidHelp.FTMS_2AD2);
                    }
                    break;
                    case MyConstant.CATEGORY_SKI: {
                        // 滑雪机
                        UuidHelp.enableCharacteristic(mBluetoothGatt, list, UuidHelp.FTMS_2AD1);
                        UuidHelp.setCharacterNotification(mBluetoothGatt, list, UuidHelp.FTMS_2AD1);
                    }
                    break;
                    case MyConstant.CATEGORY_STEP: {
                    }
                    break;
                }
            }

            onRunDataListener.connected();
            return;
        }

        // 运动数据
        if (data[1] == 0x41 && data[2] == 0x02 && isToExamine) {
            {
                sendRespondData(data);
                if (runStatus == RUN_STATUS_RUNNING) {
                    rowerDataBean1.setDrag(resolveData(data, RowerDataParam.DRAG_INX, RowerDataParam.DRAG_LEN));
                    rowerDataBean1.setInterval(resolveData(data, RowerDataParam.INTERVAL_INX, RowerDataParam.INTERVAL_LEN));

                    int runMode = rowerDataBean1.getRunMode();

                    switch (BleManager.categoryType) {
                        case MyConstant.CATEGORY_BOAT: {
                            // 时间4字节
                            if (BleManager.deviceType == MyConstant.DEVICE_AA01990) {
                                rowerDataBean1.setReset_time(resolveData(data, 17, 2));
                                if (MyConstant.isCustomIntervalMode(runMode)) {
                                    // 自定义间歇模式
                                    switch (runMode) {
                                        case MyConstant.CUSTOM_INTERVAL_TIME:
                                            rowerDataBean1.setTime(resolveData(data, 19, 4));
                                            rowerDataBean1.setSetIntervalTime(resolveData(data, 7, 4));
                                            break;
                                        case MyConstant.CUSTOM_INTERVAL_DISTANCE:
                                            rowerDataBean1.setSetIntervalDistance(resolveData(data, 11, 4));
                                            break;
                                        case MyConstant.CUSTOM_INTERVAL_CALORIES:
                                            rowerDataBean1.setSetIntervalCalorie(resolveData(data, 15, 2));
                                            break;
                                    }
                                }
                            }
                        }
                        break;
                        case MyConstant.CATEGORY_BIKE: {
                            rowerDataBean1.setOneKmTime(resolveData(data, RowerDataParam.ONE_KM_TIME_INX, RowerDataParam.ONE_KM_TIME_LEN));
                            rowerDataBean1.setAveOneKmTime(resolveData(data, RowerDataParam.AVERAGE_ONE_KM_TIME_INX, RowerDataParam.AVERAGE_ONE_KM_TIME_LEN));
                            rowerDataBean1.setSplitOneKmTime(resolveData(data, RowerDataParam.SPLIT_ONE_KM_TIME_INX, RowerDataParam.SPLIT_ONE_KM_TIME_LEN));
                            rowerDataBean1.setSplitCal(resolveData(data, RowerDataParam.SPLIT_CAL_INX, RowerDataParam.SPLIT_CAL_LEN));
                        }
                        break;
                        case MyConstant.CATEGORY_SKI: {

                        }
                        break;
                    }


                    if (MyConstant.isIntervalMode(runMode) || MyConstant.isCustomIntervalMode(runMode)) {
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
                    } else if (MyConstant.isGoalMode(runMode)) {
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

    }

    private void saveRowDataBean1() {
        // TODO: 2021/11/12 应该判断是否符合保存  如 运动不足5秒，等

        if (rowerDataBean1.getRunMode() == MyConstant.GOAL_TIME) {
            // 时间是倒数的，用距离判断
            if (rowerDataBean1.getDistance() >= 10) {
                canSave = true;
            }
        } else if (rowerDataBean1.getRunMode() == MyConstant.INTERVAL_TIME || rowerDataBean1.getRunMode() == MyConstant.CUSTOM_INTERVAL_TIME) {
            if (rowerDataBean1.getInterval() > 0 || rowerDataBean1.getDistance() >= 10) {
                canSave = true;
            }
        } else if (rowerDataBean1.getRunMode() == MyConstant.INTERVAL_DISTANCE || rowerDataBean1.getRunMode() == MyConstant.CUSTOM_INTERVAL_DISTANCE) {
            if (rowerDataBean1.getInterval() > 0 || rowerDataBean1.getTime() >= 5) {
                canSave = true;
            }
        } else if (rowerDataBean1.getRunMode() == MyConstant.INTERVAL_CALORIES || rowerDataBean1.getRunMode() == MyConstant.CUSTOM_INTERVAL_CALORIES) {
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
            rowerDataBean1.setDeviceType(deviceType);
            rowerDataBean1.setCategoryType(MyConstant.getCategory(deviceType));
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

    private static int initDeviceType(byte[] data) {
        int temp = -1;
        if (data.length == 10) {
            temp = DataTypeConversion.doubleBytesToIntLiterEnd(data, 5);
            Logger.i("2个字节机型");
        } else {
            if (data.length == 9) {
                temp = data[5];
                Logger.i("1个字节机型");
            }
        }
        return temp;
    }

    private static void tempSave(LitePalSupport support) {
        support.save();
    }

    /**
     * 恢复默认值 -> 连接设备时
     */
    protected void reset() {
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
     * 断开设备
     */
    public void disConnectDevice() {
        Logger.e("disConnectDevice()");
        reset();
        resetDeviceType();
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
        }
    }

    public void whenBTClosed() {
        // 蓝牙关闭后的操作
        isOpen = false;
        isCanning = false;
        resetDeviceType();
        reset();
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
        onRunDataListener = null;
    }

    public void setHrInt(short heart_rate) {
        if (rowerDataBean1 != null) {
            rowerDataBean1.setHeart_rate(heart_rate);
        }
    }

    public String getUuid() {
        return uuidServiceFTMS;
    }

    @Override
    public void disableCharacterNotifiy() {
        UuidHelp.disableCharacterNotifiy1(mBluetoothGatt, mBluetoothGattServices);
    }

    @Override
    protected String getConnectTag() {
        return isConnectTag;
    }
}
