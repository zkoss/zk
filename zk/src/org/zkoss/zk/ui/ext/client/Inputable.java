/* Inputable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 14 22:47:29     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

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
 * <p>Note: For components supporting only ZK 3.0.3 or later,
 * it is suggested to implement {@link InputableX} instead of
 * this interface, since it has more control about whether to send
 * the onChange event.
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
 * @see InputableX
 * @see org.zkoss.zk.ui.event.InputEvent
 */
public interface Inputable {
	/** Sets the value in string (aka., text) by the client.
	 * <p>This method is designed to be used by engine.
	 * Don't invoke it directly. Otherwise, the client and server
	 * might mismatch.
	 */
	public void setTextByClient(String value) throws WrongValueException;
}
