/* InfoChangeEvent.java

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

import org.zkoss.gmaps.Ginfo;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.Component;

/**
 * Represents a Google Maps {@link org.zkoss.gmaps.Gmaps} related event which is
 * triggered whenever the currently opened {@link org.zkoss.gmaps.Ginfo} or 
 * {@link org.zkoss.gmaps.Gmarker} is changed.
 * 
 * @author henrichen
 */
public class InfoChangeEvent extends Event {
	private final Ginfo _info;

	/** Constructs a Google Maps info window change relevant event.
	 */
	public InfoChangeEvent(String name, Component target, Ginfo info) {
		super(name, target);
		_info = info;
	}
	/** Returns the new opened info window of the Google Map (null means none is opened).
	 */
	public final Ginfo getInfo() {
		return _info;
	}
}
