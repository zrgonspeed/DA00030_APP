package com.bike.ftms.app.ble.help;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import com.bike.ftms.app.utils.Logger;

import java.util.List;

public class UuidHelp {
    /**
     * 启用特征值通知
     *
     * @param bluetoothGatt
     * @param list
     * @param uuid_4
     */
    public static void enableCharacteristic(BluetoothGatt bluetoothGatt, List<BluetoothGattCharacteristic> list, String uuid_4) {
        for (BluetoothGattCharacteristic gattCharacteristic : list) {
            if (gattCharacteristic.getUuid().toString().contains(uuid_4)) {
                List<BluetoothGattDescriptor> bluetoothGattDescriptors = gattCharacteristic.getDescriptors();
                for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptors) {
                    boolean r = bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    bluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
                    Logger.d("" + gattCharacteristic.getUuid().toString() + ",bluetoothGattDescriptor " + r);
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
    public static void disableCharacterNotifiy(BluetoothGatt bluetoothGatt, List<BluetoothGattService> bluetoothGattServices) {
        Logger.e("disableCharacterNotifiy()");
        if (bluetoothGattServices == null || bluetoothGatt == null) {
            return;
        }
        for (BluetoothGattService service : bluetoothGattServices) {
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                if (characteristic.getUuid().toString().contains("ffe0") ||
                        characteristic.getUuid().toString().contains("2ada") ||
                        characteristic.getUuid().toString().contains("2ad1") ||
                        characteristic.getUuid().toString().contains("2ad2") ||
                        characteristic.getUuid().toString().contains("2ad3")
                ) {
                    bluetoothGatt.setCharacteristicNotification(characteristic, false);
                }
            }
        }
    }
}
