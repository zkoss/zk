/* RepeatableInputStream.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Mar 12, 2008 12:03:53 PM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2008 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.zkoss.lang.Strings;

/**
 * This class is used to wrap the input stream in a repeatable input stream,
 * which can reread the input stream. There are two options that can allow the
 * repeatable input stream caching in a memory or in a template file system.
 * 
 * @author jumperchen
 * @since 3.0.4
 */
public class RepeatableInputStream extends InputStream {
	private InputStream _is;
	private OutputStream _out;
	private InputStream _in;
	private boolean _close, _native;
	private File _f;
	/**
	 * Sets the system property with "org.zkoss.io.beffer_limit_size".
	 * The maximum limit of bytes that can be read before the mark position becomes invalid.
	 * It is only used caching in memory.
	 * <p>Default: 10240000
	 */
	public static String BEFFER_LIMIT_SIZE = System.getProperty("org.zkoss.io.beffer_limit_size");
	/** 
	 * Sets the system property with "org.zkoss.io.max_allow-size".
	 * The maximum limit of bytes that can be cached in memory. Otherwise, the 
	 * repeatable input stream should be chached in file system.
	 * <p>Default: 512000
	 */
	public static String MAX_ALLOW_SIZE = System.getProperty("org.zkoss.io.max_allow-size");
	/**
	 * Template file directory.
	 */
	public static String TEMP_FILE_DIR = "ZK";


	private RepeatableInputStream(InputStream is) {
		_is = is;
	}

	/**
	 * Returns a repeatable media with a repeatable input stream, if media is
	 * not null.
	 */
	public static InputStream getInstance(InputStream is) {
		if (is != null && !(is instanceof RepeatableInputStream)) {
			return new RepeatableInputStream(is);
		}
		return is;
	}

	/**
	 * Returns whether to treat the InputStream as binary.
	 * <p>
	 * Default: true.
	 */
	public boolean isNative() {
		return _native;
	}

	/**
	 * Sets whether to treat the InputStream as binary.
	 */
	public void setNative(boolean alwaysNative) {
		_native = alwaysNative;
	}

	private void initOutputstream() throws IOException {
		final String p = System.getProperty("org.zkoss.io.max_allow-size");
		final int maxsize = !Strings.isBlank(p) ? Integer.parseInt(p) : 512000;
		if (_is.available() > maxsize)
			setNative(false);
		if (isNative()) {
			_out = new ByteArrayOutputStream();
		} else {
			final File f = new File(System.getProperty("java.io.tmpdir")
					+ TEMP_FILE_DIR);
			if (!f.isDirectory())
				f.mkdir();
			_f = File.createTempFile("zk.io", ".zk.io", f);
			_out = new FileOutputStream(_f);
		}
	}

	public int read() throws IOException {
		if (!_close) {
			final int i = _is.read();
			if (_out == null) initOutputstream();
			_out.write(i);
			return i;
		} else {
			if (!isNative() && _in == null) initInputStream();
			return _in.read();
		}
	}

	private void initInputStream() throws FileNotFoundException {
		if (isNative()) {
			_in = new ByteArrayInputStream(((ByteArrayOutputStream) _out)
					.toByteArray());
			_in.mark(BEFFER_LIMIT_SIZE == null ? 10240000 : Integer
					.parseInt(BEFFER_LIMIT_SIZE));
		} else {
			_in = new FileInputStream(_f);
		}
	}

	public void close() throws IOException {
		if (!_close) {
			_close = true;
			_is.close();
			_out.close();
			_out = null;
		} else {
			if (isNative())
				_in.reset();
			else {
				_in.close();
				_in = null;
			}
		}
	}

	protected void finalize() throws Throwable {
		if (_f != null)
			_f.delete();
		super.finalize();
	}

}
