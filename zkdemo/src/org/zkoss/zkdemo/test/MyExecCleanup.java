/* MyExecCleanup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jan 17 15:19:13     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test;

import java.util.List;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.ExecutionCleanup;

/**
 * Test of execution cleanup.
 *
 * Used to test Bug 1868371.
 *
 * @author tomyeh
 */
public class MyExecCleanup implements ExecutionCleanup {
	public void cleanup(Execution exec,
	Execution parent, List errs) throws Exception {
		Desktop desktop = exec.getDesktop();
		desktop.removeAttribute("test");
	}
}
