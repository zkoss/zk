/* OperationException.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 27 16:47:06     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import org.zkoss.lang.Expectable;

/**
 * The operation exception is a special UI exception that happens
 * 'reasonably' -- usually caused by user rather than by programming error.
 * 
 * @author tomyeh
 */
public class OperationException extends UiException implements Expectable {
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
