/* Openable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul  8 16:57:56     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext.client;

/**
 * Implemented by the object returned by {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl},
 * if a component allows users to change its open status from the client.
 *
 * <p>{@link org.zkoss.zk.ui.event.OpenEvent} will be sent wih name as "onOpen"
 * <b>after</b> {@link #setOpenByClient} is called
 * to notify application developers that it is called by user
 * (rather than by codes).
 * 
 * @author tomyeh
 * @see org.zkoss.zk.ui.event.OpenEvent
 */
public interface Openable {
	/** Sets the open state caused by client's operation.
	 * <p>This method is designed to be used by engine.
	 * Don't invoke it directly. Otherwise, the client and server
	 * might mismatch.
	 */
	public void setOpenByClient(boolean open);
}
