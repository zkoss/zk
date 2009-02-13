/* DspException.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 10:06:53     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp;

import org.zkoss.lang.SystemException;
import org.zkoss.lang.Exceptions;

/**
 * Represents a DSP exception.
 *
 * @author tomyeh
 * @since 3.0.7
 */
public class DspException extends SystemException {
	/** Utilities.
	 *
	 * <p>The reason to use a class to hold static utilities is we can
	 * override the method's return type later.
	 */
	public static class Aide {
		/** Converts an exception to DspException if it is
		 * not RuntimeException nor Error.
		 * @see Exceptions#wrap
		 */
		public static DspException wrap(Throwable t) {
			return (DspException)Exceptions.wrap(t, DspException.class);
		}
		/** Converts an exception to DspException if it is
		 * not RuntimeException nor Error.
		 * @see Exceptions#wrap
		 */
		public static DspException wrap(Throwable t, String msg) {
			return (DspException)Exceptions.wrap(t, DspException.class, msg);
		}
		/** Converts an exception to DspException if it is
		 * not RuntimeException nor Error.
		 * @see Exceptions#wrap
		 */
		public static DspException wrap(Throwable t, int code, Object[] fmtArgs) {
			return (DspException)Exceptions.wrap(t, DspException.class, code, fmtArgs);
		}
		/** Converts an exception to DspException if it is
		 * not RuntimeException nor Error.
		 * @see Exceptions#wrap
		 */
		public static DspException wrap(Throwable t, int code, Object fmtArg) {
			return (DspException)Exceptions.wrap(t, DspException.class, code, fmtArg);
		}
		/** Converts an exception to DspException if it is
		 * not RuntimeException nor Error.
		 * @see Exceptions#wrap
		 */
		public static DspException wrap(Throwable t, int code) {
			return (DspException)Exceptions.wrap(t, DspException.class, code);
		}
	}

	public DspException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public DspException(String s) {
		super(s);
	}
	public DspException(Throwable cause) {
		super(cause);
	}
	public DspException() {
	}

	public DspException(int code, Object[] fmtArgs, Throwable cause) {
		super(code, fmtArgs, cause);
	}
	public DspException(int code, Object fmtArg, Throwable cause) {
		super(code, fmtArg, cause);
	}
	public DspException(int code, Object[] fmtArgs) {
		super(code, fmtArgs);
	}
	public DspException(int code, Object fmtArg) {
		super(code, fmtArg);
	}
	public DspException(int code, Throwable cause) {
		super(code, cause);
	}
	public DspException(int code) {
		super(code);
	}
}
