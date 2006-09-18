/* Scrollable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Oct  2 21:30:31     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.ext;

/**
 * Used to decorate a {@link com.potix.zk.ui.Component} object that
 * it is a slider or scrollbar with a position that user could change.
 *
 * <p>{@link com.potix.zk.ui.event.ScrollEvent} will be sent wih name as "onScroll" after
 * {@link #setCurposByClient} is called
 * to notify application developers that it is called by user
 * (rather than by codes).
 *
 * <p>For components that implement this interface MIGHT also support
 * {@link com.potix.zk.ui.event.ScrollEvent} with "onScrolling". It is used to notified the server
 * that user is changing its content (changing is on progress and not finished).
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface Scrollable {
	/** Sets the value in string (aka., text) by client.
	 * <p>This method is designed to be used by engine.
	 * Don't invoke it directly. Otherwise, the client and server
	 * might mismatch.
	 */
	public void setCurposByClient(int pos);
}
