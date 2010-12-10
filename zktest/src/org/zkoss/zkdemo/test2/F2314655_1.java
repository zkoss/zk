/* F2314655_1.java

	Purpose:
		
	Description:
		
	History:
		Mon Feb 23 14:42:20     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zkdemo.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;

/**
 * Used to test F36-2314655
 * @author tomyeh
 */
public class F2314655_1 implements org.zkoss.zk.ui.util.Composer {
	public void doAfterCompose(Component comp) {
		new Label("first,").setParent(comp);
	}
}
