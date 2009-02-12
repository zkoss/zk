/* NPEEventThreadInit.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Aug 29 22:05:52     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventThreadInit;

/**
 * A test of NPE in EventThreadInit.
 *
 * @author tomyeh
 */
public class NPEEventThreadInit implements EventThreadInit {
	private static boolean _once;

	public void prepare(Component comp, Event evt) throws Exception {
		if (!_once) {
			_once = true;
			throw new NullPointerException("EventThreadInit.prepare failed");
		}
	}
	public boolean init(Component comp, Event evt) throws Exception {
		return true;
	}
}
