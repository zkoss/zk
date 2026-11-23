/* B90_ZK_4526_2VM.java

		Purpose:
		
		Description:
		
		History:
				Mon Mar 09 17:59:07 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;

public class B90_ZK_4526_2VM {
	@Init
	public void init() {
		Clients.log("init VM2");
	}
}
