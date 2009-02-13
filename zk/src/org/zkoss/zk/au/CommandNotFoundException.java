/* CommandNotFoundException.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun  6 12:56:02     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au;

import org.zkoss.zk.ui.UiException;

/**
 * Represents an update-relevant exception.
 *
 * @author tomyeh
 */
public class CommandNotFoundException extends UiException {
	public CommandNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public CommandNotFoundException(String s) {
		super(s);
	}
	public CommandNotFoundException(Throwable cause) {
		super(cause);
	}
	public CommandNotFoundException() {
	}

	public CommandNotFoundException(int code, Object[] fmtArgs, Throwable cause) {
		super(code, fmtArgs, cause);
	}
	public CommandNotFoundException(int code, Object fmtArg, Throwable cause) {
		super(code, fmtArg, cause);
	}
	public CommandNotFoundException(int code, Object[] fmtArgs) {
		super(code, fmtArgs);
	}
	public CommandNotFoundException(int code, Object fmtArg) {
		super(code, fmtArg);
	}
	public CommandNotFoundException(int code, Throwable cause) {
		super(code, cause);
	}
	public CommandNotFoundException(int code) {
		super(code);
	}
}
