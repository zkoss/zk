/* ScrollEvent.java

	Purpose:
		
	Description:
		
	History:
		Sun Oct  2 21:30:36     2005, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import java.util.Map;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;

/**
 * Represents an event caused by that user is scrolling or
 * has scrolled at the client.
 * 
 * @author tomyeh
 */
public class ScrollEvent extends Event {
	private final int _pos;

	/** Converts an AU request to a scroll event.
	 * @since 5.0.0
	 */
	public static final ScrollEvent getScrollEvent(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, request);
		final Map data = request.getData();
		if (data == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {data, request});

		return new ScrollEvent(request.getCommand(), comp,
			AuRequests.getInt(data, "", 0));
	}

	/** Constructs an scroll-relevant event.
	 * @param pos the new position
	 */
	public ScrollEvent(String name, Component target, int pos) {
		super(name, target);
		_pos = pos;
	}
	/** Returns the position.
	 */
	public final int getPos() {
		return _pos;
	}
}
