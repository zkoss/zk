/* OpenCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Oct  2 14:15:18     2005, Created by tomyeh
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
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.ext.client.Openable;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;

/**
 * Used only by {@link AuRequest} to implement the {@link OpenEvent}
 * relevant command.
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class OpenCommand extends Command {
	public OpenCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	//-- super --//
	protected void process(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);
		final String[] data = request.getData();
		if (data == null || data.length < 1 || data.length > 3)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), this});

		final boolean open = "true".equals(data[0]);
		final Component ref = data.length >= 2 && data[1] != null ?
			request.getDesktop().getComponentByUuidIfAny(data[1]): null;
		final Object xc = ((ComponentCtrl)comp).getExtraCtrl();
		if (xc instanceof Openable)
			((Openable)xc).setOpenByClient(open);
		Events.postEvent(new OpenEvent(getId(), comp, open, ref,
			data.length == 3 ? data[2]: null));
			//FUTURE: support non-String value (by coerce to comp.value.class)
	}
}
