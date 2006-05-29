/* Errorable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 21 15:47:42     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.ext;

/**
 * Used to decorate a {@link com.potix.zk.ui.Component} object that
 * users might enter a wrong value.
 *
 * <p>{@link com.potix.zk.ui.event.ErrorEvent} is sent when the client detects users entered
 * a wrong value. Note: if the client doesn't detect the error,
 * the value is sent back to the server with regular event, such as
 * {@link com.potix.zk.ui.event.InputEvent}.
 *
 * <p>The server must memorize the error until another {@link #setErrorByClient}
 * is called with null message. Then, when getValue() or similar method
 * is called, it shall throw {@link com.potix.zk.ui.WrongValueException}
 * with the error message (set by {@link #setErrorByClient}).
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/05/29 04:28:03 $
 */
public interface Errorable {
	/** Sets or clears the error by client.
	 * <p>This method is designed to be used by engine.
	 * Don't invoke it directly. Otherwise, the client and server
	 * might mismatch.
	 *
	 * @param value the vaue that user entered and causes an error.
	 * @param message the message describes the error, if not null.
	 * If null, it means user at the client cleared the error.
	 * This happens: user entered a wrong value and corrected by restoring
	 * the default value. If user restored the error by typing another
	 * value, a regular event, like {@link com.potix.zk.ui.event.InputEvent}, is sent instead.
	 */
	public void setErrorByClient(String value, String message);
}
