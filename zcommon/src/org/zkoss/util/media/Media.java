/* Media.java

	Purpose:
		
	Description:
		
	History:
		Wed Feb 26 13:29:14     2003, Created by tomyeh

Copyright (C) 2002 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util.media;

import java.io.Reader;
import java.io.InputStream;

/**
 * Represents any multi-medai, such as a voice, a pdf file, an excel file,
 * an image and so on.
 *
 * <p>By implementing this interface, objects can be processed generically
 * by servlets and many other codes.
 *
 * @author tomyeh
 */
public interface Media {
	/** Returns whether the format of tis content is binary or text-based.
	 * If true, use {@link #getByteData} or {@link #getStreamData}, depending on
	 * {@link #inMemory}, to retrieve its content.
	 * If false, use {@link #getStringData} or {@link #getReaderData}, depending on
	 * {@link #inMemory}, to retrieve its content.
	 *
	 * <p>To decide which API to use, you need to examine
	 * both {@link #isBinary} and {@link #inMemory}.
	 *
	 * @see #getStringData
	 * @see #getByteData
	 * @see #getReaderData
	 * @see #getStreamData
	 */
	public boolean isBinary();
	/** Returns whether the data is cached in memory (in form of
	 * byte[] or String).
	 * If true, use {@link #getByteData} or {@link #getStringData}, depending on
	 * {@link #isBinary}, to retrieve its content.
	 * If false, use {@link #getStreamData} or {@link #getReaderData}, depending on
	 * {@link #isBinary}, to retrieve its content.
	 *
	 * <p>To decide which API to use, you need to examine
	 * both {@link #isBinary} and {@link #inMemory}.
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
	 * @exception IllegalStateException if {@link #isBinary} returns false,
	 * or {@link #inMemory} returns false.
	 * @see #getStringData
	 */
	public byte[] getByteData();
	/** Returns the raw data in string.
	 *
	 * @exception IllegalStateException if {@link #isBinary} returns false,
	 * or {@link #inMemory} returns false.
	 * @see #getByteData
	 */
	public String getStringData();
	/** Returns the raw data in InputStream.
	 *
	 * <p>Note: it wraps {@link #getByteData} with ByteArrayInputStream
	 * if it is in memory ({@link #inMemory} returns true.
	 *
	 * @exception IllegalStateException if {@link #isBinary} returns false.
	 * @see #getReaderData
	 */
	public InputStream getStreamData();
	/** Returns the raw data in Reader.
	 *
	 * <p>Note: it wraps {@link #getStringData} with StringReader,
	 * if it is in memory ({@link #inMemory} returns true.
	 *
	 * @exception IllegalStateException if {@link #isBinary} returns true.
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
}
