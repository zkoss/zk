/* CheckEvent.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun 17 00:28:22     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import java.util.Map;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.Component;

/**
 * Represents an event cause by user's check a state at the client.
 * 
 * @author tomyeh
 */
public class CheckEvent extends Event {
	private final Boolean _checked;

	/** Converts an AU request to a check event.
	 * @since 5.0.0
	 */
	public static final CheckEvent getCheckEvent(AuRequest request) {
		final Map<String, Object> data = request.getData();
		return new CheckEvent(request.getCommand(), request.getComponent(), (Boolean) data.get(""));
	}

	/** Constructs a check-relevant event.
	 * @param checked whether it is checked.
	 */
	public CheckEvent(String name, Component target, Boolean checked) {
		super(name, target);
		_checked = checked;
	}

	/** Returns whether the state is checked.
	 */
	public final boolean isChecked() {
		return (_checked == null) ? false : _checked;
	}

	/** Returns the nullable state is changed.
	 */
	public final Boolean getState() {
		return _checked;
	}
}
