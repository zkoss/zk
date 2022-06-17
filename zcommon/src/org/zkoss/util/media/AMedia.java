/* AMedia.java

	Purpose:
		
	Description:
		
	History:
		Thu May 27 15:10:46     2004, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util.media;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import org.zkoss.io.Files;
import org.zkoss.io.NullInputStream;
import org.zkoss.io.NullReader;
import org.zkoss.io.RepeatableInputStream;
import org.zkoss.io.RepeatableReader;
import org.zkoss.lang.Library;
import org.zkoss.lang.SystemException;

/**
 * A media object holding content such PDF, HTML, DOC or XLS content.
 *
 * <p>AMedia is serializable, but, if you are using InputStream or Reader,
 * you have to extend this class, and provide the implementation to
 * serialize and deserialize {@link #_isdata} or {@link #_rddata}
 * (they are both transient).
 * @author tomyeh
 */
public class AMedia implements Media, java.io.Serializable {

	/** Used if you want to implement a media whose input stream is created
	 * dynamically each time {@link #getStreamData} is called.
	 * @see #AMedia(String,String,String,InputStream)
	 */
	protected static final InputStream DYNAMIC_STREAM = new NullInputStream();
	/** Used if you want to implement a media whose reader is created
	 * dynamically each time {@link #getReaderData} is called.
	 * @see #AMedia(String,String,String,Reader)
	 */
	protected static final Reader DYNAMIC_READER = new NullReader();

	/** The binary data, {@link #getByteData}. */
	private byte[] _bindata;
	/** The text data, {@link #getStringData}. */
	private String _strdata;
	/** The input stream, {@link #getStreamData} */
	protected transient InputStream _isdata;
	/** The input stream, {@link #getReaderData} */
	protected transient Reader _rddata;
	/** The content type. */
	private String _ctype;
	/** The format (e.g., pdf). */
	private String _format;
	/** The name (usually filename). */
	private String _name;
	/** Whether to allow Content-Disposition 
	 * or not when writing the media to response header. */
	private boolean _cntDisposition = true;

	/** Construct with name, format, content type and binary data.
	 *
	 * <p>It tries to construct format and ctype from each other or name.
	 *
	 * @param name the name (usually filename); might be null.
	 * @param format the format; might be null. Example: "html" and "xml"
	 * @param ctype the content type; might be null. Example: "text/html"
	 * and "text/xml;charset=UTF-8".
	 * @param data the binary data; never null
	 */
	public AMedia(String name, String format, String ctype, byte[] data) {
		if (data == null)
			throw new IllegalArgumentException("data");
		_bindata = data;
		setup(name, format, ctype);
	}
	/** Construct with name, format, content type and text data.
	 *
	 * <p>It tries to construct format and ctype from each other or name.
	 *
	 * @param name the name (usually filename); might be null.
	 * @param format the format; might be null.
	 * @param ctype the content type; might be null.
	 * @param data the text data; never null
	 */
	public AMedia(String name, String format, String ctype, String data) {
		if (data == null)
			throw new IllegalArgumentException("data");
		_strdata = data;
		setup(name, format, ctype);
	}
	/** Construct with name, format, content type and stream data (binary).
	 *
	 * <p>It tries to construct format and ctype from each other or name.
	 *
	 * @param name the name (usually filename); might be null.
	 * @param format the format; might be null.
	 * @param ctype the content type; might be null.
	 * @param data the binary data; never null.
	 * If the input stream is created dynamically each time {@link #getStreamData}
	 * is called, you shall pass {@link #DYNAMIC_STREAM}
	 * as the data argument. Then, override {@link #getStreamData} to return
	 * the correct stream.
	 * Note: the caller of {@link #getStreamData} has to close
	 * the returned input stream.
	 */
	public AMedia(String name, String format, String ctype, InputStream data) {
		if (data == null)
			throw new IllegalArgumentException("data");
		_isdata = data;
		setup(name, format, ctype);
	}
	/** Construct with name, format, content type and reader data (textual).
	 *
	 * <p>It tries to construct format and ctype from each other or name.
	 *
	 * @param name the name (usually filename); might be null.
	 * @param format the format; might be null.
	 * @param ctype the content type; might be null.
	 * @param data the string data; never null
	 * If the reader is created dynamically each time {@link #getReaderData}
	 * is called, you shall pass {@link #DYNAMIC_READER}
	 * as the data argument. Then, override {@link #getReaderData} to return
	 * the correct reader.
	 */
	public AMedia(String name, String format, String ctype, Reader data) {
		if (data == null)
			throw new IllegalArgumentException("data");
		_rddata = data;
		setup(name, format, ctype);
	}
	/** Construct with name, format, content type and a file.
	 *
	 * <p>Unlike others, it uses the so-called repeatable input
	 * stream or reader (depending on binary or not) to represent the file,
	 * so the input stream ({@link #getStreamData})
	 * or the reader ({@link #getReaderData}) will be re-opened
	 * in the next invocation of {@link InputStream#read}
	 * after {@link InputStream#close} is called.
	 * See also {@link RepeatableInputStream} and {@link RepeatableReader}.
	 *
	 * @param name the name (usually filename); might be null.
	 * If null, the file name is used.
	 * @param format the format; might be null.
	 * @param ctype the content type; might be null.
	 * @param file the file; never null.
	 * @param binary whether it is binary.
	 * If not binary, "UTF-8" is assumed.
	 */
	public AMedia(String name, String format, String ctype, File file,
	boolean binary) throws java.io.FileNotFoundException {
		this(name, format, ctype, file, binary ? null: "UTF-8");
	}
	/** Construct with name, format, content type and a file.
	 *
	 * <p>Unlike others, it uses the so-called repeatable input
	 * stream or reader (depending on charset is null or not)
	 * to represent the file, so the input stream ({@link #getStreamData})
	 * or the reader ({@link #getReaderData}) will be re-opened
	 * in the next invocation of {@link InputStream#read}
	 * after {@link InputStream#close} is called.
	 * See also {@link RepeatableInputStream} and {@link RepeatableReader}.
	 *
	 * @param name the name (usually filename); might be null.
	 * If null, the file name is used.
	 * @param format the format; might be null.
	 * @param ctype the content type; might be null.
	 * @param file the file; never null.
	 * @param charset the charset. If null, it is assumed to be binary.
	 */
	public AMedia(String name, String format, String ctype, File file,
	String charset) throws java.io.FileNotFoundException {
		if (file == null)
			throw new IllegalArgumentException("file");

		if (charset == null)
			_isdata = RepeatableInputStream.getInstance(file);
		else
			_rddata = RepeatableReader.getInstance(file, charset);

		if (name == null) name = file.getName();
		setup(name, format, ctype);
	}
	/** Construct with a file.
	 * It is the same as AMedia(null, null, ctype, file, charset).
	 *
	 * @param ctype the content type; might be null.
	 * If null, it is retrieved from the file name's extension.
	 * @since 3.0.8
	 */
	public AMedia(File file, String ctype, String charset)
	throws java.io.FileNotFoundException {
		this(null, null, ctype, file, charset);
	}
	/** Construct with name, format, content type and URL.
	 *
	 * <p>Unlike others, it uses the so-called repeatable input
	 * stream or reader (depending on charset is null or not) to represent the
	 * resource, so the input stream ({@link #getStreamData})
	 * or the reader ({@link #getReaderData}) will be re-opened
	 * in the next invocation of {@link InputStream#read}
	 * after {@link InputStream#close} is called.
	 * See also {@link RepeatableInputStream} and {@link RepeatableReader}.
	 *
	 * @param name the name; might be null.
	 * If null, URL's name is used.
	 * @param format the format; might be null.
	 * @param ctype the content type; might be null.
	 * @param url the resource URL; never null.
	 * @since 3.0.8
	 */
	public AMedia(String name, String format, String ctype, URL url,
	String charset) throws java.io.FileNotFoundException {
		if (url == null)
			throw new IllegalArgumentException("url");

		if (charset == null)
			_isdata = RepeatableInputStream.getInstance(url);
		else
			_rddata = RepeatableReader.getInstance(url, charset);

		if (name == null) {
			name = url.toExternalForm();
			final int j = name.lastIndexOf('/');
			if (j >= 0 && j < name.length() - 1)
				name = name.substring(j + 1);
		}
		setup(name, format, ctype);
	}
	/** Construct with a file.
	 * It is the same as AMedia(null, null, ctype, url, charset).
	 *
	 * @param ctype the content type; might be null.
	 * If null, it is retrieved from the file name's extension.
	 * @since 3.0.8
	 */
	public AMedia(URL url, String ctype, String charset)
	throws java.io.FileNotFoundException {
		this(null, null, ctype, url, charset);
	}

	/** Sets up the format and content type.
	 * It assumes one of them is not null.
	 */
	private void setup(String name, String format, String ctype) {
		if (ctype != null && !Boolean.parseBoolean(Library.getProperty("org.zkoss.zul.Filedownload.contentTypeAsIs"))) {
			int j = ctype.indexOf(';');
			if (j >= 0) ctype = ctype.substring(0, j);
		}

		if (ctype != null && format == null) {
			format = ContentTypes.getFormat(ctype);
		} else if (ctype == null && format != null) {
			ctype = ContentTypes.getContentType(format);
		}
		if (name != null) {
			if (format == null) {
				final int j = name.lastIndexOf('.');
				if (j >= 0) {
					format = name.substring(j + 1);
					if (ctype == null) {
						ctype = ContentTypes.getContentType(format);
					}
				}
			}
		}

		_name = name;
		_format = format;
		_ctype = ctype;
	}

	/** Set whether to allow Content-Disposition 
	 * or not when writing the media to response header.
	 * @since 7.0.0
	 */
	public void setContentDisposition(boolean cntDisposition) {
		_cntDisposition = cntDisposition;
	}

	//-- Media --//
	public boolean isBinary() {
		return _bindata != null || _isdata != null;
	}
	public boolean inMemory() {
		return _bindata != null || _strdata != null;
	}

	public byte[] getByteData() {
		if (_bindata != null) return _bindata;
		
		InputStream is = _isdata == DYNAMIC_STREAM ? getStreamData() : _isdata ; //ZK-938
		if (is != null) {
			try {
				byte[] bs = Files.readAll(is);
				is.close();
				return bs;
			} catch (java.io.IOException ex) {
				throw SystemException.Aide.wrap(ex);
			}
		}
		throw newIllegalStateException();
	}
	public String getStringData() {
		if (_strdata != null) return _strdata;
		
		Reader reader =  _rddata == DYNAMIC_READER ? getReaderData() : _rddata; //ZK-938
		if (reader != null) {
			try {
				String ct = Files.readAll(reader).toString();
				reader.close();
				return ct;
			} catch (java.io.IOException ex) {
				throw SystemException.Aide.wrap(ex);
			}
		}
		throw newIllegalStateException();
		
	}
	/** Returns the input stream of this media.
	 *
	 * <p>Note: the caller has to invoke {@link InputStream#close()}
	 * after using the input stream returned by this method.
	 * <p>It wraps {@link #getByteData()} with {@link ByteArrayInputStream}
	 * if {@link #inMemory()} returns true.
	 * @exception IllegalStateException if the media {@link #isBinary()} returns false
	 */
	public InputStream getStreamData() {
		if (_isdata != null) return _isdata;
		if (_bindata != null) return new ByteArrayInputStream(_bindata);
		throw newIllegalStateException();
	}
	/** Returns the reader of this media to retrieve the data.
	 *
	 * <p>Note: the caller has to invoke {@link Reader#close()}
	 * after using the input stream returned by this method.
	 * <p>It wraps {@link #getStringData()} with {@link StringReader},
	 * if {@link #inMemory()} returns true.
	 * @exception IllegalStateException if {@link #isBinary()} returns true
	 */
	public Reader getReaderData() {
		if (_rddata != null) return _rddata;
		if (_strdata != null) return new StringReader(_strdata);
		throw newIllegalStateException();
	}
	private IllegalStateException newIllegalStateException() {
		return new IllegalStateException(
			"Use get"
			+(_bindata != null ? "Byte": _strdata != null ? "String":
				_isdata != null ? "Stream": "Reader")
			+ "Data() instead");
	}

	public String getName() {
		return _name;
	}
	public String getFormat() {
		return _format;
	}
	public String getContentType() {
		return _ctype;
	}
	public boolean isContentDisposition() {
		return _cntDisposition;
	}

	//-- Object --//
	public String toString() {
		return _name != null ? _name: "Media "+_format;
	}
}
