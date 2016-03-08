/* DigestUtilsHelper.java

	Purpose:

	Description:

	History:
		10:12 AM 3/08/16, Created by wenning

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.sys;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A helper class for class DigestUtils for md5 encoding, internal use only.
 * The following implementation for md5 encoding is referred from
 * https://commons.apache.org/proper/commons-codec/archives/1.8/index.html
 *
 * @author wenning
 * @since 8.0.2
 */
public class DigestUtilsHelper {

    public static String md5Hex(String data) {
        return encodeHexString(md5(data));
    }

    public static byte[] md5(String data) {
        return md5(getBytesUtf8(data));
    }

    public static byte[] md5(byte[] data) {
        return getMd5Digest().digest(data);
    }

    public static MessageDigest getMd5Digest() {
        return getDigest("MD5");
    }

    public static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException var2) {
            throw new IllegalArgumentException(var2);
        }
    }

    public static byte[] getBytesUtf8(String string) {
        return getBytes(string, Charset.forName("UTF-8"));
    }

    private static byte[] getBytes(String string, Charset charset) {
        return string == null?null:string.getBytes(charset);
    }

    public static String encodeHexString(byte[] data) {
        return new String(encodeHex(data));
    }

    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }

    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toLowerCase?DIGITS_LOWER:DIGITS_UPPER);
    }

    private static final char[] DIGITS_LOWER;
    private static final char[] DIGITS_UPPER;

    static {
        DIGITS_LOWER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        DIGITS_UPPER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    }

    protected static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[l << 1];
        int i = 0;

        for(int j = 0; i < l; ++i) {
            out[j++] = toDigits[(240 & data[i]) >>> 4];
            out[j++] = toDigits[15 & data[i]];
        }

        return out;
    }

}
