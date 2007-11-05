/* MapMoveEvent.java

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
 * is triggered whenever the center is moved.
 * 
 * @author henrichen
 */
public class MapMoveEvent extends Event {
	private final double _lat, _lng;

	/** Constructs a Google Maps moving relevant event.
	 */
	public MapMoveEvent(String name, Component target, double lat, double lng) {
		super(name, target);
		_lat = lat;
		_lng = lng;
	}
	/** Returns the latitude of the Google Map center after moved.
	 */
	public final double getLat() {
		return _lat;
	}
	/** Returns the longitude of the Google Map center after moved.
	 */
	public final double getLng() {
		return _lng;
	}
}
