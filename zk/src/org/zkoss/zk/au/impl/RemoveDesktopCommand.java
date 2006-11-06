/* RemoveDesktopCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Mar 15 11:02:26     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.impl;

import org.zkoss.lang.Objects;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;

/**
 * A command to remove the specified desktop.
 * 
 * @author tomyeh
 */
public class RemoveDesktopCommand extends Command {
	public RemoveDesktopCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}
	protected void process(AuRequest request) {
		final String[] data = request.getData();
		if (data != null && data.length != 0)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), this});

		Events.postEvent(new Event(Events.ON_REMOVE_DESKTOP, null));
	}
}
