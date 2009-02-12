/* InputableX.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jan 29 12:51:25     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext.client;

import org.zkoss.zk.ui.WrongValueException;

/**
 * Implemented by the object returned by {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl},
 * if a component allows users to change its content from the client.
 *
 * <p>{@link org.zkoss.zk.ui.event.InputEvent} will be sent wih name as "onChange" after
 * {@link #setTextByClient} is called
 * to notify application developers that it is called by user
 * (rather than by codes).
 *
 * <p>For components that implement this interface MIGHT also support
 * {@link org.zkoss.zk.ui.event.InputEvent} with "onChanging". It is used to notified the server
 * that user is changing its content (changing is on progress and not finished).
 *
 * <p>The server usually uses onChanging to implement auto-completion and
 * similar feature.
 *
 * @author tomyeh
 * @see org.zkoss.zk.ui.event.InputEvent
 * @since 3.0.3
 */
public interface InputableX {
	/** Sets the value in string (aka., text) by the client.
	 * <p>This method is designed to be used by engine.
	 * Don't invoke it directly. Otherwise, the client and server
	 * might mismatch.
	 *
	 * @return false if nothing is changed.
	 */
	public boolean setTextByClient(String value) throws WrongValueException;
}
