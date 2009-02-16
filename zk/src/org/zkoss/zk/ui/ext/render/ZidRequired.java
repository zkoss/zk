/* ZidRequired.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 13 15:47:20     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext.render;

/**
 * Implemented by the object returned by {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl}
 * if the component might be accessible at the client with
 * {@link org.zkoss.zk.ui.Component#getId}.
 *
 * <p>In other words, the client engine will handle extra info related to
 * {@link org.zkoss.zk.ui.IdSpace} when this interface is declared and
 * {@link #isZidRequired} returns true.
 *
 * <p>Most components don't need to implement this interface for the object
 * returned by {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl}.
 *
 * <p>Note: if a component is NOT derived from {@link org.zkoss.zk.ui.HtmlBasedComponent},
 * it has to detect this interface and generate z.zid if {@link #isZidRequired}
 * return true.
 *
 * @author tomyeh
 */
public interface ZidRequired {
	/** Returns whether the component that owns this object
	 * can be accessible at the client with
	 * {@link org.zkoss.zk.ui.Component#getId}.
	 */
	public boolean isZidRequired();
}
