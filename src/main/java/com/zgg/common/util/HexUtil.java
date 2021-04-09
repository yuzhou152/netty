package com.zgg.common.util;

import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * 十六进制转换成其他进制工具类
 */

public class HexUtil {


    /**
     * int转换成byte
     * [注意]：int转换成byte后，该方法只保留一个字节，高字节被丢弃。
     *
     * @param x 需要转换的int
     * @return byte 1个字节
     */
    public static byte intToByte(int x) {
        return (byte) x;
    }

    /**
     * byte转换成int
     *
     * @param b 需要转换的数
     * @return int 四字节INT值
     */
    public static int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }

    /**
     * byte[]转换成int,数组长度大于4丢失精度
     * byte数组长度不足四位会导致 java.lang.ArrayIndexOutOfBoundsException 异常
     *
     * @param b 需要转换的数
     * @return int 四字节int值
     */
    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    /**
     * byte[]转换成Integer
     *
     * @param b 需要转换的数 byte数组长度为4，大于4返回null
     * @return int 四字节int值
     */
    public static Integer byteArrayToInteger(byte[] b) {
        Integer result = null;
        switch (b.length) {
            case 4:
                result = b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24;
                break;
            case 3:
                result = (b[2] & 0xFF) | (b[1] & 0xFF) << 8 | (b[0] & 0xFF) << 16;
                break;
            case 2:
                result = (b[1] & 0xFF) | (b[0] & 0xFF) << 8;
                break;
            case 1:
                result = (b[0] & 0xFF);
                break;
            default:
                result = null;
        }
        return result;
    }


    /**
     * int转换成byte[]
     *
     * @param a 需要转换的数
     * @return byte[] 4字节byte数组
     */
    public static byte[] intToByteArray(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    /**
     * short转换成byte[]
     *
     * @param a 需要转换的数
     * @return short 2字节short值
     */
    public static byte[] shortToByteArray(short a) {
        return new byte[]{
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    /**
     * 通过byte数组取到short，数组长度大于2丢失精度
     * byte数组长度不足两位会导致 java.lang.ArrayIndexOutOfBoundsException 异常
     *
     * @param b
     * @return
     */
    public static short byteArrayToShort(byte[] b) {
        return (short) (((b[0] << 8) | b[1] & 0xff));
    }

    /**
     * 通过byte数组取到short,有可能为负数
     *
     * @param b byte数组长度为2，大于2返回null
     * @return
     */
    public static Short byteArrayToShortP(byte[] b) {
        Short result = null;
        switch (b.length) {
            case 2:
                result = (short) ((b[0] << 8) | b[1] & 0xff);
                break;
            case 1:
                result = (short) ((0x00 << 8) | (b[0] & 0xFF) << 24);
                break;
            default:
                result = null;
        }
        return result;
    }


    /**
     * 通过byte数组取到short
     *
     * @param b
     * @return
     */
    public static short byteToShort(byte b) {
        return (short) (b & 0xff);
    }

    /**
     * long转换成byte[]
     *
     * @param x 需要转换的数
     * @return byte[] 8字节long值
     */
    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, x);
        return buffer.array();
    }

    /**
     * byte[]转换成long
     *
     * @param bytes 需要转换的数
     * @return Long
     */
    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        if (bytes.length < 8) {
            for (int a = 0; a < 8 - bytes.length; a++) {
                byte aa = 0x00;
                buffer.put(aa);
            }
        }
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    /**
     * byte数组转换成字符串
     * @param src 需要转换的byte数组
     * @param delimiter 分隔符，如果为 "" 或者 null 则没有分隔符
     * @return
     */
    public static String bytesToHexString(byte[] src,String delimiter) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            if(StringUtils.isNotBlank(delimiter)){
                if (i < src.length - 1) {
                    stringBuilder.append(delimiter);
                }
            }
        }
        return stringBuilder.toString().toUpperCase();
    }

    /**
     * byte数组转换成字符串
     * [格式]："XX|XX|XX|XX"
     *
     * @param src 需要转换的byte数组
     * @return String
     */
    public static String bytesToHexString(byte[] src) {
        return bytesToHexString(src,"|");
    }

    /**
     * 字符转换成数组
     * [格式]："XX|XX|XX|XX"
     *
     * @param hexString 需要转换的字符串
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        String[] hexStrings = hexString.split("\\|");
        char[] hexChars = null;
        byte[] d = new byte[hexStrings.length];
        for (int i = 0; i < hexStrings.length; i++) {
            hexChars = hexStrings[i].toCharArray();
            d[i] = (byte) (charToByte(hexChars[0]) << 4 | charToByte(hexChars[1]));
        }
        return d;
    }

    /**
     * char转成成1字节byte
     *
     * @param c 需要转换的char
     * @return byte 1字节byte值
     */
    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 把7位的byte[]转换为时间字符串
     * yyyy(2字节) MM(1字节) dd(1字节) HH(1字节) mm(1字节) ss(1字节)
     *
     * @param bytes 需要转换的数组
     * @return String yyyyMMddHHmmss
     */
    public static String bytesToDatetime(byte[] bytes) {
        if (bytes.length != 7) {
            return null;
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append(byteArrayToShort(Arrays.copyOfRange(bytes, 0, 2)));
            for (int a = 2; a < bytes.length; a++) {
                short value = byteToShort(bytes[a]);
                sb.append(value > 9 ? value : "0" + value);
            }
            return sb.toString();
        }
    }

    /**
     * 把7位的byte[]转换为时间字符串
     * yyyy(2字节) MM(1字节) dd(1字节) HH(1字节) mm(1字节) ss(1字节)
     *
     * @param datetime 需要转换的时间
     * @return String yyyyMMddHHmmss
     */
    public static byte[] datetimeToByteArray(Date datetime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datetime);
        byte[] bytes = new byte[7];
        byte[] year = shortToByteArray((short) calendar.get(Calendar.YEAR));
        System.arraycopy(year, 0, bytes, 0, year.length);
        bytes[2] = intToByte(calendar.get(Calendar.MONTH) + 1);
        bytes[3] = intToByte(calendar.get(Calendar.DAY_OF_MONTH));
        bytes[4] = intToByte(calendar.get(Calendar.HOUR_OF_DAY));
        bytes[5] = intToByte(calendar.get(Calendar.MINUTE));
        bytes[6] = intToByte(calendar.get(Calendar.SECOND));
        return bytes;
    }

    /**
     * 移除数组左边的零，处理数字情况下左边有零的情况
     * 例：传入=byte[]{0,0,0,2,0}
     * 输入=byte[]{2,0}
     * 如果 传入=byte[]{0,0,0,0,0}=byte[]{0}
     * @param array 需要转换的数组
     * @return byte[]
     */
    public static byte[] removeZero(byte[] array) {
        int flag = 0;
        for (int a = 0; a < array.length; a++) {
            if (array[a] != 0x00) {
                flag = a;
                break;
            } else if ((a == array.length - 1) && array[a] == 0x00) {
                flag = a;
            }
        }
        byte[] result = new byte[array.length - flag];
        System.arraycopy(array, flag, result, 0, array.length - flag);
        return result;
    }


}
