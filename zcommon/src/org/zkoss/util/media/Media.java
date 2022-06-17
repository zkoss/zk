/* Media.java

	Purpose:
		
	Description:
		
	History:
		Wed Feb 26 13:29:14     2003, Created by tomyeh

Copyright (C) 2002 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util.media;

import java.io.Reader;
import java.io.InputStream;

/**
 * Represents any multi-media, such as a voice, a pdf file, an excel file,
 * an image and so on.
 *
 * <p>By implementing this interface, objects can be processed generically
 * by servlets and many other codes.
 *
 * @author tomyeh
 */
public interface Media {
	/** Returns whether the format of this content is binary or text-based.
	 * If true, use {@link #getByteData} or {@link #getStreamData}
	 * to retrieve its content.
	 * If false, use {@link #getStringData} or {@link #getReaderData}
	 * to retrieve its content.
	 *
	 * @see #getStringData
	 * @see #getByteData
	 * @see #getReaderData
	 * @see #getStreamData
	 */
	public boolean isBinary();
	/** Returns whether the data is cached in memory (in form of
	 * byte[] or String).
	 *
	 * @see #getStringData
	 * @see #getByteData
	 * @see #getReaderData
	 * @see #getStreamData
	 */
	public boolean inMemory();

	/** Returns the raw data in byte array.
	 *
	 * <p>It might <i>not</i> be a copy, so don't modify
	 * it directly unless you know what you are doing.
	 *
	 * <p>If the data is not cached in memory ({@link #inMemory} return false),
	 * the data will be read from {@link #getStreamData}. Furthermore, it
	 * also implies you can not invoke this method again.
	 *
	 * @exception IllegalStateException if {@link #isBinary} returns false
	 * @see #getStringData
	 */
	public byte[] getByteData();
	/** Returns the raw data in string.
	 *
	 * <p>If the data is not cached in memory ({@link #inMemory} return false),
	 * the data will be read from {@link #getReaderData}. Furthermore, it
	 * also implies you can not invoke this method again.
	 *
	 * @exception IllegalStateException if {@link #isBinary} returns false
	 * @see #getByteData
	 */
	public String getStringData();
	/** Returns the raw data in InputStream.
	 * The return object is implementation-specific, please check javadoc for its implementation class.
	 *
	 * @exception IllegalStateException if {@link #isBinary()} returns false.
	 * @see #getReaderData
	 */
	public InputStream getStreamData();
	/** Returns the raw data in Reader.
	 * The return object is implementation-specific, please check javadoc for its implementation class.
	 *
	 * @exception IllegalStateException if {@link #isBinary()} returns true.
	 * @see #getStreamData
	 */
	public Reader getReaderData();

	/** Returns the name (usually filename) of this media, or null
	 * if not available.
	 */
	public String getName();

	/** Returns the format name, e.g., "jpeg", or null if not available.
	 * @see #getContentType
	 */
	public String getFormat();
	/** Returns the content type, e.g., "image/jpeg", or null if not available.
	 * @see #getFormat
	 */
	public String getContentType();
	
	/** Whether to allow Content-Disposition or not when writing the media to response header.
	 * <p>
	 * Default: true
	 * @since 7.0.0
	 */
	public boolean isContentDisposition();
}
