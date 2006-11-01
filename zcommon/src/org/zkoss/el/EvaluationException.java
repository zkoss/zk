/* EvaluationException.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 28 17:13:15     2004, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.el;

import org.zkoss.lang.Exceptions;
import org.zkoss.mesg.Messageable;
import javax.servlet.jsp.el.ELException;

/**
 * Our evaluation exception.
 *
 * @author tomyeh
 */
public class EvaluationException extends ELException implements Messageable {
	protected int _code = NULL_CODE;

	/**
	 * Constructs an EvaluationException by specifying message directly.
	 */
	public EvaluationException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public EvaluationException(String msg) {
		super(msg);
	}
	public EvaluationException(Throwable cause) {
		super(cause);
	}
	public EvaluationException() {
	}

	/**
	 * Constructs an EvaluationException by use of an error code.
	 * The error code must be defined in
	 * one of properties files, e.g., msgsys.properties.
	 *
	 * @param code the error code
	 * @param fmtArgs the format arguments
	 * @param cause the chained throwable object
	 */
	public EvaluationException(int code, Object[] fmtArgs, Throwable cause) {
		super(Exceptions.getMessage(code, fmtArgs), cause);
		_code = code;
	}
	public EvaluationException(int code, Object fmtArg, Throwable cause) {
		super(Exceptions.getMessage(code, fmtArg), cause);
		_code = code;
	}
	public EvaluationException(int code, Object[] fmtArgs) {
		super(Exceptions.getMessage(code, fmtArgs));
		_code = code;
	}
	public EvaluationException(int code, Object fmtArg) {
		super(Exceptions.getMessage(code, fmtArg));
		_code = code;
	}
	public EvaluationException(int code, Throwable cause) {
		super(Exceptions.getMessage(code), cause);
		_code = code;
	}
	public EvaluationException(int code) {
		super(Exceptions.getMessage(code));
		_code = code;
	}

	//-- Messageable --//
	public final int getCode() {
		return _code;
	}
}
