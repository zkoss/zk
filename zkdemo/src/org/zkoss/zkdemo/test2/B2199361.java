/* B2199361.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Oct 27 09:43:10     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test2;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zul.Label;

/**
 * Test of Bug 2199361.
 *
 * @author tomyeh
 */
public class B2199361 implements Initiator {
	public void doInit(Page page, Object[] args) throws Exception {
		page.setId("abc"); //Bug 2525344
		new Label("Hello, Initiator").setPage(page);
	}
	public void doAfterCompose(Page page) throws Exception {
	}

	public boolean doCatch(Throwable ex) throws Exception {
		return false;
	}
	public void doFinally() throws Exception {
	}
}
