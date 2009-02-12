/* MinMaximizeCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 23, 2008 5:49:11 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.in;

import org.zkoss.lang.Objects;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MaximizeEvent;
import org.zkoss.zk.ui.event.MinimizeEvent;
import org.zkoss.zk.ui.ext.client.Maximizable;
import org.zkoss.zk.ui.ext.client.Minimizable;
import org.zkoss.zk.ui.sys.ComponentCtrl;

/**
 * Used only by {@link AuRequest} to implement the {@link MinimizeEvent} and {@link MaximizeEvent}
 * relevant command.
 * @author jumperchen
 * @since 3.5.0
 */
public class MinMaximizeCommand extends Command {
	
	public MinMaximizeCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	//-- super --//
	protected void process(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);
		final String[] data = request.getData();
		if (data == null || data.length != 5)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), this});

		if (Objects.equals(getId(), Events.ON_MINIMIZE)) {
			final Minimizable min = (Minimizable)((ComponentCtrl)comp).getExtraCtrl();
			final boolean minimized = "true".equals(data[4]);
			min.setLeftByClient(data[0]);
			min.setTopByClient(data[1]);
			min.setWidthByClient(data[2]);
			min.setHeightByClient(data[3]);
			min.setMinimizedByClient(minimized);
			Events.postEvent(new MinimizeEvent(getId(), comp, data[0], data[1],
					data[2], data[3], minimized));
		} else {
			final Maximizable max = (Maximizable)((ComponentCtrl)comp).getExtraCtrl();
			final boolean maximized = "true".equals(data[4]);
			max.setLeftByClient(data[0]);
			max.setTopByClient(data[1]);
			max.setWidthByClient(data[2]);
			max.setHeightByClient(data[3]);
			max.setMaximizedByClient(maximized);
			Events.postEvent(new MaximizeEvent(getId(), comp, data[0], data[1],
					data[2], data[3], maximized));
		}
	}

}
