/* UploadEvent.java

	Purpose:
		
	Description:
		
	History:
		Fri May 11 17:10:32     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import static org.zkoss.lang.Generics.cast;

import java.util.List;

import org.zkoss.util.UploadUtils;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;

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
		_meds = meds != null && meds.length > 0 ? meds : null;
	}

	/** Returns the first media being uploaded, or null if no file
	 * is uploaded.
	 */
	public final Media getMedia() {
		return _meds != null ? _meds[0] : null;
	}

	/** Returns the array of media being uploaded, or null
	 * if the user uploaded no file at all.
	 * If non-null is returned, the array length must be at least one.
	 */
	public final Media[] getMedias() {
		return _meds;
	}

	/**
	 * Creates an instance of {@link UploadEvent} based on the event name and component.
	 *
	 * @param name event name
	 * @param component component that triggers the upload event
	 * @return upload event
	 * @since 8.6.0
	 */
	public static UploadEvent getUploadEvent(String name, Component component) {
		Desktop desktop = component.getDesktop();
		String uuid = component.getUuid();
		final List<Media> result = cast((List) desktop.getAttribute(uuid));
		desktop.removeAttribute(uuid);
		return new UploadEvent(name, desktop.getComponentByUuid(uuid), UploadUtils.parseResult(result));
	}
}
