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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.zkoss.util.logging.Log;

/**
 * {@link RepeatableInputStream} adds functionality to another input stream,
 * the ability to read repeatedly.
 * Unlike other input stream, {@link RepeatableInputStream} re-opened
 * the buffered input stream, when {@link #close} is called.
 * In other words, the buffered input stream is never closed until
 * it is GC-ed.
 *
 * <p>If the content size of the given input stream is smaller than
 * the value specified in the system property called
 * "org.zkoss.io.memoryLimitSize", the content will be buffered in
 * the memory. If the size exceeds, the content will be buffered in
 * a temporary file. By default, it is 512KB.
 * Note: the maximal value is {@link Integer#MAX_VALUE}
 *
 * <p>If the content size of the given input stream is larger than
 * the value specified in the system property called
 * "org.zkoss.io.bufferLimitSize", the content won't be buffered,
 * and it means the read is not repeatable. By default, it is 20MB.
 * Note: the maximal value is {@link Integer#MAX_VALUE}
 * 
 * @author jumperchen
 * @since 3.0.4
 */
public class RepeatableInputStream extends InputStream {
	private static final Log log = Log.lookup(RepeatableInputStream.class);

	private InputStream _org;
	private OutputStream _out;
	private InputStream _in;
	private File _f;
	/** The content size. It is meaningful only if !_nobuf.
	 * Note: int is enough (since long makes no sense for buffering)
	 */
	private int _cntsz;
	private final int _bufmaxsz, _memmaxsz;
	private boolean _nobuf;

	private RepeatableInputStream(InputStream is) {
		_org = is;
		_bufmaxsz = getIntProp("org.zkoss.io.bufferLimitSize", 20 * 1024 * 1024);
		_memmaxsz = getIntProp("org.zkoss.io.memoryLimitSize", 512 * 1024);
	}

	/**
	 * Returns a repeatable media with a repeatable input stream, if media is
	 * not null.
	 *
	 * <p>Use this method instead of instantiating {@link RepeatableInputStream}
	 * with the constructor.
	 */
	public static InputStream getInstance(InputStream is) {
		if (is != null && !(is instanceof RepeatableInputStream)) {
			return new RepeatableInputStream(is);
		}
		return is;
	}

	private static int getIntProp(String name, int defVal) {
		String val = null;
		try {
			val = System.getProperty(name);
			if (val != null)
				return Integer.parseInt(val);
		} catch (Throwable ex) { //ignore
			log.warning("Ignored: illegal number, "+val+", for "+name);
		}
		return defVal;
	}

	private OutputStream getOutputStream() throws IOException {
		if (_out == null)
			return _nobuf ? null: (_out = new ByteArrayOutputStream());
				//it is possible _membufsz <= 0, but OK to use memory first

		if (_cntsz >= _bufmaxsz) { //too large to buffer
			disableBuffering();
			return null;
		}

		if (_f == null && _cntsz >= _memmaxsz) { //memory to file
			try {
				final File f =
					new File(System.getProperty("java.io.tmpdir"), "zk");
				if (!f.isDirectory())
					f.mkdir();
				_f = File.createTempFile("zk.io", ".zk.io", f);
				final byte[] bs = ((ByteArrayOutputStream)_out).toByteArray();
				_out = new FileOutputStream(_f);
				_out.write(bs);
			} catch (Throwable ex) {
				log.warning("Ingored: failed to buffer to a file, "+_f+"\nCause: "+ex.getMessage());
				disableBuffering();
			}
		}
		return _out;
	}
	private void disableBuffering() {
		_nobuf = true;
		if (_out != null) {
			try {
				_out.close();
			} catch (Throwable ex) { //ignore
			}
			_out = null;
		}
		if (_f != null) {
			try {
				_f.delete();
			} catch (Throwable ex) { //ignore
			}
			_f = null;
		}
	}

	public int read() throws IOException {
		if (_org != null) {
			final int i = _org.read();
			if (!_nobuf)
				if (i >= 0) {
					final OutputStream out = getOutputStream();
					if (out != null) out.write(i);
					_cntsz += i;
				}
			return i;
		} else {
			if (_in == null)
				_in = new FileInputStream(_f); //_f must be non-null

			return _in.read();
		}
	}

	public void close() throws IOException {
		_cntsz = 0;
		if (_org != null) {
			_org.close();

			if (_out != null) {
				try {
					_out.close();
				} catch (Throwable ex) {
					log.warning("Ignored: failed to close the buffer.\nCause: "+ex.getMessage());
					disableBuffering();
					return;
				}
				if (_f == null)
					_in = new ByteArrayInputStream(
						((ByteArrayOutputStream)_out).toByteArray());
					//we don't initilize _in if _f is not null
					//to reduce memory use (after all, read might not be called)
				_out = null;
				_org = null;
			}
		} else if (_in != null) {
			if (_f != null) {
				_in.close();
				_in = null;
			} else {
				_in.reset();
			}
		}
	}

	protected void finalize() throws Throwable {
		disableBuffering();
		if (_org != null)
			_org.close();
		if (_in != null) {
			_in.close();
			_in = null;
		}
		super.finalize();
	}

}
