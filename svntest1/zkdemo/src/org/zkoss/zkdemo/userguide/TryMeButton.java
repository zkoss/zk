/* TryMeButton.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 28 21:02:57     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkdemo.userguide;

import org.zkoss.zk.ui.*;
import org.zkoss.zul.*;

/**
 * The "Try me!" button.
 * 
 * @author tomyeh
 */
public class TryMeButton extends Button {
	public void onClick() {
		((Groupbox)getFellow("tryView")).setOpen(true);
		((CodeView)getFellow("codeView")).execute();
	}
}
