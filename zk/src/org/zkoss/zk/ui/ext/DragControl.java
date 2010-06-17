/* DragControl.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 15 17:54:04 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.ext;

import org.zkoss.zk.ui.Component;

/**
 * Decorates {@link Component} to denote that
 * the component will control the default drag-and-drop behavior
 * of its children.
 * In other words, if this interface is implemented, the children is default
 * draggable and the meaning of draggable is changed a little.
 * The portal layout is a typical example.
 * @author tomyeh
 * @since 5.0.3
 */
public interface DragControl {
}
