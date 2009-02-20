/* Selectable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun 16 18:14:14     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext.client;

import java.util.Set;

import org.zkoss.zk.ui.UiException;

/**
 * Implemented by the object returned by {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl},
 * if a component allows users to change the selection from the client.
 *
 * <p>{@link org.zkoss.zk.ui.event.SelectEvent} will be sent after {@link #selectItemsByClient}
 * is called
 * to notify application developers that it is called by user
 * (rather than by codes).
 * 
 * @author tomyeh
 * @see org.zkoss.zk.ui.event.SelectEvent
 */
public interface Selectable {
	/** Set the selection to a set of specified items.
	 * <p>This method is designed to be used by engine.
	 * Don't invoke it directly. Otherwise, the client and server
	 * might mismatch.
	 */
	public void selectItemsByClient(Set selectedItems);
	/** Clears the selection.
	 * <p>This method is designed to be used by engine.
	 * Don't invoke it directly. Otherwise, the client and server
	 * might mismatch.
	 * @since 3.6.0
	 */
	public void clearSelectionByClient();
}
