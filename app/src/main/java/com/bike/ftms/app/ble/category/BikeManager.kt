package com.bike.ftms.app.ble.category

import com.bike.ftms.app.ble.BleManager
import com.bike.ftms.app.ble.base.CategoryType
import com.bike.ftms.app.ble.bean.rundata.raw.RowerDataBean1
import com.bike.ftms.app.ble.category.BikeManager
import com.bike.ftms.app.ble.heart.BleHeartDeviceManager
import com.bike.ftms.app.common.RowerDataParam
import com.bike.ftms.app.utils.ConvertData
import com.bike.ftms.app.utils.DataTypeConversion
import com.bike.ftms.app.utils.Logger

class BikeManager private constructor() : CategoryType() {
    // 室内自行车数据
    private fun setRunData_2AD2(data: ByteArray, rowerDataBean1: RowerDataBean1) {
        if (BleManager.getInstance().onRunDataListener == null) {
            return
        }
        if (BleManager.getInstance().runStatus == BleManager.RUN_STATUS_STOP) {
            return
        }

        // bike
        rowerDataBean1.instSpeed =
            if (RowerDataParam.INSTANTANEOUS_SPEED_INX == -1) 0 else DataTypeConversion.resolveData(
                data,
                RowerDataParam.INSTANTANEOUS_SPEED_INX,
                RowerDataParam.INSTANTANEOUS_SPEED_LEN
            ) / 2
        rowerDataBean1.instRpm =
            if (RowerDataParam.INSTANTANEOUS_RPM_INX == -1) 0 else DataTypeConversion.resolveData(
                data,
                RowerDataParam.INSTANTANEOUS_RPM_INX,
                RowerDataParam.INSTANTANEOUS_RPM_LEN
            ) / 2
        rowerDataBean1.level =
            if (RowerDataParam.RESISTANCE_LEVEL_INX == -1) 0 else DataTypeConversion.resolveData(
                data,
                RowerDataParam.RESISTANCE_LEVEL_INX,
                RowerDataParam.RESISTANCE_LEVEL_LEN
            )

        // boat
        rowerDataBean1.sm =
            if (RowerDataParam.STROKE_RATE_INX == -1) 0 else DataTypeConversion.resolveData(
                data,
                RowerDataParam.STROKE_RATE_INX,
                RowerDataParam.STROKE_RATE_LEN
            ) / 2
        rowerDataBean1.strokes =
            if (RowerDataParam.STROKE_COUNT_INX == -1) 0 else DataTypeConversion.resolveData(
                data,
                RowerDataParam.STROKE_COUNT_INX,
                RowerDataParam.STROKE_COUNT_LEN
            )

        // ftms通用
        rowerDataBean1.distance =
            if (RowerDataParam.TOTAL_DISTANCE_INX == -1) 0 else DataTypeConversion.resolveData(
                data,
                RowerDataParam.TOTAL_DISTANCE_INX,
                RowerDataParam.TOTAL_DISTANCE_LEN
            ).toLong()
        rowerDataBean1.five_hundred =
            if (RowerDataParam.INSTANTANEOUS_PACE_INX == -1) 0 else DataTypeConversion.resolveData(
                data,
                RowerDataParam.INSTANTANEOUS_PACE_INX,
                RowerDataParam.INSTANTANEOUS_PACE_LEN
            ).toLong()
        rowerDataBean1.calorie =
            if (RowerDataParam.TOTAL_ENERGY_INX == -1) 0 else DataTypeConversion.resolveData(
                data,
                RowerDataParam.TOTAL_ENERGY_INX,
                RowerDataParam.TOTAL_ENERGY_LEN
            ).toLong()
        rowerDataBean1.calories_hr =
            if (RowerDataParam.ENERGY_PER_HOUR_INX == -1) 0 else DataTypeConversion.resolveData(
                data,
                RowerDataParam.ENERGY_PER_HOUR_INX,
                RowerDataParam.ENERGY_PER_HOUR_LEN
            )
        if (!BleHeartDeviceManager.getInstance().isConnected) {
            rowerDataBean1.heart_rate =
                if (RowerDataParam.HEART_RATE_INX == -1) 0 else DataTypeConversion.resolveData(
                    data,
                    RowerDataParam.HEART_RATE_INX,
                    RowerDataParam.HEART_RATE_LEN
                )
        }
        rowerDataBean1.watts =
            if (RowerDataParam.INSTANTANEOUS_POWER_INX == -1) 0 else DataTypeConversion.resolveData(
                data,
                RowerDataParam.INSTANTANEOUS_POWER_INX,
                RowerDataParam.INSTANTANEOUS_POWER_LEN
            )
        rowerDataBean1.ave_watts =
            if (RowerDataParam.AVERAGE_POWER_INX == -1) 0 else DataTypeConversion.resolveData(
                data,
                RowerDataParam.AVERAGE_POWER_INX,
                RowerDataParam.AVERAGE_POWER_LEN
            )
        rowerDataBean1.ave_five_hundred =
            if (RowerDataParam.AVERAGE_PACE_INX == -1) 0 else DataTypeConversion.resolveData(
                data,
                RowerDataParam.AVERAGE_PACE_INX,
                RowerDataParam.AVERAGE_PACE_LEN
            ).toLong()
        if (RowerDataParam.REMAINING_TIME_INX == -1) {
            rowerDataBean1.time =
                if (RowerDataParam.ELAPSED_TIME_INX == -1) 0 else DataTypeConversion.resolveData(
                    data,
                    RowerDataParam.ELAPSED_TIME_INX,
                    RowerDataParam.ELAPSED_TIME_LEN
                ).toLong()
        } else {
            rowerDataBean1.time =
                if (RowerDataParam.REMAINING_TIME_INX == -1) 0 else DataTypeConversion.resolveData(
                    data,
                    RowerDataParam.REMAINING_TIME_INX,
                    RowerDataParam.REMAINING_TIME_LEN
                ).toLong()
        }
        // 只精确到秒，毫秒域为 000
        rowerDataBean1.date = System.currentTimeMillis() / 1000 * 1000
        if (BleManager.getInstance().onRunDataListener != null) {
            BleManager.getInstance().onRunDataListener.onRunData(rowerDataBean1)
        }
    }

    override fun setRunData(data: ByteArray, rowerDataBean1: RowerDataBean1) {
        setBleDataInxOfBike(byteArrayOf(data[0], data[1]))
        setRunData_2AD2(data, rowerDataBean1)
    }

    /**
     * FTMS
     */
    private fun setBleDataInxOfBike(data: ByteArray) {
        if (BleManager.getInstance().setBleDataInx) {
            return
        }
        BleManager.getInstance().setBleDataInx = true
        var inxLen = 2
        var s = ConvertData.byteArrToBinStr(data)
        Logger.d("FTMS协议---BIKE--低位在前高位在后-------s == $s")
        val strings = s.split(",").toTypedArray()
        val stringBuffer = StringBuffer()
        for (string in strings) {
            for (i in string.length - 1 downTo 0) {
                stringBuffer.append(string.subSequence(i, i + 1))
            }
        }
        s = stringBuffer.toString()
        s.indices.forEach { i ->
            // Logger.i(s.subSequence(i, i + 1) + "");
            val isOne = "1" == s.subSequence(i, i + 1)
            if (i != 0 && !isOne) {
                return@forEach
            }
            when (i) {
                0 -> if ("0" == s.subSequence(i, i + 1)) {
                    RowerDataParam.INSTANTANEOUS_SPEED_INX = inxLen
                    inxLen += RowerDataParam.INSTANTANEOUS_SPEED_LEN
                    Logger.d("setBleDataInx  INSTANTANEOUS_SPEED_INX=" + RowerDataParam.INSTANTANEOUS_SPEED_INX)
                }
                1 -> if (isOne) {
                    RowerDataParam.AVERAGE_SPEED_INX = inxLen
                    inxLen += RowerDataParam.AVERAGE_SPEED_LEN
                    Logger.d("setBleDataInx  AVERAGE_SPEED_INX=" + RowerDataParam.AVERAGE_SPEED_INX)
                }
                2 -> if (isOne) {
                    RowerDataParam.INSTANTANEOUS_RPM_INX = inxLen
                    inxLen += RowerDataParam.INSTANTANEOUS_RPM_LEN
                    Logger.d("setBleDataInx  INSTANTANEOUS_RPM_INX=" + RowerDataParam.INSTANTANEOUS_RPM_INX)
                }
                3 -> if (isOne) {
                    RowerDataParam.AVERAGE_RPM_INX = inxLen
                    inxLen += RowerDataParam.AVERAGE_RPM_LEN
                    Logger.d("setBleDataInx  AVERAGE_RPM_INX=" + RowerDataParam.AVERAGE_RPM_INX)
                }
                4 -> if (isOne) {
                    RowerDataParam.TOTAL_DISTANCE_INX = inxLen
                    inxLen += RowerDataParam.TOTAL_DISTANCE_LEN
                    Logger.d("setBleDataInx  TOTAL_DISTANCE_INX=" + RowerDataParam.TOTAL_DISTANCE_INX)
                }
                5 -> if (isOne) {
                    RowerDataParam.RESISTANCE_LEVEL_INX = inxLen
                    inxLen += RowerDataParam.RESISTANCE_LEVEL_LEN
                    Logger.d("setBleDataInx  RESISTANCE_LEVEL_INX=" + RowerDataParam.RESISTANCE_LEVEL_INX)
                }
                6 -> if (isOne) {
                    RowerDataParam.INSTANTANEOUS_POWER_INX = inxLen
                    inxLen += RowerDataParam.INSTANTANEOUS_POWER_LEN
                    Logger.d("setBleDataInx  INSTANTANEOUS_POWER_INX=" + RowerDataParam.INSTANTANEOUS_POWER_INX)
                }
                7 -> if (isOne) {
                    RowerDataParam.AVERAGE_POWER_INX = inxLen
                    inxLen += RowerDataParam.AVERAGE_POWER_LEN
                    Logger.d("setBleDataInx  AVERAGE_POWER_INX=" + RowerDataParam.AVERAGE_POWER_INX)
                }
                8 -> if (isOne) {
                    RowerDataParam.TOTAL_ENERGY_INX = inxLen
                    inxLen += RowerDataParam.TOTAL_ENERGY_LEN
                    RowerDataParam.ENERGY_PER_HOUR_INX = inxLen
                    inxLen += RowerDataParam.ENERGY_PER_HOUR_LEN
                    RowerDataParam.ENERGY_PER_MINUTE_INX = inxLen
                    inxLen += RowerDataParam.ENERGY_PER_MINUTE_LEN
                    Logger.d("setBleDataInx  TOTAL_ENERGY_INX=" + RowerDataParam.TOTAL_ENERGY_INX)
                    Logger.d("setBleDataInx  ENERGY_PER_HOUR_INX=" + RowerDataParam.ENERGY_PER_HOUR_INX)
                    Logger.d("setBleDataInx  ENERGY_PER_MINUTE_INX=" + RowerDataParam.ENERGY_PER_MINUTE_INX)
                }
                9 -> if (isOne) {
                    RowerDataParam.HEART_RATE_INX = inxLen
                    inxLen += RowerDataParam.HEART_RATE_LEN
                    Logger.d("setBleDataInx  HEART_RATE_INX=" + RowerDataParam.HEART_RATE_INX)
                }
                10 -> if (isOne) {
                    RowerDataParam.METABOLIC_EQUIVALENT_INX = inxLen
                    inxLen += RowerDataParam.METABOLIC_EQUIVALENT_LEN
                    Logger.d("setBleDataInx  METABOLIC_EQUIVALENT_INX=" + RowerDataParam.METABOLIC_EQUIVALENT_INX)
                }
                11 -> if (isOne) {
                    RowerDataParam.ELAPSED_TIME_INX = inxLen
                    inxLen += RowerDataParam.ELAPSED_TIME_LEN
                    Logger.d("setBleDataInx  ELAPSED_TIME_INX=" + RowerDataParam.ELAPSED_TIME_INX)
                }
                12 -> if (isOne) {
                    RowerDataParam.REMAINING_TIME_INX = inxLen
                    inxLen += RowerDataParam.REMAINING_TIME_LEN
                    Logger.d("setBleDataInx  REMAINING_TIME_INX=" + RowerDataParam.REMAINING_TIME_INX)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        var instance: BikeManager? = null
            get() {
                if (field == null) {
                    synchronized(BikeManager::class.java) {
                        if (field == null) {
                            field = BikeManager()
                        }
                    }
                }
                return field
            }
            private set
    }
}