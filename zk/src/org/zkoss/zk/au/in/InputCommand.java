/* InputCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Oct  2 13:18:24     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

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
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.ext.client.Inputable;
import org.zkoss.zk.ui.ext.client.InputableX;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;

/**
 * Used only by {@link AuRequest} to implement the {@link InputEvent}
 * relevant command.
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class InputCommand extends Command {
	public InputCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	//-- super --//
	protected void process(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);
		final String[] data = request.getData();
		if (data == null || (data.length != 1 && data.length != 3))
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), this});

		final String newval = data[0];
		if (getId().equals(Events.ON_CHANGE)) {
			final Object xc = ((ComponentCtrl)comp).getExtraCtrl();
			if (xc instanceof InputableX) {
				if (!((InputableX)xc).setTextByClient(newval))
					return; //Bug 1881557: don't post event
			} else if (xc instanceof Inputable) {
				((Inputable)xc).setTextByClient(newval);
			}
		}

		if (data.length == 1)
			Events.postEvent(new InputEvent(getId(), comp, newval,	false, 0));
		else 
			Events.postEvent(new InputEvent(getId(), comp, newval,
				"true".equals(data[1]), Integer.parseInt(data[2])));
	}
}
