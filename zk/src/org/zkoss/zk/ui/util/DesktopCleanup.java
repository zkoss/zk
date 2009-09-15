/* DesktopCleanup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Mar 30 18:28:18     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Desktop;

/**
 * Used to clean up a desktop.
 *
 * <p>There are two ways to use this interface.
 * <h2>First: add the listener at the run time</h2>
 * <p>Call {@link Desktop#addListener} to register it.
 *
 * <h2>Second: use configuration</h2>
 * <ol>
 * <li>Specify a class that implements this interface
 * in WEB-INF/zk.xml as a listener.
 * </li>
 * <li>Then, each time ZK loader is destroying a desktop, an instnace of
 * the specified class is instantiated and {@link #cleanup} is called.</li>
 * </ol>
 * 
 * @author tomyeh
 */
public interface DesktopCleanup {
	/** called when a desktop is about to be destroyed.
	 *
	 * <p>If this method throws an exception, the error message is
	 * only logged (user won't see it).
	 */
	public void cleanup(Desktop desktop) throws Exception;
}
