package com.bike.ftms.app.bean;


import com.bike.ftms.app.util.Logger;
import com.bike.ftms.app.utils.ByteArrTransUtil;

/**
 * 蓝牙传输格式
 *
 * @author wjh on 2021/1/18
 */
public class FormatBean {
    private short star_code;  //包头 2byte 0xF455
    private byte data_type;  //数据类型 1byte
    private short body_len;   //数据长度 2byte
    private byte[] data_body;  //数据包   n
    private byte syn_code;   //校验和
    private byte stop_code;  //包尾 1byte 0xFB

    public FormatBean() {
        star_code = (short) 0xF455;
        stop_code = (byte) 0xFB;
    }

    public FormatBean(byte[] bytes) {
        if (bytes.length >= 8) {
            byte[] star = new byte[2];
            System.arraycopy(bytes, 0, star, 0, 2);
            star_code = ByteArrTransUtil.byteToShort(star);
            data_type = bytes[2];
            byte[] len = new byte[2];
            System.arraycopy(bytes, 3, len, 0, 2);
            body_len = ByteArrTransUtil.byteToShort(len);
            data_body = new byte[body_len];
            System.arraycopy(bytes, 5, data_body, 0, body_len);
            syn_code = bytes[bytes.length - 2];
            stop_code = bytes[bytes.length - 1];
            //KLog.i("BleManager","FormatBean:bytes"+ Arrays.toString(bytes));
            Logger.i("BleManager", "FormatBean:toHex" + ByteArrTransUtil.toHexValue(bytes));
        }

    }

    public byte[] toByteArray() {
        byte[] bytes = new byte[body_len + 7];
        System.arraycopy(ByteArrTransUtil.shortToByte(star_code), 0, bytes, 0, 2);
        bytes[2] = data_type;
        System.arraycopy(ByteArrTransUtil.shortToByte(body_len), 0, bytes, 3, 2);
        System.arraycopy(data_body, 0, bytes, 5, data_body.length);
        bytes[5 + data_body.length] = onSynSum();
        bytes[bytes.length - 1] = stop_code;
        //KLog.i("BleManager","FormatBean:toByteArray"+ Arrays.toString(bytes));
        Logger.i("BleManager", "FormatBean:toHex" + ByteArrTransUtil.toHexValue(bytes));
        return bytes;
    }

    /**
     * @return 校验和
     * <累加取一个字节有效位>
     */
    private byte onSynSum() {
        int syn = body_len;
        for (byte i : data_body) {
            syn = i + syn;
        }
        syn_code = (byte) syn;
        return syn_code;
    }

    public short getStar_code() {
        return star_code;
    }

    public void setStar_code(short star_code) {
        this.star_code = star_code;
    }

    public byte getData_type() {
        return data_type;
    }

    public void setData_type(byte data_type) {
        this.data_type = data_type;
    }

    public short getBody_len() {
        return body_len;
    }

    public void setBody_len(short body_len) {
        this.body_len = body_len;
    }

    public byte[] getData_body() {
        return data_body;
    }

    public void setData_body(byte[] data_body) {
        this.data_body = data_body;
    }

    public byte getSyn_code() {
        return syn_code;
    }

    public void setSyn_code(byte syn_code) {
        this.syn_code = syn_code;
    }

    public byte getStop_code() {
        return stop_code;
    }

    public void setStop_code(byte stop_code) {
        this.stop_code = stop_code;
    }
}