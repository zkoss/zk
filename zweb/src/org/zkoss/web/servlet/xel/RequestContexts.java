/* RequestContexts.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Apr  8 12:16:23     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.xel;

import java.util.List;
import java.util.LinkedList;

/**
 * RequestContexts maintains a stack of {@link RequestContext} to simplify
 * the signatures of the XEL function.
 *
 * <p>It is designed to make the signature of XEL functions
 * (see {@link org.zkoss.web.fn.ServletFns}) simpler.
 * For example, {@link org.zkoss.web.fn.ServletFns#isExplorer} requires
 * no argument, since it assumes the current context can be retrieved
 * from {@link #getCurrent}.
 *
 * <p>Spec Issue:<br/>
 * It is controversial whether the introduction of {@link RequestContext} and
 * {@link RequestContexts} is worth. However, we have to maintain the backward
 * compatibility of the XEL/EL function signatures.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class RequestContexts {
	protected RequestContexts() {} //prevent from instantiated

	/** A list of RequestContext. */
	private static final ThreadLocal _elCtxs = new ThreadLocal();

	/** Returns the current page context if this thread is evaluating a page,
	 * or null if not.
	 */
	public static final RequestContext getCurrent() {
		final List jcs = (List)_elCtxs.get();
		return jcs != null && !jcs.isEmpty() ? (RequestContext)jcs.get(0): null;
	}

	/** Pushes the context as the current context, such that it will
	 * be returned by {@link #getCurrent}. The reason this method exists is
	 * many functions ({@link org.zkoss.web.fn.ServletFns}) counts on it.
	 *
	 * <p>However, you don't need to invoke this method if you are using
	 * DSP.
	 * <ol>
	 * <li>If go thru DSP, it is done automatically
	 * (by {@link org.zkoss.web.servlet.dsp.Interpreter}</li>
	 * </ol>
	 *
	 * <p>Note: you must use try/finally as follows:
	 * <pre><code>RequestContexts.push(jc);
	 *try {
	 *  ...
	 *} finally {
	 *  RequestContexts.pop();
	 *}</code></pre>
	 */
	public static final void push(RequestContext jc) {
		if (jc == null)
			throw new IllegalArgumentException("null");

		List jcs = (List)_elCtxs.get();
		if (jcs == null)
			_elCtxs.set(jcs = new LinkedList());
		jcs.add(0, jc);
	}
	/** Pops the context out and use the previous one as the current context.
	 *
	 * <p>However, you don't need to invoke this method if you are using
	 * DSP.
	 *
	 * @see #push
	 */
	public static final void pop() {
		((List)_elCtxs.get()).remove(0);
	}
}
