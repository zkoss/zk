/* ErrorEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 21 15:49:42     2005, Created by tomyeh@potix.com
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
 * Represents an event cause by user's entering a wrong data
 * or clearing the last wrong data.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.4 $ $Date: 2006/05/29 04:28:01 $
 * @see com.potix.zk.ui.ext.Errorable
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
