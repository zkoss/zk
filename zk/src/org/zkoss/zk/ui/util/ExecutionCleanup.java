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

import java.util.List;
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
	 * <p>If an exception occurs before calling this method, this method
	 * will be called with a {@link ErrorInfo} instance
	 *(thru <code>errInfo</code>). You can then examine the exceptions,
	 * and even clean them up with ({@link ErrorInfo#getErrors}).
	 * If all errors are cleaned, the user won't be alerted.
	 *
	 * <p>If this method throws an exception, the stack trace will be logged
	 * and the error message will be displayed at the client.
	 *
	 * @param exec the exection to clean up.
	 * @param parent the previous execution, or null if no previous at all
	 * @param errInfo information about the exceptions being thrown
	 * (and not handled) during the execution, or null if it executes successfully.
	 */
	public void cleanup(Execution exec, Execution parent, ErrorInfo errInfo);

	/** The infomation about the exception, if any, that occurs before
	 * {@link ExecutionCleanup#cleanup} is called.
	 */
	public interface ErrorInfo {
		/** Returns a non-empty list of exceptioins that occur before
		 * {@link ExecutionCleanup#cleanup} is called.
		 *
		 * <p>The returned list is live. In other words, you can add, remove
		 * or clean exception from it directly.
		 */
		public List getErrors();
	}
}
