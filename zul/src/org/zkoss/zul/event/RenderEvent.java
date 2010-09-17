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

import static org.zkoss.lang.Generics.cast;

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
public class RenderEvent<T extends Component> extends Event {
	private final Set<T> _items;

	/** Converts an AU request to a render event.
	 * @since 5.0.0
	 */
	public static final <C extends Component> RenderEvent<C> getRenderEvent(AuRequest request) {
		final Map data = request.getData();
		final List<String> sitems = cast((List)data.get("items"));
		final Set<C> items = AuRequests.convertToItems(request.getDesktop(), sitems);
		return new RenderEvent<C>(request.getCommand(),
			request.getComponent(), items);
	}

	/**
	 * @since 5.0.0
	 */
	public RenderEvent(String name, Component comp, Set<T> items) {
		super(name, comp);
		if (items == null) throw new IllegalArgumentException();
		_items = items;
	}
	public RenderEvent(String name, Set<T> items) {
		this(name, null, items);
	}

	/** Returns the (readonly) collection of items to render (never null).
	 */
	public Set<T> getItems() {
		return _items;
	}
}
