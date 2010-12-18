/* B2227929Initiator.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  6 18:58:23     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zkdemo.test2;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Textbox;

/**
 * Used to test B30-2227929-inc.zul
 * @author tomyeh
 */
public class B2227929Initiator implements org.zkoss.zk.ui.util.Initiator {
	public void doInit(Page page, java.util.Map args) throws Exception {
	}
	public void doAfterCompose(Page page) throws Exception {
		page.getFirstRoot().appendChild(new Textbox());
	}

	public boolean doCatch(Throwable ex) throws Exception {
		return false;
	}
	public void doFinally() throws Exception {
	}
}
