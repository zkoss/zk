/* RenderEvent.java

	Purpose:
		
	Description:
		
	History:
		Sat Jan 31 23:11:53     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zul.event;

import java.util.Set;
import java.util.Map;
import java.util.List;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
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
		final Map data = request.getData();
		if (data == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {data, request});
		return new RenderEvent(request.getCommand(),
			request.getComponent(),
			AuRequests.convertToItems(request.getDesktop(), (List)data.get("items")));
	}

	/**
	 * @since 5.0.0
	 */
	public RenderEvent(String name, Component comp, Set items) {
		super(name, comp);
		if (items == null) throw new IllegalArgumentException();
		_items = items;
	}
	public RenderEvent(String name, Set items) {
		this(name, null, items);
	}

	/** Returns the (readonly) collection of items to render (never null).
	 */
	public Set getItems() {
		return _items;
	}
}
