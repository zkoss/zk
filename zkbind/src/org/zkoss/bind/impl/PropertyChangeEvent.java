/* PropertyChangeEvent.java

	Purpose:
		
	Description:
		
	History:
		2012/1/31 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * queue event for notify change, internal use only
 * @author dennis
 * @since 6.0.0
 */
public class PropertyChangeEvent extends Event {
	private static final long serialVersionUID = 201109091736L;
	private final Object _base;
	private final String _propName;

	public PropertyChangeEvent(Component comp, Object base, String propName) {
		super("onPropertyChange", comp);
		this._base = base;
		this._propName = propName;
	}

	public Object getBase() {
		return _base;
	}

	public String getPropertyName() {
		return _propName;
	}
}
