/* UiException.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan 20 15:47:23     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import org.zkoss.lang.SystemException;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.Expectable;

/**
 * Represents an UI-relevant runtime exception.
 * 
 * @author tomyeh
 */
public class UiException extends SystemException {
	/** Utilities to wrap {@link UiException}.
	 *
	 * <p>The reason to use a class to hold static utilities is we can
	 * override the method's return type later.
	 */
	public static class Aide {
		/** Converts an exception to UiException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static UiException wrap(Throwable t) {
			t = Exceptions.unwrap(t);
			if (t instanceof Expectable)
				return (OperationException)
					Exceptions.wrap(t, OperationException.class);
			return (UiException)
				Exceptions.wrap(t, UiException.class);
		}
		/** Converts an exception to UiException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static UiException wrap(Throwable t, String msg) {
			t = Exceptions.unwrap(t);
			if (t instanceof Expectable)
				return (OperationException)
					Exceptions.wrap(t, OperationException.class, msg);
			return (UiException)
				Exceptions.wrap(t, UiException.class, msg);
		}
		/** Converts an exception to UiException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static UiException wrap(Throwable t, int code, Object[] fmtArgs) {
			t = Exceptions.unwrap(t);
			if (t instanceof Expectable)
				return (OperationException)
					Exceptions.wrap(t, OperationException.class, code, fmtArgs);
			return (UiException)
				Exceptions.wrap(t, UiException.class, code, fmtArgs);
		}
		/** Converts an exception to UiException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static UiException wrap(Throwable t, int code, Object fmtArg) {
			t = Exceptions.unwrap(t);
			if (t instanceof Expectable)
				return (OperationException)
					Exceptions.wrap(t, OperationException.class, code, fmtArg);
			return (UiException)
				Exceptions.wrap(t, UiException.class, code, fmtArg);
		}
		/** Converts an exception to UiException or OperationException
		 * depending on whether t implements Expetable.
		 * @see Exceptions#wrap
		 */
		public static UiException wrap(Throwable t, int code) {
			t = Exceptions.unwrap(t);
			if (t instanceof Expectable)
				return (OperationException)
					Exceptions.wrap(t, OperationException.class, code);
			return (UiException)
				Exceptions.wrap(t, UiException.class, code);
		}
	}

	public UiException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public UiException(String s) {
		super(s);
	}
	public UiException(Throwable cause) {
		super(cause);
	}
	public UiException() {
	}

	public UiException(int code, Object[] fmtArgs, Throwable cause) {
		super(code, fmtArgs, cause);
	}
	public UiException(int code, Object fmtArg, Throwable cause) {
		super(code, fmtArg, cause);
	}
	public UiException(int code, Object[] fmtArgs) {
		super(code, fmtArgs);
	}
	public UiException(int code, Object fmtArg) {
		super(code, fmtArg);
	}
	public UiException(int code, Throwable cause) {
		super(code, cause);
	}
	public UiException(int code) {
		super(code);
	}
}
