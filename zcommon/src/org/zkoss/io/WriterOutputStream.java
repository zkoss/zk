/* WriterOutputStream.java

	Purpose:
		
	Description:
		
	History:
		Mon May  1 22:00:51     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.io;

import java.io.Writer;
import java.io.OutputStream;
import java.io.IOException;

/**
 * An output stream that is on top of a writer.
 *
 * @author tomyeh
 */
public class WriterOutputStream extends OutputStream {
	private final Writer _writer;
	private final String _charset;
	private final byte[] _bs; //used only with writer
	private int _cnt, _type; //used only with writer

	/** Constructs an out stream with the specified encoding.
	 *
	 * @param charset the charset. If null, "UTF-8" is assumed.
	 */
	public WriterOutputStream(Writer writer, String charset) {
		if (writer == null) throw new IllegalArgumentException("null");
		_writer = writer;
		_bs = new byte[3];
		_charset = charset == null ? "UTF-8": charset;
	}
	/** Constructs an output stream assuming UTF-8 encoding.
	 */
	public WriterOutputStream(Writer writer) {
		this(writer, null);
	}
	public void write(byte[] b) throws IOException {
		_writer.write(new String(b, _charset));
	}
	public void write(byte[] b, int off, int len) throws IOException {
		_writer.write(new String(b, 0, b.length, _charset));
	}
	public void write(int b) throws IOException {
		if (_type != 0) {
			if (!"UTF-8".equals(_charset) || (b & 0xc0) == 0x80) {
				_bs[_cnt ++] = (byte)b;
				if (_cnt == _type) { //complete
					_writer.write(new String(_bs, 0, _type, _charset));
					_type = 0; //reset
				}
				return;
			} else { //failed
				for (int j = 0; j < _cnt; ++j)
					_writer.write(_bs[j]);
				_type = 0; //reset
			}
		} else {
			if ("UTF-8".equals(_charset)) {
				if ((b & 0xe0) == 0xc0 || (b & 0xf0) == 0xe0) {
					_type = (b & 0xf0) == 0xe0 ? 3: 2;
					_bs[0] = (byte)b;
					_cnt = 1;
					return;
				}
			} else {
				//assume two-bytes per char if 0x80 -- might fail.
				if ((b & 0x80) != 0) {
					_type = 2;
					_bs[0] = (byte)b;
					_cnt = 1;
					return;
				}
			}
		}
		_writer.write(b);
	}
	public void flush() throws IOException {
		if (_type != 0) {
			for (int j = 0; j < _cnt; ++j)
				_writer.write(_bs[j]); //reset
			_type = 0;
		}
		_writer.flush();
		super.flush();
	}
	public void close() throws IOException {
		flush();
		_writer.close();
		super.close();
	}
}
