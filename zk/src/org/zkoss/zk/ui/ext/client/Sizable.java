/* Sizable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Dec  7 11:16:06     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext.client;

/**
 * Implemented by the object returned by {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl}
 * to indicate a component is sizable by the client.
 *
 * <p>Once it is re-sized by the user, {@link #setWidthByClient} and
 * {@link #setHeightByClient} is called.
 * Depending on the compoent, {@link org.zkoss.zk.ui.event.SizeEvent} is
 * usually sent to notify the component.
 * 
 * @author tomyeh
 */
public interface Sizable {
	/** Sets the width of the component, caused by user's activity at
	 * the client.
	 * <p>This method is designed to be used by engine.
	 * Don't invoke it directly. Otherwise, the client and server
	 * might mismatch.
	 */
	public void setWidthByClient(String width);
	/** Sets the height of the component, caused by user's activity at
	 * the client.
	 * <p>This method is designed to be used by ZK update engine.
	 * Don't invoke it directly. Otherwise, the client and server
	 * might mismatch.
	 */
	public void setHeightByClient(String height);
}
