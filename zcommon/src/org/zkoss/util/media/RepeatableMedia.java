/* RepeatableMedia.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mar 12, 2008 9:21:30 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util.media;

import java.io.InputStream;
import java.io.Reader;

import org.zkoss.io.RepeatableInputStream;
import org.zkoss.io.RepeatableReader;

/**
 * {@link RepeatableMedia} adds functionality to another media,
 * the ability to read repeatedly.
 * Unlike other media, {@link RepeatableMedia} uses
 * {@link RepeatableInputStream}, if binary, or {@link RepeatableReader}
 * to make {@link #getStreamData} and {@link #getReaderData} to
 * be able to re-open when {@link InputStream#close} 
 * or {@link Reader#close} is called.
 * In other words, the buffered input stream of the give media
 * is never closed until it is GC-ed.
 * 
 * @author jumperchen
 * @author tomyeh
 * @since 3.0.4
 */
public class RepeatableMedia implements Media {
	private final Media _media;
	private InputStream _isdata;
	private Reader _rddata;
	
	private RepeatableMedia(Media media, InputStream data){
		_media = media;
		_isdata  = data;
	}
	private RepeatableMedia(Media media, Reader data){
		_media = media;
		_rddata = data;
	}
	
	/** 
	 * Returns a repeatable media with a repeatable input stream or
	 * reader, or null if the given media is null.
	 */
	public static Media getInstance(Media media) {
		if (media != null && !media.inMemory()
		&& !(media instanceof RepeatableMedia)) {
			if (media.isBinary()) {
				final InputStream data = media.getStreamData(),
					after  = RepeatableInputStream.getInstance(data);
				if (data != after)
					return new RepeatableMedia(media, after);
			} else {
				final Reader data = media.getReaderData(),
					after = RepeatableReader.getInstance(data);
				if (data != after)
					return new RepeatableMedia(media, after);
			}
		}
		return media;
	}

	/**
	 * @see Media#getReaderData()
	 */
	public Reader getReaderData() {
		return _rddata;
	}

	/**
	 * Returs the repeatable input stream, if the original input stream is not null.
	 * @see Media#getStreamData()
	 * @see RepeatableInputStream#getInstance(InputStream)
	 */
	public InputStream getStreamData() {
		return _isdata;
	}
	
	/**
	 * @see Media#getByteData()
	 */
	public byte[] getByteData() {
		return _media.getByteData();
	}

	/**
	 * @see Media#getContentType()
	 */
	public String getContentType() {
		return _media.getContentType();
	}

	/**
	 * @see Media#getFormat()
	 */
	public String getFormat() {
		return _media.getFormat();
	}

	/**
	 * @see Media#getName()
	 */
	public String getName() {
		return _media.getName();
	}

	/**
	 * @see Media#getStringData()
	 */
	public String getStringData() {
		return _media.getStringData();
	}

	/**
	 * @see Media#inMemory()
	 */
	public boolean inMemory() {
		return _media.inMemory();
	}

	/**
	 * @see Media#isBinary()
	 */
	public boolean isBinary() {
		return _media.isBinary();
	}

}
