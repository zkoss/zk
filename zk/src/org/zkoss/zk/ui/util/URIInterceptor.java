/* URIInterceptor.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jan 19 17:05:53     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

/**
 * Used to intercept the loading of ZUML pages associated with the specfied URI.
 * Developers usually use it to do the security check.
 *
 * <p>How this interface is used.
 * <ol>
 * <li>First, you specify a class that implements this interface
 * in WEB-INF/zk.xml as a listener.
 * </li>
 * <li>Then, even time ZK wants to load a page definition based on an URI,
 * an instnace of the specified class is instantiated and
 * {@link #init} is called.
 * Note: {@link #init} is called even if the page definition is cached.</li>
 * </ol>
 * 
 * @author tomyeh
 */
public interface URIInterceptor {
	/** Called when a desktop is created and initialized.
	 *
	 * <p>Note: when it is called, {@link org.zkoss.zk.ui.Executions#getCurrent}
	 * might be null, since a page definition might be loaded by a background thread.
	 */
	public void intercept(String uri);
}
