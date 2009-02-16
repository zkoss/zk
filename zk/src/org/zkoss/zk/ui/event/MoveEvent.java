/* MoveEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 12 08:34:35     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
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
	private final int _keys;

	/** Indicates whether the Alt key is pressed.
	 * It might be returned as part of {@link #getKeys}.
	 */
	public static final int ALT_KEY = MouseEvent.ALT_KEY;
	/** Indicates whether the Ctrl key is pressed.
	 * It might be returned as part of {@link #getKeys}.
	 */
	public static final int CTRL_KEY = MouseEvent.CTRL_KEY;
	/** Indicates whether the Shift key is pressed.
	 * It might be returned as part of {@link #getKeys}.
	 */
	public static final int SHIFT_KEY = MouseEvent.SHIFT_KEY;

	/** Constructs a mouse relevant event.
	 */
	public MoveEvent(String name, Component target, String left, String top,
	int keys) {
		super(name, target);
		_left = left;
		_top = top;
		_keys = keys;
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
	/** Returns what keys were pressed when the component is moved, or 0 if
	 * none of them was pressed.
	 * It is a combination of {@link #CTRL_KEY}, {@link #SHIFT_KEY}
	 * and {@link #ALT_KEY}.
	 */
	public final int getKeys() {
		return _keys;
	}
}
