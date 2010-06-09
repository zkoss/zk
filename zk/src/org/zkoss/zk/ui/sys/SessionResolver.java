/* SessionResolver.java

	Purpose:
		
	Description:
		
	History:
		Sat Jul  4 17:27:07     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.sys;

import org.zkoss.zk.ui.Session;

/**
 * Used with {@link SessionsCtrl#setCurrent(SessionResolver)}
 * to resolve a session dynamically.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public interface SessionResolver {
	/** Returns the session, or null if no session available and
	 * <code>create</code> is false.
	 *
	 * @param create whether to create the session if not found.
	 * If true, a new session will be create if there is no one available.
	 */
	public Session getSession(boolean create);
}
