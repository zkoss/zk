/* B1881921.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jan 30 14:26:11     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test2;

import java.util.Collection;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.DesktopInit;
import org.zkoss.zk.ui.util.DesktopCleanup;

/**
 * Used to verify Bug 1881921: the execution order of DesktopInit and
 * DesktopCleanup.
 *
 * @author tomyeh
 */
public class B1881921 implements DesktopInit, DesktopCleanup {
	public void init(Desktop desktop,  Object request) throws Exception {
		System.out.println("init "+desktop);
	}
	public void cleanup(Desktop desktop) throws Exception {
		System.out.println("cleanup "+desktop);
	}
}
