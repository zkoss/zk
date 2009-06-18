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

import java.util.Map;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;

/**
 * Represents an event cause by mouse activitly.
 *
 * <p>There are two possible way to identify a mouse event.
 * One is by coordinate ({@link #getX} and {@link #getY}.
 * The other is by a logical name, called area ({@link #getArea}).
 *
 * @author tomyeh
 */
public class MouseEvent extends Event {
	private final int _x, _y, _pgx, _pgy;
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
	/** Indicatees whether the left button is clicked.
	 */
	public static final int LEFT_CLICK = 0x100;
	/** Indicatees whether the right button is clicked.
	 */
	public static final int RIGHT_CLICK = 0x200;
	/** Indicatees whether the middle button is clicked.
	 * @since 5.0.0
	 */
	public static final int MIDDLE_CLICK = 0x400;

	/** Converts an AU request to a mouse event.
	 * @since 5.0.0
	 */
	public static MouseEvent getMouseEvent(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, request);
		final Map data = request.getData();
		if (data == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {data, request});
		final String name = request.getCommand();
		final int keys = AuRequests.parseKeys(data);
		final String area = (String)data.get("area");
		return area != null ? new MouseEvent(name, comp, area, keys): //area
			new MouseEvent(name, comp, //coord
				AuRequests.getInt(data, "x", 0), AuRequests.getInt(data, "y", 0),
				AuRequests.getInt(data, "pageX", 0), AuRequests.getInt(data, "pageY", 0),
				keys);
	}

	/** Construct a mouse relevant event with coordinate or area.
	 */
	public MouseEvent(String name, Component target) {
		super(name, target);
		_area = null;
		_x = _y = _pgx = _pgy = _keys = 0;
	}
	/** Constructs a mouse relevant event.
	 * @since 5.0.0
	 */
	public MouseEvent(String name, Component target, int x, int y,
	int pageX, int pageY) {
		this(name, target, x, y, pageX, pageY, 0);
	}
	/** Constructs a mouse relevant event.
	 *
	 * @param keys a combination of {@link #CTRL_KEY}, {@link #SHIFT_KEY}
	 * {@link #ALT_KEY}, {@link #LEFT_CLICK} and {@link #RIGHT_CLICK}.
	 * @since 5.0.0
	 */
	public MouseEvent(String name, Component target, int x, int y,
	int pageX, int pageY, int keys) {
		super(name, target);
		_x = x;
		_y = y;
		_pgx = pageX;
		_pgy = pageY;
		_area = null;
		_keys = keys;
	}
	/** Constructs a mouse relevant event with a logic name called area.
	 * @since 5.0.0
	 */
	public MouseEvent(String name, Component target, String area, int keys) {
		super(name, target);
		_area = area;
		_x = _y = _pgx = _pgy = 0;
		_keys = keys;
	}
	/** @deprecated As of release 5.0.0, replaced with
	 * {@link #MouseEvent(String,Component,int,int,int,int)}.
	 */
	public MouseEvent(String name, Component target, int x, int y) {
		this(name, target, x, y, x, y, 0);
	}
	/** @deprecated As of release 5.0.0, replaced with
	 * {@link #MouseEvent(String,Component,int,int,int,int,int)}.
	 */
	public MouseEvent(String name, Component target, int x, int y, int keys) {
		this(name, target, x, y, x, y, keys);
	}
	/** @deprecated As of release 5.0.0, replaced with
	 * {@link #MouseEvent(String,Component,String,int)}.
	 */
	public MouseEvent(String name, Component target, String area) {
		this(name, target, area, 0);
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
	/** Returns the horizontal coordinate of the mouse pointer relevant to
	 * the component.
	 */
	public final int getX() {
		return _x;
	}
	/** Returns the vertical coordinate of the mouse pointer relevant to
	 * the component.
	 */
	public final int getY() {
		return _y;
	}
	/** Returns the horizontal coordinate of the mouse pointer relative
	 * to the whole document.
	 * @since 5.0.0
	 */
	public final int getPageX() {
		return _pgx;
	}
	/** Returns the vertical coordinate of the mouse pointer relative
	 * to the whole document.
	 * @since 5.0.0
	 */
	public final int getPageY() {
		return _pgy;
	}

	/** Returns what keys were pressed when the mouse is clicked, or 0 if
	 * none of them was pressed.
	 * It is a combination of {@link #CTRL_KEY}, {@link #SHIFT_KEY}
	 * {@link #ALT_KEY}, {@link #LEFT_CLICK} and {@link #RIGHT_CLICK}.
	 */
	public final int getKeys() {
		return _keys;
	}
}
