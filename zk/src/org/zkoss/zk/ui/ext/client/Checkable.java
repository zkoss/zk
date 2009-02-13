/* Checkable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun 17 00:26:46     2005, Created by tomyeh
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
 * if a component allows users to check its state from the client.
 *
 * <p>{@link org.zkoss.zk.ui.event.CheckEvent} will be sent after {@link #setCheckedByClient}
 * is called to notify application developers that it is called by user
 * (rather than by codes).
 * 
 * @author tomyeh
 * @see org.zkoss.zk.ui.event.CheckEvent
 */
public interface Checkable {
	/** Checks the state caused by client.
	 * <p>This method is designed to be used by engine.
	 * Don't invoke it directly. Otherwise, the client and server
	 * might mismatch.
	 */
	public void setCheckedByClient(boolean checked);
}
