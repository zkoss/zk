/* MarkerDropEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 9, 2007 3:22:30 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.gmaps.event;

import org.zkoss.zk.ui.Component;

/**
 * Represents a Google Maps {@link org.zkoss.gmaps.Gmaps} related event which 
 * is triggered whenever the GMarker is dropped.
 * 
 * @author henrichen
 */
public class MarkerDropEvent extends MapMoveEvent {
	public MarkerDropEvent(String name, Component target, double lat, double lng) {
		super(name, target, lat, lng);
	}
}
