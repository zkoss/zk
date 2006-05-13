/* ScrollEvent.java

{{IS_NOTE
	$Id: ScrollEvent.java,v 1.3 2006/03/31 03:20:42 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Sun Oct  2 21:30:36     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.event;

import com.potix.zk.ui.Component;

/**
 * Represents an event caused by that user is scrolling or
 * has scrolled at the client.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/03/31 03:20:42 $
 * @see com.potix.zk.ui.ext.Scrollable
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
