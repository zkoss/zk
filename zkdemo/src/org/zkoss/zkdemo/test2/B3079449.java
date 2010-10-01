/* B3079449.java

	Purpose:
		
	Description:
		
	History:
		Fri Oct  1 22:04:27 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zkdemo.test2;

import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.GenericInitiator;

/**
 * Test Bug 3079449
 * @author tomyeh
 */
public class B3079449 extends GenericInitiator {
	public void doInit(Page page, Map args) throws Exception {
		Executions.sendRedirect("B50-3079449a.zul");
	}
}
