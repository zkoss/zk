/* DropEvent.java

{{IS_NOTE
	$Id: DropEvent.java,v 1.2 2006/02/27 03:54:49 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Mon Feb 27 00:08:50     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.event;

import com.potix.zk.ui.Component;

/**
 * Represents an event cause by user's dragging and dropping a component.
 *
 * <p>The component being dragged can be retrieved by {@link #getDragged}.
 * The component that received the dragged component is {@link #getTarget}.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:54:49 $
 */
public class DropEvent extends Event {
	private final Component _dragged;

	/** Constructs a drop event.
	 * @param dragged The component being dragged and drop to {@link #getTarget}.
	 */
	public DropEvent(String name, Component target, Component dragged) {
		super(name, target);
		_dragged = dragged;
	}
	/** Returns the component being dragged and drop to {@link #getTarget}.
	 */
	public final Component getDragged() {
		return _dragged;
	}
}
