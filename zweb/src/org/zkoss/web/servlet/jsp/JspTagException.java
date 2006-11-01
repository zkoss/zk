/* JspTagException.java

{{IS_NOTE
	Purpose: 
	Description: 
	History:
	2001/12/03 16:06:08, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.jsp;

import javax.servlet.jsp.JspException;

import org.zkoss.lang.Expectable;
import org.zkoss.lang.Exceptions;
import org.zkoss.mesg.Messageable;

/**
 * The I3 version of JspTagException.
 *
 * @author tomyeh
 */
public class JspTagException
extends javax.servlet.jsp.JspTagException implements Messageable {
	/** Utilities.
	 *
	 * <p>The reason to use a class to hold static utilities is we can
	 * override the method's return type later.
	 */
	public static class Aide {
		/** Converts an exception to JspTagException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static JspException wrap(Throwable t) {
			t = Exceptions.unwrap(t);
			if (t instanceof JspException)
				return (JspException)t;
			if (t instanceof Expectable)
				return (OperationException)
					Exceptions.wrap(t, OperationException.class);
			return (JspTagException)
				Exceptions.wrap(t, JspTagException.class);
		}
		/** Converts an exception to JspTagException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static JspTagException wrap(Throwable t, String msg) {
			t = Exceptions.unwrap(t);
			if (t instanceof Expectable)
				return (OperationException)
					Exceptions.wrap(t, OperationException.class, msg);
			return (JspTagException)
				Exceptions.wrap(t, JspTagException.class, msg);
		}
		/** Converts an exception to JspTagException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static JspTagException wrap(Throwable t, int code, Object[] fmtArgs) {
			t = Exceptions.unwrap(t);
			if (t instanceof Expectable)
				return (OperationException)
					Exceptions.wrap(t, OperationException.class, code, fmtArgs);
			return (JspTagException)
				Exceptions.wrap(t, JspTagException.class, code, fmtArgs);
		}
		/** Converts an exception to JspTagException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static JspTagException wrap(Throwable t, int code, Object fmtArg) {
			t = Exceptions.unwrap(t);
			if (t instanceof Expectable)
				return (OperationException)
					Exceptions.wrap(t, OperationException.class, code, fmtArg);
			return (JspTagException)
				Exceptions.wrap(t, JspTagException.class, code, fmtArg);
		}
		/** Converts an exception to JspTagException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static JspTagException wrap(Throwable t, int code) {
			t = Exceptions.unwrap(t);
			if (t instanceof Expectable)
				return (OperationException)
					Exceptions.wrap(t, OperationException.class, code);
			return (JspTagException)
				Exceptions.wrap(t, JspTagException.class, code);
		}
	}

	protected int _code = NULL_CODE;

	/**
	 * Constructs an JspTagException by specifying message directly.
	 */
	public JspTagException(String msg, Throwable cause) {
		super(msg);
		initCause(cause);
	}
	public JspTagException(String msg) {
		super(msg);
	}
	public JspTagException(Throwable cause) {
		initCause(cause);
	}
	public JspTagException() {
	}

	/**
	 * Constructs an JspTagException by use of an error code.
	 * The error code must be defined in
	 * one of properties files, e.g., msgsys.properties.
	 *
	 * @param code the error code
	 * @param fmtArgs the format arguments
	 * @param cause the chained throwable object
	 */
	public JspTagException(int code, Object[] fmtArgs, Throwable cause) {
		super(Exceptions.getMessage(code, fmtArgs));
		initCause(cause);
		_code = code;
	}
	public JspTagException(int code, Object fmtArg, Throwable cause) {
		super(Exceptions.getMessage(code, fmtArg));
		initCause(cause);
		_code = code;
	}
	public JspTagException(int code, Object[] fmtArgs) {
		super(Exceptions.getMessage(code, fmtArgs));
		_code = code;
	}
	public JspTagException(int code, Object fmtArg) {
		super(Exceptions.getMessage(code, fmtArg));
		_code = code;
	}
	public JspTagException(int code, Throwable cause) {
		super(Exceptions.getMessage(code, null));
		initCause(cause);
		_code = code;
	}
	public JspTagException(int code) {
		super(Exceptions.getMessage(code, null));
		_code = code;
	}

	//-- Messageable --//
	public final int getCode() {
		return _code;
	}
}
