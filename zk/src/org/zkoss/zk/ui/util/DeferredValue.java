/* DeferredValue.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Dec 17 14:22:06     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

/**
 * Used with {@link org.zkoss.zk.ui.AbstractComponent#smartUpdate}
 * to represent a value that shall be evaluated only in the rendering phase
 * (rather than in the event processing phase).
 *
 * @author tomyeh
 * @since 3.0.1
 */
public interface DeferredValue {
	/** Returns the value.
	 * It is called to retrieve the real value
	 * by the ZK engine in the rendering phase.
	 * @since 5.0.0 (return type becomes Object)
	 */
	public Object getValue();
}
