/* B95_ZK_4698VM.java

	Purpose:
		
	Description:
		
	History:
		Wed Oct 14 18:19:44 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author rudyhuang
 */
public class B95_ZK_4698VM {
	@Init
	public void init(@ExecutionArgParam("myParam") String myParam) {
		Clients.log("myParam = " + myParam);
	}
}
