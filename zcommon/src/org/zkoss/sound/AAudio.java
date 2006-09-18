/* AAudio.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Nov 16 15:15:40     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.sound;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.potix.util.media.ContentTypes;

/**
 * Represents an audio.
 * Unlike javax.sound.AudioClip, this class is used only to hold the raw
 * data as opaque rather than manilupate the sound.
 *
 * <p>In other words, it is used to retrieve and store the opaque data
 * as polymorphic thru the {@link com.potix.util.media.Media} interface.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class AAudio implements Audio {
	/** The raw data in byte array.
	 * Exactly one of {@link #_data} and {@link #_isdata} is not null.
	 */
	protected final byte[] _data;
	/** The raw data in stream.
	 * Exactly one of {@link #_data} and {@link #_isdata} is not null.
	 */
	protected final InputStream _isdata;

	/** The format name, e.g., "jpeg", "gif" and "png". */
	protected final String _format;
	/** The content type. */
	protected final String _ctype;
	/** The name (usually filename). */
	protected final String _name;

	public AAudio(String name, byte[] data) throws IOException {
		if (data == null)
			throw new IllegalArgumentException("null data");
		_name = name;
		_data = data;
		_isdata = null;

		String format = null;
		try {
			format =
				AudioSystem.getAudioFileFormat(new ByteArrayInputStream(data))
				.getType().getExtension();
		} catch (UnsupportedAudioFileException ex) {
			format = getFormatByName(_name);
			if (format == null)
				throw (IOException)new IOException().initCause(ex);
		}
		_format = format;
		_ctype = getContentType(_format);
	}
	public AAudio(String name, InputStream data) throws IOException {
		if (data == null)
			throw new IllegalArgumentException("null data");
		_name = name;
		_isdata = data;
		_data = null;

		String format = null;
		try {
			format = AudioSystem.getAudioFileFormat(data)
				.getType().getExtension();
		} catch (UnsupportedAudioFileException ex) {
			format = getFormatByName(_name);
			if (format == null)
				throw (IOException)new IOException().initCause(ex);
		}
		_format = format;
		_ctype = getContentType(_format);
	}
	public AAudio(URL url) throws IOException {
		if (url == null)
			throw new IllegalArgumentException("null url");
		_name = getName(url);
		_isdata = url.openStream();
		_data = null;

		String format = null;
		try {
			format = AudioSystem.getAudioFileFormat(url)
				.getType().getExtension();
		} catch (UnsupportedAudioFileException ex) {
			format = getFormatByName(_name);
			if (format == null)
				throw (IOException)new IOException().initCause(ex);
		}
		_format = format;
		_ctype = getContentType(_format);
	}
	public AAudio(File file) throws IOException {
		if (file == null)
			throw new IllegalArgumentException("null url");
		_name = file.getName();
		_isdata = new FileInputStream(file);
		_data = null;

		String format = null;
		try {
			format = AudioSystem.getAudioFileFormat(file)
				.getType().getExtension();
		} catch (UnsupportedAudioFileException ex) {
			format = getFormatByName(_name);
			if (format == null)
				throw (IOException)new IOException().initCause(ex);
		}
		_format = format;
		_ctype = getContentType(_format);
	}
	public AAudio(String filename) throws IOException {
		this(new File(filename));
	}
	public AAudio(InputStream is) throws IOException {
		this(null, is);
	}

	private static String getName(URL url) {
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
		return ctype != null ? ctype: "audio/" + format;
	}
	private static String getFormatByName(String name) {
		if (name != null) {
			final int j = name.lastIndexOf('.') + 1,
				k = name.lastIndexOf('/') + 1;
			if (j > k && j < name.length())
				return name.substring(j); 
		}
		return null;
	}

	//-- Media --//
	public final boolean isBinary() {
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
	/** Always throws IllegalStateException.
	 */
	public final String getStringData() {
		throw newIllegalStateException();
	}
	/** Always throws IllegalStateException.
	 */
	public InputStream getStreamData() {
		if (_isdata != null) return _isdata;
		return new ByteArrayInputStream(_data);
	}
	/** Always throws IllegalStateException.
	 */
	public final Reader getReaderData() {
		throw newIllegalStateException();
	}
	private final IllegalStateException newIllegalStateException() {
		return new IllegalStateException(
			_isdata != null ? "Use getStreamData() instead":
				"Use getByteData() instead");
	}

	public final String getName() {
		return _name;
	}
	public final String getFormat() {
		return _format;
	}
	public final String getContentType() {
		return _ctype;
	}
}
