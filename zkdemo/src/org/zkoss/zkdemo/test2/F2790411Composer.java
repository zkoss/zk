/* F2790411Composer.java

	Purpose:
		
	Description:
		
	History:
		Tue May 19 18:58:53     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zkdemo.test2;

import org.zkoss.zk.ui.*;
import org.zkoss.zul.*;

/**
 * 
 * @author tomyeh
 */
public class F2790411Composer implements org.zkoss.zk.ui.util.Composer {
	public void doAfterCompose(Component comp) throws Exception {
		Component child = Executions.createComponents(
			null, "/test2/F36-2790411_sub2.zul", null)[0];
		comp.appendChild(child);
	}
}
