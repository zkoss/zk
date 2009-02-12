/* PageSizeCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Jun 30 20:55:36     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

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
import org.zkoss.zul.event.PageSizeEvent;
import org.zkoss.zul.ext.Pageable;
import org.zkoss.zul.ext.Paginal;

/**
 * Used only by {@link AuRequest} to implement the {@link PageSizeEvent}
 * related command.
 * 
 * @author tomyeh
 * @since 2.4.1
 */
public class PageSizeCommand extends Command {
	public PageSizeCommand(String evtnm, int flags) {
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

		int pgsz = Integer.parseInt(data[0]);
		if (pgsz <= 0) pgsz = 1;
		((Pageable)comp).setPageSize(pgsz);
		if (!(comp instanceof Paginal))
			Events.postEvent(new PageSizeEvent(getId(), comp, pgsz));
	}
}
