/* PropertyChangeEvent.java

	Purpose:
		
	Description:
		
	History:
		2012/1/31 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * The property-change event
 * @author dennis
 * @since 6.0.1
 */
public class PropertyChangeEvent extends Event {
	private static final long serialVersionUID = 201109091736L;
	private final Object _base;
	private final String _prop;

	public PropertyChangeEvent(Component comp, Object base, String prop) {
		super("onPropertyChange", comp);
		this._base = base;
		this._prop = prop;
	}

	/**
	 * Gets the base object
	 */
	public Object getBase() {
		return _base;
	}

	/**
	 * Gets the property
	 */
	public String getProperty() {
		return _prop;
	}
}
