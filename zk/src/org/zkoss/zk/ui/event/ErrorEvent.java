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

import org.zkoss.zk.ui.Component;

/**
 * Represents an event cause by user's entering a wrong data
 * or clearing the last wrong data.
 * 
 * @author tomyeh
 * @see org.zkoss.zk.ui.ext.client.Errorable
 */
public class ErrorEvent extends InputEvent {
	private final String _msg;

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
