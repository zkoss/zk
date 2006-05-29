/* InputEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 14 17:39:00     2005, Created by tomyeh@potix.com
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
 * Represents an event cause by user's input something at the client.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @see com.potix.zk.ui.ext.Inputable
 */
public class InputEvent extends Event {
	private final String _val;

	/** Constructs a input-relevant event.
	 * @param val the new value
	 */
	public InputEvent(String name, Component target, String val) {
		super(name, target);
		_val = val;
	}
	/** Returns the value that user input.
	 */
	public final String getValue() {
		return _val;
	}
}
