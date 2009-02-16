/* SizeEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Dec  7 11:46:12     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import org.zkoss.zk.ui.Component;

/**
 * Represents an event caused by a component being re-sized.
 *
 * <p>Component Implementation Note:<br/>
 * A sizable component must implement {@link org.zkoss.zk.ui.ext.client.Sizable}
 * for the returned object of {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl}.
 * 
 * @author tomyeh
 */
public class SizeEvent extends Event {
	private final String _width, _height;
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
	public SizeEvent(String name, Component target, String width, String height,
	int keys) {
		super(name, target);
		_width = width;
		_height = height;
		_keys = keys;
	}
	/** Returns the width of the component after re-sized.
	 */
	public final String getWidth() {
		return _width;
	}
	/** Returns the height of the component after re-sized.
	 */
	public final String getHeight() {
		return _height;
	}
	/** Returns what keys were pressed when the component is resized, or 0 if
	 * none of them was pressed.
	 * It is a combination of {@link #CTRL_KEY}, {@link #SHIFT_KEY}
	 * and {@link #ALT_KEY}.
	 */
	public final int getKeys() {
		return _keys;
	}
}
