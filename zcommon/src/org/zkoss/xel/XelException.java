/* XelException.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 10:06:53     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel;

import org.zkoss.lang.SystemException;
import org.zkoss.lang.Exceptions;

/**
 * Represents a XEL exception.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class XelException extends SystemException {
	/** Utilities.
	 *
	 * <p>The reason to use a class to hold static utilities is we can
	 * override the method's return type later.
	 */
	public static class Aide {
		/** Converts an exception to XelException if it is
		 * not RuntimeException nor Error.
		 * @see Exceptions#wrap
		 */
		public static XelException wrap(Throwable t) {
			return (XelException)Exceptions.wrap(t, XelException.class);
		}
		/** Converts an exception to XelException if it is
		 * not RuntimeException nor Error.
		 * @see Exceptions#wrap
		 */
		public static XelException wrap(Throwable t, String msg) {
			return (XelException)Exceptions.wrap(t, XelException.class, msg);
		}
		/** Converts an exception to XelException if it is
		 * not RuntimeException nor Error.
		 * @see Exceptions#wrap
		 */
		public static XelException wrap(Throwable t, int code, Object[] fmtArgs) {
			return (XelException)Exceptions.wrap(t, XelException.class, code, fmtArgs);
		}
		/** Converts an exception to XelException if it is
		 * not RuntimeException nor Error.
		 * @see Exceptions#wrap
		 */
		public static XelException wrap(Throwable t, int code, Object fmtArg) {
			return (XelException)Exceptions.wrap(t, XelException.class, code, fmtArg);
		}
		/** Converts an exception to XelException if it is
		 * not RuntimeException nor Error.
		 * @see Exceptions#wrap
		 */
		public static XelException wrap(Throwable t, int code) {
			return (XelException)Exceptions.wrap(t, XelException.class, code);
		}
	}

	public XelException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public XelException(String s) {
		super(s);
	}
	public XelException(Throwable cause) {
		super(cause);
	}
	public XelException() {
	}

	public XelException(int code, Object[] fmtArgs, Throwable cause) {
		super(code, fmtArgs, cause);
	}
	public XelException(int code, Object fmtArg, Throwable cause) {
		super(code, fmtArg, cause);
	}
	public XelException(int code, Object[] fmtArgs) {
		super(code, fmtArgs);
	}
	public XelException(int code, Object fmtArg) {
		super(code, fmtArg);
	}
	public XelException(int code, Throwable cause) {
		super(code, cause);
	}
	public XelException(int code) {
		super(code);
	}
}
