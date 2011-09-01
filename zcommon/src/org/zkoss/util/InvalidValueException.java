/* InvalidValueException.java


	Purpose: Thrown by Validate to indicate an error
	Description: 
	History:
	 2001/4/19, Tom M. Yeh: Created.


Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import org.zkoss.lang.Expectable;

/**
 * Denotes an invalid value is passed to a setter method.
 *
 * <p>Unlike {@link ModificationException}, this exception is
 * {@link org.zkoss.lang.Expectable}, i.e., it is usually user's error,
 * not program's bug.
 *
 * @author tomyeh
 */
public class InvalidValueException extends ModificationException
implements Expectable {
	public InvalidValueException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public InvalidValueException(String s) {
		super(s);
	}
	public InvalidValueException(Throwable cause) {
		super(cause);
	}
	public InvalidValueException() {
	}

	public InvalidValueException(int code, Object[] fmtArgs, Throwable cause) {
		super(code, fmtArgs, cause);
	}
	public InvalidValueException(int code, Object fmtArg, Throwable cause) {
		super(code, fmtArg, cause);
	}
	public InvalidValueException(int code, Object[] fmtArgs) {
		super(code, fmtArgs);
	}
	public InvalidValueException(int code, Object fmtArg) {
		super(code, fmtArg);
	}
	public InvalidValueException(int code, Throwable cause) {
		super(code, cause);
	}
	public InvalidValueException(int code) {
		super(code);
	}
}
