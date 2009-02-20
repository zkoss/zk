/* PortalMoveEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug 12 11:43:38 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.event;

import org.zkoss.zk.ui.Component;
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
	/**
	 * Constructs a ColumnMoved event.
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
	/**
	 * Returns the dropped index.
	 * @since 3.6.0
	 */
	public int getDroppedIndex() {
		return _droppedIndex;
	}
}
