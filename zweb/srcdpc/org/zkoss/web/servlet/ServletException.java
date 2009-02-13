/* ServletException.java

{{IS_NOTE
	Purpose: 
	Description: 
	History:
		2001/11/13 20:17:38, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet;

import org.zkoss.lang.Expectable;
import org.zkoss.lang.Exceptions;
import org.zkoss.mesg.Messageable;

/**
 * @deprecated As of release 3.0.7, no longer used.
 *
 * @author tomyeh
 */
public class ServletException extends javax.servlet.ServletException
implements Messageable {
	/** Utilities.
	 *
	 * <p>The reason to use a class to hold static utilities is we can
	 * override the method's return type later.
	 */
	public static class Aide {
		/** Converts an exception to ServletException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static javax.servlet.ServletException wrap(Throwable t) {
			t = Exceptions.unwrap(t);
			if (t instanceof javax.servlet.ServletException)
				return (javax.servlet.ServletException)t;
			if (t instanceof Expectable)
				return (OperationException)
					Exceptions.wrap(t, OperationException.class);
			return (ServletException)
				Exceptions.wrap(t, ServletException.class);
		}
		/** Converts an exception to ServletException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static ServletException wrap(Throwable t, String msg) {
			t = Exceptions.unwrap(t);
			if (t instanceof Expectable)
				return (OperationException)
					Exceptions.wrap(t, OperationException.class, msg);
			return (ServletException)
				Exceptions.wrap(t, ServletException.class, msg);
		}
		/** Converts an exception to ServletException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static ServletException wrap(Throwable t, int code, Object[] fmtArgs) {
			t = Exceptions.unwrap(t);
			if (t instanceof Expectable)
				return (OperationException)
					Exceptions.wrap(t, OperationException.class, code, fmtArgs);
			return (ServletException)
				Exceptions.wrap(t, ServletException.class, code, fmtArgs);
		}
		/** Converts an exception to ServletException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static ServletException wrap(Throwable t, int code, Object fmtArg) {
			t = Exceptions.unwrap(t);
			if (t instanceof Expectable)
				return (OperationException)
					Exceptions.wrap(t, OperationException.class, code, fmtArg);
			return (ServletException)
				Exceptions.wrap(t, ServletException.class, code, fmtArg);
		}
		/** Converts an exception to ServletException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static ServletException wrap(Throwable t, int code) {
			t = Exceptions.unwrap(t);
			if (t instanceof Expectable)
				return (OperationException)
					Exceptions.wrap(t, OperationException.class, code);
			return (ServletException)
				Exceptions.wrap(t, ServletException.class, code);
		}
	}

	protected int _code = NULL_CODE;

	public ServletException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public ServletException(String msg) {
		super(msg);
	}
	public ServletException(int code, Object[] fmtArgs, Throwable cause) {
		super(Exceptions.getMessage(code, fmtArgs), cause);
		_code = code;
	}
	public ServletException(int code, Object fmtArg, Throwable cause) {
		super(Exceptions.getMessage(code, fmtArg), cause);
		_code = code;
	}
	public ServletException(int code, Object[] fmtArgs) {
		super(Exceptions.getMessage(code, fmtArgs));
		_code = code;
	}
	public ServletException(int code, Object fmtArg) {
		super(Exceptions.getMessage(code, fmtArg));
		_code = code;
	}
	public ServletException(int code, Throwable cause) {
		super(Exceptions.getMessage(code, null), cause);
		_code = code;
	}
	public ServletException(int code) {
		super(Exceptions.getMessage(code, null));
		_code = code;
	}
	public ServletException(Throwable cause) {
		super(cause);
	}
	public ServletException() {
	}

	//-- Messageable --//
	public final int getCode() {
		return _code;
	}

	//-- override --//
	private transient boolean _getCause = false;
	public Throwable getCause() {
		//The case: ServletException implements getRootCause
		//by returning getCause -> it causes a dead loop
		if (_getCause) //recursive back?
			return null;

		//The case: ServletException has implemented getCause (1.4-ware)
		Throwable t = super.getCause();
		if (t != null)
			return t;

		try {
			_getCause = true;
			return getRootCause();
		} finally {
			_getCause = false;
		}
	}
}
