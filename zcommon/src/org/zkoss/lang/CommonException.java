/* CommonException.java

{{IS_NOTE

	Purpose: The most fundamental non-runtime exception
	Description: 
	History:
	 2001/6/1, Tom M. Yeh: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.lang;

import org.zkoss.mesg.Messageable;

/**
 * The most fundamental non-runtime exception of Potix classes.
 * All exceptions specific to Potix classes must derive from
 * SystemException or CommonException.
 *
 * <p>SystemException indicates programming bugs, while
 * CommonException indicates exceptinal cases.
 *
 * @author tomyeh
 * @see SystemException
 */
public class CommonException extends Exception implements Messageable {
	protected int _code = NULL_CODE;

	/**
	 * Constructs an CommonException by specifying message directly.
	 */
	public CommonException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public CommonException(String msg) {
		super(msg);
	}
	public CommonException(Throwable cause) {
		super(cause);
	}
	public CommonException() {
	}

	/**
	 * Constructs an CommonException by use of an error code.
	 * The error code must be defined in
	 * one of properties files, e.g., msgsys.properties.
	 *
	 * @param code the error code
	 * @param fmtArgs the format arguments
	 * @param cause the chained throwable object
	 */
	public CommonException(int code, Object[] fmtArgs, Throwable cause) {
		super(Exceptions.getMessage(code, fmtArgs), cause);
		_code = code;
	}
	public CommonException(int code, Object fmtArg, Throwable cause) {
		super(Exceptions.getMessage(code, fmtArg), cause);
		_code = code;
	}
	public CommonException(int code, Object[] fmtArgs) {
		super(Exceptions.getMessage(code, fmtArgs));
		_code = code;
	}
	public CommonException(int code, Object fmtArg) {
		super(Exceptions.getMessage(code, fmtArg));
		_code = code;
	}
	public CommonException(int code, Throwable cause) {
		super(Exceptions.getMessage(code), cause);
		_code = code;
	}
	public CommonException(int code) {
		super(Exceptions.getMessage(code));
		_code = code;
	}

	//-- Messageable --//
	public final int getCode() {
		return _code;
	}
}
