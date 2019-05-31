/* DragStartEvent.java

	Purpose:
		
	Description:
		
	History:
		Fri May 31 10:08:50 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import java.util.Map;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zk.ui.Component;

/**
 * Represents an event cause by user's dragging a component.
 * <p>The component being dragged can be retrieved by {@link #getTarget}.
 *
 * @author jameschu
 */
public class DragStartEvent extends MouseEvent {
	public static final String DRAG_START_TARGET = "org.zkoss.zul.dragstart.target";
	/** Converts an AU request to a dragStart event.
	 * @since 8.6.2
	 */
	public static DragStartEvent getDragStartEvent(AuRequest request) {
		final Map<String, Object> data = request.getData();
		final int keys = AuRequests.parseKeys(data);
		return new DragStartEvent(request.getCommand(), request.getComponent(), AuRequests.getInt(data, "x", 0),
				AuRequests.getInt(data, "y", 0), AuRequests.getInt(data, "pageX", 0),
				AuRequests.getInt(data, "pageY", 0), keys);
	}

	/** Constructs a dragStart event.
	 * @since 8.6.2
	 */
	public DragStartEvent(String name, Component target, int x, int y, int pageX, int pageY, int keys) {
		super(name, target, x, y, pageX, pageY, keys);
	}

	/** Inherited from {@link MouseEvent}, but not applicable to {@link DragStartEvent}.
	 * It always returns null.
	 */
	public String getArea() {
		return null;
	}
}