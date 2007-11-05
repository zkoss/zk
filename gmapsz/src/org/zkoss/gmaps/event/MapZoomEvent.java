/* MapZoomEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 13 14:51:13     2006, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.gmaps.event;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.Component;

/**
 * Represents a Google Maps {@link org.zkoss.gmaps.Gmaps} related event which
 * is triggered whenever the zoom level is changed.
 *
 * @author henrichen
 */
public class MapZoomEvent extends Event {
	private final int _zoom;

	/** Constructs a Google Maps zoom level relevant event.
	 */
	public MapZoomEvent(String name, Component target, int zoom) {
		super(name, target);
		_zoom = zoom;
	}
	/** Returns the zoom level of the Google Map after zoomed in or zoomed out.
	 */
	public final int getZoom() {
		return _zoom;
	}
}
