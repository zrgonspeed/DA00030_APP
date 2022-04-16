package com.bike.ftms.app.ble.help;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import com.bike.ftms.app.ble.BleManager;
import com.bike.ftms.app.ble.heart.BleHeartDeviceManager;
import com.bike.ftms.app.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class UuidHelp {
    public static final String uuidServiceFTMS = "00001826-0000-1000-8000-00805f9b34fb";      //  Fitness Machine	    健康设备     1826
    public static final String FTMS_2ADA = "2ada";  // Fitness Machine Status 	健身设备状态
    public static final String FTMS_2AD1 = "2ad1";  // Rower Data    桨手数据
    public static final String FTMS_2AD2 = "2ad2";  // Indoor Bike Data 室内自行车数据
    public static final String FTMS_2AD3 = "2ad3";  // Training Status	    训练状况

    public static final String uuidSendData = "0000ffe5-0000-1000-8000-00805f9b34fb";  // 自定义服务uuid
    public static final String CUSTOM_FFE0 = "ffe0";    // 自定义特征,  中心设备发
    public static final String CUSTOM_FFE9 = "ffe9";    // 自定义特征, 手机发


    public static final String uuidServiceHeartRate = "0000180d-0000-1000-8000-00805f9b34fb"; // "Heart Rate"
    public static final String HR_2A37 = "2a37";    // "Heart Rate Measurement"
    public static final String HR_2A38 = "2a38";    // "Body Sensor Location"

    /**
     * 启用特征值通知
     *
     * @param bluetoothGatt
     * @param list
     * @param uuid_4
     */
    public static void enableCharacteristic(BluetoothGatt bluetoothGatt, List<BluetoothGattCharacteristic> list, String uuid_4) {
        Logger.d("enableCharacteristic()-------");
        for (BluetoothGattCharacteristic gattCharacteristic : list) {
            if (gattCharacteristic.getUuid().toString().contains(uuid_4)) {
                List<BluetoothGattDescriptor> bluetoothGattDescriptors = gattCharacteristic.getDescriptors();
                for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptors) {
                    boolean r = bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    bluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
                    Logger.d("bluetoothGatt.writeDescriptor-----" + gattCharacteristic.getUuid().toString() + ",bluetoothGattDescriptor " + r);
                }
            }
        }
    }

    /**
     * 启用特征值通知
     *
     * @param bluetoothGatt
     * @param list
     * @param s
     */
    public static void setCharacterNotification(BluetoothGatt bluetoothGatt, List<BluetoothGattCharacteristic> list, String s) {
        for (BluetoothGattCharacteristic gattCharacteristic : list) {
            if (gattCharacteristic.getUuid().toString().contains(s)) {//接收通道
                if (bluetoothGatt != null) {
                    boolean enabled = bluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                    Logger.i(s + ",注册通知::" + enabled);
                }
            }
        }
    }

    /**
     * 禁用特征值通知
     */
    public static void disableCharacterNotifiy1(BluetoothGatt bluetoothGatt, List<BluetoothGattService> bluetoothGattServices) {
        Logger.e("disableCharacterNotifiy()");
        if (bluetoothGattServices == null || bluetoothGatt == null) {
            return;
        }
        for (BluetoothGattService service : bluetoothGattServices) {
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                if (characteristic.getUuid().toString().contains(CUSTOM_FFE0) ||
                        characteristic.getUuid().toString().contains(CUSTOM_FFE9) ||
                        characteristic.getUuid().toString().contains(FTMS_2ADA) ||
                        characteristic.getUuid().toString().contains(FTMS_2AD1) ||
                        characteristic.getUuid().toString().contains(FTMS_2AD2) ||
                        characteristic.getUuid().toString().contains(FTMS_2AD3)
                ) {
                    bluetoothGatt.setCharacteristicNotification(characteristic, false);
                }
            }
        }
    }

    /**
     * 禁用特征值通知
     */
    public static void disableCharacterNotifiy2(BluetoothGatt bluetoothGatt, List<BluetoothGattService> bluetoothGattServices) {
        Logger.e("disableCharacterNotifiy2()");
        if (bluetoothGattServices == null || bluetoothGatt == null) {
            return;
        }
        for (BluetoothGattService service : bluetoothGattServices) {
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                if (characteristic.getUuid().toString().contains(HR_2A37)) {
                    bluetoothGatt.setCharacteristicNotification(characteristic, false);
                }
            }
        }
    }

    public static void printBleServices(BluetoothGatt mBluetoothGatt) {
        Logger.d("这个蓝牙设备的所有service: ");
        List<BluetoothGattService> services = mBluetoothGatt.getServices();
        for (BluetoothGattService service : services) {
            Logger.d("service uuid = " + service.getUuid());
            // 指定一个service
            // BluetoothGattService localGattService = mBluetoothGatt.getService(UUID.fromString(service.getUuid().toString()));
            // 获取这个service下的所有character
            List<BluetoothGattCharacteristic> list = new ArrayList<>();
            if (service != null) {
                list = service.getCharacteristics();
                for (BluetoothGattCharacteristic c : list) {
                    Logger.d("--character = " + c.getUuid());
                }
            }
        }
    }
}
