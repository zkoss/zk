/* MapClickEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jan 19 16:08:51     2007, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.gmaps.event;

import org.zkoss.gmaps.Gmarker;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.Component;

/**
 * Represents a Google Maps {@link org.zkoss.gmaps.Gmaps} related event which 
 * is triggered whenever the map is clicked on.
 * 
 * @author henrichen
 */
public class MapClickEvent extends Event {
	private final Gmarker _gmarker;
	private final double _lat, _lng;

	/** Constructs a Google Maps moving relevant event.
	 */
	public MapClickEvent(String name, Component target, Gmarker gmarker, double lat, double lng) {
		super(name, target);
		_gmarker = gmarker;
		_lat = lat;
		_lng = lng;
	}
	/** Returns the clicked Gmarker or null if not click on a Gmarker.
	 */
	public final Gmarker getGmarker() {
		return _gmarker;
	}
	
	/** Returns the latitude of the clicked position.
	 */
	public final double getLat() {
		return _lat;
	}
	/** Returns the longitude of the clicked position.
	 */
	public final double getLng() {
		return _lng;
	}
}
