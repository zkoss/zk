/* DesktopInit.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 30 18:28:12     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Desktop;

/**
 * Used to initialize a desktop when it is created.
 *
 * <p>How this interface is used.
 * <ol>
 * <li>First, you specify a class that implements this interface
 * in WEB-INF/zk.xml as a listener.
 * </li>
 * <li>Then, even time ZK loader creates a new desktop, an instnace of
 * the specified class is instantiated and {@link #init} is called.</li>
 * </ol>
 * 
 * @author tomyeh
 */
public interface DesktopInit {
	/** Called when a desktop is created and initialized.
	 *
	 * <p>Note: you can access the execution by calling
	 * {@link Desktop#getExecution} or
	 * {@link org.zkoss.zk.ui.Executions#getCurrent}.
	 *
	 * @param desktop the desktop being created and initialized
	 * @param request the request caused the desktop being created.
	 * If HTTP and servlet, it is javax.servlet.http.HttpServletRequest.
	 * If portlet, it is javax.portlet.RenderRequest.
	 * @since 3.0.1
	 */
	public void init(Desktop desktop,  Object request) throws Exception;
}
