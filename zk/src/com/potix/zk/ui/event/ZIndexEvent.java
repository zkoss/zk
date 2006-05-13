/* ZIndexEvent.java

{{IS_NOTE
	$Id: ZIndexEvent.java,v 1.3 2006/03/31 03:20:43 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Sat Dec 24 23:04:41     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.event;

import com.potix.zk.ui.Component;

/**
 * Represents an event caused by a component whose z-index is modified
 * by the client.
 *
 * <p>A z-indexed component must implement {@link com.potix.zk.ui.ext.ZIndexed}.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/03/31 03:20:43 $
 */
public class ZIndexEvent  extends Event {
	private final int _zIndex;

	/** Constructs a mouse relevant event.
	 */
	public ZIndexEvent(String name, Component target, int zIndex) {
		super(name, target);
		_zIndex = zIndex;
	}
	/** Returns the z-index of the component after moved.
	 */
	public final int getZIndex() {
		return _zIndex;
	}
}
