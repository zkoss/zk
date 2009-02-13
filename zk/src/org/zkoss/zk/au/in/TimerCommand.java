/* TimerCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jan 17 17:08:43     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

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
import org.zkoss.zk.ui.ext.client.Timer;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;

/**
 * Used only by {@link AuRequest} to handle the timer requests.
 * 
 * @author tomyeh
 * @since 3.0.2
 */
public class TimerCommand extends Command {
	public TimerCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	//-- super --//
	protected void process(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);

		final Object xc = ((ComponentCtrl)comp).getExtraCtrl();
		if (xc instanceof Timer)
			((Timer)xc).onTimer();

		final String[] data = request.getData();
		Events.postEvent(
			data == null || data.length == 0 ? new Event(getId(), comp):
			data.length == 1 ? new Event(getId(), comp, data[0]):
				new Event(getId(), comp, data));
	}
}
