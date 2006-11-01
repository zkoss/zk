/* ExecutionCleanup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 24 23:31:23     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Execution;

/**
 * Used to clean up an execution.
 *
 * <p>How this interface is used.
 * <ol>
 * <li>First, you specify a class that implements this interface
 * in WEB-INF/zk.xml as a listener.
 * </li>
 * <li>Then, even time ZK loader is destroying an exection, an instnace of
 * the specified class is instantiated and {@link #cleanup} is called.</li>
 * </ol>
 * 
 * @author tomyeh
 */
public interface ExecutionCleanup {
	/** called when an execution is about to be destroyed.
	 *
	 * <p>If this method throws an exception, the error message will be displayed
	 * at the client.
	 *
	 * @param exec the exection to clean up.
	 * @param parent the previous execution, or null if no previous at all
	 * @param ex the first exception being thrown (and not handled) during
	 * the execution, or null it is executed successfully.
	 */
	public void cleanup(Execution exec, Execution parent, Throwable ex);
}
