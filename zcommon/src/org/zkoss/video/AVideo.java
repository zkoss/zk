/* AVideo.java

        Purpose:
                
        Description:
                
        History:
                Thu Apr 12 12:30:09 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.video;

import org.zkoss.io.NullInputStream;
import org.zkoss.lang.SystemException;
import org.zkoss.util.media.ContentTypes;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

/**
 * Represents a video.
 * <p>
 * it is used to retrieve and store the opaque data
 * as polymorphic thru the {@link org.zkoss.util.media.Media} interface.
 * <p>
 * <p>AVideo is serializable, but, if you are using InputStream,
 * you have to extend this class, and provide the implementation to
 * serialize and deserialize {@link #_isdata}.
 *
 * @since 8.6.0
 */
public class AVideo implements Video, java.io.Serializable {
	/**
	 * Used if you want to implement a media whose input stream is created
	 * dynamically each time {@link #getStreamData} is called.
	 *
	 * @see #AVideo(String, , InputStream)
	 */
	protected static final InputStream DYNAMIC_STREAM = new NullInputStream();
	/**
	 * The raw data in byte array.
	 * Exactly one of {@link #_data} and {@link #_isdata} is not null.
	 */
	private final byte[] _data;
	/**
	 * The raw data in stream (or {@link #DYNAMIC_STREAM}).
	 * Exactly one of {@link #_data} and {@link #_isdata} is not null.
	 */
	protected final transient InputStream _isdata;
	/**
	 * The URL of the data.
	 */
	private final URL _url;
	/**
	 * The file of the data.
	 */
	private final File _file;
	/**
	 * The format name, e.g., "mp4", "webm" and "ogg".
	 */
	private String _format;
	/**
	 * The content type.
	 */
	private String _ctype;
	/**
	 * The name (usually filename).
	 */
	private final String _name;
	
	public AVideo(String name, byte[] data) throws IOException {
		if (data == null)
			throw new IllegalArgumentException("null data");
		_name = name;
		_data = data;
		_isdata = null;
		_url = null;
		_file = null;
	}
	
	/**
	 * Creates an instance of a video with an input stream.
	 * If the stream shall be created each time {@link #getStreamData} is called,
	 * you can pass {@link #DYNAMIC_STREAM} to the data argument, and then
	 * override {@link #getStreamData}.
	 * <p>
	 * <p>Note: the caller of {@link #getStreamData} has to close
	 * the returned input stream.
	 */
	public AVideo(String name, InputStream isdata) throws IOException {
		if (isdata == null)
			throw new IllegalArgumentException("null stream");
		_name = name;
		_isdata = isdata;
		_data = null;
		_url = null;
		_file = null;
	}
	
	/**
	 * Constructs a video with an URL.
	 */
	public AVideo(URL url) {
		if (url == null)
			throw new IllegalArgumentException("null url");
		_name = getName(url);
		_url = url;
		_isdata = DYNAMIC_STREAM;
		_data = null;
		_file = null;
	}
	
	/**
	 * Constructs a video with a file.
	 */
	public AVideo(File file) {
		if (file == null)
			throw new IllegalArgumentException("null url");
		_name = file.getName();
		_file = file;
		_isdata = DYNAMIC_STREAM;
		_data = null;
		_url = null;
	}
	
	/**
	 * Constructs a video with a file name.
	 */
	public AVideo(String filename) throws IOException {
		this(new File(filename));
	}
	
	/**
	 * Creates an instance of a video with an input stream.
	 * If the stream shall be created each time {@link #getStreamData} is called,
	 * you can pass {@link #DYNAMIC_STREAM} to the data argument, and then
	 * override {@link #getStreamData}.
	 * <p>
	 * <p>Note: the caller of {@link #getStreamData} has to close
	 * the returned input stream.
	 */
	public AVideo(InputStream is) throws IOException {
		this(null, is);
	}
	
	private String getName(URL url) {
		String name = url.getPath();
		if (name != null) {
			{
				final int j = name.lastIndexOf(File.pathSeparatorChar);
				if (j >= 0) name = name.substring(j + 1);
			}
			if (File.pathSeparatorChar != '/') {
				final int j = name.lastIndexOf('/');
				if (j >= 0) name = name.substring(j + 1);
			}
		}
		return name;
	}
	
	private static String getContentType(String format) {
		final String ctype = ContentTypes.getContentType(format);
		return ctype != null ? ctype : "video/" + format;
	}
	
	/**
	 * Returns the data in the input stream.
	 * <p>
	 * <p>Note: the caller has to invoke {@link InputStream#close}
	 * after using the input stream returned by {@link #getStreamData}.
	 */
	public InputStream getStreamData() {
		try {
			if (_url != null) {
				InputStream is = _url.openStream();
				return is != null ? new BufferedInputStream(is) : null;
			}
			if (_file != null)
				return new BufferedInputStream(new FileInputStream(_file));
		} catch (java.io.IOException ex) {
			throw new SystemException("Unable to read "
					+ (_url != null ? _url.toString() : _file.toString()), ex);
		}
		if (_isdata != null) return _isdata;
		return new ByteArrayInputStream(_data);
	}
	
	/**
	 * Always throws IllegalStateException.
	 */
	public final String getStringData() {
		throw newIllegalStateException();
	}
	
	/**
	 * Not supported. It always throws IllegalStateException.
	 */
	public final Reader getReaderData() {
		throw newIllegalStateException();
	}
	
	private final IllegalStateException newIllegalStateException() {
		return new IllegalStateException(
				_isdata != null ? "Use getStreamData() instead" :
						"Use getByteData() instead");
	}
	
	
	public String getContentType() {
		if (_ctype == null) {
			_ctype = getContentType(getFormat());
		}
		return _ctype;
	}
	
	public String getFormat() {
		if (_format == null) {
			_format = getFormatByName(_name);
		}
		return _format;
	}
	
	private String getFormatByName(String name) {
		if (name != null) {
			final int j = name.lastIndexOf('.') + 1,
					k = name.lastIndexOf('/') + 1;
			if (j > k && j < name.length())
				return name.substring(j);
		}
		return null;
	}
	
	public String getName() {
		return _name;
	}
	
	public boolean isContentDisposition() {
		return true;
	}
	
	public boolean isBinary() {
		return true;
	}
	
	public final boolean inMemory() {
		return _data != null;
	}
	
	public byte[] getByteData() {
		if (_data == null)
			throw new IllegalStateException("Use getStreamData() instead");
		return _data;
	}
}
