/* CharsetFinder.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 18 15:48:01     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import java.io.InputStream;
import java.io.IOException;

/**
 * Represents a class that decides the character set based on the
 * content type and the real content.
 *
 * <p>It is currently used to decide the encoding of the upload file
 * if specified by {@link Configuration#setUploadCharsetFinder}.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public interface CharsetFinder {
	/** Returns the character set for the specified content type,
	 * or null if it is unable to determine one.
	 *
	 * @param contentType the content type (never null)
	 * @param content the content
	 */
	public String getCharset(String contentType, InputStream content)
	throws IOException;
}
