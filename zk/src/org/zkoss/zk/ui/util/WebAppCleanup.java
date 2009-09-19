/* WebAppCleanup.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 21 19:32:30     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.WebApp;

/**
 * Used to initialize a ZK application when it about to be destroyed.
 *
 * <p>How this interface is used.
 * <ol>
 * <li>First, you specify a class that implements this interface
 * in WEB-INF/zk.xml as a listener.
 * </li>
 * <li>Then, even time a ZK application is going to detroy, an instnace of
 * the specified class is instantiated and {@link #cleanup} is called.</li>
 * </ol>
 * 
 * @author tomyeh
 */
public interface WebAppCleanup {
	/** called when a ZK application is about to be destroyed.
	 *
	 * <p>If this method throws an exception, the error message is
	 * only logged (user won't see it).
	 */
	public void cleanup(WebApp wapp) throws Exception;
}
