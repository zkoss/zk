/* HeadersElement.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec  7 09:43:48     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.event.ColSizeEvent;
import org.zkoss.zul.event.ColsSizeEvent;
import org.zkoss.zul.event.ZulEvents;

/**
 * A skeletal implementation for headers, the parent of
 * a group of {@link HeaderElement}.
 *
 * @author tomyeh
 */
abstract public class HeadersElement extends XulElement implements org.zkoss.zul.impl.api.HeadersElement{

	static {
		addClientEvent(HeadersElement.class, ZulEvents.ON_COL_SIZE, CE_IMPORTANT); //no CE_DUPLICATE_IGNORE (might apply to diff index)
		addClientEvent(HeadersElement.class, ZulEvents.ON_COLS_SIZE, CE_IMPORTANT); //no CE_DUPLICATE_IGNORE (might apply to diff index)
	}
	
	private boolean _sizable;

	/** Returns whether the width of the child column is sizable.
	 */
	public boolean isSizable() {
		return _sizable;
	}
	/** Sets whether the width of the child column is sizable.
	 * If true, an user can drag the border between two columns (e.g., {@link org.zkoss.zul.Column})
	 * to change the widths of adjacent columns.
	 * <p>Default: false.
	 */
	public void setSizable(boolean sizable) {
		if (_sizable != sizable) {
			_sizable = sizable;
			smartUpdate("sizable", sizable);
		}
	}

	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);
		
		render(renderer, "sizable", _sizable);
	}
	//-- ComponentCtrl --//
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link XulElement#service},
	 * it also handles onColSize and onColsSize.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(ZulEvents.ON_COL_SIZE)) {
			ColSizeEvent evt = ColSizeEvent.getColSizeEvent(request);
			((HeaderElement)evt.getColumn()).setWidthByClient(evt.getWidth());
			Events.postEvent(evt);
		} else if (cmd.equals(ZulEvents.ON_COLS_SIZE)) { //feature#3177275: Listheader should override hflex when sized by end user
			((MeshElement)this.getParent()).setSpan(false); //clear span
			ColsSizeEvent evt = ColsSizeEvent.getColsSizeEvent(request);
			int j = 0;
			for(Iterator it = getChildren().iterator(); it.hasNext(); ++j) {
				final HeaderElement header = (HeaderElement) it.next(); 
				header.setWidthByClient(evt.getWidth(j));
				if (header.getHflex() != null) {
					header.setHflexByClient(null);
				}
			}
			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}
}
