/* ScrollEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Oct  2 21:30:36     2005, Created by tomyeh
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
 * Represents an event caused by that user is scrolling or
 * has scrolled at the client.
 * 
 * @author tomyeh
 * @see org.zkoss.zk.ui.ext.client.Scrollable
 */
public class ScrollEvent extends Event {
	private final int _pos;

	/** Constructs an scroll-relevant event.
	 * @param pos the new position
	 */
	public ScrollEvent(String name, Component target, int pos) {
		super(name, target);
		_pos = pos;
	}
	/** Returns the position.
	 */
	public final int getPos() {
		return _pos;
	}
}
