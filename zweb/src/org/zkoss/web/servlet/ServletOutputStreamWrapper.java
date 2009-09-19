/* ServletOutputStreamWrapper.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 17 14:08:22     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet;

import java.io.Writer;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

import org.zkoss.io.WriterOutputStream;

/**
 * A facade of OutputStream for implementing ServletOutputStream.
 *
 * @author tomyeh
 */
public class ServletOutputStreamWrapper extends ServletOutputStream {
	private final OutputStream _stream;

	/** Returns a facade of the specified stream. */
	public static ServletOutputStream getInstance(OutputStream stream) {
		if (stream instanceof ServletOutputStream)
			return (ServletOutputStream)stream;
		return new ServletOutputStreamWrapper(stream);
	}
	/** Returns a facade of the specified writer.
	 *
	 * @param charset the charset. If null, "UTF-8" is assumed.
	 */
	public static ServletOutputStream getInstance(Writer writer, String charset) {
		return new ServletOutputStreamWrapper(writer, charset);
	}

	private ServletOutputStreamWrapper(OutputStream stream) {
		if (stream == null)
			throw new IllegalArgumentException("null");
		_stream = stream;
	}
	/**
	 * @param charset the charset. If null, "UTF-8" is assumed.
	 */
	public ServletOutputStreamWrapper(Writer writer, String charset) {
		if (writer == null)
			throw new IllegalArgumentException("null");

		_stream = new WriterOutputStream(writer, charset);
	}

	public void write(int b) throws IOException {
		_stream.write(b);
	}
	public void flush() throws IOException {
		_stream.flush();
		super.flush();
	}
	public void close() throws IOException {
		_stream.close();
		super.close();
	}
}
