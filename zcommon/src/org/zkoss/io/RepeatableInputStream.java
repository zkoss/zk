/* RepeatableInputStream.java

 Purpose:
 
 Description:
 
 History:
 Mar 12, 2008 12:03:53 PM , Created by jumperchen

 Copyright (C) 2008 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under LGPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.net.URL;

import org.zkoss.lang.Library;
import org.zkoss.util.logging.Log;

/**
 * {@link RepeatableInputStream} adds functionality to another input stream,
 * the ability to read repeatedly.
 * By repeatable-read we meaen, after {@link #close}, the next invocation of
 * {@link #read} will re-open the input stream.
 *
 * <p>{@link RepeatableInputStream} actually creates a temporary space
 * to buffer the content, so it can be re-opened again after closed.
 * Notice that the temporary space (aka., the buffered input stream)
 * is never closed until garbage-collected.
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
 * @author tomyeh
 * @since 3.0.4
 */
public class RepeatableInputStream extends InputStream implements Repeatable {
	private static final Log log = Log.lookup(RepeatableInputStream.class);

	/*package*/ static final String BUFFER_LIMIT_SIZE = "org.zkoss.io.bufferLimitSize";
	/*package*/ static final String MEMORY_LIMIT_SIZE = "org.zkoss.io.memoryLimitSize";

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
		_bufmaxsz = Library.getIntProperty(BUFFER_LIMIT_SIZE, 20 * 1024 * 1024);
		_memmaxsz = Library.getIntProperty(MEMORY_LIMIT_SIZE, 512 * 1024);
	}

	/**
	 * Returns an input stream that can be read repeatedly, or null if the
	 * given input stream is null.
	 * Note: the returned input stream encapsulates the given input stream, rd
	 * (aka., the buffered input stream) to adds the functionality to
	 * re-opens the input stream once {@link #close} is called.
	 *
	 * <p>By repeatable-read we meaen, after {@link #close}, the next
	 * invocation of {@link #read} will re-open the input stream.
	 *
	 * <p>Use this method instead of instantiating {@link RepeatableInputStream}
	 * with the constructor.
	 *
	 * @see #getInstance(File)
	 */
	public static InputStream getInstance(InputStream is) {
		if (is instanceof ByteArrayInputStream)
			return new ResetableInputStream(is);
		else if (is != null && !(is instanceof Repeatable))
			return new RepeatableInputStream(is);
		return is;
	}
	/**
	 * Returns an input stream of a file that can be read repeatedly.
	 * Note: it assumes the file is binary (rather than text).
	 *
	 * <p>By repeatable-read we meaen, after {@link #close}, the next
	 * invocation of {@link #read} will re-open the input stream.
	 *
	 * <p>Note: it is effecient since we don't have to buffer the
	 * content of the file to make it repeatable-read.
	 *
	 * @exception IllegalArgumentException if file is null.
	 * @exception FileNotFoundException if file not found
	 * @see #getInstance(InputStream)
 	 * @see #getInstance(String)
	 */
	public static InputStream getInstance(File file)
	throws FileNotFoundException {
		if (file == null)
			throw new IllegalArgumentException("null");
		if (!file.exists())
			throw new FileNotFoundException(file.toString());
		return new RepeatableFileInputStream(file);
	}
	/**
	 * Returns an input stream of a file that can be read repeatedly.
	 * Note: it assumes the file is binary (rather than text).
	 *
	 * <p>By repeatable-read we meaen, after {@link #close}, the next
	 * invocation of {@link #read} will re-open the input stream.
	 *
	 * <p>Note: it is effecient since we don't have to buffer the
	 * content of the file to make it repeatable-read.
	 *
	 * @exception IllegalArgumentException if file is null.
	 * @see #getInstance(InputStream)
	 * @see #getInstance(File)
	 */
	public static InputStream getInstance(String filename)
	throws FileNotFoundException {
		return getInstance(new File(filename));
	}
	/**
	 * Returns an input stream of the resource of the given URL
	 * that can be read repeatedly.
	 * Note: it assumes the resource is binary (rather than text).
	 *
	 * <p>By repeatable-read we meaen, after {@link #close}, the next
	 * invocation of {@link #read} will re-open the input stream.
	 *
	 * <p>Note: it is effecient since we don't have to buffer the
	 * content of the resource to make it repeatable-read.
	 *
	 * @exception IllegalArgumentException if url is null.
	 * @see #getInstance(InputStream)
 	 * @see #getInstance(String)
	 */
	public static InputStream getInstance(URL url) {
		if (url == null)
			throw new IllegalArgumentException("null");
		return new RepeatableURLInputStream(url);
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
				_out = new BufferedOutputStream(new FileOutputStream(_f));
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
			final int b = _org.read();
			if (!_nobuf)
				if (b >= 0) {
					final OutputStream out = getOutputStream();
					if (out != null) out.write(b);
					++_cntsz;
				}
			return b;
		} else {
			if (_in == null)
				_in = new BufferedInputStream(new FileInputStream(_f)); //_f must be non-null

			return _in.read();
		}
	}

	/** Closes the current access, and the next call of {@link #close}
	 * re-opens the buffered input stream.
	 */
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

	//Object//
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
/*package*/ class ResetableInputStream extends InputStream
implements Repeatable {
	private final InputStream _org;
	ResetableInputStream(InputStream bais) {
		_org = bais;
	}

	public int read() throws IOException {
		return _org.read();
	}
	/** Closes the current access, and the next call of {@link #read}
	 * re-opens the buffered input stream.
	 */
	public void close() throws IOException {
		_org.reset();
	}

	//Object//
	protected void finalize() throws Throwable {
		_org.close();
		super.finalize();
	}
}
/*package*/ class RepeatableFileInputStream extends InputStream
implements Repeatable {
	private final File _file;
	private InputStream _in;

	RepeatableFileInputStream(File file) {
		_file = file;
	}

	public int read() throws IOException {
		if (_in == null)
			_in = new BufferedInputStream(new FileInputStream(_file));
		return _in.read();
	}
	/** Closes the current access, and the next call of {@link #read}
	 * re-opens the buffered input stream.
	 */
	public void close() throws IOException {
		if (_in != null) {
			_in.close();
			_in = null;
		}
	}

	//Object//
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
}
/*package*/ class RepeatableURLInputStream extends InputStream
implements Repeatable {
	private final URL _url;
	private InputStream _in;

	RepeatableURLInputStream(URL url) {
		_url = url;
	}

	public int read() throws IOException {
		if (_in == null) {
			_in = _url.openStream();
			if (_in == null) throw new FileNotFoundException(_url.toExternalForm());
			_in = new BufferedInputStream(_in);
		}
		return _in.read();
	}
	/** Closes the current access, and the next call of {@link #read}
	 * re-opens the buffered input stream.
	 */
	public void close() throws IOException {
		if (_in != null) {
			_in.close();
			_in = null;
		}
	}

	//Object//
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
}
