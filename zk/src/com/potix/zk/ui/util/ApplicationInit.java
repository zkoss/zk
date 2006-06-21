/* ApplicationInit.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun 21 19:13:09     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zk.ui.util;

import com.potix.zk.ui.WebApp;

/**
 * Used to initialize a ZK application when it is created.
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
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface ApplicationInit {
	/** Called when a ZK application is created and initialized.
	 *
	 * <p>You could
	 * retrieve the servlet context by {@link WebApp#getNativeContext}</p>
	 */
	public void init(WebApp wapp);
}
