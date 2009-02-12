/* ColSizeCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Dec  7 11:08:01     2006, Created by tomyeh
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
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.client.Sizable;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;
import org.zkoss.zk.au.in.Commands;

import org.zkoss.zul.event.ColSizeEvent;

/**
 * Used only by {@link AuRequest} to implement the {@link ColSizeEvent}
 * related command.
 * 
 * @author tomyeh
 */
public class ColSizeCommand extends Command {
	public ColSizeCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	//-- super --//
	protected void process(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);
		final String[] data = request.getData();
		if (data == null || data.length != 4)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), this});

		final Desktop desktop = request.getDesktop();
		final int icol = Integer.parseInt(data[0]);
		final Component col1 = desktop.getComponentByUuid(data[1]);

		((Sizable)((ComponentCtrl)col1).getExtraCtrl()).setWidthByClient(data[2]);
		Events.postEvent(
			new ColSizeEvent(getId(), comp, icol, col1,
				Commands.parseKeys(data[3])));
	}
}
