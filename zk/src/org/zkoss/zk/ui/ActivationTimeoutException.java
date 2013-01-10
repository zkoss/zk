/* ActivationTimeoutException.java

	Purpose:
		
	Description:
		
	History:
		Sun Jan  7 2013, Created by tomyeh

Copyright (C) 2013 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

/**
 * Indicates the activation of an exceution is taking too long.
 *
 * @author tomyeh
 * @since 6.5.2
 */
public class ActivationTimeoutException extends UiException {
	public ActivationTimeoutException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public ActivationTimeoutException(String s) {
		super(s);
	}
	public ActivationTimeoutException(Throwable cause) {
		super(cause);
	}
	public ActivationTimeoutException() {
	}

	public ActivationTimeoutException(int code, Object[] fmtArgs, Throwable cause) {
		super(code, fmtArgs, cause);
	}
	public ActivationTimeoutException(int code, Object fmtArg, Throwable cause) {
		super(code, fmtArg, cause);
	}
	public ActivationTimeoutException(int code, Object[] fmtArgs) {
		super(code, fmtArgs);
	}
	public ActivationTimeoutException(int code, Object fmtArg) {
		super(code, fmtArg);
	}
	public ActivationTimeoutException(int code, Throwable cause) {
		super(code, cause);
	}
	public ActivationTimeoutException(int code) {
		super(code);
	}
}
