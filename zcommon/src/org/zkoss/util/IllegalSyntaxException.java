/* IllegalSyntaxException.java

{{IS_NOTE

	Purpose: 
	Description: 
	History:
	2001/10/25 10:31:13, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import org.zkoss.lang.SystemException;

/**
 * Represents a syntax error.
 *
 * @author tomyeh
 */
public class IllegalSyntaxException  extends SystemException {
	public IllegalSyntaxException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public IllegalSyntaxException(String msg) {
		super(msg);
	}
	public IllegalSyntaxException(Throwable cause) {
		super(cause);
	}
	public IllegalSyntaxException() {
	}

	public IllegalSyntaxException
	(int code, Object[] fmtArgs, Throwable cause) {
		super(code, fmtArgs, cause);
	}
	public IllegalSyntaxException
	(int code, Object fmtArg, Throwable cause) {
		super(code, fmtArg, cause);
	}
	public IllegalSyntaxException(int code, Object[] fmtArgs) {
		super(code, fmtArgs);
	}
	public IllegalSyntaxException(int code, Object fmtArg) {
		super(code, fmtArg);
	}
	public IllegalSyntaxException(int code, Throwable cause) {
		super(code, cause);
	}
	public IllegalSyntaxException(int code) {
		super(code);
	}
}
