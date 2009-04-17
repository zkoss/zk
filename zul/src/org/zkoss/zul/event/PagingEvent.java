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

import java.util.Map;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;

import org.zkoss.zul.ext.Pageable;
import org.zkoss.zul.ext.Paginal;

/**
 * Used to notify that a new page is selected by the user, or by
 * {@link Paginal} (such as {@link org.zkoss.zul.Paging}).
 * It is used for paging long content.
 *
 * @author tomyeh
 */
public class PagingEvent extends Event {
	private final Pageable _pgi;
	private final int _actpg;

	/** Converts an AU request to a render event.
	 * @since 5.0.0
	 */
	public static final PagingEvent getPagingEvent(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, request);
		final Map data = request.getData();
		if (data == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {data, request});

		int pgi = AuRequests.getInt(data, "", 0);
		final Pageable pageable = (Pageable)comp;
		if (pgi < 0) pgi = 0;
		else {
			final int pgcnt = pageable.getPageCount();
			if (pgi >= pgcnt) {
				pgi = pgcnt - 1;
				if (pgi < 0) pgi = 0;
			}
		}
		return new PagingEvent(request.getCommand(), comp, pgi);
	}
	
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
	/** Construct a paging event that the target is different
	 * from the page controller.
	 *
	 * @param target the event target
	 * @param pageable the paging controller. In other words,
	 * it is usually {@link Paginal}.
	 */
	public PagingEvent(String name, Component target, Pageable pageable,
	int actpg) {
		super(name, target);
		_pgi = pageable;
		_actpg = actpg;
	}

	/** Returns the pageable controller.
	 * @since 2.4.1
	 */
	public Pageable getPageable() {
		return _pgi;
	}

	/** Returns the active page (starting from 0).
	 * <p>It is the same as {@link #getPageable}'s {@link Pageable#getActivePage}.
	 *
	 * <p>To get the index of the first visible item, use<br/>
	 * <code>{@link #getActivePage} * {@link Pageable#getPageSize}</code>.
	 */
	public int getActivePage() {
		return _actpg;
	}
}
