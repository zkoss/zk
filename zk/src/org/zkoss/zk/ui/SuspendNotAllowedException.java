/* SuspendNotAllowedException.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Apr  9 13:35:26     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

/**
 * Denotes the current event processing thread cannot be suspended
 * because there are too many suspended threads.
 *
 * <p>Deployers can control the maximal allowed number of suspended threads
 * by specifying <code>max-suspended-thread</code> in <code>zk.xml</code>,
 * or invoking {@link org.zkoss.zk.ui.util.Configuration#setMaxSuspendedThreads}.
 *
 * <p>By default, no limit at all (and this exception won't be thrown)
 *
 * @author tomyeh
 */
public class SuspendNotAllowedException extends UiException {
	public SuspendNotAllowedException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public SuspendNotAllowedException(String s) {
		super(s);
	}
	public SuspendNotAllowedException(Throwable cause) {
		super(cause);
	}
	public SuspendNotAllowedException() {
	}

	public SuspendNotAllowedException(int code, Object[] fmtArgs, Throwable cause) {
		super(code, fmtArgs, cause);
	}
	public SuspendNotAllowedException(int code, Object fmtArg, Throwable cause) {
		super(code, fmtArg, cause);
	}
	public SuspendNotAllowedException(int code, Object[] fmtArgs) {
		super(code, fmtArgs);
	}
	public SuspendNotAllowedException(int code, Object fmtArg) {
		super(code, fmtArg);
	}
	public SuspendNotAllowedException(int code, Throwable cause) {
		super(code, cause);
	}
	public SuspendNotAllowedException(int code) {
		super(code);
	}
}
