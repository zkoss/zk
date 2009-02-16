/* PortalMoveCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug 12 11:40:02 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.au.in;

import org.zkoss.lang.Objects;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.client.Updatable;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;
import org.zkoss.zkmax.event.PortalMoveEvent;
import org.zkoss.zkmax.zul.Portalchildren;
import org.zkoss.zul.Panel;

/**
 * Used only by {@link AuRequest} to implement the {@link PortalMoveEvent}
 * related command.
 * 
 * @author jumperchen
 * @since 3.5.0
 */
public class PortalMoveCommand extends Command {
	public PortalMoveCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	//-- super --//
	protected void process(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);

		final String[] data = request.getData();
		if (data == null || data.length != 4)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA, new Object[] {
					Objects.toString(data), this });
		final Desktop desktop = request.getDesktop();
		final Portalchildren from = (Portalchildren) desktop.getComponentByUuid(data[0]);
		final Portalchildren to = (Portalchildren) desktop.getComponentByUuid(data[1]);
		final Panel dragged = (Panel) desktop.getComponentByUuid(data[2]);
		final int droppedIndex = Integer.parseInt(data[3]);
		try {
			((Updatable) (from).getExtraCtrl()).setResult(Boolean.TRUE);
			((Updatable) (to).getExtraCtrl()).setResult(Boolean.TRUE);
			((Updatable) (dragged).getExtraCtrl()).setResult(new Object[]{Boolean.TRUE, to});
			to.insertBefore(dragged, droppedIndex < to.getChildren().size() ?
					(Component)to.getChildren().get(droppedIndex) : null);
		} finally {
			((Updatable) (dragged).getExtraCtrl()).setResult(new Object[]{Boolean.FALSE, null});
			((Updatable) (from).getExtraCtrl()).setResult(Boolean.FALSE);
			((Updatable) (to).getExtraCtrl()).setResult(Boolean.FALSE);
		}
		Events.postEvent(new PortalMoveEvent(getId(), comp, from, to, dragged));
	}
}
