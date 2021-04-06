package com.bike.ftms.app.utils;

public class ConvertData {
    private static StringBuffer buffer = new StringBuffer();

    public static byte intLowToByte(int ary) {
        byte value;
        value = (byte) (ary & 0xFF);
        return value;
    }

    public static int byteToInt(byte ary) {
        return ((ary & 0xFF) | 0x00000000);
    }

    public static short bytesToShortLiterEnd(byte[] ary, int offset) {
        short value;
        value = (short) ((ary[offset] & 0xFF)
                | ((ary[offset + 1] << 8) & 0xFF00));
        return value;
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        for (int i = begin; i < begin + count; i++) bs[i - begin] = src[i];
        return bs;
    }

    public static byte[] shortToBytes(short value) {
        byte[] byte_src = new byte[2];
        byte_src[1] = (byte) ((value & 0xFF00) >> 8);
        byte_src[0] = (byte) ((value & 0x00FF));
        return byte_src;
    }


    public static String byteArrayToHexString(byte[] b, int length) {
        if (buffer.length() > 0) {
            buffer.setLength(0);
        }
        for (int i = 0; i < length; ++i) {
            buffer.append("0x" + toHexString(b[i]) + " ");
        }
        return buffer.toString();
    }

    public static String toHexString(byte b) {
        String s = Integer.toHexString(b & 0xFF);
        if (s.length() == 1) {
            return "0" + s;
        } else {
            return s;
        }
    }

    /**
     * byte数组中取int数值，本方法适用于(高位在前，低位在后)的顺序。2个字节
     *
     * @param ary
     * @param offset
     * @return
     */
    public static int bytesToShortBigEnd(byte[] ary, int offset) {
        int value;
        value = (short) ((ary[offset + 1] & 0x0FF)
                | ((ary[offset] << 8) & 0x0FF00));
        return value;
    }

    /**
     * 倡佑 2个字节 数据转整数，自己缩小或者放大（高位在前，低位在后）
     *
     * @param ary
     * @param offset
     * @return
     */
    public static int cyByteToInt(byte[] ary, int offset) {
        //low
        int low = ary[offset + 1] & 0xff;
        //hi
        int hi = ary[offset] & 0xff;
        return low + 100 * hi;
    }

    /**
     * 倡佑 整数转byte 数组 (2位)（高位在前，低位在后）
     *
     * @param value 不超过4位数
     * @return
     */
    public static byte[] cyIntToByte(int value) {
        byte[] data = new byte[2];
        data[1] = (byte) ((value % 100) & 0xFF);
        data[0] = (byte) ((value / 100) & 0xFF);
        return data;
    }

    /**
     * 公制转英制
     *
     * @param value
     * @return
     */
    public static float getKmToMile1Float(float value) {
        return (float) (Math.round((value / 1.6093f) * 10) / 10.0);
    }

    /**
     * byte转换为二进制字符串
     **/
    /**
     * byte数组转换为二进制字符串,每个字节以","隔开
     **/
    public static String byteArrToBinStr(byte[] b) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            StringBuffer sbTwo = new StringBuffer();
            String two = Long.toString(b[i] & 0xff, 2);
            for (int j = two.length(); j< 8; j++) {
                sbTwo.append("0");
            }
            sbTwo.append(two);
            result.append(sbTwo.toString() + ",");
        }
        return result.toString().substring(0, result.length() - 1);
    }

}