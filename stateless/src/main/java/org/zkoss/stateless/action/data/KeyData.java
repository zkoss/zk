/* KeyData.java

	Purpose:

	Description:

	History:
		9:50 AM 2021/12/28, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.action.data;

/**
 * Represents a key pressed by the user.
 * @author jumperchen
 */
public class KeyData implements ActionData {

	public static final int F1 = 112;
	public static final int F2 = 113;
	public static final int F3 = 114;
	public static final int F4 = 115;
	public static final int F5 = 116;
	public static final int F6 = 117;
	public static final int F7 = 118;
	public static final int F8 = 119;
	public static final int F9 = 120;
	public static final int F10 = 121;
	public static final int F11 = 122;
	public static final int F12 = 123;

	public static final int PAGE_UP = 33;
	public static final int PAGE_DOWN = 34;
	public static final int END = 35;
	public static final int HOME = 36;
	public static final int LEFT = 37;
	public static final int UP = 38;
	public static final int RIGHT = 39;
	public static final int DOWN = 40;

	public static final int INSERT = 45;
	public static final int DELETE = 46;

	private int keyCode;
	private boolean ctrlKey, shiftKey, altKey, metaKey;
	/** Returns the key code.
	 */
	public final int getKeyCode() {
		return keyCode;
	}

	/** Returns whether CTRL is pressed.
	 */
	public final boolean isCtrlKey() {
		return ctrlKey;
	}

	/** Returns whether SHIFT is pressed.
	 */
	public final boolean isShiftKey() {
		return shiftKey;
	}

	/** Returns whether ALT is pressed.
	 */
	public final boolean isAltKey() {
		return altKey;
	}

	/** Returns whether META is pressed.
	 */
	public final boolean isMetaKey() {
		return metaKey;
	}

	/**
	 * @hidden for Javadoc
	 */
	@Override
	public String toString() {
		return "KeyData{" + "keyCode=" + keyCode + ", ctrlKey=" + ctrlKey
				+ ", shiftKey=" + shiftKey + ", altKey=" + altKey + ", metaKey="
				+ metaKey + '}';
	}
}