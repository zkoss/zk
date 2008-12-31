/* DropEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Feb 27 00:08:50     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import org.zkoss.zk.ui.Component;

/**
 * Represents an event cause by user's dragging and dropping a component.
 *
 * <p>The component being dragged can be retrieved by {@link #getDragged}.
 * The component that received the dragged component is {@link #getTarget}.
 * 
 * @author tomyeh
 */
public class DropEvent extends MouseEvent {
	private final Component _dragged;

	/** Constructs a drop event.
	 * @param dragged The component being dragged and drop to {@link #getTarget}.
	 * @since 5.0.0
	 */
	public DropEvent(String name, Component target, Component dragged,
	int x, int y, int pageX, int pageY, int keys) {
		super(name, target, x, y, pageX, pageY, keys);
		_dragged = dragged;
	}
	/** @deprecated As of release 5.0.0, replaced with
	 * {@link #DropEvent(String,Component,Component,int,int,int)}.
	 */
	public DropEvent(String name, Component target, Component dragged,
	int x, int y, int keys) {
		this(name, target, dragged, x, y, x, y, keys);
	}
	/** Returns the component being dragged and drop to {@link #getTarget}.
	 */
	public final Component getDragged() {
		return _dragged;
	}
	/** Not appliable to {@link DropEvent}.
	 * It always returns null if you drag and drop a component to
	 * components that partition itself into several areas, such
	 * as <code>imagemap</code>
	 */
	public String getArea() {
		return null;
	}
}
