/* DeferredValue.java

	Purpose:
		
	Description:
		
	History:
		Mon Dec 17 14:22:06     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au;

/**
 * Used with {@link org.zkoss.zk.ui.AbstractComponent#smartUpdate}
 * to represent the value of a smart update
 * that shall be evaluated only in the rendering phase
 * (rather than in the event processing phase).
 *
 * <p>Notice that, if you are sending a smart update that will
 * instantiate the client'side widget, it is better to use
 * {@link org.zkoss.zk.ui.sys.UiEngine#addSmartUpdate(org.zkoss.zk.ui.Component, String, Object, int)}
 * with priority 10000, such that it will execute before others.
 *
 * @author tomyeh
 * @since 6.0.0
 */
public interface DeferredValue {
	/** Returns the value.
	 * It is called to retrieve the real value
	 * by the ZK engine in the rendering phase.
	 */
	public Object getValue();
}
