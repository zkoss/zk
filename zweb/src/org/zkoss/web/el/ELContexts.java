/* ELContexts.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Apr  8 12:16:23     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.el;

import java.util.List;
import java.util.LinkedList;
import java.io.Writer;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.zkoss.lang.D;
import org.zkoss.util.logging.Log;
import org.zkoss.el.PageContext;

/**
 * Utilities to access the JSP context.
 *
 * @author tomyeh
 */
public class ELContexts {
//	private static final Log log = Log.lookup(ELContexts.class);

	protected ELContexts() {} //prevent from instantiated

	/** A list of ELContext. */
	private static final ThreadLocal _elCtxs = new ThreadLocal();

	/** Returns the current page context if this thread is evaluating a page,
	 * or null if not.
	 */
	public static final ELContext getCurrent() {
		final List jcs = (List)_elCtxs.get();
		return jcs != null && !jcs.isEmpty() ? (ELContext)jcs.get(0): null;
	}

	/** Pushes the context as the current context, such that it will
	 * be returned by {@link #getCurrent}. The reason this method exists is
	 * many functions ({@link org.zkoss.web.fn.ServletFns}) counts on it.
	 *
	 * <p>However, you rarely need to invoke this method directly.
	 * <ol>
	 * <li>If go thru DSP, it is done automatically
	 * (by {@link org.zkoss.web.servlet.dsp.Interpreter}</li>
	 * </ol>
	 *
	 * <p>Note: you must use try/finally as follows:
	 * <pre><code>ELContexts.push(jc);
	 *try {
	 *  ...
	 *} finally {
	 *  ELContexts.pop();
	 *}</code></pre>
	 */
	public static final void push(ELContext jc) {
		if (jc == null)
			throw new IllegalArgumentException("null");

		List jcs = (List)_elCtxs.get();
		if (jcs == null)
			_elCtxs.set(jcs = new LinkedList());
		jcs.add(0, jc);
	}
	/** Pushs a page context to be {@link ELContext}.
	 */
	public static final void push(final PageContext pc) {
		push(new PageELContext(pc));
	}
	/** Pops the context out and use the previous one as the current context.
	 *
	 * <p>In most cases, you don't need to invoke this method, which is
	 * done automatically.
	 *
	 * @see #push
	 */
	public static final void pop() {
		((List)_elCtxs.get()).remove(0);
	}
}
