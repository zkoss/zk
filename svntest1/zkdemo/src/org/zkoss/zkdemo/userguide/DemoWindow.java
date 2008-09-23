/* DemoWindow.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep  2 13:08:20     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.userguide;

import org.zkoss.zul.Window;
import org.zkoss.zul.Include;

/**
 * The demo window.
 *
 * @author tomyeh
 */
public class DemoWindow extends Window {
	public void onCreate() {
		final Include inc = new Include();
		inc.setSrc("/userguide/bar.zul");
		insertBefore(inc, getFirstChild());
	}
}
