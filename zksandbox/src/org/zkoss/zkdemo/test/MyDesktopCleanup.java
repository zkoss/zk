/* MyDesktopCleanup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 18 18:53:42     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.util.DesktopCleanup;

/**
 * Test of DesktopCleanup
 * @author tomyeh
 */
public class MyDesktopCleanup implements DesktopCleanup {
	/** called when a desktop is about to be destroyed.
	 *
	 * <p>If this method throws an exception, the error message is
	 * only logged (user won't see it).
	 */
	public void cleanup(Desktop desktop) throws Exception {
		System.out.println("Cleanup "+desktop);
	}
}
