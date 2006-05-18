/* InvalidValueException.java

{{IS_NOTE

	$Header: //time/potix/rd/cvs/zk1/pxcommon/src/com/potix/util/InvalidValueException.java,v 1.3 2006/02/27 03:42:02 tomyeh Exp $
	Purpose: Thrown by Validate to indicate an error
	Description: 
	History:
	 2001/4/19, Tom M. Yeh: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util;

import com.potix.lang.Expectable;

/**
 * Denotes an invalid value is passed to a setter method.
 *
 * <p>Unlike {@link ModificationException}, this exception is
 * {@link com.potix.lang.Expectable}, i.e., it is usually user's error,
 * not program's bug.
 *
 * @author <a href="mailto:tomyeh@potix.com">Tom M. Yeh</a>
 * @version $Revision: 1.3 $ $Date: 2006/02/27 03:42:02 $
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
