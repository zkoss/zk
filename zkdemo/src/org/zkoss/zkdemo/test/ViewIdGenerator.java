/* ViewIdGenerator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 13 23:16:06     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.sys.IdGenerator;

/**
 * Used to see the status when IdGenerator is called.
 *
 * @author tomyeh
 */
public class ViewIdGenerator implements IdGenerator {
	public String nextComponentUuid(Desktop desktop, Component comp) {
		System.out.println("nextComponentUuid for "+comp.getClass()+", parent="+comp.getParent()+", page="+comp.getPage());
		return null;
	}
	public String nextPageUuid(Page page) {
		return null;
	}
	public String nextDesktopId(Desktop desktop) {
		return null;
	}
}
