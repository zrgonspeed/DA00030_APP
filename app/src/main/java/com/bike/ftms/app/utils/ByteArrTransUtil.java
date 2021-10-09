package com.bike.ftms.app.utils;

/**
 * byte转换工具类
 *
 * @author wjh on 2021/1/18
 */
public class ByteArrTransUtil {


       /*高位在前
       // long -> byte[]
        byte[] bl = java.nio.ByteBuffer.allocate(Long.BYTES).putLong(0, (long) aLongNumber).array();

        // int -> byte[]
        byte[] bi = java.nio.ByteBuffer.allocate(Integer.BYTES).putInt(0, (int) anInteger).array();

        // short -> byte[]
        byte[] bs = java.nio.ByteBuffer.allocate(Short.BYTES).putShort(0, (short) aShortNumber).array();

        // char -> byte[]
        byte[] bc = java.nio.ByteBuffer.allocate(Character.BYTES).putChar(0, (char) aCharValue).array();*/

    /**
     * 低位在前
     *
     * @param bytes
     * @return
     */
    public static int bytes2Int(byte[] bytes) {
        int int1 = bytes[0] & 0xff;
        int int2 = (bytes[1] & 0xff) << 8;
        int int3 = (bytes[2] & 0xff) << 16;
        int int4 = (bytes[3] & 0xff) << 24;

        return int1 | int2 | int3 | int4;
    }

    /**
     * 低位在前
     */
    public static byte[] int2bytes(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }

    /**
     * 高位在前
     * 注释：short到字节数组的转换
     */
    public static byte[] shortToByte(short number) {

        byte[] s = new byte[2];

        s[0] = (byte) (number >> 8 & 0xff);
        s[1] = (byte) (number & 0xff);

        return s;
    }

    /**
     * 高位在前
     * 注释：字节数组到short的转换
     */
    public static short byteToShort(byte[] b) {
        short s = 0;
        short s0 = (short) (b[0] & 0xff);//高位
        short s1 = (short) (b[1] & 0xff);
        s0 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }

    public static String toHexValue(byte[] messageDigest) {
        if (messageDigest == null)
            return "";
        StringBuilder hexValue = new StringBuilder();
        for (byte aMessageDigest : messageDigest) {
            // 同256 进行与操作   byte 0~128~-127（256） 范围 刚好是16进制的 0xFF
            // 如果不进行与操作    byte的-1 转成 int类型时会变成 0xFFFFFFFF
            int val = 0xFF & aMessageDigest;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
}
