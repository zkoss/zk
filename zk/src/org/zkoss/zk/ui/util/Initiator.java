/* Initiator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug  4 12:09:19     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;

/**
 * Implemented by an initiator that will be invoked if it is specified
 * in the init directive.
 *
 * <p>&lt;?init class="MyInit"?&gt;
 *
 * <p>Once specified, an instance is created and {@link #doInit} is called
 * before the page is evaluated. Then, {@link #doAfterCompose} is called
 * after all components are created, and before any event is processed.
 * In additions, {@link #doFinally} is called
 * after the page has been evaluated. If an exception occurs, {@link #doCatch}
 * is called.
 *
 * <p>A typical usage: starting a transaction in doInit, rolling back it
 * in {@link #doCatch} and commit it in {@link #doFinally}
 * (if {@link #doCatch} is not called).
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
	 * @param args an array of arguments passed with
	 * the arg0, arg1, arg2 and arg3 attributes (of the init directive).
	 * If no argument is specified, args is an array with zero length.
	 */
	public void doInit(Page page, Object[] args) throws Exception;
	/** Called after all components are created (aka., composed),
	 * and before any event is processed.
	 *
	 * <p>Note: if {@link InitiatorExt} is also implemented,
	 * this method won't be called. Rather, {@link InitiatorExt#doAfterCompose}
	 * will be called instead.
	 *
	 * <p>For example, the data-binding managers could process the binding
	 * at this callback.
	 *
	 * <p>It won't be called if an un-caught exception occurs when creating
	 * components.
	 *
	 * @param page the page that new components are attached to. It is the same
	 * as {@link #doInit}'s page argument.
	 */
	public void doAfterCompose(Page page) throws Exception;

	/** Called when an exception occurs during the evaluation of the page.
	 *
	 * <p>This is only a notification for the happening of an exception.
	 * You DO NOT re-throw exception here.
	 * The exception is always re-throwed after calling this method
	 *
	 * <p>Notice: this method won't be called if the exception occurs
	 * in {@link #doInit}.
	 *
	 * @param ex the exception being thrown
	 */
	public void doCatch(Throwable ex);
	/** Do the cleanup after the page has been evaluated.
	 * It won't be called if {@link #doInit} throws an exception.
	 * However,it is always called no matter whether {@link #doCatch} is called.
	 */
	public void doFinally();
}
