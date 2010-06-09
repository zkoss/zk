/* SessionInit.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 22 11:34:09     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Session;

/**
 * Used to initialize a session when it is created.
 *
 * <p>How this interface is used.
 * <ol>
 * <li>First, you specify a class that implements this interface
 * in WEB-INF/zk.xml as a listener.
 * </li>
 * <li>Then, even time ZK loader creates a new session, an instnace of
 * the specified class is instantiated and {@link #init} is called.</li>
 * </ol>
 * 
 * @author tomyeh
 */
public interface SessionInit {
	/** Called when a session is created and initialized.
	 *
	 * <p>If the client is based on HTTP (such as browsers), you could
	 * retrieve the HTTP session by {@link Session#getNativeSession}</p>
	 *
	 * @param sess the session being created and initialized
	 * @param request the request caused the session being created.
	 * If HTTP and servlet, it is javax.servlet.http.HttpServletRequest.
	 * If portlet, it is javax.portlet.RenderRequest.
	 * @since 3.0.1
	 */
	public void init(Session sess, Object request) throws Exception;
}
