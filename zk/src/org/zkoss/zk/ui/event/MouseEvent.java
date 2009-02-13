/* MouseEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 14 17:35:55     2005, Created by tomyeh
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
 * Represents an event cause by mouse activitly.
 *
 * <p>There are two possible way to identify a mouse event.
 * One is by coordination ({@link #getX} and {@link #getY}.
 * The other is by a logical name, called area ({@link #getArea}).
 *
 * @author tomyeh
 */
public class MouseEvent extends Event {
	private final int _x, _y;
	private final String _area;
	private final int _keys;

	/** Indicates whether the Alt key is pressed.
	 * It might be returned as part of {@link #getKeys}.
	 */
	public static final int ALT_KEY = 0x001;
	/** Indicates whether the Ctrl key is pressed.
	 * It might be returned as part of {@link #getKeys}.
	 */
	public static final int CTRL_KEY = 0x002;
	/** Indicates whether the Shift key is pressed.
	 * It might be returned as part of {@link #getKeys}.
	 */
	public static final int SHIFT_KEY = 0x004;

	/** Construct a mouse relevant event with coordination or area.
	 */
	public MouseEvent(String name, Component target) {
		super(name, target);
		_area = null;
		_x = _y = _keys = 0;
	}
	/** Constructs a mouse relevant event.
	 */
	public MouseEvent(String name, Component target, int x, int y) {
		this(name, target, x, y, 0);
	}
	/** Constructs a mouse relevant event.
	 *
	 * @param keys a combination of {@link #CTRL_KEY}, {@link #SHIFT_KEY}
	 * and {@link #ALT_KEY}.
	 */
	public MouseEvent(String name, Component target, int x, int y, int keys) {
		super(name, target);
		_x = x;
		_y = y;
		_area = null;
		_keys = keys;
	}
	/** Constructs a mouse relevant event with a logic name called area.
	 */
	public MouseEvent(String name, Component target, String area) {
		super(name, target);
		_area = area;
		_x = _y = _keys = 0;
	}

	/** Returns the logical name of the area that the click occurs, or
	 * null if not available.
	 *
	 * <p>It is used only with some special components, such as <code>imagemap</code>,
	 * that partition itself into several logical areas.
	 */
	public String getArea() {
		return _area;
	}
	/** Returns the x coordination of the mouse pointer relevant to
	 * the component.
	 */
	public final int getX() {
		return _x;
	}
	/** Returns the y coordination of the mouse pointer relevant to
	 * the component.
	 */
	public final int getY() {
		return _y;
	}

	/** Returns what keys were pressed when the mouse is clicked, or 0 if
	 * none of them was pressed.
	 * It is a combination of {@link #CTRL_KEY}, {@link #SHIFT_KEY}
	 * and {@link #ALT_KEY}.
	 */
	public final int getKeys() {
		return _keys;
	}
}
