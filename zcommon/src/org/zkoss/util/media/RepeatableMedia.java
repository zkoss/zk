/* RepeatableMedia.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mar 12, 2008 9:21:30 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util.media;

import java.io.InputStream;
import java.io.Reader;

import org.zkoss.io.RepeatableInputStream;

/**
 * This class is used to wrap the media in a repeatable media, which can reread the 
 * media more than once.
 * @author jumperchen
 * @since 3.0.4
 */
public class RepeatableMedia implements Media {
	private Media _media;
	private InputStream _isdata;
	
	private RepeatableMedia(Media media){
		_media = media;
	}
	
	/** 
	 * Returns a repeatable media with a repeatable input stream, if media is not null.
	 */
	public static Media getInstance(Media media) {
		if (media != null && !media.inMemory()
		&& !(media instanceof RepeatableMedia)) {
			return new RepeatableMedia(media);
		}
		return media;
	}

	/**
	 * @see {@link Media#getReaderData()}
	 */
	public Reader getReaderData() {
		return _media.getReaderData();
	}

	/**
	 * Returs the repeatable input stream, if the original input stream is not null.
	 * @see {@link Media#getStreamData()}
	 * @see {@link RepeatableInputStream#getInstance(InputStream)}
	 */
	public InputStream getStreamData() {
		if (_isdata == null) {
			_isdata = _media.getStreamData() instanceof RepeatableInputStream ?
						_media.getStreamData() : 
						RepeatableInputStream.getInstance(_media.getStreamData());
		}
		return _isdata;
	}
	
	/**
	 * @see {@link Media#getByteData()}
	 */
	public byte[] getByteData() {
		return _media.getByteData();
	}

	/**
	 * @see {@link Media#getContentType()}
	 */
	public String getContentType() {
		return _media.getContentType();
	}

	/**
	 * @see {@link Media#getFormat()}
	 */
	public String getFormat() {
		return _media.getFormat();
	}

	/**
	 * @see {@link Media#getName()}
	 */
	public String getName() {
		return _media.getName();
	}

	/**
	 * @see {@link Media#getStringData()}
	 */
	public String getStringData() {
		return _media.getStringData();
	}

	/**
	 * @see {@link Media#inMemory()}
	 */
	public boolean inMemory() {
		return _media.inMemory();
	}

	/**
	 * @see {@link Media#isBinary()}
	 */
	public boolean isBinary() {
		return _media.isBinary();
	}

}
