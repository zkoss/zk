/* ExecutionsCtrl.java

	Purpose:
		
	Description:
		
	History:
		Mon Jun  6 12:20:51     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.impl.Attributes;

/**
 * Additional utilities for {@link Execution}.
 *
 * @author tomyeh
 */
public class ExecutionsCtrl extends Executions {
	protected ExecutionsCtrl() {} //prevent from instantiation

	/** Sets the execution for the current thread.
	 * Called only internally.
	 *
	 * <p>Note: you have to clean up the current execution
	 * with try/finally:
	 * <pre><code>
	 * setCurrent(exec);
	 * try {
	 * ...
	 * finally {
	 *   setCurrent(null);
	 * }</code></pre>
	 */
	public static final void setCurrent(Execution exec) {
		_exec.set(exec);
	}
	/** Returns the current {@link ExecutionCtrl}.
	 */
	public static final ExecutionCtrl getCurrentCtrl() {
		return (ExecutionCtrl)getCurrent();
	}

	/** Returns the page redraw control.
	 * It first check if a request attribute called
	 * {@link Attributes#PAGE_REDRAW_CONTROL} is defined.
	 * If not, it checks a request parameter called <code>zk.redrawCtrl</code>
	 * is defined. If none of them is defined, null is returned.
	 * @since 5.0.0
	 */
	public static final String getPageRedrawControl(Execution exec) {
		String ctl =
			(String)exec.getAttribute(Attributes.PAGE_REDRAW_CONTROL);
		return ctl != null ? ctl: exec.getParameter("zk.redrawCtrl");
	}
}
