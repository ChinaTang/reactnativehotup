package com.hotupdate.basebuild.baseaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by tangdi on 9/22/17.
 */

public class MD5Util {

    private final static int DEFAULT_BIT = 16;
    //用于加密的字符
    private final static char[] hexDigits = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 字节流转16进制
     *
     * @param bytes
     * @return
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        int t;
        for (int i = 0; i < 16; i++) {
            t = bytes[i];
            if (t < 0)
                t += 256;
            sb.append(hexDigits[(t >>> 4)]);
            sb.append(hexDigits[(t % 16)]);
        }
        return sb.toString();
    }

    /**
     * 默认16位
     *
     * @param input
     * @return
     * @throws Exception
     */
    public static String encrypt(String input) {
        return encrypt(input, DEFAULT_BIT);
    }

    /**
     * @param input
     * @param bit
     * @return
     * @throws Exception
     */
    public static String encrypt(String input, int bit) {
        try {
            MessageDigest md = MessageDigest.getInstance(System.getProperty(
                    "MD5.algorithm", "MD5"));
            if (bit == 16) {
                return bytesToHex(md.digest(input.getBytes("utf-8")))
                        .substring(8, 24);
            } else {
                return bytesToHex(md.digest(input.getBytes("utf-8")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用于校验下发zip包
     * @param imagePath
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static String getMD5(String imagePath) throws NoSuchAlgorithmException, IOException {

        InputStream in = new FileInputStream(new File(imagePath));

        StringBuffer md5 = new StringBuffer();
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] dataBytes = new byte[1024];

        int nread = 0;
        while ((nread = in.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        }

        byte[] mdbytes = md.digest();

        // convert the byte to hex format
        for (int i = 0; i < mdbytes.length; i++) {
            md5.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        in.close();
        return md5.toString();
    }
}
