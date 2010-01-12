/* UnWire.java

	Purpose:
		
	Description:
		
	History:
		Jan 7, 2010 10:37:45 AM , Created by joy

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import java.util.Map;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * @author Joy Lo
 * @since 5.0.0
 */
public class UnwireEvent extends Event {
	
	private final String _id;
	
	public UnwireEvent(String name, Component target, String id) {
		super(name, target);
		_id = id;
	}

	/** Converts an AU request to a wire event. 
	 */
	public static final UnwireEvent getUnwireEvent(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, request);
		final Map data = request.getData();
		if (data == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {data, request});
		return new UnwireEvent(request.getCommand(), comp, (String)data.get("id"));
	}
	
	/** Returns the id of the component.
	 */
	public final String getId() {
		return _id;
	}
}
