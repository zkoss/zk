/* PagingCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 18 09:12:45     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.au.in;

import org.zkoss.lang.Objects;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;
import org.zkoss.zul.event.PagingEvent;
import org.zkoss.zul.ext.Pageable;
import org.zkoss.zul.ext.Paginal;

/**
 * Used only by {@link AuRequest} to implement the {@link PagingEvent}
 * related command.
 * 
 * @author tomyeh
 */
public class PagingCommand extends Command {
	public PagingCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	//-- super --//
	protected void process(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);
		final String[] data = request.getData();
		if (data == null || data.length != 1)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), this});

		final Pageable pageable = (Pageable)comp;
		int pgi = Integer.parseInt(data[0]);
		if (pgi < 0) pgi = 0;
		else {
			final int pgcnt = pageable.getPageCount();
			if (pgi >= pgcnt) {
				pgi = pgcnt - 1;
				if (pgi < 0) pgi = 0;
			}
		}
		pageable.setActivePage(pgi);
		Events.postEvent(new PagingEvent(getId(), comp, pgi));
	}
}
