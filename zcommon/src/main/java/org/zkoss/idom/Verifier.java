/* Verifier.java


	Purpose:
	Description:
	History:
	2001/10/21 17:03:23, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom;

import org.xml.sax.Locator;

/**
 * The verifier to verify W3C/DOM related constraints.
 *
 * @author tomyeh
 */
public class Verifier {
	// The following implementation are referred from jdom2's implementation to support
	// UTF-16 characters. License under BSD - 
	// https://raw.githubusercontent.com/hunterhacker/jdom/master/core/src/java/org/jdom2/Verifier.java
	/*
	 * KEY TO UNDERSTANDING MASKS.
	 * ===========================
	 * 
	 * This Verifier uses bitwise logic to perform fast validation on
	 * XML characters. The concept is as follows...
	 * 
	 * There are 7 major tests for characters in JDOM and one special case.
	 * Can the character be a regular character, can it be part of an XML Name
	 * (element, attribute, entity-ref, etc.), does it represent a letter,
	 * digit, or combining character. Finally can a character be the first
	 * character in a name, or can the character be part of a URI. The special
	 * case is that Attributes and Element names in JDOM do not include the
	 * namespace prefix, thus, for Attribute and Elements, the name is the
	 * identical test to other XML names, but excludes the ':'. For performance
	 * reasons we only have the bitmask for the JDOM names, and then add the
	 * ':' for the general case tests.
	 * 
	 * These 7 tests are often performed in very tight performance critical
	 * loops. It is essential for them to be fast.
	 * 
	 * These 7 tests conveniently can be represented as 8 bits in a byte.
	 * We can thus have a single byte that represents the possible roles for
	 * each possible character. There are 64K characters... thus we need 64K
	 * bytes to represent each character's possible roles.
	 * 
	 * We could use arrays of booleans to accomplish the same thing, but each
	 * boolean is a byte of memory, and using a bitmask allows us to put the
	 * 8 bitmask tests in the same memory space as just one boolean array.
	 * 
	 * The end solution is to have an array of these bytes, one per character,
	 * and to then query each bit on the byte to see whether the corresponding
	 * character is able to perform in the respective role.
	 * 
	 * The complicated part of this process is three-fold. The hardest part is
	 * knowing what role each character can play. The next hard part is
	 * converting this knowledge in to an array of bytes we can express in this
	 * Verifier class. The final part is querying that array for each test.
	 * 
	 * Before this particular performance upgrade, the knowledge of what roles
	 * each character can play was embedded in each of the isXML*() methods.
	 * Those methods have been transferred in to the 'contrib' class
	 * org.jdom2.contrib.verifier.VerifierBuilder. That VerifierBuilder class
	 * has a main method which takes that knowledge, and converts it in to a
	 * 'compressed' set of two arrays, the byte mask, and the number of
	 * consecutive characters that have that mask, which are then copy/pasted
	 * in to this file as the VALCONST and LENCONST arrays.
	 * 
	 * These two arrays are then 'decompressed' in to the CHARFLAGS array.
	 * 
	 * The CHARFLAGS array is then queried for each of the 8 critical tests
	 * to determine which roles a character performs.
	 * 
	 * If you need to change the roles a character plays in XML (i.e. change
	 * the return-value of one of the isXML...() methods, then you need to:
	 * 
	 *  - update the logic in org.jdom2.contrib.verifier.VerifierBuilder
	 *  - run the VerifierBuilder
	 *  - copy/paste the output to this file.
	 *  - update the JUnit test harness TestVerifier
	 */

	/**
	 * The seed array used with LENCONST to populate CHARFLAGS.
	 */
	private static final byte[] VALCONST = new byte[] {
        0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x41, 0x01, 
        0x41, 0x49, 0x41, 0x59, 0x41, 0x01, 0x41, 0x01, 
        0x41, 0x4f, 0x01, 0x4d, 0x01, 0x4f, 0x01, 0x41, 
        0x01, 0x09, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 
        0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 
        0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 
        0x01, 0x0f, 0x01, 0x09, 0x01, 0x29, 0x01, 0x29, 
        0x01, 0x0f, 0x09, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 
        0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 
        0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 
        0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x29, 
        0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 
        0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 
        0x01, 0x0f, 0x01, 0x29, 0x01, 0x29, 0x01, 0x29, 
        0x01, 0x29, 0x01, 0x29, 0x01, 0x29, 0x01, 0x0f, 
        0x01, 0x0f, 0x01, 0x0f, 0x01, 0x09, 0x0f, 0x29, 
        0x01, 0x19, 0x01, 0x29, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x29, 0x0f, 0x29, 
        0x01, 0x29, 0x01, 0x19, 0x01, 0x29, 0x01, 0x0f, 
        0x01, 0x29, 0x0f, 0x29, 0x01, 0x29, 0x01, 0x0f, 
        0x29, 0x01, 0x19, 0x01, 0x29, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x29, 0x01, 0x29, 0x01, 0x29, 0x01, 
        0x29, 0x01, 0x29, 0x01, 0x0f, 0x01, 0x0f, 0x29, 
        0x01, 0x19, 0x0f, 0x01, 0x29, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x29, 0x01, 0x29, 0x01, 
        0x29, 0x01, 0x29, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x19, 0x29, 0x0f, 0x01, 0x29, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x29, 0x0f, 0x29, 0x01, 
        0x29, 0x01, 0x29, 0x01, 0x0f, 0x01, 0x19, 0x01, 
        0x29, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x29, 0x0f, 
        0x29, 0x01, 0x29, 0x01, 0x29, 0x01, 0x29, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x19, 0x01, 0x29, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x29, 0x01, 0x29, 0x01, 
        0x29, 0x01, 0x29, 0x01, 0x19, 0x01, 0x29, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x29, 0x01, 0x29, 0x01, 0x29, 0x01, 
        0x29, 0x01, 0x0f, 0x01, 0x19, 0x01, 0x29, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x29, 0x01, 0x29, 0x01, 0x29, 0x01, 
        0x29, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x19, 0x01, 
        0x29, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x29, 0x01, 0x29, 0x01, 0x29, 0x01, 
        0x29, 0x01, 0x0f, 0x01, 0x19, 0x01, 0x0f, 0x01, 
        0x0f, 0x29, 0x0f, 0x29, 0x01, 0x0f, 0x09, 0x29, 
        0x01, 0x19, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 
        0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 
        0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 
        0x01, 0x0f, 0x01, 0x0f, 0x29, 0x0f, 0x29, 0x01, 
        0x29, 0x0f, 0x01, 0x0f, 0x01, 0x09, 0x01, 0x29, 
        0x01, 0x19, 0x01, 0x29, 0x01, 0x19, 0x01, 0x29, 
        0x01, 0x29, 0x01, 0x29, 0x01, 0x29, 0x0f, 0x01, 
        0x0f, 0x01, 0x29, 0x01, 0x29, 0x01, 0x29, 0x01, 
        0x29, 0x01, 0x29, 0x01, 0x29, 0x01, 0x29, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x29, 0x01, 
        0x29, 0x01, 0x0f, 0x01, 0x0f, 0x01, 0x0f, 0x01, 
        0x0f, 0x01, 0x09, 0x01, 0x0f, 0x01, 0x0f, 0x29, 
        0x01, 0x09, 0x01, 0x0f, 0x01, 0x29, 0x01, 0x09, 
        0x01, 0x0f, 0x01, 0x09, 0x01, 0x0f, 0x01, 0x0f, 
        0x01, 0x0f, 0x01, 0x00, 0x01, 0x00};

	/**
	 * The seed array used with VALCONST to populate CHARFLAGS.
	 */
	private static final int [] LENCONST = new int [] {
        9,     2,     2,     1,    18,     1,     1,     2, 
        9,     2,     1,    10,     1,     2,     1,     1, 
        2,    26,     4,     1,     1,    26,     3,     1, 
       56,     1,     8,    23,     1,    31,     1,    58, 
        2,    11,     2,     8,     1,    53,     1,    68, 
        9,    36,     3,     2,     4,    30,    56,    89, 
       18,     7,    14,     2,    46,    70,    26,     2, 
       36,     1,     1,     3,     1,     1,     1,    20, 
        1,    44,     1,     7,     3,     1,     1,     1, 
        1,     1,     1,     1,     1,    18,    13,    12, 
        1,    66,     1,    12,     1,    36,     1,     4, 
        9,    53,     2,     2,     2,     2,     3,    28, 
        2,     8,     2,     2,    55,    38,     2,     1, 
        7,    38,    10,    17,     1,    23,     1,     3, 
        1,     1,     1,     2,     1,     1,    11,    27, 
        5,     3,    46,    26,     5,     1,    10,     8, 
       13,    10,     6,     1,    71,     2,     5,     1, 
       15,     1,     4,     1,     1,    15,     2,     2, 
        1,     4,     2,    10,   519,     3,     1,    53, 
        2,     1,     1,    16,     3,     4,     3,    10, 
        2,     2,    10,    17,     3,     1,     8,     2, 
        2,     2,    22,     1,     7,     1,     1,     3, 
        4,     2,     1,     1,     7,     2,     2,     2, 
        3,     9,     1,     4,     2,     1,     3,     2, 
        2,    10,     2,    16,     1,     2,     6,     4, 
        2,     2,    22,     1,     7,     1,     2,     1, 
        2,     1,     2,     2,     1,     1,     5,     4, 
        2,     2,     3,    11,     4,     1,     1,     7, 
       10,     2,     3,    12,     3,     1,     7,     1, 
        1,     1,     3,     1,    22,     1,     7,     1, 
        2,     1,     5,     2,     1,     1,     8,     1, 
        3,     1,     3,    18,     1,     5,    10,    17, 
        3,     1,     8,     2,     2,     2,    22,     1, 
        7,     1,     2,     2,     4,     2,     1,     1, 
        6,     3,     2,     2,     3,     8,     2,     4, 
        2,     1,     3,     4,    10,    18,     2,     1, 
        6,     3,     3,     1,     4,     3,     2,     1, 
        1,     1,     2,     3,     2,     3,     3,     3, 
        8,     1,     3,     4,     5,     3,     3,     1, 
        4,     9,     1,    15,     9,    17,     3,     1, 
        8,     1,     3,     1,    23,     1,    10,     1, 
        5,     4,     7,     1,     3,     1,     4,     7, 
        2,     9,     2,     4,    10,    18,     2,     1, 
        8,     1,     3,     1,    23,     1,    10,     1, 
        5,     4,     7,     1,     3,     1,     4,     7, 
        2,     7,     1,     1,     2,     4,    10,    18, 
        2,     1,     8,     1,     3,     1,    23,     1, 
       16,     4,     6,     2,     3,     1,     4,     9, 
        1,     8,     2,     4,    10,   145,    46,     1, 
        1,     1,     2,     7,     5,     6,     1,     8, 
        1,    10,    39,     2,     1,     1,     2,     2, 
        1,     1,     2,     1,     6,     4,     1,     7, 
        1,     3,     1,     1,     1,     1,     2,     2, 
        1,     2,     1,     1,     1,     2,     6,     1, 
        2,     1,     2,     5,     1,     1,     1,     6, 
        2,    10,    62,     2,     6,    10,    11,     1, 
        1,     1,     1,     1,     4,     2,     8,     1, 
       33,     7,    20,     1,     6,     4,     6,     1, 
        1,     1,    21,     3,     7,     1,     1,   230, 
       38,    10,    39,     9,     1,     1,     2,     1, 
        3,     1,     1,     1,     2,     1,     5,    41, 
        1,     1,     1,     1,     1,    11,     1,     1, 
        1,     1,     1,     3,     2,     3,     1,     5, 
        3,     1,     1,     1,     1,     1,     1,     1, 
        1,     3,     2,     3,     2,     1,     1,    40, 
        1,     9,     1,     2,     1,     2,     2,     7, 
        2,     1,     1,     1,     7,    40,     1,     4, 
        1,     8,     1,  3078,   156,     4,    90,     6, 
       22,     2,     6,     2,    38,     2,     6,     2, 
        8,     1,     1,     1,     1,     1,     1,     1, 
       31,     2,    53,     1,     7,     1,     1,     3, 
        3,     1,     7,     3,     4,     2,     6,     4, 
       13,     5,     3,     1,     7,   211,    13,     4, 
        1,    68,     1,     3,     2,     2,     1,    81, 
        3,  3714,     1,     1,     1,    25,     9,     6, 
        1,     5,    11,    84,     4,     2,     2,     2, 
        2,    90,     1,     3,     6,    40,  7379, 20902, 
     3162, 11172,    92,  2048,  8190,     2};

	/**
	 * The number of characters in Java.
	 */
	private static final int  CHARCNT = Character.MAX_VALUE + 1;
	
	/**
	 * An array of byte where each byte represents the roles that the
	 * corresponding character can play. Use the bit mask values
	 * to access each character's role.
	 */
	private static final byte[] CHARFLAGS = buildBitFlags();

	/**
	 * Convert the two compressed arrays in to th CHARFLAGS array.
	 * @return the CHARFLAGS array.
	 */
	private static final byte[] buildBitFlags() {
		final byte[] ret = new byte[CHARCNT];
		int index = 0;
		for (int i = 0; i < VALCONST.length; i++) {
			// v represents the roles a character can play.
			final byte v = VALCONST[i];
			// l is the number of consecutive chars that have the same
			// roles 'v'
			int l = LENCONST[i];
			// we need to give the next 'l' chars the role bits 'v'
			while (--l >= 0) {
				ret[index++] = v;
			}
		}
		return ret;
	}
	/** Mask used to test for {@link #isXMLCharacter(int)} */
	private static final byte MASKXMLCHARACTER  = 1 << 0;
	/** Mask used to test for {@link #isXMLLetter(char)} */
	private static final byte MASKXMLLETTER     = 1 << 1;
	/** Mask used to test for {@link #isXMLNameStartCharacter(char)} */
	private static final byte MASKXMLSTARTCHAR  = 1 << 2;
	/** Mask used to test for {@link #isXMLNameCharacter(char)} */
	private static final byte MASKXMLNAMECHAR   = 1 << 3;
	/** Mask used to test for {@link #isXMLDigit(char)} */
	private static final byte MASKXMLDIGIT      = 1 << 4;
	/** Mask used to test for {@link #isXMLCombiningChar(char)} */
	private static final byte MASKXMLCOMBINING  = 1 << 5;
	/** Mask used to test for {@link #isURICharacter(char)} */
	private static final byte MASKURICHAR       = 1 << 6;
	/** Mask used to test for {@link #isXMLLetterOrDigit(char)} */
	private static final byte MASKXMLLETTERORDIGIT = MASKXMLLETTER | MASKXMLDIGIT;
	
	private Verifier() {
	}

	/**
	 * Checks whether an element's name is valid.
	 */
	public static final void checkElementName(String name, Locator loc) {
		if (name.indexOf(":") >= 0)
			throw new DOMException(DOMException.INVALID_CHARACTER_ERR,
				"Element or attribute names cannot contain colons", loc);
		checkXMLName(name, loc);
	}

	/**
	 * Checks whether an attribute's name is valid.
	 */
	public static final void checkAttributeName(String name, Locator loc) {
		// Allow xml:space and xml:lang as special cases
		if (!name.equals("xml:space") && !name.equals("xml:lang"))
			checkElementName(name, loc);
	}

	protected static final StringBuffer appendAsHex(StringBuffer sb, char c) {
		return sb.append('\'').append(c)
			.append("' (0x").append(Integer.toHexString(c)).append(')');
	}

	/**
	 * Checks whether a text is valid.
	 */
	public static final void checkCharacterData(String text, Locator loc) {
		if (text == null)
			throw new DOMException(DOMException.INVALID_CHARACTER_ERR,
				"A null is not a legal XML value", loc);

		final int len = text.length();
		for (int i = 0; i < len; i++) {
			// we are expecting a normal char, but may be a surrogate.
			// the isXMLCharacter method takes an int argument, but we have a char.
			// we save a lot of time by doing the test directly here without
			// doing the unnecessary cast-to-int and double-checking ranges
			// for the char.
			// Also, note that we only need to check for non-zero flags, instead
			// of checking for an actual bit, because all the other
			// character roles are a pure subset of CharacterData. Put another way,
			// any character with any bit set, will always also have the
			// CharacterData bit set.
			while (CHARFLAGS[text.charAt(i)] != (byte)0) {
				// fast-loop through the chars until we find something that's not.
				if (++i == len) {
					// we passed all the characters...
					return;
				}
			}
			// the character is not a normal character.
			// we need to sort out what it is. Neither high nor low
			// surrogate pairs are valid characters, so they will get here.
			
			if (Character.isHighSurrogate(text.charAt(i))) {
				// we have the valid high char of a pair.
				// we will expect the low char on the next index,
				i++;
				if (i >= len) {
					// we got a normal character, but we wanted a low surrogate
					throw new DOMException(DOMException.INVALID_CHARACTER_ERR,
							 String.format("Truncated Surrogate Pair 0x%04x????",
										(int)text.charAt(i - 1)), loc);
				}
				if (Character.isLowSurrogate(text.charAt(i))) {
					// we now have the low char of a pair, decode and validate
					if (!isXMLCharacter(decodeSurrogatePair(
							text.charAt(i - 1), text.charAt(i)))) {
						// Likely this character can't be easily displayed
						// because it's a control so we use it'd hexadecimal 
						// representation in the reason.
						throw new DOMException(DOMException.INVALID_CHARACTER_ERR,
								String.format("0x%06x is not a legal XML character",
										decodeSurrogatePair(
												text.charAt(i - 1), text.charAt(i))), loc);
					}
				} else {
					// we got a normal character, but we wanted a low surrogate
					throw new DOMException(DOMException.INVALID_CHARACTER_ERR,
							String.format("Illegal Surrogate Pair 0x%04x%04x",
									(int)text.charAt(i - 1), (int)text.charAt(i)), loc);
				}
			} else {
				// Likely this character can't be easily displayed
				// because it's a control so we use its hexadecimal 
				// representation in the reason.
				throw new DOMException(DOMException.INVALID_CHARACTER_ERR,
						 String.format("0x%04x is not a legal XML character",
									(int)text.charAt(i)), loc);
			}
		}
	}
	/**
	 * This is a utility function to decode a non-BMP 
	 * UTF-16 surrogate pair.
	 * @param high high 16 bits
	 * @param low low 16 bits
	 * @return decoded character
	 */
	public static int decodeSurrogatePair(final char high, final char low) {
		return 0x10000 + (high - 0xD800) * 0x400 + (low - 0xDC00);
	}

	/**
	 * Checks whether a CDATA is valid.
	 */
	public static final void checkCData(String data, Locator loc) {
		if (data.indexOf("]]>") >= 0)
			throw new DOMException(DOMException.INVALID_CHARACTER_ERR,
				"']]>' is not allowed inside a CDATA string", loc);
		checkCharacterData(data, loc);
	}

	/**
	 * Checks whether the prefix of a namespace is valid.
	 */
	public static final void checkNamespacePrefix(String prefix, Locator loc) {
		if (prefix == null || prefix.length() == 0)
			return; //OK: null or empty

		String reason = null;
		char first = prefix.charAt(0);
		if (isXMLDigit(first)) {
			reason = "a number";
		} else if (first == '$') {
			reason = "a dollar sign ($)";
		} else if (first == '-') {
			reason = "a hyphen (-)";
		} else if (first == '.') {
			reason = "a period (.)";
		} else {
			final String s = prefix.toLowerCase(java.util.Locale.ENGLISH);
			if (s.startsWith("xml") && !s.equals("xmlns"))
				reason = "\"xml\" in any combination of case";
		}

		if (reason != null)
			throw new DOMException(DOMException.INVALID_CHARACTER_ERR,
				"Namespace prefixes, "+prefix+", cannot begin with " + reason, loc);

		for (int j=0, len = prefix.length(); j<len; j++)
			if (!isXMLNameCharacter(prefix.charAt(j))) {
				StringBuffer sb =
					new StringBuffer("Namespace prefixes cannot contain ");
				throw new DOMException(DOMException.INVALID_CHARACTER_ERR,
					appendAsHex(sb, prefix.charAt(j)).toString(), loc);
			}

		if (prefix.indexOf(":") >= 0)
			throw new DOMException(DOMException.INVALID_CHARACTER_ERR,
				"Namespace prefixes cannot contain colons", loc);
	}

	/**
	 * Checks whether the URI of a namespace is valid.
	 */
	public static final void checkNamespaceURI(String uri, Locator loc) {
		if (uri == null || uri.length() == 0)
			return; //OK: null or empty

		String reason = null;
		char first = uri.charAt(0);
		if (Character.isDigit(first))
			reason = "a number";
		else if (first == '$')
			reason = "a dollar sign ($)";
		else if (first == '-')
			reason = "a hyphen (-)";

		if (reason != null)
			throw new DOMException(DOMException.INVALID_CHARACTER_ERR,
				"Namespace URIs cannot begin with " + reason, loc);
	}

	/**
	 * Checks whether a processing instruction target is valid.
	 */
	public static final void
	checkPITarget(String target, Locator loc) {
		if (target.indexOf(":") >= 0)
			throw new DOMException(DOMException.INVALID_CHARACTER_ERR,
				"Processing instruction targets cannot contain colons", loc);
		if (target.equalsIgnoreCase("xml"))
			throw new DOMException(DOMException.INVALID_CHARACTER_ERR,
				"Processing instruction targets cannot be " +
				"\"xml\" in any combination of case", loc);

		checkXMLName(target, loc);
	}

	/**
	 * Checks whether a comment data is valid.
	 */
	public static final void checkCommentData(String data, Locator loc) {
		if (data.indexOf("--") >= 0)
			throw new DOMException(DOMException.INVALID_CHARACTER_ERR,
				"Comments cannot contain double hyphens (--)", loc);

		checkCharacterData(data, loc);
	}

	/**
	 * Checks whether a name is valid.
	 */
	public static void checkXMLName(String name, Locator loc) {
		if (name == null || name.length() == 0)
			throw new DOMException(DOMException.INVALID_CHARACTER_ERR,
				"XML names cannot be null or empty", loc);

		if (!isXMLNameStartCharacter(name.charAt(0)))
			throw new DOMException(DOMException.INVALID_CHARACTER_ERR,
				"XML names cannot begin with \'" + name.charAt(0) + '\'', loc);

		for (int j=0, len = name.length(); j<len; j++)
			if (!isXMLNameCharacter(name.charAt(j)))
				throw new DOMException(DOMException.INVALID_CHARACTER_ERR,
					"XML names cannot contain \'" + name.charAt(j) + '\'', loc);
	}

	/**
	 * Checks whether a character is valid.
	 */
	public static boolean isXMLCharacter(int c) {
		if (c >= CHARCNT) {
			return c <= 0x10FFFF;
		}
		return (byte)0 != (byte)(CHARFLAGS[c] & MASKXMLCHARACTER);
	}

	/**
	 * Checks whether a character can be part of a name.
	 */
	public static boolean isXMLNameCharacter(char c) {
		return (byte)0 != (byte)(CHARFLAGS[c] & MASKXMLNAMECHAR) || c == ':';
	}

	/**
	 * Checks whether a character can be the first character of a name.
	 */
	public static boolean isXMLNameStartCharacter(char c) {
		return (byte)0 != (byte)(CHARFLAGS[c] & MASKXMLSTARTCHAR) || c == ':';
	}

	/**
	 * Checks whether a character is a letter or digit.
	 */
	public static boolean isXMLLetterOrDigit(char c) {
		return (byte)0 != (byte)(CHARFLAGS[c] & MASKXMLLETTERORDIGIT);
	}

	/**
	 * Checks whether a character is a letter.
	 */
	public static boolean isXMLLetter(final char c) {
		return (byte)0 != (byte)(CHARFLAGS[c] & MASKXMLLETTER);
	}

	/**
	 * Checks whether a character is a combining character according to
	 * production 87 of the XML 1.0 specification.
	 */
	public static boolean isXMLCombiningChar(final char c) {
		return (byte)0 != (byte)(CHARFLAGS[c] & MASKXMLCOMBINING);
	}

	/**
	 * Checks whether a character is an extender according to
	 * production 88 of the XML 1.0 specification.
	 */
	public static boolean isXMLExtender(final char c) {
		/*
		 * This function is not accellerated by the bitmask system because
		 * there are no longer any actual calls to it from the JDOM code.
		 * It used to be called by the isXMLNameCharacter() method before
		 * the bitmask optimization. Now the VerifierBuilder code actually
		 * calls this method instead.
		 */

		if (c < 0x00B6) return false;  // quick short circuit

		// Extenders                               
		if (c == 0x00B7) return true;
		if (c == 0x02D0) return true;
		if (c == 0x02D1) return true;
		if (c == 0x0387) return true;
		if (c == 0x0640) return true;
		if (c == 0x0E46) return true;
		if (c == 0x0EC6) return true;
		if (c == 0x3005) return true;

		if (c < 0x3031) return false;  if (c <= 0x3035) return true;
		if (c < 0x309D) return false;  if (c <= 0x309E) return true;
		if (c < 0x30FC) return false;  if (c <= 0x30FE) return true;

		return false;

	}

	/**
	 * <p>
	 * Checks whether a character is a digit according to
	 * production 88 of the XML 1.0 specification.
	 */
	public static boolean isXMLDigit(final char c) {
		return (byte)0 != (byte)(CHARFLAGS[c] & MASKXMLDIGIT);
	}  
}
