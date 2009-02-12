/* ZIndexed.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Dec 24 23:01:45     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext.client;

/**
 * Implemented by the object returned by {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl}
 * if the z-index of a component is modifiable by the client.
 *
 * <p>Once it is moved by the user, {@link #setZIndexByClient}
 * are called and {@link org.zkoss.zk.ui.event.ZIndexEvent} is sent to notify the component.
 * 
 * @author tomyeh
 */
public interface ZIndexed {
	/** Sets the z-index of the component, caused by user's activity at
	 * the client.
	 */
	public void setZIndexByClient(int zIndex);
}
