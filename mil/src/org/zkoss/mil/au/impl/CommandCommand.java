/* CommandCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 1, 2007 3:20:43 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.mil.au.impl;

import org.zkoss.lang.Objects;
import org.zkoss.mil.Command;
import org.zkoss.mil.event.CommandEvent;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;

/**
 * Used only by {@link AuRequest} to implement the {@link CommandEvent}
 * relevant command.
 * @author henrichen
 * @since 2.4.0
 */
public class CommandCommand extends org.zkoss.zk.au.Command {

	public CommandCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	protected void process(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);
		final String[] data = request.getData();
		if (data == null || data.length != 1)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), this});

		final Command cmd = data[0] != null ? 
			(Command) comp.getDesktop().getComponentByUuidIfAny(data[0]) : null;

		Events.postEvent(new CommandEvent(getId(), comp, cmd));
	}

}
