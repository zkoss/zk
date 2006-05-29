/* ShowEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 15 12:17:21     2005, Created by tomyeh@potix.com
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
 * Represents an event cause by user's showing or hiding
 * something at the client.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class ShowEvent extends Event {
	private final boolean _visible;

	/** Constructs a show event.
	 * @param visible whether the new status is visible
	 */
	public ShowEvent(String name, Component target, boolean visible) {
		super(name, target);
		_visible = visible;
	}
	/** Returns whether it causes by making the target visible (aka., showing).
	 */
	public final boolean isVisible() {
		return _visible;
	}
}
