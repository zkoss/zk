/* UploadEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri May 11 17:10:32     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import org.zkoss.util.media.Media;

import org.zkoss.zk.ui.Component;

/**
 * Represents that user has uploaded one or several files from
 * the client to the server.
 * 
 * @author tomyeh
 */
public class UploadEvent extends Event {
	private final Media[] _meds;

	/** Constructs the upload event.
	 * @param meds the media being uploaded, or null if no file is
	 * uploaded. If a zero-length array is passed, null is assumed.
	 */
	public UploadEvent(String name, Component target, Media[] meds) {
		super(name, target);
		_meds = meds != null && meds.length > 0 ? meds: null;
	}
	/** Returns the first media being uploaded, or null if no file
	 * is uploaded.
	 */
	public final Media getMedia() {
		return _meds != null ? _meds[0]: null;
	}
	/** Returns the array of media being uploaded, or null
	 * if the user uploaded no file at all.
	 * If non-null is returned, the array length must be at least one.
	 */
	public final Media[] getMedias() {
		return _meds;
	}
}
