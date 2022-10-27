/* MouseData.java

	Purpose:

	Description:

	History:
		4:34 PM 2021/10/15, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.action.data;

/**
 * Represents mouse data by mouse activity
 *
 * @author jumperchen
 */
public class MouseData implements ActionData {
	private int x, y, pageX, pageY;
	private int which = -1;
	private boolean altKey, ctrlKey, shiftKey, metaKey;
	private String area;

	/**
	 * Indicates whether the Alt key is pressed.
	 * It might be returned as part of {@link #getKeys}.
	 */
	public static final int ALT_KEY = 0x001;
	/**
	 * Indicates whether the Ctrl key is pressed.
	 * It might be returned as part of {@link #getKeys}.
	 */
	public static final int CTRL_KEY = 0x002;
	/**
	 * Indicates whether the Shift key is pressed.
	 * It might be returned as part of {@link #getKeys}.
	 */
	public static final int SHIFT_KEY = 0x004;
	/**
	 * Indicates whether the Meta key is pressed.
	 * It might be returned as part of {@link #getKeys}.
	 */
	public static final int META_KEY = 0x008;
	/**
	 * Indicates whether the left button is clicked.
	 */
	public static final int LEFT_CLICK = 0x100;
	/**
	 * Indicates whether the right button is clicked.
	 */
	public static final int RIGHT_CLICK = 0x200;
	/**
	 * Indicates whether the middle button is clicked.
	 */
	public static final int MIDDLE_CLICK = 0x400;

	/**
	 * Returns the horizontal coordinate of the mouse pointer relevant to
	 * the component.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Returns the vertical coordinate of the mouse pointer relevant to
	 * the component.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Returns the horizontal coordinate of the mouse pointer relative
	 * to the whole document.
	 */
	public int getPageX() {
		return pageX;
	}

	/**
	 * Returns the vertical coordinate of the mouse pointer relative
	 * to the whole document.
	 */
	public int getPageY() {
		return pageY;
	}

	public int getWhich() {
		return which;
	}

	public boolean isAltKey() {
		return altKey;
	}

	public boolean isCtrlKey() {
		return ctrlKey;
	}

	public boolean isShiftKey() {
		return shiftKey;
	}

	public boolean isMetaKey() {
		return metaKey;
	}

	/**
	 * Returns the logical name of the area that the click occurs, or
	 * null if not available.
	 *
	 * @return
	 */
	public String getArea() {
		return area;
	}

	/**
	 * Returns what keys were pressed when the mouse is clicked, or 0 if
	 * none of them was pressed.
	 * It is a combination of {@link #CTRL_KEY}, {@link #SHIFT_KEY}
	 * {@link #ALT_KEY}, {@link #LEFT_CLICK} and {@link #RIGHT_CLICK}.
	 */
	public final int getKeys() {
		int keys = 0;
		if (isAltKey())
			keys |= ALT_KEY;
		if (isCtrlKey())
			keys |= CTRL_KEY;
		if (isShiftKey())
			keys |= SHIFT_KEY;
		if (isMetaKey())
			keys |= META_KEY;
		switch (which) {
		case 1:
			keys |= LEFT_CLICK;
			break;
		case 2:
			keys |= MIDDLE_CLICK;
			break;
		case 3:
			keys |= RIGHT_CLICK;
			break;
		}
		return keys;
	}

	/**
	 * @hidden for Javadoc
	 */
	public String toString() {
		return "MouseData{" + "x=" + x + ", y=" + y + ", pageX=" + pageX
				+ ", pageY=" + pageY + ", which=" + which + ", altKey=" + altKey
				+ ", ctrlKey=" + ctrlKey + ", shiftKey=" + shiftKey
				+ ", metaKey=" + metaKey + ", area='" + area + '\'' + '}';
	}
}
