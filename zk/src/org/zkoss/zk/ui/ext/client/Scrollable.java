/* Scrollable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Oct  2 21:30:31     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext.client;

/**
 * Implemented by the object returned by {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl},
 * if a component is a slider or scrollbar with a position that user could change.
 *
 * <p>{@link org.zkoss.zk.ui.event.ScrollEvent} will be sent wih name as "onScroll" after
 * {@link #setCurposByClient} is called
 * to notify application developers that it is called by user
 * (rather than by codes).
 *
 * <p>For components that implement this interface MIGHT also support
 * {@link org.zkoss.zk.ui.event.ScrollEvent} with "onScrolling". It is used to notified the server
 * that user is changing its content (changing is on progress and not finished).
 *
 * @author tomyeh
 */
public interface Scrollable {
	/** Sets the value in string (aka., text) by client.
	 * <p>This method is designed to be used by engine.
	 * Don't invoke it directly. Otherwise, the client and server
	 * might mismatch.
	 */
	public void setCurposByClient(int pos);
}
