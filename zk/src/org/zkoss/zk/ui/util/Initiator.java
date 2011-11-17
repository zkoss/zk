/* Initiator.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug  4 12:09:19     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import java.util.Map;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;

/**
 * Implemented by an initiator that will be invoked if it is specified
 * in the init directive.
 *
 * <p>&lt;?init class="MyInit"?&gt;
 *
 * <p>Once specified, an instance is created and {@link #doInit} is called
 * before the page is evaluated.
 * <p>If you'd like to intercept other activity, you could implement
 * {@link InitiatorExt} too.
 * Then, {@link InitiatorExt#doAfterCompose} is called
 * after all components are created, and before any event is processed.
 * In additions, {@link InitiatorExt#doFinally} is called
 * after the page has been evaluated. If an exception occurs, {@link InitiatorExt#doCatch}
 * is called.
 *
 * <p>A typical usage: starting a transaction in doInit, rolling back it
 * in {@link InitiatorExt#doCatch} and commit it in {@link InitiatorExt#doFinally}
 * (if {@link InitiatorExt#doCatch} is not called).
 *
 * @author tomyeh
 * @see InitiatorExt
 */
public interface Initiator {
	/** Does the initializes before the page is evaluated.
	 *
	 * <p>Note: when it is called, {@link Page#getDesktop},
	 * {@link Page#getId} and {@link Page#getTitle} all return null, since
	 * the page is not initialized yet.
	 * To get the current desktop, you have to use
	 * {@link org.zkoss.zk.ui.Execution#getDesktop} (from 
	 * {@link org.zkoss.zk.ui.Executions#getCurrent}) instead.
	 * On the other hand, you can set the page's ID, title or style in
	 * this method (to override the declarations in the page definition)
	 * by {@link org.zkoss.zk.ui.Page#setId}, {@link Page#setTitle} and {@link Page#setStyle}.
	 * In additions, {@link Page#getRequestPath}
	 * and {@link Page#getAttribute} are all available.
	 *
	 * @param page the page being evaluated
	 * @param args a map of arguments.
	 * Prior to 3.6.2, it is an array. To upgrade, use args.get("arg0")
	 * instead of args[0], args.get("arg1") instead of args[1] and so on.
	 * Of course, it is better to have a more meaningful name for
	 * each argument.
	 * If no argument is specified, args is an empty map (never null).
	 */
	public void doInit(Page page, Map<String, Object> args) throws Exception;
}
