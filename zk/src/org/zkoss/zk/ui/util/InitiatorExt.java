/* InitiatorExt.java

	Purpose:
		
	Description:
		
	History:
		Mon Oct 15 11:40:42     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;

/**
 * An extra interface that can be implemented with {@link Initiator} to
 * have the better control.
 *
 * @author tomyeh
 * @see Initiator
 * @since 3.0.0
 */
public interface InitiatorExt {
	/** Called after all components are created (aka., composed),
	 * and before any event is processed.
	 *
	 * <p>It won't be called if an un-caught exception occurs when creating
	 * components.
	 *
	 * @param page the page that new components are attached to. It is the same
	 * as {@link Initiator#doInit}'s page argument.
	 * @param comps the root components being created (never null, but the length
	 * might be zero).
	 * Note: It is not necessary the same as {@link Page#getRoots}, since
	 * this method might be called thru {@link org.zkoss.zk.ui.Executions#createComponents}.
	 * @since 3.0.0
	 */
	public void doAfterCompose(Page page, Component[] comps) throws Exception;

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
	 * in {@link Initiator#doInit}.
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
	 * It won't be called if {@link Initiator#doInit} throws an exception.
	 * However,it is always called no matter whether {@link #doCatch} is called.
	 *
	 * <p>An exception thrown in this method is simply logged. It has no
	 * effect on the execution.
	 */
	public void doFinally() throws Exception;
}
