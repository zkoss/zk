/* Inputable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 14 22:47:29     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.ext;

import com.potix.zk.ui.WrongValueException;

/**
 * Used to decorate a {@link com.potix.zk.ui.Component} object that
 * it allows users to change its content from the client.
 *
 * <p>{@link com.potix.zk.ui.event.InputEvent} will be sent wih name as "onChange" after
 * {@link #setTextByClient} is called
 * to notify application developers that it is called by user
 * (rather than by codes).
 *
 * <p>For components that implement this interface MIGHT also support
 * {@link com.potix.zk.ui.event.InputEvent} with "onChanging". It is used to notified the server
 * that user is changing its content (changing is on progress and not finished).
 *
 * <p>The server usually uses onChanging to implement auto-completion and
 * similar feature.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @see com.potix.zk.ui.event.InputEvent
 */
public interface Inputable {
	/** Sets the value in string (aka., text) by the client.
	 * <p>This method is designed to be used by engine.
	 * Don't invoke it directly. Otherwise, the client and server
	 * might mismatch.
	 */
	public void setTextByClient(String value) throws WrongValueException;
}
