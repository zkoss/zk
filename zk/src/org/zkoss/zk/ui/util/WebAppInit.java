/* WebAppInit.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun 21 19:13:09     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.WebApp;

/**
 * Used to initialize a ZK application when it is created.
 *
 * <p>Notice that, when {@link #init} was called, {@link WebApp} has been
 * created. In other words, it is too late to change the implementation
 * class of UiEngine via {@link Configuration}.
 * Rather, use {@link org.zkoss.zk.ui.sys.WebAppCtrl#setUiFactory}
 * and other methods instead.
 *
 * <p>How this interface is used.
 * <ol>
 * <li>First, you specify a class that implements this interface
 * in WEB-INF/zk.xml as a listener.
 * </li>
 * <li>Then, even time a ZK application is created, an instnace of
 * the specified class is instantiated and {@link #init} is called.</li>
 * </ol>
 * 
 * @author tomyeh
 */
public interface WebAppInit {
	/** Called when a ZK application is created and initialized.
	 *
	 * <p>You could
	 * retrieve the servlet context by {@link WebApp#getNativeContext}</p>
	 */
	public void init(WebApp wapp) throws Exception;
}
