/* ExecutionInit.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 24 23:31:06     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.util;

import com.potix.zk.ui.Execution;

/**
 * Used to initialize an execution when it is created.
 *
 * <p>How this interface is used.
 * <ol>
 * <li>First, you specify a class that implements this interface
 * in WEB-INF/zk.xml as a listener.
 * </li>
 * <li>Then, even time ZK loader creates a new execution, an instnace of
 * the specified class is instantiated and {@link #init} is called.</li>
 * </ol>
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface ExecutionInit {
	/** Called when an exection is created and initialized.
	 *
	 * <p>Note: this method is called after exec is activated. In other words,
	 * {@link com.potix.zk.ui.Executions#getCurrent} is the same as
	 * the exec argument.
	 *
	 * @param exec the execution being created.
	 * @param parent the previous execution in the same (Servlet) request, or
	 * null if this is the first execution of the request.
	 */
	public void init(Execution exec, Execution parent);
}
