/* DigestUtilsHelper.java

	Purpose:

	Description:

	History:
		10:12 AM 3/08/16, Created by wenning

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.sys;

import java.io.UnsupportedEncodingException;
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
		try {
			return string == null ? null : string.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// Should never happen with UTF-8
			// If it does - ignore & return null
		}
		return null;
	}

	public static String encodeHexString(byte[] data) {
		return new String(encodeHex(data));
	}

	public static char[] encodeHex(byte[] data) {
		return encodeHex(data, true);
	}

	public static char[] encodeHex(byte[] data, boolean toLowerCase) {
		return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	}

	private static final char[] DIGITS_LOWER;
	private static final char[] DIGITS_UPPER;

	static {
		DIGITS_LOWER = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		DIGITS_UPPER = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	}

	protected static char[] encodeHex(byte[] data, char[] toDigits) {
		int l = data.length;
		char[] out = new char[l << 1];
		int i = 0;

		for (int j = 0; i < l; ++i) {
			out[j++] = toDigits[(240 & data[i]) >>> 4];
			out[j++] = toDigits[15 & data[i]];
		}

		return out;
	}

	public static byte[] decodeHexString(String data) throws IllegalArgumentException {
		return decodeHex(data.toCharArray());
	}

	public static byte[] decodeHex(final char[] data) throws IllegalArgumentException {

		final int len = data.length;

		if ((len & 0x01) != 0) {
			throw new IllegalArgumentException("Odd number of characters.");
		}

		final byte[] out = new byte[len >> 1];

		// two characters form the hex value.
		for (int i = 0, j = 0; j < len; i++) {
			int f = toDigit(data[j], j) << 4;
			j++;
			f = f | toDigit(data[j], j);
			j++;
			out[i] = (byte) (f & 0xFF);
		}

		return out;
	}

	protected static int toDigit(final char ch, final int index) throws IllegalArgumentException {
		final int digit = Character.digit(ch, 16);
		if (digit == -1) {
			throw new IllegalArgumentException("Illegal hexadecimal character " + ch + " at index " + index);
		}
		return digit;
	}

}
