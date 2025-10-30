/* B85_ZK_3347Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 21 13:05:23 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.io.Files;
import org.zkoss.zk.ui.sys.DigestUtilsHelper;

/**
 * @author rudyhuang
 */
public class B85_ZK_3347Test {
	@Test
	public void testMd5HexInputStream() throws Exception {
		String expected;
		try (InputStream in = getClassInputStream()) {
			Assertions.assertNotNull(in, "This file stream can not be null! (Not compiled?)");
			expected = bytesToHexString(hashMd5(in));
		}
		try (InputStream in = getClassInputStream()) {
			Assertions.assertEquals(expected, DigestUtilsHelper.md5Hex(in));
		}
	}

	private InputStream getClassInputStream() {
		return getClass().getResourceAsStream(getClass().getSimpleName() + ".class");
	}

	private byte[] hashMd5(InputStream inputStream) throws IOException, NoSuchAlgorithmException {
		DigestInputStream digestStream = new DigestInputStream(inputStream, MessageDigest.getInstance("MD5"));
		Files.copy(new NullOutputStream(), digestStream); //consume the stream to build the MD5 without buffering in memory
		return digestStream.getMessageDigest().digest();
	}

	private String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	private class NullOutputStream extends OutputStream {
		@Override
		public void write(byte[] b, int off, int len) {}

		@Override
		public void write(int b) {}

		@Override
		public void write(byte[] b) throws IOException {}
	}
}
