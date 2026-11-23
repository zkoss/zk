/* F3259969.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 31 11:02:04 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zktest.test2;

import java.util.Map;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zk.ui.util.InitiatorExt;
import org.zkoss.zul.Label;

/**
 * Test of system-level initiator
 * @author tomyeh
 */
public class F3259969 implements Initiator, InitiatorExt {
	public void doInit(Page page, Map args) throws Exception {
	}
	public void doAfterCompose(Page page, Component[] comps) throws Exception {
		new Label("system initiator called").setPage(page);
	}
	public boolean doCatch(Throwable ex) throws Exception {
		return false;
	}
	public void doFinally() throws Exception {
	}
}
