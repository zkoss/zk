/* MapZoomCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct  13 14:37:25     2006, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.gmaps;

import org.zkoss.gmaps.event.MapZoomEvent;

import org.zkoss.lang.Objects;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;

/**
 * Used only by {@link AuRequest} to implement the {@link MapZoomEvent}
 * relevant command.
 * 
 * @author henrichen
 */
/* package */ class MapZoomCommand extends Command {
	public MapZoomCommand(String evtnm, int flags) {
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

		final Gmaps gmaps = (Gmaps) comp;
		final int zoom = Integer.parseInt(data[0]);
		gmaps.setZoomByClient(zoom);
		Events.postEvent(new MapZoomEvent(getId(), comp, zoom));
	}
}
