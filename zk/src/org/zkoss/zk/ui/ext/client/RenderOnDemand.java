/* RenderOnDemand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 18 23:46:02     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext.client;

import java.util.Set;

/**
 * Used to decorate a {@link org.zkoss.zk.ui.Component} object that
 * it supports render-on-demand. In other words, the component renders
 * its content only the client request for it.
 * A typical example is Listbox (it is similar to JList and ListModel in
 * Swing).
 *
 * <p>This interface is moved from the org.zkoss.zk.ui.ext package
 * since ZK 2.4.1.
 *
 * @author tomyeh
 * @since 2.4.1
 */
public interface RenderOnDemand {
	/** Renders a set of specified items, if they are not rendered before.
	 * If an item was rendered before, nothing is changed.
	 */
	public void renderItems(Set items);
}
