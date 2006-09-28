/* ReloadButton.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 28 21:09:04     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkdemo;

import java.util.Set;
import java.util.HashSet;

import org.zkoss.zk.ui.*;
import org.zkoss.zul.*;

/**
 * The "Reload" button.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class ReloadButton extends Button {
	public void onClick() {
		Path.getComponent("//userGuide/root/contents/xcontents").invalidate();
	}
}
