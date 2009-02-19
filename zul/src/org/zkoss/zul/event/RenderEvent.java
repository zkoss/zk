/* RenderEvent.java

	Purpose:
		
	Description:
		
	History:
		Sat Jan 31 23:11:53     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zul.event;

import java.util.Set;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zk.ui.event.Event;

/**
 * Represents the onRender event.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class RenderEvent extends Event {
	private final Set _items;

	/** Converts an AU request to a render event.
	 * @since 5.0.0
	 */
	public static final RenderEvent getRenderEvent(AuRequest request) {
		return new RenderEvent(request.getName(), AuRequests.convertToItems(request));
	}

	public RenderEvent(String name, Set items) {
		super(name, null);
		if (items == null) throw new IllegalArgumentException();
		_items = items;
	}

	/** Returns the (readonly) collection of items to render (never null).
	 */
	public Set getItems() {
		return _items;
	}
}
