/* KeyEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep 27 09:15:39     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import org.zkoss.json.JSONObject;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.au.AuRequest;

/**
 * Represents a key pressed by the user.
 * 
 * @author tomyeh
 */
public class KeyEvent extends Event {
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

	/** Converts an AU request to a key event.
	 * @since 5.0.0
	 */
	public static final KeyEvent getKeyEvent(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, request);
		final JSONObject data = request.getData();
		if (data == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {data, request});

		return new KeyEvent(request.getName(), comp,
			data.optInt("keyCode"), data.optBoolean("ctrlKey"),
			data.optBoolean("shiftKey"), data.optBoolean("altKey"),
			request.getDesktop().getComponentByUuidIfAny(data.optString("reference", null)));
	}

	private final int _keyCode;
	private final boolean _ctrlKey, _shiftKey, _altKey;
	private final Component _ref;
	public KeyEvent(String name, Component target, int keyCode,
			boolean ctrlKey, boolean shiftKey, boolean altKey) {
		this(name, target, keyCode, ctrlKey, shiftKey, altKey, null);
	}
	/** Constructs a mouse relevant event.
	 */
	public KeyEvent(String name, Component target, int keyCode,
	boolean ctrlKey, boolean shiftKey, boolean altKey, Component ref) {
		super(name, target);
		_keyCode = keyCode;
		_ctrlKey = ctrlKey;
		_shiftKey = shiftKey;
		_altKey = altKey;
		_ref = ref;
	}
	/** Returns the key code.
	 */
	public final int getKeyCode() {
		return _keyCode;
	}
	/** Returns whether CTRL is pressed.
	 */
	public final boolean isCtrlKey() {
		return _ctrlKey;
	}
	/** Returns whether SHIFT is pressed.
	 */
	public final boolean isShiftKey() {
		return _shiftKey;
	}
	/** Returns whether ALT is pressed.
	 */
	public final boolean isAltKey() {
		return _altKey;
	}

	/** 
	 * Returns the reference item that is the component causing the key event to
	 * be fired.
	 *
	 * @since 3.0.6
	 */
	public Component getReference() {
		return _ref;
	} 
}
