/* Portallayout.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 8, 2008 4:18:10 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.zul;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkmax.event.*;
import org.zkoss.zul.Panel;
import org.zkoss.zul.impl.XulElement;

/**
 * A portal layout lays out a container which can have multiple columns, and each
 * column may contain one or more panel. Portal layout provides a way to drag-and-drop
 * panel into other portalchildren from the same portal layout.<br>
 * 
 * Use Portallayout need assign width (either present or pixel) on every
 * Portalchildren, or we cannot make sure about layout look.
 * 
 * <p>Events:<br/>
 * onPortalMove.<br/>
 * 
 * <p>Default {@link #getZclass}: z-portal-layout.
 * 
 * @author jumperchen
 * @since 3.5.0
 */
public class Portallayout extends XulElement implements org.zkoss.zkmax.zul.api.Portallayout {

	static {
		addClientEvent(Portallayout.class, ZkMaxEvents.ON_PORTAL_MOVE, CE_IMPORTANT);
	}
	
	public Portallayout() {
	}
	
	/**
	 * Returns the specified pnael, or null if not available.
	 * @param col which Portalchildren to fetch (starting at 0).
	 * @param row which Panel to fetch (starting at 0).
	 */
	public Panel getPanel(int col, int row) {
		if (col < 0 || row < 0 || getChildren().size() <= col) return null;
		final List children = ((Component)getChildren().get(col)).getChildren();
		return children.size() <= row ? null: (Panel)children.get(row);
	}
	
	/**
	 * Sets the specified panel via the position(col and row).
	 * @param panel a new panel component
	 * @param col which Portalchildren to fetch (starting at 0).
	 * @param row which Panel to fetch (starting at 0).
	 * @return If false, the added panel fails.
	 */
	public boolean setPanel(Panel panel, int col, int row) {
		if (col < 0 || row < 0 || panel == null || getChildren().size() <= col) return false;
		final Portalchildren children = ((Portalchildren)getChildren().get(col));
		if (children.getChildren().size() >= row)
			return children.appendChild(panel);
		else
			return children.insertBefore(panel, (Component)children.getChildren().get(row));
	}
	
	/**
	 * Returns an int array[col, row] that indicates the specified panel located
	 * within this portal layout. If not found, [-1, -1] is assumed.
	 */
	public int[] getPosition(Panel panel) {
		int[] pos = new int[] {-1, -1};
		if (panel == null || panel.getParent() == null) return pos;
		pos[0] = getChildren().indexOf(panel.getParent());
		if (pos[0] < 0)
			pos[1] = pos[0];
		else pos[1] = panel.getParent().getChildren().indexOf(panel);
		return pos; 
	}

	public String getZclass() {
		return _zclass == null ? "z-portal-layout" : _zclass;
	}
	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Portalchildren))
			throw new UiException("Unsupported child for Portallayout: "
					+ child);
		super.beforeChildAdded(child, refChild);
	}

	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link XulElement#process},
	 * it also handles onPortalMove.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(ZkMaxEvents.ON_PORTAL_MOVE)) {
			PortalMoveEvent evt = PortalMoveEvent.getPortalMoveEvent(request);
			try {
				evt.getFrom().disableSmartUpdate(true);
				final Portalchildren to = evt.getTo();
				to.disableSmartUpdate(true);
				final Panel dragged = evt.getDragged();
				dragged.disableSmartUpdate(true, to);
				final int droppedIndex = evt.getDroppedIndex();
				to.insertBefore(dragged,
					droppedIndex < to.getChildren().size() ?
						(Component)to.getChildren().get(droppedIndex) : null);
			} finally {
				evt.getDragged().disableSmartUpdate(false, null);
				evt.getFrom().disableSmartUpdate(false);
				evt.getTo().disableSmartUpdate(false);
			}
			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}

	/** Called by Portalchildren to call smart update.
	 */
	/*package*/ void smartUpdateDirectly(String name, boolean value) {
		super.smartUpdate(name, value);
	}
}
