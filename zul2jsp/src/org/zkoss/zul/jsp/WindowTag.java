/* WindowTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jul 23 15:45:45     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.jsp;

import org.zkoss.zk.ui.Component;

import org.zkoss.zul.Window;
import org.zkoss.zul.jsp.impl.BranchTag;

/**
 * Represents the window component of XUL.
 *
 * @author tomyeh
 */
public class WindowTag extends BranchTag {
	private String _title;

	//BranchTag//
	public Component newComponent() {
		final Window wnd = new Window();
		wnd.setTitle(_title);
		return wnd;
	}

	//Extra//
	/** Sets the window title.
	 */
	public void setTitle(String title) {
		_title = title;
	}
}
