/* ErrorEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 21 15:49:42     2005, Created by tomyeh
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
 * Represents an event cause by user's entering a wrong data
 * or clearing the last wrong data.
 * 
 * @author tomyeh
 */
public class ErrorEvent extends InputEvent {
	private final String _msg;

	/** Converts an AU request to an error event.
	 * @since 5.0.0
	 */
	public static final ErrorEvent getErrorEvent(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, request);
		final JSONObject data = request.getData();
		if (data == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {data, request});

		try {
			return new ErrorEvent(request.getName(), comp,
				data.getString("value"), data.getString("message"));
		} catch (org.zkoss.json.JSONException ex) {
			throw new UiException(ex);
		}
	}

	/** Constructs an error-relevant event.
	 * @param val the new value
	 * @param msg the error message if not null. If null, it means the
	 * error (notified by previous {@link ErrorEvent}) is cleared.
	 */
	public ErrorEvent(String name, Component target, String val, String msg) {
		super(name, target, val);
		_msg = msg;
	}
	/** Returns the error message if this event is caused by a wrong data,
	 * or null if it is to clear messsage.
	 */
	public final String getMessage() {
		return _msg;
	}
}
