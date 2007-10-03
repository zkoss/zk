/* ReloadButton.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 28 21:09:04     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkdemo.userguide;

import java.util.Set;
import java.util.HashSet;

import org.zkoss.zk.ui.*;
import org.zkoss.zul.*;

/**
 * The "Reload" button.
 * 
 * @author tomyeh
 */
public class ReloadButton extends Button {
	public void onClick() {
		Path.getComponent("//userGuide/xcontents").invalidate();
	}
}
