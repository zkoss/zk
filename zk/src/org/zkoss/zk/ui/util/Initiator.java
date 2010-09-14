/* Initiator.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug  4 12:09:19     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
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
	 * @param args a map of arguments.
	 * Prior to 3.6.2, it is an array. To upgrade, use args.get("arg0")
	 * instead of args[0], args.get("arg1") instead of args[1] and so on.
	 * Of course, it is better to have a more meaningful name for
	 * each argument.
	 * If no argument is specified, args is an empty map (never null).
	 */
	public void doInit(Page page, Map<String, Object> args) throws Exception;
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
	 * <p>If you don't want to handle the exception, simply returns false.
	 * <code>boolean doCatch(Throwable ex) {return false;}</code>
	 *
	 * <p>An exception thrown in this method is simply logged. It has no
	 * effect on the execution.
	 * If you want to ignore the exception, just return true.
	 *
	 * <p>Notice: this method won't be called if the exception occurs
	 * in {@link #doInit}.
	 *
	 * @param ex the exception being thrown
	 * @return whether to ignore the exception. If false is returned,
	 * the exception will be re-thrown.
	 * Note: once an initiator's doCatch returns true, the exception will be
	 * ignored and it means doCatch of the following initiators won't be called.
	 * Prior to ZK 3.0.0, void is returned and it means always re-thrown
	 */
	public boolean doCatch(Throwable ex) throws Exception;
	/** Do the cleanup after the page has been evaluated.
	 * It won't be called if {@link #doInit} throws an exception.
	 * However,it is always called no matter whether {@link #doCatch} is called.
	 *
	 * <p>An exception thrown in this method is simply logged. It has no
	 * effect on the execution.
	 */
	public void doFinally() throws Exception;
}
