/* SizeCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Dec  7 12:01:06     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.in;

import org.zkoss.lang.Objects;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SizeEvent;
import org.zkoss.zk.ui.ext.client.Sizable;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;

/**
 * Used only by {@link AuRequest} to implement the {@link SizeEvent}
 * relevant command.
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class SizeCommand extends Command {
	public SizeCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	//-- super --//
	protected void process(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);
		final String[] data = request.getData();
		if (data == null || data.length != 3)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), this});

		final Sizable size = (Sizable)((ComponentCtrl)comp).getExtraCtrl();
		size.setWidthByClient(data[0]);
		size.setHeightByClient(data[1]);
		Events.postEvent(new SizeEvent(getId(), comp, data[0], data[1],
			Commands.parseKeys(data[2])));
	}
}
