/* B90_ZK_4526_1VM.java

		Purpose:
		
		Description:
		
		History:
				Mon Mar 09 17:58:26 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;

public class B90_ZK_4526_1VM {
	@Init
	public void init() {
		Clients.log("init VM1");
	}
}
