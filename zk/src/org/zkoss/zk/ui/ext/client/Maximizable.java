/* Maximizable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 23, 2008 5:42:18 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext.client;

/**
 * Implemented by the object returned by {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl},
 * a component is maximized by the client.
 * @author jumperchen
 * @since 3.5.0
 */
public interface Maximizable extends Sizable, Movable {

	/** Sets the maximized of the component, caused by user's activity at
	 * the client.
	 * <p>This method is designed to be used by engine.
	 * Don't invoke it directly. Otherwise, the client and server
	 * might mismatch.
	 */
	public void setMaximizedByClient(boolean maximized);
}
