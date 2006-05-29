/* MouseEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 14 17:35:55     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.event;

import com.potix.zk.ui.Component;

/**
 * Represents an event cause by mouse activitly.
 *
 * <p>There are two possible way to identify a mouse event.
 * One is by coordination ({@link #getX} and {@link #getY}.
 * The other is by a logical name, called area ({@link #getArea}).
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.4 $ $Date: 2006/05/29 04:28:02 $
 */
public class MouseEvent extends Event {
	private final int _x, _y;
	private final String _area;

	/** Construct a mouse relevant event with coordination or area.
	 */
	public MouseEvent(String name, Component target) {
		super(name, target);
		_area = null;
		_x = _y = 0;
	}
	/** Constructs a mouse relevant event.
	 */
	public MouseEvent(String name, Component target, int x, int y) {
		super(name, target);
		_x = x;
		_y = y;
		_area = null;
	}
	/** Constructs a mouse relevant event with a logic name called area.
	 */
	public MouseEvent(String name, Component target, String area) {
		super(name, target);
		if (area == null)
			throw new IllegalArgumentException("area");
		_area = area;
		_x = _y = 0;
	}

	/** Returns the logical name of the area that the click occurs, or
	 * null if not available.
	 */
	public final String getArea() {
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
}
