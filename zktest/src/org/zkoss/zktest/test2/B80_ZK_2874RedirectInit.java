/* B80_ZK_2874RedirectInit.java

	Purpose:
		
	Description:
		
	History:
		5:05 PM 10/15/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Initiator;

/**
 * @author jumperchen
 */
public class B80_ZK_2874RedirectInit implements Initiator {
	public void doInit(Page page, Map<String, Object> args) throws Exception{
		Executions.sendRedirect("#page2");
	}
}