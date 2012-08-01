/* OrientationEvent.java

	Purpose:
		
	Description:
		
	History:
		Jul 25, 2012 3:42:36 PM , Created by vincentjian

Copyright (C) 2012 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zk.ui.event;

import java.util.Map;

import org.zkoss.zk.au.AuRequest;

/**
 * Represents orientation change event on tablet when user changes the
 * orientation.
 * <p>All root components of all pages of the desktop will
 * receives this event.
 * 
 * @author vincentjian
 * @since 6.5.0
 */
public class OrientationEvent extends Event {
	private String _orient;
	
	public static Event getOrientationEvent(AuRequest request) {
		Map<String, Object> data = request.getData();
		return new OrientationEvent(request.getCommand(), (String)data.get("orient"));
	}
	
	public OrientationEvent(String name, String orient) {
		super(name);
		_orient = orient;
	}
	
	public String getOrientation() {
		return _orient;
	}
}
