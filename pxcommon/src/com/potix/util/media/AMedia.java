/* AMedia.java

{{IS_NOTE
	$Id: AMedia.java,v 1.9 2006/04/25 02:40:54 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Thu May 27 15:10:46     2004, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util.media;

import java.io.Reader;
import java.io.InputStream;

/**
 * A media object holding content such PDF, HTML, DOC or XLS content.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.9 $ $Date: 2006/04/25 02:40:54 $
 */
public class AMedia implements Media {
	/** The binary data, {@link #getByteData}. */
	protected byte[] _bindata;
	/** The text data, {@link #getStringData}. */
	protected String _strdata;
	/** The input stream, {@link #getStreamData} */
	protected InputStream _isdata;
	/** The input stream, {@link #getReaderData} */
	protected Reader _rddata;
	/** The content type. */
	protected String _ctype;
	/** The format (e.g., pdf). */
	protected String _format;
	/** The name (usually filename). */
	protected String _name;

	/** Construct with name, format, content type and binary data.
	 *
	 * <p>It tries to construct format and ctype from each other or name.
	 *
	 * @param name the name (usually filename); might be null.
	 * @param format the format; might be null.
	 * @param ctype the content type; might be null.
	 * @param data the binary data; never null
	 */
	public AMedia(String name, String format, String ctype, byte[] data) {
		if (data == null)
			throw new NullPointerException("data");
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
			throw new NullPointerException("data");
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
	 * @param data the binary data; never null
	 */
	public AMedia(String name, String format, String ctype, InputStream data) {
		if (data == null)
			throw new NullPointerException("data");
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
	 * @param data the binary data; never null
	 */
	public AMedia(String name, String format, String ctype, Reader data) {
		if (data == null)
			throw new NullPointerException("data");
		_rddata = data;
		setup(name, format, ctype);
	}
	/** Constructs a media holding nothing. Deriving class must initialize
	 * exactly one of {@link #_bindata}, {@link #_strdata}, {@link #_isdata}
	 * or {@link #_rddata} properly.
	 */
	protected AMedia() {
	}
	/** Constructs a media holding nothing. Deriving class must initialize
	 * {@link #_bindata} or {@link #_strdata} properly.
	 */
	protected AMedia(String name, String format, String ctype) {
		setup(name, format, ctype);
	}
	/** Sets up the format and content type.
	 * It assumes one of them is not null.
	 */
	protected void setup(String name, String format, String ctype) {
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

	//-- Media --//
	public boolean isBinary() {
		return _bindata != null || _isdata != null;
	}
	public boolean inMemory() {
		return _bindata != null || _strdata != null;
	}
	public byte[] getByteData() {
		if (_bindata == null) throw new IllegalStateException();
		return _bindata;
	}
	public String getStringData() {
		if (_strdata == null) throw new IllegalStateException();
		return _strdata;
	}
	public InputStream getStreamData() {
		if (_isdata == null) throw new IllegalStateException();
		return _isdata;
	}
	public Reader getReaderData() {
		if (_rddata == null) throw new IllegalStateException();
		return _rddata;
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

	//-- Object --//
	public String toString() {
		return _name != null ? _name: "Media "+_format;
	}
}
