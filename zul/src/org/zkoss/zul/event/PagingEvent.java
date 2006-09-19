/* PagingEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 17 16:18:13     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul.event;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

import org.zkoss.zul.ext.Paginal;

/**
 * Used to notify that a new page is selected by the user.
 * It is used for paging long content. In other words, it is fired by
 * a paging component (aka., implementing {@link org.zkoss.zul.ext.Paginal}),
 * such as {@link org.zkoss.zul.Paging}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class PagingEvent extends Event {
	private final Paginal _pgi;
	private final int _actpg;

	/** Construct a paging event.
	 *
	 * @param target the target must be a paginal component, i.e.,
	 * implements {@link Paginal}.
	 * @param actpg the active page
	 */
	public PagingEvent(String name, Component target, int actpg) {
		super(name, target);
		if (!(target instanceof Paginal))
			throw new IllegalArgumentException("Not implement "+Paginal.class.getName()+": "+target);
		_pgi = (Paginal)target;
		_actpg = actpg;
	}
	/** Construct a paging event.
	 */
	public PagingEvent(String name, Component target, Paginal paginal, int actpg) {
		super(name, target);
		_pgi = paginal;
		_actpg = actpg;
	}

	/** Returns the paginal controller.
	 */
	public Paginal getPaginal() {
		return _pgi;
	}
	/** Returns the active page (starting from 0).
	 * <p>It is the same as {@link #getPaginal}'s {@link Paginal#getActivePage}.
	 *
	 * <p>To get the index of the first visible item, use<br/>
	 * <code>{@link #getActivePage} * {@link Paginal#getPageSize}</code>.
	 */
	public int getActivePage() {
		return _actpg;
	}
}
