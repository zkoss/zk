/* ExecutionInit.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 24 23:31:06     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Execution;

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
 * @author tomyeh
 */
public interface ExecutionInit {
	/** Called when an exection is created and initialized.
	 *
	 * <p>Note: this method is called after exec is activated. In other words,
	 * {@link org.zkoss.zk.ui.Executions#getCurrent} is the same as
	 * the exec argument.
	 *
	 * <p>When this method is called, you can retrieve the current page with
	 * {@link org.zkoss.zk.ui.sys.ExecutionCtrl#getCurrentPage}.
	 * However, the page is not initialized yet. In other words,
	 * {@link org.zkoss.zk.ui.Page#getDesktop},
	 * {@link org.zkoss.zk.ui.Page#getId} and {@link org.zkoss.zk.ui.Page#getTitle}
	 * all return null.
	 * To get the current desktop, you have to use
	 * {@link org.zkoss.zk.ui.Execution#getDesktop} (from
	 * {@link org.zkoss.zk.ui.Executions#getCurrent}) instead.
	 * On the other hand, you can set the page's ID, title or style in
	 * this method (to override the declarations in the page definition)
	 * by {@link org.zkoss.zk.ui.Page#setId}, {@link org.zkoss.zk.ui.Page#setTitle}
	 * and {@link org.zkoss.zk.ui.Page#setStyle}.
	 * In additions, {@link org.zkoss.zk.ui.Page#getRequestPath}
	 * and {@link org.zkoss.zk.ui.Page#getAttribute} are all available.
	 *
	 * @param exec the execution being created.
	 * @param parent the previous execution in the same (Servlet) request, or
	 * null if this is the first execution of the request.
	 */
	public void init(Execution exec, Execution parent) throws Exception;
}
