/* ZIndexEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Dec 24 23:04:41     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import org.zkoss.zk.ui.Component;

/**
 * Represents an event caused by a component whose z-index is modified
 * by the client.
 *
 * <p>Component Implementation Note:<br/>
 * A z-indexed component must implement {@link org.zkoss.zk.ui.ext.client.ZIndexed}
 * for the returned object of {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl}.
 * 
 * @author tomyeh
 */
public class ZIndexEvent  extends Event {
	private final int _zIndex;

	/** Constructs a mouse relevant event.
	 */
	public ZIndexEvent(String name, Component target, int zIndex) {
		super(name, target);
		_zIndex = zIndex;
	}
	/** Returns the z-index of the component after moved.
	 */
	public final int getZIndex() {
		return _zIndex;
	}
}
