/* KeyEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep 27 09:15:39     2005, Created by tomyeh@potix.com
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
 * Represents a key pressed by the user.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class KeyEvent extends Event {
	private final int _keyCode;
	private final boolean _ctrlKey, _shiftKey, _altKey;
	/** Constructs a mouse relevant event.
	 */
	public KeyEvent(String name, Component target, int keyCode,
	boolean ctrlKey, boolean shiftKey, boolean altKey) {
		super(name, target);
		_keyCode = keyCode;
		_ctrlKey = ctrlKey;
		_shiftKey = shiftKey;
		_altKey = altKey;
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
}
