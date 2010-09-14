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
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.sys.Attributes;

/**
 * Additional utilities for {@link Execution}.
 *
 * @author tomyeh
 */
public class ExecutionsCtrl extends Executions {
	/** The virtual ID space. */
	private static final ThreadLocal<IdSpace> _virtIS = new ThreadLocal<IdSpace>();

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

	/** Returns the virtual ID space, or null if not found.
	 * It is used by {@link Execution#createComponents} and related methods
	 * to simulate an ID space if the components being created doesn't
	 * belong to any ID space.
	 * @since 5.0.4
	 */
	public static IdSpace getVirtualIdSpace() {
		return _virtIS.get();
	}
	/** Sets the virtual ID space.
	 * It is used by {@link Execution#createComponents} and related methods
	 * to simulate an ID space if the components being created doesn't
	 * belong to any ID space.
	 * <p>Note: you have to clean it up with try/finally:
	 * <pre><code>
	 * final IdSpace oldis = setVirtualIdSpace(idspace);
	 * try {
	 * ...
	 * finally {
	 *   setVirtualIdSpace(oldis);
	 * }</code></pre>
	 * @return the previous virtual ID space if any
	 * @see org.zkoss.zk.ui.impl.SimpleIdSpace
	 * @since 5.0.4
	 */
	public static IdSpace setVirtualIdSpace(IdSpace idspace) {
		final IdSpace old = getVirtualIdSpace();
		_virtIS.set(idspace);
		return old;
	}
}
