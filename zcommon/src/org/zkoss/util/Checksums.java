/* Checksums.java

	Purpose:
		
	Description:
		
	History:
		Thu Feb  5 11:40:21     2004, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

//NOTE: DO NOT modify the algorithm here because i3lb/HostInfo counts on it.
//If you really have to modify, you have to reserve old codes and modify
//HostInfo to call the old ones.

/**
 * Checksum relevant utilities.
 *
 * @author tomyeh
 */
public class Checksums {
	/** The default skips. */
	public static final String SKIPS = "DEOX";

	/** Returns a readable string plus a checksum.
	 *
	 * @param skips specifies a string of characters that shall be skipped.
	 * If null specified, "DEOX" is assumed. To skip nothing, specify "".
	 * You can only specify upper-case leters: A-Z. And, it must be in
	 * alphabetic order.
	 */
	public static final String toReadable(long val, String skips) {
		if (skips == null) skips = SKIPS;

		final int mod = 36 - skips.length();

		if (val < 0) val = -val;
		int patch = (int)val; //used to patch for 4 digits
		if (patch < 0) patch = -patch;

		final StringBuffer sb = new StringBuffer(32);
		for (int digits = 0;;) {
			if (val == 0) { //no more digit
				if (digits == 4) {
					digits = 0;
					sb.append('-');
				}
				while (digits++ != 3) { //align to 4 - 1
					final int v = patch % mod;
					patch /= mod;
					sb.append(toReadableChar(v, skips));
				}
				sb.append(getChecksum(sb, skips));
				return sb.toString();
			}

			if (digits++ == 4) {
				digits = 1;
				sb.append('-');
			}
			final int v = (int)(val % mod);
			val /= mod;
			sb.append(toReadableChar(v, skips));
		}
	}
	/** Returns the character of the specified val by skiping skips.
	 * Note: the caller must ensure val is in the right range:
	 * 0 - (36 - skips.length()).
	 *
	 * @param skips specifies a string of characters that shall be skipped.
	 * If null specified, "DEOX" is assumed. To skip nothing, specify "".
	 * You can only specify upper-case leters: A-Z. And, it must be in
	 * alphabetic order.
	 */
	public static final char toReadableChar(int val, String skips) {
		if (val < 10)
			return (char)(val + '0');

		if (skips == null) skips = SKIPS;

		char cc = (char)(val + ('A' - 10));
		for (int j = 0, sklen = skips.length(); j < sklen; ++j) {
			final char sk = skips.charAt(j);
			if (cc < sk)
				break;
			++cc;
		}
		return cc;
	}

	/** Returns the checksum character of the specified val.
	 * <p>It use {@link #toReadableChar} to convert the checksum to a character.
	 * <p>Note: it skips '-' and ' '.
	 *
	 * @param skips specifies a string of characters that shall be skipped.
	 * If null specified, "DEOX" is assumed. To skip nothing, specify "".
	 * You can only specify upper-case leters: A-Z. And, it must be in
	 * alphabetic order.
	 */
	public static final char getChecksum(String val, String skips) {
		if (skips == null) skips = SKIPS;

		final int len = val.length();
		int cksum = 0;
		for (int j = 0; j < len; ++j) {
			final char cc = val.charAt(j);
			if (cc != '-' && cc != ' ')
				cksum = cksum * 27 + cc;
		}

		if (cksum < 0) cksum = -cksum;
		return toReadableChar(cksum % (36 - skips.length()), skips);
	}
	/** Returns the checksum character of the specified val.
	 * <p>It use {@link #toReadableChar} to convert the checksum to a character.
	 * <p>Note: it skips '-' and ' '.
	 *
	 * @param skips specifies a string of characters that shall be skipped.
	 * If null specified, "DEOX" is assumed. To skip nothing, specify "".
	 * You can only specify upper-case leters: A-Z. And, it must be in
	 * alphabetic order.
	 */
	public static final char getChecksum(StringBuffer val, String skips) {
		if (skips == null) skips = SKIPS;

		final int len = val.length();
		int cksum = 0;
		for (int j = 0; j < len; ++j) {
			final char cc = val.charAt(j);
			if (cc != '-' && cc != ' ')
				cksum = cksum * 27 + cc;
		}

		if (cksum < 0) cksum = -cksum;
		return toReadableChar(cksum % (36 - skips.length()), skips);
	}
}
