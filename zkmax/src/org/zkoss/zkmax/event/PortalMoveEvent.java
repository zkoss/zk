/* PortalMoveEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug 12 11:43:38 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.event;

import org.zkoss.lang.Objects;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zk.ui.event.Event;

import org.zkoss.zkmax.zul.Portalchildren;
import org.zkoss.zul.Panel;

/**
 * Represents an event caused by a portal being moved.
 * 
 * @author jumperchen
 * @since 3.5.0
 */
public class PortalMoveEvent extends Event {
	private final Portalchildren _from, _to;
	private final Panel _dragged;
	private final int _droppedIndex;

	/** Converts an AU request to a portal-move event.
	 * @since 5.0.0
	 */
	public static final PortalMoveEvent getPortalMoveEvent(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, request);

		final String[] data = request.getData();
		if (data == null || data.length != 4)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA, new Object[] {
					Objects.toString(data), request});
		final Desktop desktop = request.getDesktop();
		return new PortalMoveEvent(request.getName(), comp,
			(Portalchildren) desktop.getComponentByUuid(data[0]),
			(Portalchildren) desktop.getComponentByUuid(data[1]),
			(Panel)desktop.getComponentByUuid(data[2]),
			Integer.parseInt(data[3]));
	}
	/**
	 * Constructs a ColumnMoved event.
	 * @since 5.0.0
	 */
	public PortalMoveEvent(String evtnm, Component target, Portalchildren from, Portalchildren to,
	Panel dragged, int droppedIndex) {
		super(evtnm, target);
		_from = from;
		_to = to;
		_dragged = dragged;
		_droppedIndex = droppedIndex;
	}
	/**
	 * Returns the portalchildren from the dragged panel.
	 */
	public Portalchildren getFrom() {
		return _from;
	}
	/**
	 * Returns the portalchildren where the dragged panel drops to.
	 */
	public Portalchildren getTo() {
		return _to;
	}
	/** 
	 * Returns the panel being dragged.
	 */
	public final Panel getDragged() {
		return _dragged;
	}
	/** Returns the dropped index.
	 * @since 5.0.0
	 */
	public final int getDroppedIndex() {
		return _droppedIndex;
	}
}
