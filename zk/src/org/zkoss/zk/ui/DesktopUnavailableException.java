/* DesktopUnavailableException.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug  3 17:31:08     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

/**
 * Denotes the desktop being accessed is no longer available.
 * It is typical thrown when a server push is executing and the desktop
 * is removed by the client. See {@link Executions#activate} for more
 * information.
 *
 * @author tomyeh
 * @see Executions#activate
 * @since 3.0.0
 */
public class DesktopUnavailableException extends UiException {
	public DesktopUnavailableException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public DesktopUnavailableException(String s) {
		super(s);
	}
	public DesktopUnavailableException(Throwable cause) {
		super(cause);
	}
	public DesktopUnavailableException() {
	}

	public DesktopUnavailableException(int code, Object[] fmtArgs, Throwable cause) {
		super(code, fmtArgs, cause);
	}
	public DesktopUnavailableException(int code, Object fmtArg, Throwable cause) {
		super(code, fmtArg, cause);
	}
	public DesktopUnavailableException(int code, Object[] fmtArgs) {
		super(code, fmtArgs);
	}
	public DesktopUnavailableException(int code, Object fmtArg) {
		super(code, fmtArg);
	}
	public DesktopUnavailableException(int code, Throwable cause) {
		super(code, cause);
	}
	public DesktopUnavailableException(int code) {
		super(code);
	}
}
