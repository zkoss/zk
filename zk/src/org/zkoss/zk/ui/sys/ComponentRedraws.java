/* ComponentRedraws.java

	Purpose:
		
	Description:
		
	History:
		Sun Mar 21 12:19:31 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.sys;

import java.util.List;
import java.util.LinkedList;

import org.zkoss.util.logging.Log;

/**
 * Utilities to implement {@link ComponentCtrl#redraw}.
 * Like {@link HtmlPageRenders#getRenderContext}, they are used to simplify
 * the implementation of the component rendering. However, there are
 * some differences:
 * <ul>
 * <li>{@link #beforeRedraw} and {@link #afterRedraw} can be called
 * even without an execution (e.g., in a working thread).</li>
 * </ul>
 * @author tomyeh
 * @since 5.0.2
 */
public class ComponentRedraws {
	private static final Log log = Log.lookup(ComponentRedraws.class);

	/** Called before staring the redrawing.
	 * {@link org.zkoss.zk.ui.AbstractComponent#redraw} calls this method
	 * before calling {@link org.zkoss.zk.ui.AbstractComponent#renderProperties}
	 * and {@link org.zkoss.zk.ui.AbstractComponent#redrawChildren}.
	 * <p>If this method is called, {@link #afterRedraw} must be called in
	 * the finally clause:
	 * <pre><code>ComponentRedraws.beforeRedraw(false);
	 *try {
	 *  ...
	 *} finally {
	 *  ComponentRedraws.afterRedraw();
	 *}</code></pre>
	 *
	 * @param includingPage whether this invocation is caused to include a new page.
	 * Unless for implementing an include component, this parameter shall be false.
	 * @return the order of redrawing of the component being redrawn among its
	 * siblings.
	 * If 0, it means it is the first sibling. If 1, it is the 2nd sibling.
	 * If -1, it means it is the top-level component in this redrawing.
	 * In other words, it is the component that {@link ComponentCtrl#redraw}
	 * is called against.
	 */
	public static final int beforeRedraw(boolean includingPage) {
		final int order;
		final List states;
		final Object[] ctx = (Object[])_ctx.get();
		if (ctx == null) {
			_ctx.set(new Object[] {states = new LinkedList(), new StringBuffer()});
			order = -1;
		} else {
			states = (List)ctx[0];
			if (states.isEmpty()) {
				order = -1;
			} else {
				order = ((Integer)states.get(0)).intValue();
				states.set(0, new Integer(order + 1));
			}
		}
		states.add(0, new Integer(includingPage ? -1: 0));
		return order;
	}
	/** Returns the string buffer for the snippet that shall be generated
	 * after rendering the components.
	 * The content being added to the returned string buffer will be
	 * returned by {@link #afterRedraw} if it is called against
	 * the top-level component.
	 */
	public static final StringBuffer getScriptBuffer() {
		return (StringBuffer)((Object[])_ctx.get())[1];
	}
	/** Called after finsishing the redrawing.
	 * It must be called in the finally clause if {@link #beforeRedraw}
	 * is called.
	 * @return the snippet that shall be written to the HTML output.
	 * For HTML components, it is actually JavaScript snippet.
	 * To inject the snippet, just invoke {@link #getScriptBuffer} and
	 * append the snippet into the returned string buffer.
	 */
	public static final String afterRedraw() {
		try {
			final Object[] ctx = (Object[])_ctx.get();
			final List states = (List)ctx[0];
			states.remove(0);
			if (states.isEmpty()) {
				_ctx.set(null);
				return ((StringBuffer)ctx[1]).toString();
			}
		} catch (Throwable ex) {
			_ctx.set(null); //just in case
			log.realCauseBriefly(ex);
		}
		return "";
	}
	/** A list of states. Each state indicates the redrawing state:
	 * If null, it means it is the root component.
	 * If Boolean.TRUE, it means it is the first child.
	 * If Boolean.FALSE, it means other cases.
	 */
	private static final ThreadLocal _ctx = new ThreadLocal();
}
