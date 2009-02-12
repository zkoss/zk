/* SystemException.java

{{IS_NOTE

	Purpose: Thrown if a caught exception is not in the exception list.
	Description: 
	History:
	 2001/5/15, Tom M. Yeh: Created.

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
 * Indicates a system exception.
 *
 * @author tomyeh
 */
public class SystemException extends RuntimeException implements Messageable {
	/** Utilities.
	 *
	 * <p>The reason to use a class to hold static utilities is we can
	 * override the method's return type later.
	 */
	public static class Aide {
		/** Converts an exception to SystemException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static SystemException wrap(Throwable t) {
			t = Exceptions.unwrap(t);
			if (t instanceof Warning)
				return (WarningException)
					Exceptions.wrap(t, WarningException.class);
			if (t instanceof Expectable)
				return (OperationException)
					Exceptions.wrap(t, OperationException.class);
			return (SystemException)
				Exceptions.wrap(t, SystemException.class);
		}
		/** Converts an exception to SystemException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static SystemException wrap(Throwable t, String msg) {
			t = Exceptions.unwrap(t);
			if (t instanceof Warning)
				return (WarningException)
					Exceptions.wrap(t, WarningException.class, msg);
			if (t instanceof Expectable)
				return (OperationException)
					Exceptions.wrap(t, OperationException.class, msg);
			return (SystemException)
				Exceptions.wrap(t, SystemException.class, msg);
		}
		/** Converts an exception to SystemException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static SystemException wrap(Throwable t, int code, Object[] fmtArgs) {
			t = Exceptions.unwrap(t);
			if (t instanceof Warning)
				return (WarningException)
					Exceptions.wrap(t, WarningException.class, code, fmtArgs);
			if (t instanceof Expectable)
				return (OperationException)
					Exceptions.wrap(t, OperationException.class, code, fmtArgs);
			return (SystemException)
				Exceptions.wrap(t, SystemException.class, code, fmtArgs);
		}
		/** Converts an exception to SystemException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static SystemException wrap(Throwable t, int code, Object fmtArg) {
			t = Exceptions.unwrap(t);
			if (t instanceof Warning)
				return (WarningException)
					Exceptions.wrap(t, WarningException.class, code, fmtArg);
			if (t instanceof Expectable)
				return (OperationException)
					Exceptions.wrap(t, OperationException.class, code, fmtArg);
			return (SystemException)
				Exceptions.wrap(t, SystemException.class, code, fmtArg);
		}
		/** Converts an exception to SystemException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static SystemException wrap(Throwable t, int code) {
			t = Exceptions.unwrap(t);
			if (t instanceof Warning)
				return (WarningException)
					Exceptions.wrap(t, WarningException.class, code);
			if (t instanceof Expectable)
				return (OperationException)
					Exceptions.wrap(t, OperationException.class, code);
			return (SystemException)
				Exceptions.wrap(t, SystemException.class, code);
		}
	}

	protected int _code = NULL_CODE;

	/**
	 * Constructs a SystemException by specifying message directly.
	 */
	public SystemException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public SystemException(String msg) {
		super(msg);
	}
	public SystemException(Throwable cause) {
		super(cause);
	}
	public SystemException() {
	}

	/**
	 * Constructs an SystemException by use of an error code.
	 * The error code must be defined in
	 * one of properties files, e.g., msgsys.properties.
	 *
	 * @param code the error code
	 * @param fmtArgs the format arguments
	 * @param cause the chained throwable object
	 */
	public SystemException(int code, Object[] fmtArgs, Throwable cause) {
		super(Exceptions.getMessage(code, fmtArgs), cause);
		_code = code;
	}
	public SystemException(int code, Object fmtArg, Throwable cause) {
		super(Exceptions.getMessage(code, fmtArg), cause);
		_code = code;
	}
	public SystemException(int code, Object[] fmtArgs) {
		super(Exceptions.getMessage(code, fmtArgs));
		_code = code;
	}
	public SystemException(int code, Object fmtArg) {
		super(Exceptions.getMessage(code, fmtArg));
		_code = code;
	}
	public SystemException(int code, Throwable cause) {
		super(Exceptions.getMessage(code), cause);
		_code = code;
	}
	public SystemException(int code) {
		super(Exceptions.getMessage(code));
		_code = code;
	}

	//-- Messageable --//
	public final int getCode() {
		return _code;
	}
}
