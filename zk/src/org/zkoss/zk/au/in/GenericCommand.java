/* GenericCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Oct  2 13:34:19     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.in;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;

/**
 * Used by {@link AuRequest} to implement generic command
 * that does nothing but posting an {@link Event}.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class GenericCommand extends Command {
	private final boolean _broadcast;
	public GenericCommand(String evtnm, int flags) {
		super(evtnm, flags);
		_broadcast = false;
	}
	/**
	 * @param broadcast whether to broadcast the event to all root components
	 * of all pages in the same desktop, if component is null.
	 */
	public GenericCommand(String evtnm, int flags, boolean broadcast) {
		super(evtnm, flags);
		_broadcast = broadcast;
	}

	//-- super --//
	protected void process(AuRequest request) {
		final Component comp = request.getComponent();
		if (!_broadcast && comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);
		final String[] data = request.getData();
		Events.postEvent(
			data == null || data.length == 0 ? new Event(getId(), comp):
			data.length == 1 ? new Event(getId(), comp, data[0]):
				new Event(getId(), comp, data));
	}
}
