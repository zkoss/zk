/* PagingCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 18 09:12:45     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zul.au.impl;

import com.potix.lang.Objects;

import com.potix.zk.mesg.MZk;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.event.Events;
import com.potix.zk.au.AuRequest;
import com.potix.zk.au.Command;
import com.potix.zul.html.event.PagingEvent;
import com.potix.zul.html.ext.Paginal;

/**
 * Used only by {@link AuRequest} to implement the {@link PagingEvent}
 * relevant command.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
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

		final int pg = Integer.parseInt(data[0]);
		((Paginal)comp).setActivePage(pg);
		Events.postEvent(new PagingEvent(getId(), comp, pg));
	}
}
