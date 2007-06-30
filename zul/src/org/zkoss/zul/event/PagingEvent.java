/* PagingEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 17 16:18:13     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul.event;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

import org.zkoss.zul.ext.Pageable;

/**
 * Used to notify that a new page is selected by the user.
 * It is used for paging long content. In other words, it is fired by
 * a paging component (aka., implementing {@link org.zkoss.zul.ext.Pageable}),
 * such as {@link org.zkoss.zul.Paging}.
 *
 * @author tomyeh
 */
public class PagingEvent extends Event {
	private final Pageable _pgi;
	private final int _actpg;

	/** Construct a paging event.
	 *
	 * @param target the target must be a paginal component, i.e.,
	 * implements {@link Pageable}.
	 * @param actpg the active page
	 */
	public PagingEvent(String name, Component target, int actpg) {
		super(name, target);
		_pgi = (Pageable)target;
		_actpg = actpg;
	}
	/** Construct a paging event.
	 */
	public PagingEvent(String name, Component target, Pageable pageable, int actpg) {
		super(name, target);
		_pgi = pageable;
		_actpg = actpg;
	}
	/** Construct a paging event.
	 * <p>Deprecated since 2.4.1.
	 * @deprecated
	 */
	public PagingEvent(String name, Component target, org.zkoss.zul.ext.Paginal paginal, int actpg) {
		super(name, target);
		_pgi = paginal;
		_actpg = actpg;
	}

	/** Returns the pageable controller.
	 * @since 2.4.1
	 */
	public Pageable getPageable() {
		return _pgi;
	}
	/** Returns the paginal controller.
	 *
	 * <p>Deprecated since 2.4.1. Use {@link #getPageable} instead.
	 *
	 * @deprecated
	 */
	public org.zkoss.zul.ext.Paginal getPaginal() {
		return _pgi instanceof org.zkoss.zul.ext.Paginal ?
			(org.zkoss.zul.ext.Paginal)_pgi: null;
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
