/* MoveEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 12 08:34:35     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.event;

import com.potix.zk.ui.Component;

/**
 * Represents an event caused by a component being moved.
 *
 * <p>A moveable component must implement {@link com.potix.zk.ui.ext.Moveable}.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.4 $ $Date: 2006/05/29 04:28:02 $
 */
public class MoveEvent extends Event {
	private final String _left, _top;

	/** Constructs a mouse relevant event.
	 */
	public MoveEvent(String name, Component target, String left, String top) {
		super(name, target);
		_left = left;
		_top = top;
	}
	/** Returns the left of the component after moved.
	 */
	public final String getLeft() {
		return _left;
	}
	/** Returns the top of the component after moved.
	 */
	public final String getTop() {
		return _top;
	}
}
