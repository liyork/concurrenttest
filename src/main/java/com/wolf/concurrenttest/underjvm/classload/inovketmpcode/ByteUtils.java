package com.wolf.concurrenttest.underjvm.classload.inovketmpcode;

/**
 * Description: bytes数组处理工具
 * Created on 2021/7/28 1:58 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ByteUtils {
    // 第一位，(byte[0] & 0xff)<< ((--len)*8)
    // 从b中，查找，start开始的len个内容，转换成int
    public static int bytes2Int(byte[] b, int start, int len) {
        int sum = 0;
        int end = start + len;
        for (int i = start; i < end; i++) {
            int n = ((int) b[i]) & 0xff;
            n <<= (--len) * 8;
            sum = n + sum;
        }
        return sum;
    }

    public static byte[] int2Bytes(int value, int len) {
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            b[len - i - 1] = (byte) ((value >> 8 * i) & 0xff);// 从左向右填充
        }
        return b;
    }

    public static String bytes2String(byte[] b, int start, int len) {
        return new String(b, start, len);
    }

    public static byte[] string2Bytes(String str) {
        return str.getBytes();
    }

    public static byte[] bytesReplace(byte[] originalBytes, int offset, int len, byte[] replaceBytes) {
        byte[] newBytes = new byte[originalBytes.length + (replaceBytes.length - len)];
        System.arraycopy(originalBytes, 0, newBytes, 0, offset);
        System.arraycopy(replaceBytes, 0, newBytes, offset, replaceBytes.length);

        System.arraycopy(originalBytes, offset + len, newBytes, offset + replaceBytes.length, originalBytes.length - offset - len);
        return newBytes;
    }

    public static void main(String[] args) {
        //int2Bytes(1, 1);
        //int2Bytes(2, 2);
        //bytes2Int(new byte[]{(byte) 1, (byte) 2}, 0, 2);
        bytesReplace(new byte[]{(byte) 1, (byte) 2}, 1, 1, new byte[]{(byte) 3});
    }
}
