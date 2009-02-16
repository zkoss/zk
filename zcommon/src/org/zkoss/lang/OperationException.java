/* OperationException.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 21 13:15:02     2003, Created by tomyeh
}}IS_NOTE

Copyright (C) 2003 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.lang;

/**
 * The expectable system exception denoting user's operation errors.
 *
 * @author tomyeh
 */
public class OperationException extends SystemException implements Expectable {
	public OperationException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public OperationException(String s) {
		super(s);
	}
	public OperationException(Throwable cause) {
		super(cause);
	}
	public OperationException() {
	}

	public OperationException(int code, Object[] fmtArgs, Throwable cause) {
		super(code, fmtArgs, cause);
	}
	public OperationException(int code, Object fmtArg, Throwable cause) {
		super(code, fmtArg, cause);
	}
	public OperationException(int code, Object[] fmtArgs) {
		super(code, fmtArgs);
	}
	public OperationException(int code, Object fmtArg) {
		super(code, fmtArg);
	}
	public OperationException(int code, Throwable cause) {
		super(code, cause);
	}
	public OperationException(int code) {
		super(code);
	}
}
