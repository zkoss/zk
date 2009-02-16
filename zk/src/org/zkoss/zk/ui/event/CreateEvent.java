/* CreateEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun 23 20:41:25     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import java.util.Map;
import java.util.Collections;

import org.zkoss.zk.ui.Component;

/**
 * Used to notify a window that all its children are created and initialized.
 * {@link org.zkoss.zk.ui.sys.UiEngine} post this event to compoents that
 * declares the onCreate handler (either as a method or as in instance definition).
 *
 * @author tomyeh
 */
public class CreateEvent extends Event {
	private final Map _args;
	public CreateEvent(String name, Component target, Map args) {
		super(name, target);
		_args = args != null ? args: Collections.EMPTY_MAP;
	}
	/** Returns arg ({@link org.zkoss.zk.ui.Execution#getArg}) when the component is
	 * created.
	 * <p>Note: when onCreate listeners are called, {@link org.zkoss.zk.ui.Execution#getArg}
	 * is no longer available, you have to use this method instead.
	 */
	public Map getArg() {
		return _args;
	}
}
