/* CheckEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun 17 00:28:22     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import org.zkoss.lang.Objects;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;

/**
 * Represents an event cause by user's check a state at the client.
 * 
 * @author tomyeh
 */
public class CheckEvent extends Event {
	private final boolean _checked;

	/** Converts an AU request to a check event.
	 * @since 5.0.0
	 */
	public static final CheckEvent getCheckEvent(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, request);
		final String[] data = request.getData();
		if (data == null || data.length != 1)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), request});

		final boolean checked = "true".equals(data[0]);
		return new CheckEvent(request.getName(), comp, checked);
	}

	/** Constructs a check-relevant event.
	 * @param checked whether it is checked.
	 */
	public CheckEvent(String name, Component target, boolean checked) {
		super(name, target);
		_checked = checked;
	}
	/** Returns whether the state is checked.
	 */
	public final boolean isChecked() {
		return _checked;
	}
}
