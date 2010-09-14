/* Exceptions.java


	Purpose: Utilities for Exceptions
	Description: 
	History:
	 2001/4/22, Tom M. Yeh: Created.


Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.lang;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.SQLException;

import org.zkoss.lang.D;
import org.zkoss.mesg.MCommon;
import org.zkoss.mesg.Messages;
import org.zkoss.util.logging.Log;

/**
 * Utilities for Exceptions.
 *
 * @author tomyeh
 */
public class Exceptions {
	private static final Log log = Log.lookup(Exceptions.class);

	/**
	 * Finds the causes of an exception, ex, to see whether
	 * any of them is the givinge type.
	 *
	 * @return the cause if found; null if not found
	 */
	public static final Throwable findCause(Throwable ex, Class<?> cause) {
		while (ex != null) {
			if (cause.isInstance(ex))
				return ex;

			ex = getCause(ex);
		}
		return null;
	}
	/** Returns the cause of the given throwable. It is the same as
	 * t.getCause, but it solves the compatibility of J2EE that might not
	 * support JDK 1.4.
	 */
	public static final Throwable getCause(Throwable ex) {
		Throwable t = ex.getCause();
		if (t == null)
			try {
				if (ex instanceof java.rmi.RemoteException) {
					return ((java.rmi.RemoteException)ex).detail;
				} else if (ex instanceof org.xml.sax.SAXException) {
					return ((org.xml.sax.SAXException)ex).getException();
				} else if (ex instanceof javax.servlet.ServletException) {
					return ((javax.servlet.ServletException)ex).getRootCause();
				} else if (ex instanceof bsh.TargetError) {
					return ((bsh.TargetError)ex).getTarget();
				} else if (ex instanceof bsh.UtilTargetError) {
					return ((bsh.UtilTargetError)ex).t;
//Remove the dependence on EJB
//				} else if (ex instanceof javax.ejb.EJBException) {
//					return ((javax.ejb.EJBException)ex).getCausedByException();
//Remove the dependence on JSP
//				} else if (ex instanceof javax.servlet.jsp.JspException) {
//					return ((javax.servlet.jsp.JspException)ex).getRootCause();
//Remove the dependence on EL
//				} else if (ex instanceof javax.servlet.jsp.el.ELException) {
//					return ((javax.servlet.jsp.el.ELException)ex).getRootCause();
				}
			} catch (Throwable e2) {
				if (log.debugable()) log.debug("Ignored: unable to resolve " + ex.getClass());
			}
		return t;
	}
	/**
	 * Unveils the real cause. A throwable object is called a real cause,
	 * if it doesn't have any cause (or called chained throwable).
	 *
	 * @param ex the throwable
	 * @return the real cause; ex itself if it is already the real cause
	 * @see #wrap
	 */
	public static final Throwable getRealCause(Throwable ex) {
		while (true) {
			Throwable cause = getCause(ex);
			if (cause == null)
				return ex;
			ex = cause;
		}
	}
	/**
	 * Converts an exception to the sepecified class. If any of causes
	 * is the specified class, the cause is returned. If the giving exception
	 * is RuntimeException or error, it is re-thrown directly. In other words,
	 * Unlike {@link #findCause}, it is designed to be used in the
	 * <code>throw</code> statement as follows.
	 *
	 * <p>Usage 1: one kind of exception is examined.
	 * <pre><code>
	 * try {
	 *   ...
	 * } catch (Exception ex) {
	 *   throw (MyException)Exceptions.wrap(ex, MyException.class);
	 * }</code></pre>
	 *
	 * <p>Usage 2: two kinds of exceptions are examined.
	 * <pre><code>
	 * try {
	 *   ...
	 * } catch (Exception ex) {
	 *   Throwable t = Exceptions.findCause(ex, AnotherException.class);
	 *   if (t != null)
	 *      throw (AnotherException)t;
	 *   throw (MyException)Exceptions.wrap(ex, MyException.class);
	 * }</code></pre>
	 *
	 * <p>Usage 3: two kinds of exceptions are examined.
	 * <pre><code>
	 * try {
	 *   ...
	 * } catch (AnotherException ex) {
	 *   throw ex;
	 * } catch (Exception ex) {
	 *   throw (MyException)Exceptions.wrap(ex, MyException.class);
	 * }</code></pre>
	 *
	 * <p>If you already know the exception, you don't need this method
	 * -- AnotherException won't never be RuntimeException or MyException.
	 * <pre><code>
	 * try {
	 *   ...
	 * } catch (AnotherException ex) {
	 *   throw new MyException(ex);
	 * }</code></pre>
	 *
	 * <p>Note: It assumes the exception has the constructor:
	 * <code>MyException(Throwable cause);</code>
	 *
	 * @param targetExceptCls the class of exception to be converted to
	 * @param ex the exception being caught (and to be converted)
	 * @see #getRealCause
	 * @see SystemException.Aide#wrap
	 */
	public static final
	Throwable wrap(Throwable ex, Class<? extends Throwable> targetExceptCls) {
		ex = myToAnother(ex, targetExceptCls);
		if (targetExceptCls.isInstance(ex))
			return ex;

		try {
			return (Throwable)Classes.newInstance(targetExceptCls,
				new Class[] {Throwable.class}, new Object[] {ex});
		} catch (Exception e2) {
			log.warning("Unable to wrap an exception in " + targetExceptCls, e2);
			throw new SystemException(ex);
				//avoid dead-loop; don't use SystemException.Aide.wrap
		}
	}
	private static final Throwable
	myToAnother(Throwable ex, Class<? extends Throwable> targetExceptCls) {
		assert D.OFF || ex != null: "null exception";

		if (ex instanceof InvocationTargetException)
			ex = ex.getCause(); //might returns UndeclaredThrowableException
		if (ex instanceof UndeclaredThrowableException)
			ex = ex.getCause();

		//NOTE: In EJB, runtime exceptions means rolls back, so (1) some app
		//might intercept them with non-runtime, (2) you don't want runtime
		//being wrapped here unexpectedly.
		if (ex instanceof RuntimeException)
			throw (RuntimeException)ex;
		if (ex instanceof Error)
			throw (Error)ex;

		for (Throwable t = ex; t != null; t = getCause(t))
			if (targetExceptCls.isInstance(t))
				return t;

		return ex;
	}
	/**
	 * Converts an exception to the sepecified class plus a message.
	 * It is similar to {@link #wrap(Throwable, Class)} but with
	 * an additional message. Thus, the target exception class must has
	 * the constructor: <code>MyException(String msg, Throwable cause);</code>
	 */
	public static final
	Throwable wrap(Throwable ex, Class<? extends Throwable> targetExceptCls, String msg) {
		ex = myToAnother(ex, targetExceptCls);
		if (targetExceptCls.isInstance(ex))
			return ex;

		try {
			return (Throwable)Classes.newInstance(targetExceptCls,
				new Class[] {String.class, Throwable.class},
				new Object[] {msg, ex});
		} catch (Exception e2) {
			log.warning("Unable to wrap an exception in " + targetExceptCls, e2);
			throw new SystemException(ex);
				//avoid dead-loop; don't use SystemException.Aide.wrap
		}
	}
	/**
	 * Converts an exception to the sepecified class plus a message.
	 * It is similar to {@link #wrap(Throwable, Class)} but with
	 * an additional message code. Thus, the target exception class must has
	 * the constructor:
	 * <code>MyException(int code, Object[] fmtArgs, Throwable cause);</code>
	 */
	public static final Throwable
	wrap(Throwable ex, Class<? extends Throwable> targetExceptCls, int code, Object[] fmtArgs) {
		ex = myToAnother(ex, targetExceptCls);
		if (targetExceptCls.isInstance(ex))
			return ex;

		try {
			return (Throwable)Classes.newInstance(targetExceptCls,
				new Class[] {int.class, Object[].class, Throwable.class},
				new Object[] {new Integer(code), fmtArgs, ex});
		} catch (Exception e2) {
			log.warning("Unable to wrap an exception in " + targetExceptCls, e2);
			throw new SystemException(ex);
				//avoid dead-loop; don't use SystemException.Aide.wrap
		}
	}
	/**
	 * Converts an exception to the sepecified class plus a message.
	 * It is similar to {@link #wrap(Throwable, Class)} but with
	 * an additional message code. Thus, the target exception class must has
	 * the constructor:
	 * <code>MyException(int code, Object fmtArgs, Throwable cause);</code>
	 */
	public static final Throwable
	wrap(Throwable ex, Class<? extends Throwable> targetExceptCls, int code, Object fmtArg) {
		ex = myToAnother(ex, targetExceptCls);
		if (targetExceptCls.isInstance(ex))
			return ex;

		try {
			return (Throwable)Classes.newInstance(targetExceptCls,
				new Class[] {int.class, Object.class, Throwable.class},
				new Object[] {new Integer(code), fmtArg, ex});
		} catch (Exception e2) {
			log.warning("Unable to wrap an exception in " + targetExceptCls, e2);
			throw new SystemException(ex);
				//avoid dead-loop; don't use SystemException.Aide.wrap
		}
	}
	/**
	 * Converts an exception to the sepecified class plus a message.
	 * It is similar to {@link #wrap(Throwable, Class)} but with
	 * an additional message code. Thus, the target exception class must has
	 * the constructor:
	 * <code>MyException(int code, Throwable cause);</code>
	 */
	public static final Throwable
	wrap(Throwable ex, Class<? extends Throwable> targetExceptCls, int code) {
		ex = myToAnother(ex, targetExceptCls);
		if (targetExceptCls.isInstance(ex))
			return ex;

		try {
			return (Throwable)Classes.newInstance(targetExceptCls,
				new Class[] {int.class, Throwable.class},
				new Object[] {new Integer(code), ex});
		} catch (Exception e2) {
			log.warning("Unable to wrap an exception in " + targetExceptCls, e2);
			throw new SystemException(ex);
				//avoid dead-loop; don't use SystemException.Aide.wrap
		}
	}

	/** Unwraps an exception if the enclosing one is InvocationTargetException
	 * or UndeclaredThrowableException.
	 * <p>Use it if you are catching exceptions thrown by Method.invoke().
	 */
	public static final Throwable unwrap(Throwable ex) {
		for (;;) {
			if (ex instanceof InvocationTargetException)
				ex = ex.getCause(); //might returns UndeclaredThrowableException
			else if (ex instanceof UndeclaredThrowableException)
				ex = ex.getCause();
			else {
				try {
					if (ex instanceof bsh.TargetError) {
						final Throwable t = ((bsh.TargetError)ex).getTarget();
						if (t != null) ex = t;
					} else if (ex instanceof bsh.UtilTargetError) {
						final Throwable t = ((bsh.UtilTargetError)ex).t;
						if (t != null) ex = t;
					}
				} catch (Throwable e2) {
					if (log.debugable()) log.debug("Ignored: unable to resolve " + ex.getClass());
				}
//Remove the dependence on EL
/*				try {
					if (ex instanceof javax.servlet.jsp.el.ELException) {
						final Throwable t =
							((javax.servlet.jsp.el.ELException)ex).getRootCause();
						if (t != null) ex = t;
					}
				} catch (Throwable e2) {
					if (log.debugable()) log.debug("Ignored: unable to resolve " + ex.getClass());
				}*/
				return ex;
			}
			assert D.OFF || ex != null: "null cause";
		}
	}
	/**
	 * Returns the extra message, or null if no extra.
	 *
	 * <p>Currently, it handles only SQLException
	 */
	public static final String getExtraMessage(Throwable ex) {
		ex = findCause(ex, SQLException.class);
		if (ex != null) {
			SQLException e = (SQLException)ex;
			return "[SQL: " + e.getErrorCode() + ", " + e.getSQLState() + ']';
		}
		return null;
	}
	/** Returns a message of the exception.
	 */
	public static final String getMessage(Throwable ex) {
		String s;
		for (Throwable t = ex;;) {
			s = t.getMessage();
			if (s != null && s.length() > 0)
				break; //found

			t = getCause(t);
			if (t == null) {
				s = Messages.get(MCommon.UNKNOWN_EXCEPTION, ex.getClass().getName());
				break; //failed
			}
		}
		final String s2 = getExtraMessage(ex);
		return s2 != null ? s + s2: s;
	}

	/**
	 * Gets the message for an exception's getMessage method.
	 *
	 * <p>It actually delegates to Messages, and then
	 * appends an error code.
	 */
	public static final String getMessage(int code, Object[] fmtArgs) {
		return Messages.get(code, fmtArgs);
	}
	/**
	 * Gets the message for an exception with one format-argument.
	 */
	public static final String getMessage(int code, Object fmtArg) {
		return getMessage(code, new Object[] {fmtArg});
	}
	/**
	 * Gets the message for an exception without format-argument.
	 */
	public static final String getMessage(int code) {
		return getMessage(code, (Object[])null);
	}

	/** Returns the first few lines of the stack trace.
	 * It is useful to make the cause exception's stacktrace to be
	 * part of the message of the exception thrown to the user.
	 */
	public static final String getBriefStackTrace(Throwable t) {
		return formatStackTrace(null, t, ">>", 6).toString();
	}
	/** Formats the stack trace and returns the result.
	 * Currently, it only adds the prefix to each line.
	 *
	 * @param prefix the prefix shown in front of each line of the stack trace;
	 * null to denote empty
	 * @see org.zkoss.util.logging.Log#realCause
	 */
	public static final String formatStackTrace(Throwable t, String prefix) {
		return formatStackTrace(null, t, prefix).toString();
	}
	/** Formats the stack trace and appends it to the specified string buffer.
	 * @param sb the string buffer to append the stack trace. A string buffer
	 * will be created if null.
	 * @param prefix the prefix shown in front of each line of the stack trace;
	 * null to denote empty
	 */
	public static final StringBuffer
	formatStackTrace(StringBuffer sb, Throwable t, String prefix) {
		return formatStackTrace(sb, t, prefix, 0);
	}
	/** Formats the stack trace and appends it to the specified string buffer,
	 * but only display at most maxcnt lines.
	 *
	 * <p>The maximal allowed number of lines is controlled by
	 * maxcnt. Note: a stack frame is not counted, if it belongs
	 * to java.*, javax.* or sun.*.
	 *
	 * @param sb the string buffer to append the stack trace. A string buffer
	 * will be created if null.
	 * @param prefix the prefix shown in front of each line of the stack trace;
	 * null to denote empty
	 * @param maxcnt the maximal allowed number of lines to dump (<=0: no limit)
	 */
	public static final StringBuffer
	formatStackTrace(StringBuffer sb, Throwable t, String prefix, int maxcnt) {
		final StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		final StringBuffer trace = sw.getBuffer();

		if (prefix == null) prefix = "";
		if (maxcnt > 0 || prefix.length() > 0) {
			final int len = trace.length();
			if (sb == null)
			 	sb = new StringBuffer(len + 256);
			if (maxcnt <= 0)
				maxcnt = Integer.MAX_VALUE;
			boolean ignoreCount = false;
			for (int j = 0; j < len;) { //for each line
				if (!ignoreCount && --maxcnt < 0) {
					sb.append(prefix).append("...");
					break;
				}

				//StringBuffer has no indexOf(char,j), so...
				int k = j;
				while (k < len && trace.charAt(k++) != '\n')
					; //point k to the char after \n

				String frame = trace.substring(j, k);
				sb.append(prefix).append(frame);
				j = k;

				ignoreCount = inStack(frame, "java.")
					|| inStack(frame, "javax.") || inStack(frame, "sun.")
					|| inStack(frame, "bsh.");
			}
		} else {
			if (sb == null)
				return trace;
			sb.append(trace);
		}
		return sb;
	}
	private static boolean inStack(String frame, String sub) {
		final int j = frame.indexOf(sub);
		if (j < 0) return false;
		if (j == 0) return true;

		final char cc = frame.charAt(j - 1);
		return (cc < 'a' || cc > 'z') && (cc < 'A' || cc > 'Z');
	}
}
