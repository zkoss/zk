/* ArithmeticWrongValueException.java

	Purpose:
		
	Description:
		
	History:
		Mon Mar  22 15:27:24     2012, Created by tomyeh

Copyright (C) 2012 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zk.ui;

/**
 * For ArithmeticException wrong value wrapper.
 * 
 * @author tony
 * 
 */
public class ArithmeticWrongValueException extends WrongValueException {

	private static final long serialVersionUID = 2388564567451692996L;
	private String value;

	public ArithmeticWrongValueException(Component comp, Throwable cause,
			String value) {
		super(comp, value);
		initCause(cause);
		this.value = value;
	}

	public ArithmeticWrongValueException() {
		super();
	}

	public ArithmeticWrongValueException(Component comp, int code, Object fmtArg) {
		super(comp, code, fmtArg);
	}

	public ArithmeticWrongValueException(Component comp, int code,
			Object[] fmtArgs) {
		super(comp, code, fmtArgs);
	}

	public ArithmeticWrongValueException(Component comp, int code) {
		super(comp, code);
	}

	public ArithmeticWrongValueException(Component comp, String msg) {
		super(comp, msg);
	}

	public ArithmeticWrongValueException(int code, Object fmtArg,
			Throwable cause) {
		super(code, fmtArg, cause);
	}

	public ArithmeticWrongValueException(int code, Object fmtArg) {
		super(code, fmtArg);
	}

	public ArithmeticWrongValueException(int code, Object[] fmtArgs,
			Throwable cause) {
		super(code, fmtArgs, cause);
	}

	public ArithmeticWrongValueException(int code, Object[] fmtArgs) {
		super(code, fmtArgs);
	}

	public ArithmeticWrongValueException(int code, Throwable cause) {
		super(code, cause);
	}

	public ArithmeticWrongValueException(int code) {
		super(code);
	}

	public ArithmeticWrongValueException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public ArithmeticWrongValueException(String s) {
		super(s);
	}

	public ArithmeticWrongValueException(Throwable cause) {
		super(cause);
	}

	/**
	 * @return the value that user entered
	 */
	public String getValue() {
		return value;
	}
	
	public String getMessage() {
		return getCause() != null ? getCause().toString() : super.getMessage();
	}

}
