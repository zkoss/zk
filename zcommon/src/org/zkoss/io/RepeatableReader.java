/* RepeatableReader.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Mar 14 11:47:38     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.io;

import java.io.Reader;
import java.io.StringReader;
import java.io.CharArrayReader;
import java.io.Writer;
import java.io.StringWriter;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;

import org.zkoss.util.logging.Log;

/**
 * {@link RepeatableReader} adds functionality to another reader,
 * the ability to read repeatedly.
 * By repeatable-read we meaen, after {@link #close}, the next invocation of
 * {@link #read} will re-open the reader.
 *
 * <p>{@link RepeatableInputStream} actually creates a temporary space
 * to buffer the content, so it can be re-opened again after closed.
 * Notice that the temporary space (aka., the buffered reader)
 * is never closed until garbage-collected.
 *
 * <p>If the content size of the given reader is smaller than
 * the value specified in the system property called
 * "org.zkoss.io.memoryLimitSize", the content will be buffered in
 * the memory. If the size exceeds, the content will be buffered in
 * a temporary file. By default, it is 512KB.
 * Note: the maximal value is {@link Integer#MAX_VALUE}
 *
 * <p>If the content size of the given reader is larger than
 * the value specified in the system property called
 * "org.zkoss.io.bufferLimitSize", the content won't be buffered,
 * and it means the read is not repeatable. By default, it is 20MB.
 * Note: the maximal value is {@link Integer#MAX_VALUE}
 *
 * @author tomyeh
 * @since 3.0.4
 */
public class RepeatableReader extends Reader implements Repeatable {
	private static final Log log = Log.lookup(RepeatableReader.class);

	private Reader _org;
	private Writer _out;
	private Reader _in;
	private File _f;
	/** The content size. It is meaningful only if !_nobuf.
	 * Note: int is enough (since long makes no sense for buffering)
	 */
	private int _cntsz;
	private final int _bufmaxsz, _memmaxsz;
	private boolean _nobuf;

	private RepeatableReader(Reader is) {
		_org = is;
		_bufmaxsz = RepeatableInputStream.getIntProp(
			RepeatableInputStream.BUFFER_LIMIT_SIZE, 20 * 1024 * 1024);
		_memmaxsz = RepeatableInputStream.getIntProp(
			RepeatableInputStream.MEMORY_LIMIT_SIZE, 512 * 1024);
	}

	/**
	 * Returns a reader that can be read repeatedly, or null if the given
	 * reader is null.
	 * Note: the returned reader encapsulates the given reader, rd
	 * (aka., the buffered reader) to adds the functionality to
	 * re-opens the reader once {@link #close} is called.
	 *
	 * <p>By repeatable-read we meaen, after {@link #close}, the next
	 * invocation of {@link #read} will re-open the reader.
	 *
	 * <p>Use this method instead of instantiating {@link RepeatableReader}
	 * with the constructor.
	 *
	 * @see #getInstance(File)
	 */
	public static Reader getInstance(Reader rd) {
		if ((rd instanceof CharArrayReader) || (rd instanceof StringReader))
			return new ResetableReader(rd);
		else if (rd != null && !(rd instanceof Repeatable))
			return new RepeatableReader(rd);
		return rd;
	}
	/**
	 * Returns a reader to read a file that can be read repeatedly.
	 * Note: it assumes the file is text (rather than binary).
	 *
	 * <p>By repeatable-read we meaen, after {@link #close}, the next
	 * invocation of {@link #read} will re-open the reader.
	 *
	 * <p>Note: it is effecient since we don't have to buffer the
	 * content of the file to make it repeatable-read.
	 *
	 * @param charset the charset. If null, "UTF-8" is assumed.
	 * @exception IllegalArgumentException if file is null.
	 * @see #getInstance(Reader)
	 * @see #getInstance(String, String)
	 * @since 3.0.8
	 */
	public static Reader getInstance(File file, String charset)
	throws FileNotFoundException {
		if (file == null)
			throw new IllegalArgumentException("null");
		if (!file.exists())
			throw new FileNotFoundException(file.toString());
		return new RepeatableFileReader(file, charset);
	}
	/**
	 * Returns a reader to read a file, encoded in UTF-8,
	 * that can be read repeatedly.
	 * Note: it assumes the file is text (rather than binary).
	 *
	 * <p>By repeatable-read we meaen, after {@link #close}, the next
	 * invocation of {@link #read} will re-open the reader.
	 *
	 * <p>Note: it is effecient since we don't have to buffer the
	 * content of the file to make it repeatable-read.
	 *
	 * @exception IllegalArgumentException if file is null.
	 * @see #getInstance(Reader)
	 * @see #getInstance(String)
	 */
	public static Reader getInstance(File file)
	throws FileNotFoundException {
		return getInstance(file, "UTF-8");
	}
	/**
	 * Returns a reader to read a file that can be read repeatedly.
	 * Note: it assumes the file is text (rather than binary).
	 *
	 * <p>By repeatable-read we meaen, after {@link #close}, the next
	 * invocation of {@link #read} will re-open the reader.
	 *
	 * <p>Note: it is effecient since we don't have to buffer the
	 * content of the file to make it repeatable-read.
	 *
	 * @param filename the file name
	 * @param charset the charset. If null, "UTF-8" is assumed.
	 * @exception IllegalArgumentException if file is null.
	 * @exception FileNotFoundException if file is not found.
	 * @see #getInstance(Reader)
	 * @see #getInstance(File, String)
	 * @since 3.0.8
	 */
	public static Reader getInstance(String filename, String charset)
	throws FileNotFoundException {
		return getInstance(new File(filename));
	}
	/**
	 * Returns a reader to read a file, encoded in UTF-8,
	 * that can be read repeatedly.
	 * Note: it assumes the file is text (rather than binary).
	 *
	 * <p>By repeatable-read we meaen, after {@link #close}, the next
	 * invocation of {@link #read} will re-open the reader.
	 *
	 * <p>Note: it is effecient since we don't have to buffer the
	 * content of the file to make it repeatable-read.
	 *
	 * @param filename the file name
	 * @exception IllegalArgumentException if file is null.
	 * @exception FileNotFoundException if file is not found.
	 * @see #getInstance(Reader)
	 * @see #getInstance(File)
	 */
	public static Reader getInstance(String filename)
	throws FileNotFoundException {
		return getInstance(new File(filename), "UTF-8");
	}
	/**
	 * Returns a reader to read the resource of the specified URL.
	 * The reader can be read repeatedly.
	 * Note: it assumes the resource is text (rather than binary).
	 *
	 * <p>By repeatable-read we meaen, after {@link #close}, the next
	 * invocation of {@link #read} will re-open the reader.
	 *
	 * <p>Note: it is effecient since we don't have to buffer the
	 * content of the file to make it repeatable-read.
	 *
	 * @param charset the charset. If null, "UTF-8" is assumed.
	 * @exception IllegalArgumentException if file is null.
	 * @see #getInstance(Reader)
	 * @see #getInstance(String, String)
	 * @since 3.0.8
	 */
	public static Reader getInstance(URL url, String charset) {
		if (url == null)
			throw new IllegalArgumentException("null");
		return new RepeatableURLReader(url, charset);
	}
	/**
	 * Returns a reader to read the resource of the specified URL,
	 * encoded in UTF-8.
	 * The reader can be read repeatedly.
	 * Note: it assumes the resource is text (rather than binary).
	 *
	 * <p>By repeatable-read we meaen, after {@link #close}, the next
	 * invocation of {@link #read} will re-open the reader.
	 *
	 * <p>Note: it is effecient since we don't have to buffer the
	 * content of the file to make it repeatable-read.
	 *
	 * @exception IllegalArgumentException if file is null.
	 * @see #getInstance(Reader)
	 * @see #getInstance(String)
	 */
	public static Reader getInstance(URL url) {
		return getInstance(url, "UTF-8");
	}

	private Writer getWriter() throws IOException {
		if (_out == null)
			return _nobuf ? null: (_out = new StringWriter());
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
				final String cnt = ((StringWriter)_out).toString();
				_out = new FileWriter(_f, "UTF-8");
				_out.write(cnt);
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

	public int read(char cbuf[], int off, int len) throws IOException {
		if (_org != null) {
			final int cnt = _org.read(cbuf, off, len);
			if (!_nobuf)
				if (cnt >= 0) {
					final Writer out = getWriter();
					if (out != null) out.write(cbuf, off, cnt);
					_cntsz += cnt;
				}
			return cnt;
		} else {
			if (_in == null)
				_in = new FileReader(_f, "UTF-8"); //_f must be non-null

			return _in.read(cbuf, off, len);
		}
	}

	/** Closes the current access, and the next call of {@link #read}
	 * re-opens the buffered reader.
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
					_in = new StringReader(
						((StringWriter)_out).toString());
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
/*package*/ class ResetableReader extends Reader implements Repeatable {
	private final Reader _org;
	ResetableReader(Reader bais) {
		_org = bais;
	}

	public int read(char cbuf[], int off, int len) throws IOException {
		return _org.read(cbuf, off, len);
	}
	/** Closes the current access and the next call of {@link #read}
	 * re-opens the buffered reader.
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
/*package*/ class RepeatableFileReader extends Reader implements Repeatable {
	private final File _file;
	private Reader _in;
	private final String _charset;

	RepeatableFileReader(File file, String charset) {
		_file = file;
		_charset = charset != null ? charset: "UTF-8";
	}

	public int read(char cbuf[], int off, int len) throws IOException {
		if (_in == null)
			_in = new FileReader(_file, _charset);
		return _in.read(cbuf, off, len);
	}
	/** Closes the current access and the next call of {@link #read}
	 * re-opens the buffered reader.
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
/*package*/ class RepeatableURLReader extends Reader implements Repeatable {
	private final URL _url;
	private Reader _in;
	private final String _charset;

	RepeatableURLReader(URL url, String charset) {
		_url = url;
		_charset = charset != null ? charset: "UTF-8";
	}

	public int read(char cbuf[], int off, int len) throws IOException {
		if (_in == null)
			_in = new URLReader(_url, _charset);
		return _in.read(cbuf, off, len);
	}
	/** Closes the current access and the next call of {@link #read}
	 * re-opens the buffered reader.
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
