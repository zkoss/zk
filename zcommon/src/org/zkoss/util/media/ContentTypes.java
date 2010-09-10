/* ContentTypes.java

	Purpose:
		
	Description:
		
	History:
		Fri Oct  1 12:32:24     2004, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util.media;

import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.zkoss.lang.D;
import org.zkoss.lang.Strings;
import org.zkoss.mesg.MCommon;
import org.zkoss.util.logging.Log;

/**
 * Utilities relevant to content types.
 *
 * @author tomyeh
 */
public class ContentTypes {
	private static final Log log = Log.lookup(ContentTypes.class);

	/** A map of (String format, String contentType). */
	private static final Map<String, String> _fmt2ct = new HashMap<String, String>(64);
	/** A map of (String contentType, String format). */
	private static final Map<String, String> _ct2fmt = new HashMap<String, String>(64);

	protected ContentTypes() {} //prevent from initializing

	/** Returns whether the content type is binary.
	 * @param ctype the content type, e.g., text/plain
	 * If ctype is null or empty, false is returned.
	 * @since 3.0.6
	 */
	public static final boolean isBinary(String ctype) {
		return ctype != null && ctype.length() > 0
			&& !ctype.startsWith("text/") && ctype.indexOf("script") < 0;
	}

	/** Returns the content type of the specified format,
	 * such as "html" and "pdf", or null if not found (or format is null).
	 */
	public static final String getContentType(String format) {
		if (format == null)
			return null;

		format = format.trim().toLowerCase();
		synchronized (_fmt2ct) {
			return (String)_fmt2ct.get(format);
		}
	}
	/** Returns the format of the specified content type, or null if not found.
	 * @exception IllealArgumentException if ctype is null
	 */
	public static final String getFormat(String ctype) {
		if (ctype == null)
			throw new IllegalArgumentException();

		ctype = ctype.trim().toLowerCase();
		String format;
		synchronized (_ct2fmt) {
			format = (String)_ct2fmt.get(ctype);
		}
		if (format == null) {
			//sometime, content type is "text/html;charset=UTF-8"
			int j = ctype.indexOf(';');
			if (j >= 0) {
				ctype = ctype.substring(0, j);
				synchronized (_ct2fmt) {
					format = (String)_ct2fmt.get(ctype);
				}
			}
			if (format == null) {
				j = ctype.indexOf('/');
				format = j >= 0 ? ctype.substring(j + 1): ctype;
			}	
		}
		return format;
	}
	/** Adds additional binding of the format and content type.
	 *
	 * <p>You rarely need to invoke this method, unless your format is not
	 * by the default mapping.
	 */
	public static final void put(String format, String ctype) {
		if (format == null || ctype == null)
			throw new NullPointerException("format or ctype");

		synchronized (_fmt2ct) {
			_fmt2ct.put(format, ctype);
		}
		synchronized (_ct2fmt) {
			_ct2fmt.put(ctype, format);
		}
	}

	static {
		final String flnm =
			"/metainfo/org/zkoss/util/media/contentTypes.properties";
		if (!load(flnm))
			log.warning(MCommon.FILE_NOT_FOUND, flnm);
		load("/contentTypes.properties"); //override!
	}
	private static final boolean load(String flnm) {
		final InputStream strm = ContentTypes.class.getResourceAsStream(flnm);
		if (strm == null)
			return false;

		//NOTE: we cannot use Properties.load because there might be replicated
		//mapping (e.g., jpg=images/jpg, jpg=images/pjpeg)
		try {
			final BufferedReader in =
				new BufferedReader(new InputStreamReader(strm));

			String line;
			while ((line = in.readLine()) != null) {
				final int j = line.indexOf('=');
				if (j < 0) {
					final int k = Strings.skipWhitespaces(line, 0);
					if (k < line.length() && line.charAt(k) != '#')
						log.warning("Ignored error;  illgal format: "+line);
					continue;
				}

				final String format = line.substring(0, j).trim();
				final String ctype = line.substring(j + 1).trim();
				if (format.length() == 0 || ctype.length() == 0) {
					log.warning("Ignored error;  illgal format: "+line);
					continue;
				}

				_fmt2ct.put(format, ctype);
				_ct2fmt.put(ctype, format);
			}
		} catch (IOException ex) {
			log.warning("Ingored error: Unable to read "+flnm, ex);
		} finally {
			try {strm.close();} catch (Throwable ex) {}
		}
		return true;
	}
}
