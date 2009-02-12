/* AImage.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Feb  6 16:56:33     2003, Created by tomyeh
}}IS_NOTE

Copyright (C) 2002 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.image;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.zkoss.lang.Objects;
import org.zkoss.lang.SystemException;
import org.zkoss.util.logging.Log;
import org.zkoss.io.Files;
import org.zkoss.util.media.ContentTypes;

/**
 * Represents an image.
 * Unlike java.awt.Image and javax.swing.ImageIcon, this class holds
 * the raw image data, i.e., the original format, as opaque.
 *
 * <p>In other words, it is used to retrieve and store the opaque data
 * as polymorphic thru the {@link org.zkoss.util.media.Media} interface.
 *
 * <p>To encode AWT image to an instance of {@link Image},
 * use {@link Images#encode}.
 *
 * @author tomyeh
 * @see Images#encode
 */
public class AImage implements Image, java.io.Serializable {
	private static final Log log = Log.lookup(AImage.class);

	/** The raw data. */
	private byte[] _data;
	/** The format name, e.g., "jpeg", "gif" and "png". */
	private String _format;
	/** The content type. */
	private String _ctype;
	/** The name (usually filename). */
	private String _name;
	/** The width. */
	private int _width;
	/** The height. */
	private int _height;
	/** the hash code. */
//	private transient int _hashCode = 0;

	public AImage(String name, byte[] data) throws IOException {
		init(name, data);
	}
	/** Contructs an image with an input stream.
	 *
	 * <p>Note that this method automatically closes the input stream
	 * (since ZK 3.0.0).
	 */
	public AImage(String name, InputStream is) throws IOException {
		try {
			init(name, Files.readAll(is));
		} finally {
			is.close();
		}
	}
	/** Constructs an image with a file name.
	 */
	public AImage(String filename) throws IOException {
		this(new File(filename));
	}
	/** Constructs an image with a file.
	 */
	public AImage(File file) throws IOException {
		this(file.getName(), new FileInputStream(file));
	}
	/** Constructs an image with an URL.
	 */
	public AImage(URL url) throws IOException {
		this(getName(url), url.openStream());
	}
	private void init(String name, byte[] data) throws IOException {
		if (data == null)
			throw new IllegalArgumentException("null data");
		_name = name;
		_data = data;

		//retrieve format
		String format = null;
		try {
			final ImageInputStream imis =
				ImageIO.createImageInputStream(new ByteArrayInputStream(data));
			final Iterator it = ImageIO.getImageReaders(imis);
			if (it.hasNext()) {
				final ImageReader rd = (ImageReader)it.next();
				format = rd.getFormatName().toLowerCase();
			}
		} catch (IOException ex) {
			//not possible, but eat it and recover it later
		}

		if (format == null) {
			_format = getFormatByName(name);
			if (_format == null)
				throw new IOException("Unknown image format: "+name);
			log.warning("Unsupported image format: "+_format+"; its width and height are assumed to zero");
			_width = _height = 0;
		} else { //recognized by J2SDK
			_format = format;

			final ImageIcon ii = new ImageIcon(_data);
			_width = ii.getIconWidth();
			_height = ii.getIconHeight();
		}
		_ctype = getContentType(_format);
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
		return ctype != null ? ctype: "image/" + format;
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
		return true; //FUTURE: consider to support input stream
	}
	public byte[] getByteData() {
		return _data;
	}
	/** Always throws IllegalStateException.
	 */
	public final String getStringData() {
		throw new IllegalStateException("Use getByteData() instead");
	}
	/** An input stream on top of {@link #getByteData}.
	 * <p>Though harmless, the caller doesn't need to close the returned
	 * stream.
	 */
	public final InputStream getStreamData() {
		return new ByteArrayInputStream(_data);
	}
	/** Always throws IllegalStateException.
	 */
	public final Reader getReaderData() {
		throw new IllegalStateException("Use getStreamData() instead");
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

	//-- Image --//
	/** Returns the width.
	 */
	public final int getWidth() {
		return _width;
	}
	/** Returns the height.
	 */
	public final int getHeight() {
		return _height;
	}
	/** Converts to an image icon.
	 */
	public final ImageIcon toImageIcon() {
		return new ImageIcon(_data, _format);
	}

	//-- Object --//
	/* 20041014: Tom Yeh: Due to performance and usability, it is no sense
		to compare by content.
	public int hashCode() {
		if (_hashCode == 0)
			_hashCode = Objects.hashCode(_data, 16);
		return _hashCode;
	}
	public boolean equals(Object o) {
		if (!(o instanceof Image))
			return false;
		final Image i = (Image)o;
		return _width == i.getWidth() && _height == i.getHeight()
			&& Objects.equals(_format, i.getFormat())
			&& Objects.equals(_data, i.getByteData());
	}
	*/
}
