/* DigestUtilsHelper.java

	Purpose:

	Description:

	History:
		10:12 AM 3/08/16, Created by wenning

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.sys;

import org.apache.commons.codec.Charsets;

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

	/**
	 * Calculates the MD5 digest and returns the value as a 32 charactor hex string.
	 * @param data data to digest
	 * @return MD5 digest as a hex string
	 */
	public static String md5Hex(String data) {
		return encodeHexString(md5(data));
	}

	/**
	 * Calculates the MD5 digest and returns the value as a 16 element <code>byte[]</code>.
	 * @param data data to digest
	 * @return MD5 digest
     */
	public static byte[] md5(String data) {
		return md5(getBytesUtf8(data));
	}

	/**
	 * Calculates the MD5 digest and returns the value as a 16 element <code>byte[]</code>.
	 * @param data data to digest
	 * @return MD5 digest
     */
	public static byte[] md5(byte[] data) {
		return getMd5Digest().digest(data);
	}

	/**
	 * Returns an MD5 MessageDigest.
	 * @return An MD5 digest instance.
     */
	public static MessageDigest getMd5Digest() {
		return getDigest("MD5");
	}

	/**
	 * Returns a <code>MessageDigest</code> for the given <code>algorithm</code>.
	 * @param algorithm
	 * @return An MD5 digest instance.
	 * @throws IllegalArgumentException
     */
	public static MessageDigest getDigest(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException var2) {
			throw new IllegalArgumentException(var2);
		}
	}

	/**
	 * Encodes the given string into a sequence of bytes using the UTF-8 charset, storing the result into a new byte
	 * array.
	 *
	 * @param string
	 *            the String to encode, may be {@code null}
	 * @return encoded bytes, or {@code null} if the input string was {@code null}
	 */
	public static byte[] getBytesUtf8(String string) {
		try {
			return string == null ? null : string.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// Should never happen with UTF-8
			// If it does - ignore & return null
		}
		return null;
	}

	/**
	 * Converts an array of bytes into a String representing the hexadecimal values of each byte in order. The returned
	 * String will be double the length of the passed array, as it takes two characters to represent any given byte.
	 *
	 * @param data
	 *            a byte[] to convert to Hex characters
	 * @return A String containing hexadecimal characters
	 */
	public static String encodeHexString(byte[] data) {
		return new String(encodeHex(data));
	}

	/**
	 * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
	 * The returned array will be double the length of the passed array, as it takes two characters to represent any
	 * given byte.
	 *
	 * @param data
	 *            a byte[] to convert to Hex characters
	 * @return A char[] containing hexadecimal characters
	 */
	public static char[] encodeHex(byte[] data) {
		return encodeHex(data, true);
	}

	/**
	 * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
	 * The returned array will be double the length of the passed array, as it takes two characters to represent any
	 * given byte.
	 *
	 * @param data
	 *            a byte[] to convert to Hex characters
	 * @param toLowerCase
	 *            {@code true} converts to lowercase, {@code false} to uppercase
	 * @return A char[] containing hexadecimal characters
	 */
	public static char[] encodeHex(byte[] data, boolean toLowerCase) {
		return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	}

	/**
	 * Used to build output as Hex
	 */
	private static final char[] DIGITS_LOWER;

	/**
	 * Used to build output as Hex
	 */
	private static final char[] DIGITS_UPPER;

	static {
		DIGITS_LOWER = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		DIGITS_UPPER = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	}

	/**
	 * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
	 * The returned array will be double the length of the passed array, as it takes two characters to represent any
	 * given byte.
	 *
	 * @param data
	 *            a byte[] to convert to Hex characters
	 * @param toDigits
	 *            the output alphabet
	 * @return A char[] containing hexadecimal characters
	 */
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

	/**
	 * Converts a String into an array of bytes.
	 *
	 * @param data
	 *            a byte[] to convert to Hex characters
	 * @return A String containing hexadecimal characters
	 * @throws IllegalArgumentException
	 */
	public static byte[] decodeHexString(String data) throws IllegalArgumentException {
		return decodeHex(data.toCharArray());
	}

	/**
	 * Converts an array of characters representing hexadecimal values into an array of bytes of those same values. The
	 * returned array will be half the length of the passed array, as it takes two characters to represent any given
	 * byte. An exception is thrown if the passed char array has an odd number of elements.
	 *
	 * @param data
	 *            An array of characters containing hexadecimal digits
	 * @return A byte array containing binary data decoded from the supplied char array.
	 * @throws IllegalArgumentException
	 */
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

	/**
	 * Converts a hexadecimal character to an integer.
	 *
	 * @param ch
	 *            A character to convert to an integer digit
	 * @param index
	 *            The index of the character in the source
	 * @return An integer
	 * @throws IllegalArgumentException
	 */
	protected static int toDigit(final char ch, final int index) throws IllegalArgumentException {
		final int digit = Character.digit(ch, 16);
		if (digit == -1) {
			throw new IllegalArgumentException("Illegal hexadecimal character " + ch + " at index " + index);
		}
		return digit;
	}

}
