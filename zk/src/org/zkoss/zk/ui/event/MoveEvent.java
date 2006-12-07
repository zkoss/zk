/* MoveEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 12 08:34:35     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import org.zkoss.zk.ui.Component;

/**
 * Represents an event caused by a component being moved.
 *
 * <p>Component Implementation Note:<br/>
 * A movable component must implement {@link org.zkoss.zk.ui.ext.client.Movable}
 * for the returned object of {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl}.
 * 
 * @author tomyeh
 */
public class MoveEvent extends Event {
	private final String _left, _top;

	/** Constructs a mouse relevant event.
	 */
	public MoveEvent(String name, Component target, String left, String top) {
		super(name, target);
		_left = left;
		_top = top;
	}
	/** Returns the left of the component after moved.
	 */
	public final String getLeft() {
		return _left;
	}
	/** Returns the top of the component after moved.
	 */
	public final String getTop() {
		return _top;
	}
}
