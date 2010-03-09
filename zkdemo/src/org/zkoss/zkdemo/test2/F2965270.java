/* F2965270.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar  9 16:22:46 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zkdemo.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Composer;

import org.zkoss.zul.*;

/**
 * Used to test system level composer (Feature 2965270).
 * @author tomyeh
 */
public class F2965270 implements Composer {
	public void doAfterCompose(Component comp) {
		if (comp instanceof Window)
			comp.appendChild(new Label("root"));
	}
}
