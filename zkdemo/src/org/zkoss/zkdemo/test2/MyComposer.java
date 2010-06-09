/* MyComposer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 25 22:49:26     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Window;

/**
 * Test of Composer and {@link Components#wireVariables(Component, Object)}.
 *
 * @author tomyeh
 */
public class MyComposer implements Composer {
	public void doAfterCompose(Component comp) {
		final Window w = (Window)comp;
		w.setTitle("Composer: "+w.getTitle());
		w.setBorder("normal");
		Components.wireVariables(comp, this);
	}
	public String getHello() {
		return "Hello! ZK.";
	}
}
