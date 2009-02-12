/* ChildChangedAware.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Aug 27 12:13:44     2006, Created by tomyeh
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
 * if a component requires the <code>z.chchg</code> command,
 * when any of its children and descendants are changed.
 *
 * <p>Once a component implements this interface and {@link #isChildChangedAware}
 * returns true, the <code>z.chchg</code> command is sent to the client
 * to update the visual representation. For example, a grid uses two tables
 * to implement header and body, and then it has to re-align the header
 * once any of its descendants is changed.
 *
 * @author tomyeh
 */
public interface ChildChangedAware {
	/** Returns whether to send the <code>z.chchg</code> command
	 * to the client.
	 */
	public boolean isChildChangedAware();
}
