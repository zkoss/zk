/* MarkerDropCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 9, 2007 3:20:03 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.gmaps;

import org.zkoss.gmaps.event.MarkerDropEvent;
import org.zkoss.lang.Objects;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;

/**
 * Used only by {@link AuRequest} to implement the {@link MarkerDropEvent}
 * relevant command.
 * 
 * @author henrichen
 *
 */
/*package*/ class MarkerDropCommand extends Command {
	public MarkerDropCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	protected void process(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);
		final String[] data = request.getData();
		if (data == null || data.length != 2)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), this});

		final Gmarker gmarker = (Gmarker) comp;
		final double lat = Double.parseDouble(data[0]);
		final double lng = Double.parseDouble(data[1]);
		gmarker.setLatByClient(lat);
		gmarker.setLngByClient(lng);
		Events.postEvent(new MarkerDropEvent(getId(), comp, lat, lng));
	}
}
