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
package com.potix.zul.html.event;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.event.Event;

import com.potix.zul.html.ext.Paginal;

/**
 * Used to notify that a new page is selected by the user.
 * It is used for paging long content. In other words, it is fired by
 * a paging component (aka., implementing {@link com.potix.zul.html.ext.Paginal}),
 * such as {@link com.potix.zul.html.Paging}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class PagingEvent extends Event {
	private final int _ofs;

	/** Construct a paging relevant event with coordination or area.
	 *
	 * @param target the target must be a paginal component, i.e.,
	 * implements {@link Paginal}.
	 * @param ofs the starting offset of the page.
	 */
	public PagingEvent(String name, Component target, int ofs) {
		super(name, target);
		if (!(target instanceof Paginal))
			throw new IllegalArgumentException("Not implement "+Paginal.class.getName()+": "+target);
		_ofs = ofs;
	}

	/** Returns the paginal controller.
	 */
	public Paginal getPaginal() {
		return (Paginal)getTarget();
	}
	/** Returns the index of the first visible item (starting from 0).
	 * <p>It is the same as {@link Paginal#getActivePage} * {@link Paginal#getPageSize}.
	 */
	public int getOffset() {
		return _ofs;
	}
}
