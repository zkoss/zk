/* InnerWidth.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Nov  2 23:24:52     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext.client;

/**
 * Implemented by the object returned by {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl}
 * to indicate a component that has a sizable inner width.
 * Some components, such as listbox and grid, have the concept called inner
 * width, which is the width of inner table.
 *
 * <p>Once the inner width is re-sized by the user, {@link #setInnerWidthByClient}
 * is called.
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public interface InnerWidth {
	/** Sets the inner width of the component, caused by user's activity at
	 * the client.
	 * <p>This method is designed to be used by ZK update engine.
	 * Don't invoke it directly. Otherwise, the client and server
	 * might mismatch.
	 * @since 3.0.0
	 */
	public void setInnerWidthByClient(String width);
}
